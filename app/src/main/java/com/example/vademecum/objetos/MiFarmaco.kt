package com.example.vademecum.objetos

import com.google.gson.annotations.SerializedName

data class MiFarmaco(
    @SerializedName("nregistro")
    var nregistro:String,
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("labtitular")
    var labtitular: String
)