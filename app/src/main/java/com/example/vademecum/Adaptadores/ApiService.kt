package com.example.vademecum.Adaptadores

import com.example.vademecum.objetos.EsteFarmaco
import com.example.vademecum.objetos.MiObjeto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author José Ramón Laperal Mur
 * Interfase que captura las consultas de Retrofit a la Api
 */
interface ApiService {

    @GET("medicamento")
    fun getDetalleFarmaco(@Query("nregistro")nregistro:String):Call<EsteFarmaco>

    @GET(value = "medicamentos")
    fun getMedicamentos(@Query("nombre")nombre: String,
                        @Query("comerc")comerc: Int): Call<MiObjeto>

    @GET(value = "medicamentos")
    fun getPActivos(@Query("practiv1")practiv1: String,
        @Query("comerc")comerc: Int): Call<MiObjeto>

}