package com.example.vademecum.Dataclass

/**
 * Objeto que captura los datos de la vista DetalleFarmaco
 * El json viene en grupos de 25 fármacos en un objeto que es lo que capturamos aquí
 * En este objeto capturamos
 * @author José Ramón Laperal
 */
data class MiObjeto(
    var totalFilas: Int,
    var pagina: Int,
    var tamanioPagina: Int,
    var resultados: List<MiFarmaco>
)