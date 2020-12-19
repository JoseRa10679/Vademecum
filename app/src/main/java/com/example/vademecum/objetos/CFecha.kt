package com.example.vademecum.objetos

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.vademecum.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

/**
 * Verifica la fecha actual en cualquier programa con la fecha que límite que pongamos
 * @author José Ramón Laperal Mur
 */
object CFecha {
    //<editor-folder desc = " Constantes para el AlertDialog">

    private val ATENCION: String by lazy{ "Atención" }
    private val PROGRAMA_PASADO:String by lazy{ "El programa está pasado de fecha.\nContacta con el programador."}
    private val ACEPTAR:String by lazy{ "Aceptar" }

    //</editor-folder>

    private const val FORMATO_FECHA = "dd/MM/yyyy"

    fun comprueba(fecha: String): Boolean {
        val df = SimpleDateFormat(FORMATO_FECHA, Locale.getDefault())
        val today = Calendar.getInstance().time
        var mifecha: Date? = null

        try {
            mifecha = df.parse(fecha)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return today.after(mifecha)
    }

    fun alertaMSG(contexto: Context){

        AlertDialog.Builder(contexto)
            .setTitle(ATENCION)
            .setMessage(PROGRAMA_PASADO)
            .setIcon(R.mipmap.ic_launcher_foreground)
            .setPositiveButton(ACEPTAR) { _,_ -> exitProcess(-1) } // exitProcess(-1) sustituya a finish() en el hilo principal
            .create().show()
        
    }

}