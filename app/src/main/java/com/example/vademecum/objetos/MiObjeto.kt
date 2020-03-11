package com.example.vademecum.objetos

data class MiObjeto(
    var totalFilas: Int,
    var pagina: Int,
    var tamanioPagina: Int,
    var resultados: List<MiFarmaco>,
    var excipientes: List<Excipiente>
)