package com.example.vademecum.dataclass

/**
 * Captura los datos para saber si está comercializado o no.
 * @author José Ramón Laperal
 */
data class Presentacion(
    var nombre: String,
    var comercializado: Boolean,
    var psum: Boolean
)
