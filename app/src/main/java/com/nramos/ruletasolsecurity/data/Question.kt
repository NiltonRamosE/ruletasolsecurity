package com.nramos.ruletasolsecurity.data

data class Question(
    val id: Int,
    val text: String,
    val type: QuestionType,
    val options: List<String>? = null,
    val correctAnswer: String,
    val explanation: String? = null
)

enum class QuestionType {
    MULTIPLE_CHOICE,  // Preguntas con opciones A, B, C
    TRUE_FALSE,       // Verdadero o Falso
    FREE_RESPONSE     // Respuesta abierta (con botones Correcto/Incorrecto)
}