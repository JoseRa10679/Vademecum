package com.example.vademecum.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.example.vademecum.databinding.ActivityAcercadeBinding

/**
 * @author José Ramón Laperal Mur
 * Clase que muestra ayuda pora la aplicación
 */
class Acercade : AppCompatActivity() {

    private val binding by lazy {
        ActivityAcercadeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.txtDescripcion1.movementMethod = LinkMovementMethod.getInstance()

        binding.btnAcerca.setOnClickListener {
            finish()
        }

    }
}
