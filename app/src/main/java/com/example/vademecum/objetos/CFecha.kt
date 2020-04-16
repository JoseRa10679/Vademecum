package com.example.vademecum.objetos

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Verifica la fecha actual en cualquier programa con la fecha que límite que pongamos
 * @author José Ramón Laperal Mur
 */
object CFecha {
    //<editor-folder desc = " Constantes para el AlertDialog">

    val ATENCION: String by lazy{ "Atención" }
    val PROGRAMA_PASADO:String by lazy{ "El programa está pasado de fecha.\nContacta con el programador."}
    val ACEPTAR:String by lazy{ "Aceptar" }

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

}