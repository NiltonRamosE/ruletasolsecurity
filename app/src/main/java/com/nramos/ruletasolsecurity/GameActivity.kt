package com.nramos.ruletasolsecurity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.nramos.ruletasolsecurity.data.Category
import com.nramos.ruletasolsecurity.data.Question
import com.nramos.ruletasolsecurity.data.QuestionType
import com.nramos.ruletasolsecurity.databinding.ActivityGameBinding
import com.nramos.ruletasolsecurity.viewmodel.RuletaViewModel

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: RuletaViewModel

    private var currentCategory: Category? = null
    private var currentQuestion: Question? = null
    private var isAnswering = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[RuletaViewModel::class.java]

        setupObservers()
        setupRuleta()
        setupButtons()

        // Cargar datos si no están disponibles
        if (viewModel.categories.value.isNullOrEmpty()) {
            viewModel.loadCategories()
        } else {
            // Si ya hay datos, pasarlos a la ruleta
            viewModel.categories.value?.let { categories ->
                if (categories.isNotEmpty()) {
                    binding.ruletaView.setCategories(categories)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.categories.observe(this) { categories ->
            if (categories.isNotEmpty()) {
                android.util.Log.d("GameActivity", "Categorías recibidas: ${categories.size}")
                binding.ruletaView.setCategories(categories)
                binding.btnGirar.isEnabled = true
                binding.tvBtnGirarLabel.text = getString(R.string.btn_girar)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.tvBtnGirarLabel.text = "CARGANDO..."
                binding.btnGirar.isEnabled = false
            } else {
                binding.btnGirar.isEnabled = true
                binding.tvBtnGirarLabel.text = getString(R.string.btn_girar)
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Snackbar.make(binding.root, "Error: $it", Snackbar.LENGTH_LONG).show()
                binding.btnGirar.isEnabled = true
                binding.tvBtnGirarLabel.text = getString(R.string.btn_girar)
            }
        }
    }

    private fun setupRuleta() {
        binding.ruletaView.setOnCategorySelectedListener { category ->
            android.util.Log.d("GameActivity", "Categoría seleccionada: ${category.name}")
            currentCategory = category
            showQuestionForCategory(category)
        }
    }

    private fun setupButtons() {
        binding.btnGirar.setOnClickListener {
            if (!binding.ruletaView.isSpinning()) {
                val categories = viewModel.categories.value
                if (categories.isNullOrEmpty()) {
                    Snackbar.make(binding.root, "Cargando datos...", Snackbar.LENGTH_SHORT).show()
                    viewModel.loadCategories()
                    return@setOnClickListener
                }
                if (categories.size < 3) {
                    Snackbar.make(binding.root, "Faltan categorías", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                startSpinUi()
                binding.ruletaView.spin()
                animateButtonPress()
            }
        }

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

        binding.rgOpciones.removeAllViews()
        binding.rgOpciones.clearCheck()
        binding.btnVerificar.isEnabled = true
        binding.btnCorrecto.isEnabled = true
        binding.btnIncorrecto.isEnabled = true
    }

    private fun showQuestionForCategory(category: Category) {
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
        question.options.forEach { option ->
            radioGroup.addView(buildOptionRadioButton(option))
        }

        binding.btnVerificar.setOnClickListener { verifyMultipleChoice(question) }
    }

    private fun setupTrueFalse(question: Question) {
        binding.llOpciones.visibility = View.VISIBLE

        val radioGroup = binding.rgOpciones
        radioGroup.removeAllViews()
        question.options.forEach { option ->
            radioGroup.addView(buildOptionRadioButton(option))
        }

        binding.btnVerificar.setOnClickListener { verifyTrueFalse(question) }
    }

    private fun setupFreeResponse(question: Question) {
        binding.tvFreeResponseHint.visibility = View.VISIBLE
        binding.btnVerificar.visibility = View.GONE
        binding.llFreeResponseButtons.visibility = View.VISIBLE
    }

    private fun verifyMultipleChoice(question: Question) {
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

        binding.btnVerificar.visibility = View.GONE
        binding.llFreeResponseButtons.visibility = View.GONE
        binding.btnReiniciar.visibility = View.VISIBLE

        binding.scrollQuestion.post {
            binding.scrollQuestion.smoothScrollTo(0, binding.tvRespuestaCorrecta.top)
        }
    }
}