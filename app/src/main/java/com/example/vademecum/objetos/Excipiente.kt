package com.example.vademecum.objetos

/**
 *  @author José Ramón Laperal
 *  Objeto para capturar los excipientes..
 *  Se usa en la vsta de DetalleFarmaco
 */
data class Excipiente (
    var nombre: String,
    var cantidad: String,
    var unidad: String,
    var orden: Int
)

