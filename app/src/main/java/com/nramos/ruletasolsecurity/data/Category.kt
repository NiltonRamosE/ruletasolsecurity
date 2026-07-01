package com.nramos.ruletasolsecurity.data

data class Category(
    val id: Int,
    val name: String,
    val color: Int,
    val questions: List<Question>
)