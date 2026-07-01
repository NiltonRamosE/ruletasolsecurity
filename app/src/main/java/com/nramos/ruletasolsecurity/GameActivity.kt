package com.nramos.ruletasolsecurity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.nramos.ruletasolsecurity.data.Category
import com.nramos.ruletasolsecurity.data.Question
import com.nramos.ruletasolsecurity.data.QuestionType
import com.nramos.ruletasolsecurity.databinding.ActivityGameBinding

/**
 * Pantalla del juego.
 *
 * Tiene dos "secciones" dentro de un mismo layout, mutuamente excluyentes:
 * - [ActivityGameBinding.wheelSection]: la ruleta con el botón GIRAR.
 * - [ActivityGameBinding.questionSection]: la pregunta a pantalla completa,
 *   con contenido scrollable y una barra de acciones fija en la parte
 *   inferior (siempre visible, sin importar cuánto scroll haya).
 */
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private var currentCategory: Category? = null
    private var currentQuestion: Question? = null
    private var isAnswering = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRuleta()
        setupButtons()
    }

    // ============================================================
    // Configuración inicial
    // ============================================================

    private fun setupRuleta() {
        binding.ruletaView.setOnCategorySelectedListener { category ->
            currentCategory = category
            showQuestionForCategory(category)
        }
    }

    private fun setupButtons() {
        binding.btnGirar.setOnClickListener {
            if (!binding.ruletaView.isSpinning()) {
                startSpinUi()
                binding.ruletaView.spin()
                animateButtonPress()
            }
        }

        // El botón "X" en la pantalla de pregunta regresa a la ruleta.
        binding.btnClose.setOnClickListener {
            returnToWheel()
        }

        binding.btnCorrecto.setOnClickListener { handleFreeResponse(true) }
        binding.btnIncorrecto.setOnClickListener { handleFreeResponse(false) }
        binding.btnReiniciar.setOnClickListener { returnToWheel() }
    }

    private fun animateButtonPress() {
        val animator = ObjectAnimator.ofFloat(binding.btnGirar, "scaleX", 1f, 0.95f, 1f)
        animator.duration = 300
        animator.start()
    }

    // ============================================================
    // Transición entre la ruleta y la pregunta a pantalla completa
    // ============================================================

    private fun startSpinUi() {
        binding.btnGirar.isEnabled = false
        binding.tvBtnGirarLabel.text = getString(R.string.girando)
    }

    private fun showQuestionScreen() {
        binding.wheelSection.visibility = View.GONE
        binding.questionSection.visibility = View.VISIBLE
        binding.scrollQuestion.scrollTo(0, 0)
    }

    private fun returnToWheel() {
        binding.ruletaView.resetSpin()
        currentCategory = null
        currentQuestion = null
        isAnswering = false

        binding.questionSection.visibility = View.GONE
        binding.wheelSection.visibility = View.VISIBLE
        binding.btnGirar.isEnabled = true
        binding.tvBtnGirarLabel.text = getString(R.string.btn_girar)

        // Limpiar estado de la pregunta anterior para la próxima vez.
        binding.rgOpciones.removeAllViews()
        binding.rgOpciones.clearCheck()
        binding.btnVerificar.isEnabled = true
        binding.btnCorrecto.isEnabled = true
        binding.btnIncorrecto.isEnabled = true
    }

    // ============================================================
    // Mostrar la pregunta
    // ============================================================

    private fun showQuestionForCategory(category: Category) {
        // Restaurar el botón GIRAR para la próxima vuelta (aunque esté oculto ahora).
        binding.btnGirar.isEnabled = true
        binding.tvBtnGirarLabel.text = getString(R.string.btn_girar)

        val questions = category.questions
        if (questions.isEmpty()) {
            Snackbar.make(binding.root, "No hay preguntas en esta categoría", Snackbar.LENGTH_SHORT).show()
            return
        }

        val randomQuestion = questions.random()
        currentQuestion = randomQuestion
        displayQuestion(category, randomQuestion)
        showQuestionScreen()
    }

    private fun displayQuestion(category: Category, question: Question) {
        binding.tvCategoria.text = category.name
        binding.tvNumeroPregunta.text = "Pregunta #${question.id}"
        binding.tvPregunta.text = question.text

        // Reset de visibilidad de todos los bloques dinámicos.
        binding.llOpciones.visibility = View.GONE
        binding.tvFreeResponseHint.visibility = View.GONE
        binding.tvRespuestaCorrecta.visibility = View.GONE
        binding.tvExplicacion.visibility = View.GONE

        binding.btnVerificar.visibility = View.VISIBLE
        binding.btnVerificar.isEnabled = true
        binding.llFreeResponseButtons.visibility = View.GONE
        binding.btnReiniciar.visibility = View.GONE

        isAnswering = false

        when (question.type) {
            QuestionType.MULTIPLE_CHOICE -> setupMultipleChoice(question)
            QuestionType.TRUE_FALSE -> setupTrueFalse(question)
            QuestionType.FREE_RESPONSE -> setupFreeResponse(question)
        }
    }

    private fun buildOptionRadioButton(text: String): RadioButton {
        return RadioButton(this).apply {
            this.text = text
            textSize = 14.5f
            setTextColor(ContextCompat.getColor(context, R.color.text_title))
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = ContextCompat.getDrawable(context, R.drawable.bg_option_selector)
            val params = RadioGroup.LayoutParams(
                RadioGroup.LayoutParams.MATCH_PARENT,
                RadioGroup.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = dp(10)
            layoutParams = params
        }
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private fun setupMultipleChoice(question: Question) {
        binding.llOpciones.visibility = View.VISIBLE

        val radioGroup = binding.rgOpciones
        radioGroup.removeAllViews()
        question.options?.forEach { option ->
            radioGroup.addView(buildOptionRadioButton(option))
        }

        binding.btnVerificar.setOnClickListener { verifyMultipleChoice(question) }
    }

    private fun setupTrueFalse(question: Question) {
        binding.llOpciones.visibility = View.VISIBLE

        val radioGroup = binding.rgOpciones
        radioGroup.removeAllViews()
        question.options?.forEach { option ->
            radioGroup.addView(buildOptionRadioButton(option))
        }

        binding.btnVerificar.setOnClickListener { verifyTrueFalse(question) }
    }

    private fun setupFreeResponse(question: Question) {
        binding.tvFreeResponseHint.visibility = View.VISIBLE
        binding.btnVerificar.visibility = View.GONE
        binding.llFreeResponseButtons.visibility = View.VISIBLE
    }

    // ============================================================
    // Verificación de respuestas
    // ============================================================

    private fun verifyMultipleChoice(question: Question) {
        if (isAnswering) return
        val selectedId = binding.rgOpciones.checkedRadioButtonId
        if (selectedId == -1) {
            Snackbar.make(binding.root, "Selecciona una opción", Snackbar.LENGTH_SHORT).show()
            return
        }
        isAnswering = true
        val selectedText = findViewById<RadioButton>(selectedId).text.toString()
        val isCorrect = selectedText.startsWith(question.correctAnswer)
        showAnswerResult(isCorrect, question)
    }

    private fun verifyTrueFalse(question: Question) {
        if (isAnswering) return
        val selectedId = binding.rgOpciones.checkedRadioButtonId
        if (selectedId == -1) {
            Snackbar.make(binding.root, "Selecciona una opción", Snackbar.LENGTH_SHORT).show()
            return
        }
        isAnswering = true
        val selectedText = findViewById<RadioButton>(selectedId).text.toString()
        val isCorrect = selectedText == question.correctAnswer
        showAnswerResult(isCorrect, question)
    }

    private fun handleFreeResponse(isCorrect: Boolean) {
        if (isAnswering) return
        isAnswering = true
        currentQuestion?.let { showAnswerResult(isCorrect, it) }
    }

    private fun showAnswerResult(isCorrect: Boolean, question: Question) {
        binding.tvRespuestaCorrecta.visibility = View.VISIBLE
        val answerText = if (question.type == QuestionType.FREE_RESPONSE) {
            "Respuesta correcta: ${question.correctAnswer}"
        } else {
            if (isCorrect) "✓ ¡Correcto!" else "✗ Incorrecto"
        }
        binding.tvRespuestaCorrecta.text = answerText

        if (isCorrect) {
            binding.tvRespuestaCorrecta.setBackgroundResource(R.drawable.bg_feedback_success)
            binding.tvRespuestaCorrecta.setTextColor(ContextCompat.getColor(this, R.color.success_green_dark))
        } else {
            binding.tvRespuestaCorrecta.setBackgroundResource(R.drawable.bg_feedback_error)
            binding.tvRespuestaCorrecta.setTextColor(ContextCompat.getColor(this, R.color.error_red_dark))
        }

        question.explanation?.let {
            binding.tvExplicacion.visibility = View.VISIBLE
            binding.tvExplicacion.text = it
        }

        // Ocultar los controles de respuesta y mostrar la acción para continuar.
        binding.btnVerificar.visibility = View.GONE
        binding.llFreeResponseButtons.visibility = View.GONE
        binding.btnReiniciar.visibility = View.VISIBLE

        // Desplazar el scroll para que el resultado quede visible de inmediato.
        binding.scrollQuestion.post {
            binding.scrollQuestion.smoothScrollTo(0, binding.tvRespuestaCorrecta.top)
        }
    }
}