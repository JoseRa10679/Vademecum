package com.example.vademecum.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.vademecum.R

/**
 * @author José Ramón Laperal Mur
 * Vista de incio de la aplicación
 */
class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //4second splash time

        Handler().postDelayed({
            //start main activity
            startActivity(Intent(this@Splash, MainActivity::class.java))
            //finish this activity
            finish()
        },2000)

    }
}
