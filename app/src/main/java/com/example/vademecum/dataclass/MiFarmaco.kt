package com.example.vademecum.dataclass

import com.google.gson.annotations.SerializedName

/**
 * Captura los datos para filtrar los fármacos de la vista
 * MainActivity
 * @author José Ramón Laperal
 */
data class MiFarmaco(
    @SerializedName("nregistro")
    var nregistro: String,
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("labtitular")
    var labtitular: String
)