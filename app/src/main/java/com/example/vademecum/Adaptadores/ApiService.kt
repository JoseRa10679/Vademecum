package com.example.vademecum.Adaptadores

import com.example.vademecum.Dataclass.EsteFarmaco
import com.example.vademecum.Dataclass.MiObjeto
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

    //    Filtramos por nombre y usaremos solo los que estan comercializados.
    @GET(value = "medicamentos")
    fun getMedicamentos(
        @Query("nombre") nombre: String,
        @Query("comerc") comerc: Int
    ): Call<MiObjeto>

    //    Filtramos por principio activo y usaremos solo los que estan comercializados.
    @GET(value = "medicamentos")
    fun getPActivos(
        @Query("practiv1") practiv1: String,
        @Query("comerc") comerc: Int
    ): Call<MiObjeto>

}