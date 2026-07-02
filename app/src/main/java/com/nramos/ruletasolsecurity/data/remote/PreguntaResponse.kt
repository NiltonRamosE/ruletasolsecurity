package com.nramos.ruletasolsecurity.data.remote

import com.google.gson.annotations.SerializedName

data class PreguntaResponse(
    @SerializedName("pregunta_id")
    val preguntaId: Int,
    @SerializedName("pregunta_texto")
    val preguntaTexto: String,
    @SerializedName("pregunta_tipo")
    val preguntaTipo: String, // "multiple", "true_false", "free_response"
    @SerializedName("pregunta_explicacion")
    val preguntaExplicacion: String? = null,
    @SerializedName("opciones")
    val opciones: List<OpcionResponse>
)