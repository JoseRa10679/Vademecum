package com.example.vademecum.dataclass

import com.google.gson.annotations.SerializedName

/**
 *  Este objeto se usa solo para el layout DetalleFarmaco.
 *  Captura los datos de salida del farmaco requerido
 *  @author José Ramón Laperal
 */
data class EsteFarmaco(
    @SerializedName("nregistro")
    var nregistro: String,
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("labtitular")
    var labtitular: String,
    @SerializedName("principiosActivos")
    var pactivos: List<PActivos>,
    @SerializedName("cpresc")
    var cpresc: String,
    @SerializedName("conduc")
    var conduc: Boolean,
    @SerializedName("comerc")
    var comerc: Boolean,
    @SerializedName("presentaciones")
    var presentaciones: List<Presentacion>,
    @SerializedName("excipientes")
    var excipientes: List<Excipiente>,
    @SerializedName("docs")
    var docs: List<Docs>,
    @SerializedName("psum")
    var psum: Boolean
)