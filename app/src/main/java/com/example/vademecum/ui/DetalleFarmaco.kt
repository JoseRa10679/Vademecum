package com.example.vademecum.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.vademecum.R
import com.example.vademecum.adaptadores.ApiService
import com.example.vademecum.databinding.ActivityDetalleFarmacoBinding
import com.example.vademecum.dataclass.*
import com.example.vademecum.objetos.Comun
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @author José Ramón Laperal Mur
 * Clase que muestra el detalle del fármaco consultado
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DetalleFarmaco : AppCompatActivity() {

    private companion object {
        const val SALTO = "\n"
        const val DOBLE_SALTO = "\n\n"
        const val RUTA = "https://cima.aemps.es/cima/dochtml/ft/"
        const val FICHA = "FichaTecnica.html"
    }

    //<editor-folder desc = " Variables ">

    private val binding by lazy {
        ActivityDetalleFarmacoBinding.inflate(layoutInflater)
    }

    private val nRegistro: String by lazy { intent.getStringExtra(getString(R.string.regsitro)) }

    //</editor-folder>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //val toolbar: Toolbar = findViewById(R.id.toolbarDetalle)
        setSupportActionBar(binding.toolbarDetalle)

        getFarmacoById(Comun.service, nRegistro)

    }

    /**
     * Función que muestra las características del fármaco seleccionado
     * @param ser instancia del ApiServide de Retrofit
     * @param reg número de registro del fármco en cuestión
     */
    private fun getFarmacoById(ser: ApiService, reg: String) {

        lifecycleScope.launch(Dispatchers.IO){
            val respuesta = ser.getDetalleFarmaco(reg)
            if(respuesta.isSuccessful){
                val miMS: EsteFarmaco? = respuesta.body()
                lifecycleScope.launch(Dispatchers.Main) {
                    llenaFormulario(miMS, reg)
                    delay(300L)
                    llamaProgresion()
                }
            }else{
                Comun.noSePuedenCargarDatos(applicationContext, getString(R.string.no_cargar_datos))
            }
        }

    }

    /**
     * Finaliza el ProgressBarr
     */
    private fun llamaProgresion() {
        binding.progressBar0.visibility = View.GONE
    }

    //<editor-folder desc = " Toast ">

    /**
     * Toast que informa de red no accesible.
     */
    private fun ftNoAccesible() {
        Toast.makeText(
            applicationContext,
            getString(R.string.ft_no_accesible),
            Toast.LENGTH_SHORT
        ).apply {
            setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
            show()
        }
    }

    /**
     * Toast que informa que el prospecto no está accesible
     */
    private fun proNoAccesible() {
        Toast.makeText(
            applicationContext,
            getString(R.string.proNoAccesible),
            Toast.LENGTH_SHORT
        ).apply {
            setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
            show()
        }
    }

    //</editor-folder>

    /**
     * Llena el formulario con los datos de la BD
     * @param miM objeto que recoge los datos de la BD
     */
    @SuppressLint("SetTextI18n")
    private fun llenaFormulario(miM: EsteFarmaco?, reg: String) {
        binding.txtNombre.text = miM?.nombre
        binding.txtLaboratorio.text = miM?.labtitular
        binding.txtCpresc.text = miM?.cpresc

        val miPa: List<PActivos>? = miM?.pactivos
        var miPAct = String()
        miPa?.forEach {
            miPAct = miPAct + it.nombre + " " + it.cantidad + " " + it.unidad + DOBLE_SALTO
        }
        binding.txtPactivos.text = DOBLE_SALTO + miPAct

        val miPr: List<Presentacion>? = miM?.presentaciones
        var miPresent =  String()
        miPr?.forEach {
            val miPsuministro =  getString(R.string.hay_problemas_de_suministro)
            miPresent = if(it.psum) {
                miPresent + it.nombre + SALTO +  miPsuministro + DOBLE_SALTO
            }else{
                miPresent + it.nombre + DOBLE_SALTO
            }

        }
        binding.txtPresentaciones.text = DOBLE_SALTO + miPresent

        val miE: List<Excipiente>? = miM?.excipientes
        var miExcip = String()
        miE?.forEach {
            miExcip = miExcip + it.nombre + " " + it.cantidad + " " + it.unidad + DOBLE_SALTO
        }
        binding.txtExcipientes.text = DOBLE_SALTO + miExcip

        val misDocs: List<Docs>? = miM?.docs

        if (!misDocs!!.isNullOrEmpty()) {
            val fTecnica :String? = misDocs[0].urlHtml
            val prospecto:String? = if (misDocs.size > 1) {misDocs[1].urlHtml} else {null}

            with(binding.txtFichaT){
                text = getString(R.string.abrir_ficha_tecnica)
                setOnClickListener {
                    fTecnica?.let{
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(it)
                            startActivity(this)
                        }
                    } ?: run{
                        ftNoAccesible()
                    }
                }
            }

            with(binding.txtFichaIndicaciones){
                text = getString(R.string.indicaciones)
                setOnClickListener{
                    fTecnica?.let{
                        val posRuta =
                            "$RUTA$reg/4.1/$FICHA"
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(posRuta)
                            startActivity(this)
                        }
                    } ?: run{
                        ftNoAccesible()
                    }
                }
            }

            with(binding.txtFichaTPos){
                text = getString(R.string.posolog_a)
                setOnClickListener {
                    fTecnica?.let{
                        val posRuta =
                            "$RUTA$reg/4.2/$FICHA"
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(posRuta)
                            startActivity(this)
                        }
                    } ?: run {
                        ftNoAccesible()
                    }
                }
            }

            with(binding.txtFichaContra){
                text = getString(R.string.contraindicaciones)
                setOnClickListener {
                    fTecnica?.let{
                        val posRutaC =
                            "$RUTA$reg/4.3/$FICHA"
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(posRutaC)
                            startActivity(this)
                        }
                    } ?: run{
                        ftNoAccesible()
                    }
                }
            }

            with(binding.txtFichaInterac){
                text = getString(R.string.interacciones)
                setOnClickListener {
                    fTecnica?.let{
                        val posRutaC =
                            "$RUTA$reg/4.5/$FICHA"
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(posRutaC)
                            startActivity(this)
                        }
                    } ?: run{
                        ftNoAccesible()
                    }
                }
            }

            with(binding.txtFichaFertilidad){
                text = getString(R.string.fertilidad)
                setOnClickListener {
                    fTecnica?.let{
                        val posRutaC =
                            "$RUTA$reg/4.6/$FICHA"
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(posRutaC)
                            startActivity(this)
                        }
                    } ?: run{
                        ftNoAccesible()
                    }

                }
            }

            with(binding.txtFichaReacciones){
                text = getString(R.string.reacciones_adversas)
                setOnClickListener {
                    fTecnica?.let{
                        val posRutaC =
                            "$RUTA$reg/4.8/$FICHA"
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(posRutaC)
                            startActivity(this)
                        }
                    } ?: run{
                        ftNoAccesible()
                    }
                }
            }

            with (binding.txtProxpecto){
                text = getString(R.string.abrir_prospecto)
                setOnClickListener{
                    prospecto?.let{
                        Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(it)
                            startActivity(this)
                        }
                    } ?: run{
                        proNoAccesible()
                    }

                }
            }

        }else{

            binding.txtFichaT.setTextColor(getColor(R.color.colorGrisTexto))
            binding.txtFichaIndicaciones.setTextColor(getColor(R.color.colorGrisTexto))
            binding.txtFichaTPos.setTextColor(getColor(R.color.colorGrisTexto))
            binding.txtFichaContra.setTextColor(getColor(R.color.colorGrisTexto))
            binding.txtFichaInterac.setTextColor(getColor(R.color.colorGrisTexto))
            binding.txtFichaFertilidad.setTextColor(getColor(R.color.colorGrisTexto))
            binding.txtFichaReacciones.setTextColor(getColor(R.color.colorGrisTexto))
            binding.txtProxpecto.setTextColor(getColor(R.color.colorGrisTexto))

            ftNoAccesible()
        }

        binding.txtConduccion.text = if (miM.conduc) {
            getString(R.string.puede_afectar_cond)
        } else {
            getString(R.string.no_afecta_conduc)
        }

        if (miM.psum) {
            with(binding.txtPsum){
                text = getString(R.string.hay_problemas_suminstro)
                setTextColor(getColor(R.color.colorPrimaryDark))
            }
        } else {
            binding.txtPsum.text = getString(R.string.no_problemas_suministro)
        }

    }

}

