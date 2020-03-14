package com.example.vademecum.objetos

import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings.Global.getString
import com.example.vademecum.R
import retrofit2.Retrofit

object Comun {

    /**
     *  Comprueba si Internet está accesible
     */
    @Suppress("DEPRECATION")
    fun hasNetworkAvailable(context: Context): Boolean {
        val service: String = Context.CONNECTIVITY_SERVICE
        val manager: ConnectivityManager? = context.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        return (network != null)
    }

}