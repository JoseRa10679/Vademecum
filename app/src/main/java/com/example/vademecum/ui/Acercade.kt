package com.example.vademecum.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.vademecum.R
import com.example.vademecum.objetos.Presentacion

/**
 * @author José Ramón Laperal Mur
 * Clase que muestra ayuda pora la aplicación
 */
class Acercade : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acercade)

        val miBoton = findViewById<Button>(R.id.btnAcerca)
        miBoton.setOnClickListener {
            finish()
        }

    }
}
