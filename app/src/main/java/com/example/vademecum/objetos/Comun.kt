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

    val service: ApiService = retrofit.create(ApiService::class.java)



    //</editor-folder>

    /*

    /**
     * Permite el cambio de características de texto dentro de un TextView
     *
     * https://gist.github.com/RadoYankov/29833fc1f5ecd577b0581d6de93ff60f
     *
     */
    fun spannable(func: () -> SpannableString) = func()
    private fun span(s: CharSequence, o: Any) = (if (s is String) SpannableString(s) else s as? SpannableString
        ?: SpannableString("")).apply { setSpan(o, 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE) }

    operator fun SpannableString.plus(s: SpannableString) = SpannableString(TextUtils.concat(this, s))
    operator fun SpannableString.plus(s: String) = SpannableString(TextUtils.concat(this, s))

    fun bold(s: CharSequence) = span(s, StyleSpan(Typeface.BOLD))
    fun italic(s: CharSequence) = span(s, StyleSpan(Typeface.ITALIC))
    fun underline(s: CharSequence) = span(s, UnderlineSpan())
    fun strike(s: CharSequence) = span(s, StrikethroughSpan())
    fun sup(s: CharSequence) = span(s, SuperscriptSpan())
    fun sub(s: CharSequence) = span(s, SubscriptSpan())
    fun size(size: Float, s: CharSequence) = span(s, RelativeSizeSpan(size))
    fun color(color: Int, s: CharSequence) = span(s, ForegroundColorSpan(color))
    fun background(color: Int, s: CharSequence) = span(s, BackgroundColorSpan(color))
    fun url(url: String, s: CharSequence) = span(s, URLSpan(url))
*/

}