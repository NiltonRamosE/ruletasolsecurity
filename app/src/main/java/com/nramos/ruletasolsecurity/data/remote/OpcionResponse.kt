package com.nramos.ruletasolsecurity.data.remote

import com.google.gson.annotations.SerializedName

data class OpcionResponse(
    @SerializedName("opcion_id")
    val opcionId: Int,
    @SerializedName("opcion_texto")
    val opcionTexto: String,
    @SerializedName("es_correcto")
    val esCorrecto: Boolean
)