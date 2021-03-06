package com.example.vademecum.adaptadores

import com.example.vademecum.dataclass.EsteFarmaco
import com.example.vademecum.dataclass.MiObjeto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author José Ramón Laperal Mur
 * Interfase que captura las consultas de Retrofit a la Api
 */
interface ApiService {
    companion object{

        const val nom:String = "nombre"
        const val prac: String = "practiv1"
        const val nreg: String = "nregistro"

        const val detalleF: String = "medicamento/nregistro/{nregistro}"

        const val medicamentos: String = "medicamentos?comerc=1&pagina=1&pagesize=100"
        const val medicamentosUnPA: String = "medicamentos?comerc=1&npactiv=1&pagina=1&pagesize=100"
        const val medicamentosDosPA: String = "medicamentos?comerc=1&npactiv=2&pagina=1&pagesize=100"
        const val medicamentosTresPA: String = "medicamentos?comerc=1&npactiv=3&pagina=1&pagesize=100"

        const val pActivos: String = "medicamentos?comerc=1&pagina=1&pagesize=100"
        const val pActivosUnPA: String = "medicamentos?comerc=1&npactiv=1&pagina=1&pagesize=100"
        const val pActivosDosPA: String = "medicamentos?comerc=1&npactiv=2&pagina=1&pagesize=100"
        const val pActivosTresPA: String = "medicamentos?comerc=1&npactiv=3&pagina=1&pagesize=100"
    }


    //    Utilizamos @Path en lugar de @Query cuando accedemos a un solo registro
    @GET(detalleF)
    suspend fun getDetalleFarmaco(@Path(nreg) nregistro: String): Response<EsteFarmaco>


    //<editor-folder desc = " Get Medicamentos">

    /*
     *      Filtramos por nombre y usaremos solo los que estan comercializados.
     *      https://cima.aemps.es/cima/rest/medicamentos?nombre=AMLODIPINO&pagina=1&pagesize=100
     *      La anterior dirección filtra los medicamentos con nombre Amlodipino y nos da la
     *      primera página con un máximo de 100 registros.
     *
     */

    @GET(value = medicamentos)
    suspend fun getMedicamentos(@Query(nom) nombre: String): Response<MiObjeto>

    @GET(value = medicamentosUnPA)
    suspend fun getMedicamentosUnPA(@Query(nom) nombre: String): Response<MiObjeto>

    @GET(value = medicamentosDosPA)
    suspend fun getMedicamentosDosPA(@Query(nom) nombre: String): Response<MiObjeto>

    @GET(value = medicamentosTresPA)
    suspend fun getMedicamentosTresPA(@Query(nom) nombre: String): Response<MiObjeto>

    //</editor-folder>

    //<editor-folder desc = " Get Principio Activo ">


    /*
    *
    *     Filtramos por principio activo y usaremos solo los que estan comercializados.
    *     Filtra la primera página con un tamaño de 100 registros
    *
    * */
    @GET(value = pActivos)
    suspend fun getPActivos(@Query(prac) practiv1: String): Response<MiObjeto>

    @GET(value = pActivosUnPA)
    suspend fun getPactivosUnPA(@Query(prac) practiv1: String): Response<MiObjeto>

    @GET(value = pActivosDosPA)
    suspend fun getPactivosDosPA(@Query(prac) practiv1: String): Response<MiObjeto>

    @GET(value = pActivosTresPA)
    suspend fun getPactivosTresPA(@Query(prac) practiv1: String): Response<MiObjeto>

    //</editor-folder>


}