package com.nramos.ruletasolsecurity.data

import com.nramos.ruletasolsecurity.data.remote.CategoryResponse

data class Category(
    val id: Int,
    val name: String,
    val questions: List<Question>
) {
    companion object {
        fun fromResponse(response: CategoryResponse): Category {
            return Category(
                id = response.categoriaId,
                name = response.categoriaNombre,
                questions = response.preguntas.map { Question.fromResponse(it) }
            )
        }
    }
}