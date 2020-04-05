package com.example.vademecum.objetos

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object CFecha {

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