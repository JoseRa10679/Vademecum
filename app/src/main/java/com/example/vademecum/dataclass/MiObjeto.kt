package com.example.vademecum.dataclass

/**
 * Objeto que captura los datos de la vista DetalleFarmaco
 * El json viene en grupos de 100 fármacos definido en el GET por pagesize en un objeto que es lo que capturamos aquí
 * @author José Ramón Laperal
 */
data class MiObjeto(
    var totalFilas: Int,
    var pagina: Int,
    var tamanioPagina: Int,
    var resultados: List<MiFarmaco>
)