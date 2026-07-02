package com.nramos.ruletasolsecurity.data.remote

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("categoria_id")
    val categoriaId: Int,
    @SerializedName("categoria_nombre")
    val categoriaNombre: String,
    @SerializedName("preguntas")
    val preguntas: List<PreguntaResponse>
)