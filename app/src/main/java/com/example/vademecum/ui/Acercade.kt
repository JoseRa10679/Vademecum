package com.example.vademecum.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.vademecum.R

/**
 * @author José Ramón Laperal Mur
 * Clase que muestra ayuda pora la aplicación
 */
class Acercade : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acercade)

        val mGuias: TextView = findViewById(R.id.txtDescripcion1)
        mGuias.movementMethod = LinkMovementMethod.getInstance()

        val miBoton = findViewById<Button>(R.id.btnAcerca)
        miBoton.setOnClickListener {
            finish()
        }

    }
}
