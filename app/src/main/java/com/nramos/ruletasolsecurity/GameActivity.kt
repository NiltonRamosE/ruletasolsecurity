package com.nramos.ruletasolsecurity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.nramos.ruletasolsecurity.data.Category
import com.nramos.ruletasolsecurity.data.Question
import com.nramos.ruletasolsecurity.data.QuestionData
import com.nramos.ruletasolsecurity.data.QuestionType
import com.nramos.ruletasolsecurity.databinding.ActivityGameBinding

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

    private fun setupRuleta() {
        binding.ruletaView.setOnCategorySelectedListener { category ->
            currentCategory = category
            // Después de seleccionar categoría, mostrar la pregunta
            showQuestionForCategory(category)
        }
    }

    private fun setupButtons() {
        binding.btnGirar.setOnClickListener {
            if (!binding.ruletaView.isSpinning()) {
                resetUI()
                binding.ruletaView.spin()
                animateButtonPress()
            }
        }

        binding.btnVerificar.setOnClickListener {
            verifyAnswer()
        }

        binding.btnCorrecto.setOnClickListener {
            handleFreeResponse(true)
        }

        binding.btnIncorrecto.setOnClickListener {
            handleFreeResponse(false)
        }

        binding.btnReiniciar.setOnClickListener {
            resetGame()
        }
    }

    private fun animateButtonPress() {
        val animator = ObjectAnimator.ofFloat(binding.btnGirar, "scaleX", 1f, 0.95f, 1f)
        animator.duration = 300
        animator.start()
    }

    private fun resetUI() {
        binding.cardPregunta.visibility = View.GONE
        binding.btnGirar.isEnabled = false
        binding.btnGirar.text = getString(R.string.girando)
    }

    private fun showQuestionForCategory(category: Category) {
        binding.btnGirar.isEnabled = true
        binding.btnGirar.text = getString(R.string.btn_girar)

        // Seleccionar una pregunta aleatoria de la categoría
        val questions = category.questions
        if (questions.isEmpty()) {
            Snackbar.make(binding.root, "No hay preguntas en esta categoría", Snackbar.LENGTH_SHORT).show()
            return
        }

        val randomQuestion = questions.random()
        currentQuestion = randomQuestion
        displayQuestion(category, randomQuestion)
    }

    private fun displayQuestion(category: Category, question: Question) {
        binding.cardPregunta.visibility = View.VISIBLE
        binding.tvCategoria.text = category.name
        binding.tvNumeroPregunta.text = "Pregunta #${question.id}"
        binding.tvPregunta.text = question.text

        // Ocultar todos los tipos de respuesta
        binding.llOpciones.visibility = View.GONE
        binding.llFreeResponseButtons.visibility = View.GONE
        binding.tvRespuestaCorrecta.visibility = View.GONE
        binding.tvExplicacion.visibility = View.GONE
        binding.btnReiniciar.visibility = View.GONE
        binding.btnVerificar.isEnabled = true

        isAnswering = false

        when (question.type) {
            QuestionType.MULTIPLE_CHOICE -> setupMultipleChoice(question)
            QuestionType.TRUE_FALSE -> setupTrueFalse(question)
            QuestionType.FREE_RESPONSE -> setupFreeResponse(question)
        }
    }

    private fun setupMultipleChoice(question: Question) {
        binding.llOpciones.visibility = View.VISIBLE
        binding.llFreeResponseButtons.visibility = View.GONE

        val radioGroup = binding.rgOpciones
        radioGroup.removeAllViews()

        question.options?.forEach { option ->
            val radioButton = RadioButton(this).apply {
                text = option
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.text_title))
            }
            radioGroup.addView(radioButton)
        }

        binding.btnVerificar.setOnClickListener {
            verifyMultipleChoice(question)
        }
    }

    private fun setupTrueFalse(question: Question) {
        binding.llOpciones.visibility = View.VISIBLE
        binding.llFreeResponseButtons.visibility = View.GONE

        val radioGroup = binding.rgOpciones
        radioGroup.removeAllViews()

        question.options?.forEach { option ->
            val radioButton = RadioButton(this).apply {
                text = option
                textSize = 14f
                setTextColor(ContextCompat.getColor(context, R.color.text_title))
            }
            radioGroup.addView(radioButton)
        }

        binding.btnVerificar.setOnClickListener {
            verifyTrueFalse(question)
        }
    }

    private fun setupFreeResponse(question: Question) {
        binding.llOpciones.visibility = View.GONE
        binding.llFreeResponseButtons.visibility = View.VISIBLE
        binding.tvRespuestaCorrecta.visibility = View.GONE
        binding.tvExplicacion.visibility = View.GONE
        binding.btnReiniciar.visibility = View.GONE
    }

    private fun verifyMultipleChoice(question: Question) {
        if (isAnswering) return

        val radioGroup = binding.rgOpciones
        val selectedId = radioGroup.checkedRadioButtonId

        if (selectedId == -1) {
            Snackbar.make(binding.root, "Selecciona una opción", Snackbar.LENGTH_SHORT).show()
            return
        }

        isAnswering = true
        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        val selectedText = selectedRadioButton.text.toString()
        val isCorrect = selectedText.startsWith(question.correctAnswer)

        showAnswerResult(isCorrect, question)
    }

    private fun verifyTrueFalse(question: Question) {
        if (isAnswering) return

        val radioGroup = binding.rgOpciones
        val selectedId = radioGroup.checkedRadioButtonId

        if (selectedId == -1) {
            Snackbar.make(binding.root, "Selecciona una opción", Snackbar.LENGTH_SHORT).show()
            return
        }

        isAnswering = true
        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        val selectedText = selectedRadioButton.text.toString()
        val isCorrect = selectedText == question.correctAnswer

        showAnswerResult(isCorrect, question)
    }

    private fun handleFreeResponse(isCorrect: Boolean) {
        if (isAnswering) return
        isAnswering = true

        currentQuestion?.let { question ->
            showAnswerResult(isCorrect, question)
        }
    }

    private fun verifyAnswer() {
        // Este método se usa para los casos de multiple choice y true/false
        // La lógica está en los métodos específicos
    }

    private fun showAnswerResult(isCorrect: Boolean, question: Question) {
        // Mostrar respuesta correcta
        binding.tvRespuestaCorrecta.visibility = View.VISIBLE
        val answerText = if (question.type == QuestionType.FREE_RESPONSE) {
            "Respuesta correcta: ${question.correctAnswer}"
        } else {
            "Respuesta ${if (isCorrect) "✓ Correcta" else "✗ Incorrecta"}"
        }
        binding.tvRespuestaCorrecta.text = answerText

        // Colorear según correcto/incorrecto
        val bgColor = if (isCorrect) {
            ContextCompat.getColor(this, R.color.success_green_light)
        } else {
            ContextCompat.getColor(this, R.color.error_red_light)
        }
        val textColor = if (isCorrect) {
            ContextCompat.getColor(this, R.color.success_green_dark)
        } else {
            ContextCompat.getColor(this, R.color.error_red_dark)
        }
        binding.tvRespuestaCorrecta.setBackgroundColor(bgColor)
        binding.tvRespuestaCorrecta.setTextColor(textColor)

        // Mostrar explicación
        question.explanation?.let {
            binding.tvExplicacion.visibility = View.VISIBLE
            binding.tvExplicacion.text = it
        }

        // Deshabilitar botones de respuesta
        binding.btnVerificar.isEnabled = false
        binding.btnCorrecto.isEnabled = false
        binding.btnIncorrecto.isEnabled = false

        // Mostrar botón reiniciar
        binding.btnReiniciar.visibility = View.VISIBLE
        binding.btnReiniciar.text = getString(R.string.btn_reiniciar)
    }

    private fun resetGame() {
        // Resetear la ruleta
        binding.ruletaView.resetSpin()
        currentCategory = null
        currentQuestion = null
        isAnswering = false

        // Resetear UI
        binding.cardPregunta.visibility = View.GONE
        binding.btnGirar.isEnabled = true
        binding.btnGirar.text = getString(R.string.btn_girar)
        binding.btnReiniciar.visibility = View.GONE
        binding.tvRespuestaCorrecta.visibility = View.GONE
        binding.tvExplicacion.visibility = View.GONE

        // Resetear radio group
        binding.rgOpciones.removeAllViews()
        binding.rgOpciones.clearCheck()

        // Resetear botones
        binding.btnVerificar.isEnabled = true
        binding.btnCorrecto.isEnabled = true
        binding.btnIncorrecto.isEnabled = true

        // Snackbar de reinicio
        Snackbar.make(binding.root, "¡Ruleta reiniciada!", Snackbar.LENGTH_SHORT).show()
    }
}