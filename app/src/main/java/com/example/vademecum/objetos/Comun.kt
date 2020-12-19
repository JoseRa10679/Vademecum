package com.example.vademecum.objetos

import android.content.Context
import android.net.ConnectivityManager
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.vademecum.R
import com.example.vademecum.adaptadores.ApiService
import com.example.vademecum.ui.MainActivity
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author José Ramón Laperal
 * Objeto que contiene funciones comunes
 */
object Comun {

    val nVADEMECUM: String by lazy { "Vademecum versión: " }
    val nMIFIRMA: String by lazy { "\n@Josera. Marzo 2020" }

    private const val uRL: String = "https://cima.aemps.es/cima/rest/"


    //<editor-folder desc = "Conexión a Internet"

    /**
     * Comprueba la conexión a Internet y permite reintentar la conexión antes del diálogo de
     * salir de la aplicación
     * @param activity Actividad a la que se aplica
     */
    fun compruebaConexionInternet(activity: MainActivity) {
        if (!hasNetworkAvailable(activity)) {

            AlertDialog.Builder(activity)
                .setTitle(R.string.sinConexion)
                .setMessage(R.string.reintentar)
                .setPositiveButton(R.string.strSi) { _, _ ->
                    miComprobacion(0,activity)
                }
                .setNegativeButton(R.string.strNo) { _, _ -> activity.finish() }
                .create().show()
        }
    }

    /**
     * Permite inintentar la conexión hasta 3 veces
     * @param n número de veces que se repite el bucle
     * @param activity Actividad a la que se aplica
     */
    private fun miComprobacion(n: Int, activity: MainActivity) {
        if (!hasNetworkAvailable(activity)) {
            val builder = AlertDialog.Builder(activity)
            if (n > 2) {
                builder
                    .setTitle(R.string.salir)
                    .setMessage(R.string.salir_aplicacion)
                    .setPositiveButton(R.string.salir) { _, _ -> activity.finish() }

            } else {
                builder
                    .setTitle(R.string.salir)
                    .setMessage(R.string.salir_aplicacion)
                    .setPositiveButton(R.string.salir) { _, _ -> activity.finish() }
                    .setNegativeButton(R.string.strComprobar) { _, _ ->
                        miComprobacion(n + 1, activity)}
            }.create().show()
        }
    }


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

    //</editor-folder>

    //<editor-folder desc = " Retrofit ">

    private val miClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15,TimeUnit.SECONDS)

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(uRL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(miClient.build())
        .build()

    val service: ApiService = retrofit.create(ApiService::class.java)


    fun noSePuedenCargarDatos(context: Context, mensaje: String){
        Toast.makeText(context, mensaje,
            Toast.LENGTH_LONG
        ).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
            show()
        }
    }
    

    //</editor-folder>

    /*

    *
     * Permite el cambio de características de texto dentro de un TextView
     *
     * https://gist.github.com/RadoYankov/29833fc1f5ecd577b0581d6de93ff60f
     *
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