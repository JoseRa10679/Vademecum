package com.example.vademecum

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

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
