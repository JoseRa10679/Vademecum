package com.example.vademecum.objetos

import android.content.Context
import android.net.ConnectivityManager
import com.example.vademecum.adaptadores.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author José Ramón Laperal
 * Objeto que contiene funciones comunes
 */
object Comun {

    val nVADEMECUM: String by lazy { "Vademecum versión: " }
    val nMIFIRMA: String by lazy { "\n@Josera. Marzo 2020" }

    private const val uRL: String = "https://cima.aemps.es/cima/rest/"

    /**
     *  Comprueba si Internet está accesible
     *  @param context Contexto en la que se aplica la función
     *  @return network booleano
     */
    @Suppress("DEPRECATION")
    fun hasNetworkAvailable(context: Context): Boolean {
        val service: String = Context.CONNECTIVITY_SERVICE
        val manager: ConnectivityManager? =
            context.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        return (network != null)
    }


    //<editor-folder desc = " Retrofit ">

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(uRL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var service: ApiService = retrofit.create(ApiService::class.java)



    //</editor-folder>


}