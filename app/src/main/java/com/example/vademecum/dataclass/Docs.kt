package com.example.vademecum.dataclass

/**
 * @author José Ramón Laperal
 * Captura la url de lo ficha técnica y el prospecto.
 */
data class Docs(
    var tipo: Int,
    var urlHtml: String
)