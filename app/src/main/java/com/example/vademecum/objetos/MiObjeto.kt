package com.example.vademecum.objetos

/**
 * Objeto que captura los datos de la vista DetalleFarmaco
 * @author José Ramón Laperal
 */
data class MiObjeto(
    var totalFilas: Int,
    var pagina: Int,
    var tamanioPagina: Int,
    var resultados: List<MiFarmaco>,
    var excipientes: List<Excipiente>
)