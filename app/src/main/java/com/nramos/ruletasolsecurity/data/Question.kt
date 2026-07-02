package com.nramos.ruletasolsecurity.data

import com.nramos.ruletasolsecurity.data.remote.PreguntaResponse

data class Question(
    val id: Int,
    val text: String,
    val type: QuestionType,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String? = null
) {
    companion object {
        fun fromResponse(response: PreguntaResponse): Question {
            val type = when (response.preguntaTipo) {
                "multiple" -> QuestionType.MULTIPLE_CHOICE
                "true_false" -> QuestionType.TRUE_FALSE
                "free_response" -> QuestionType.FREE_RESPONSE
                else -> QuestionType.MULTIPLE_CHOICE
            }

            val options = response.opciones.map { it.opcionTexto }
            val correctAnswer = response.opciones.find { it.esCorrecto }?.opcionTexto ?: ""

            return Question(
                id = response.preguntaId,
                text = response.preguntaTexto,
                type = type,
                options = options,
                correctAnswer = correctAnswer,
                explanation = response.preguntaExplicacion
            )
        }
    }
}