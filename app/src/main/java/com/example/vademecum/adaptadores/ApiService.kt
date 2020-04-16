package com.example.vademecum.adaptadores

import com.example.vademecum.dataclass.EsteFarmaco
import com.example.vademecum.dataclass.MiObjeto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author José Ramón Laperal Mur
 * Interfase que captura las consultas de Retrofit a la Api
 */
interface ApiService {


    //    Utilizamos @Path en lugar de @Query cuando accedemos a un solo registro
    @GET("medicamento/nregistro/{nregistro}")
    fun getDetalleFarmaco(@Path("nregistro") nregistro: String): Call<EsteFarmaco>

    /*
    *   El filtrado por genérico o no se hace mediante un filtrado de la lista de salida
    * */

    //<editor-folder desc = " Get Medicamentos">

    /*
     *      Filtramos por nombre y usaremos solo los que estan comercializados.
     *      https://cima.aemps.es/cima/rest/medicamentos?nombre=AMLODIPINO&pagina=1&pagesize=100
     *      La anterior dirección filtra los medicamentos con nombre Amlodipino y nos da la
     *      primera página con un máximo de 100 registros.
     *
     */


    @GET(value = "medicamentos?comerc=1&pagina=1&pagesize=100")
    fun getMedicamentos(
        @Query("nombre") nombre: String
    ): Call<MiObjeto>

    @GET(value = "medicamentos?comerc=1&npactiv=1&pagina=1&pagesize=100")
    fun getMedicamentosUnPA(
        @Query("nombre") nombre: String
    ): Call<MiObjeto>

    @GET(value = "medicamentos?comerc=1&npactiv=2&pagina=1&pagesize=100")
    fun getMedicamentosDosPA(
        @Query("nombre") nombre: String
    ): Call<MiObjeto>

    @GET(value = "medicamentos?comerc=1&npactiv=3&pagina=1&pagesize=100")
    fun getMedicamentosTresPA(
        @Query("nombre") nombre: String
    ): Call<MiObjeto>

    //</editor-folder>

    //<editor-folder desc = " Get Principio Activo ">

    //    Filtramos por principio activo y usaremos solo los que estan comercializados.
    @GET(value = "medicamentos?comerc=1&pagina=1&pagesize=100")
    fun getPActivos(
        @Query("practiv1") practiv1: String
    ): Call<MiObjeto>

    @GET(value = "medicamentos?comerc=1&npactiv=1&pagina=1&pagesize=100")
    fun getPactivosUnPA(
        @Query("practiv1") practiv1: String
    ): Call<MiObjeto>

    @GET(value = "medicamentos?comerc=1&npactiv=2&pagina=1&pagesize=100")
    fun getPactivosDosPA(
        @Query("practiv1") practiv1: String
    ): Call<MiObjeto>

    @GET(value = "medicamentos?comerc=1&npactiv=3&pagina=1&pagesize=100")
    fun getPactivosTresPA(
        @Query("practiv1") practiv1: String
    ): Call<MiObjeto>


    //</editor-folder>


}