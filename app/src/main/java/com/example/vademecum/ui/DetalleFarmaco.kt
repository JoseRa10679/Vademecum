package com.example.vademecum.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.vademecum.R
import com.example.vademecum.adaptadores.ApiService
import com.example.vademecum.dataclass.*
import com.example.vademecum.objetos.Comun
import kotlinx.android.synthetic.main.activity_detalle_farmaco.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private val nRegistro: String by lazy { intent.getStringExtra(getString(R.string.regsitro)) }

    //</editor-folder>

/*
    //<editor-folder desc = " Menu ">

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.version) {
            lateinit var version: String
            var packageInfo: PackageInfo? = null
            try {
                packageInfo = packageManager.getPackageInfo(packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            if (packageInfo != null) version = packageInfo.versionName
            val toast =
                Toast.makeText(
                    this,
                    "$nVADEMECUM$version\n$nMIFIRMA",
                    Toast.LENGTH_SHORT
                )
            toast.setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
            toast.show()
        } else {

            val intent = Intent(this, Acercade::class.java)
            startActivity(intent)
        }

        return super.onOptionsItemSelected(item)
    }

    //</editor-folder>
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_farmaco)

        val toolbar: Toolbar = findViewById(R.id.toolbarDetalle)
        setSupportActionBar(toolbar)
        compruebaConexionInternet(this)

        getFarmacoById(Comun.service, nRegistro)

    }

    /**
     * Función que muestra las características del fármaco seleccionado
     * @param ser instancia del ApiServide de Retrofit
     * @param reg número de registro del fármco en cuestión
     */
    private fun getFarmacoById(ser: ApiService, reg: String) {
        ser.getDetalleFarmaco(reg).enqueue((object : Callback<EsteFarmaco>{
            override fun onResponse(call: Call<EsteFarmaco>, response: Response<EsteFarmaco>) {
                if (response.isSuccessful) {
                    val miMS: EsteFarmaco? = response.body()
                    lifecycleScope.launch(Dispatchers.Main) {
                        launch {
                            llenaFormulario(miMS, reg)
                        }

                        launch {
                            delay(1000L)
                            progressBar0.visibility = View.GONE
                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.no_cargar_datos),
                        Toast.LENGTH_LONG
                    ).apply {
                        setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
                        show()
                    }
                }
            }

            override fun onFailure(call: Call<EsteFarmaco>, t: Throwable) {
                t.printStackTrace()
            }
        }))

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
        txtNombre.text = miM?.nombre
        txtLaboratorio.text = miM?.labtitular
        txtCpresc.text = miM?.cpresc

        val miPa: List<PActivos>? = miM?.pactivos
        var miPAct = String()
        miPa?.forEach {
            miPAct = miPAct + it.nombre + " " + it.cantidad + " " + it.unidad + DOBLE_SALTO
        }
        txtPactivos.text = DOBLE_SALTO + miPAct

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
        txtPresentaciones.text = DOBLE_SALTO + miPresent

        val miE: List<Excipiente>? = miM?.excipientes
        var miExcip = String()
        miE?.forEach {
            miExcip = miExcip + it.nombre + " " + it.cantidad + " " + it.unidad + DOBLE_SALTO
        }
        txtExcipientes.text = DOBLE_SALTO + miExcip

        val misDocs: List<Docs>? = miM?.docs

        if (!misDocs!!.isNullOrEmpty()) {
            val fTecnica :String? = misDocs[0].urlHtml
            val prospecto:String? = if (misDocs.size > 1) {misDocs[1].urlHtml} else {null}

            with(txtFichaT){
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

            with(txtFichaIndicaciones){
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

            with(txtFichaTPos){
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

            with(txtFichaContra){
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

            with(txtFichaInterac){
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

            with(txtFichaFertilidad){
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

            with(txtFichaReacciones){
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

            with (txtProxpecto){
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

            txtFichaT.setTextColor(getColor(R.color.colorGrisTexto))
            txtFichaIndicaciones.setTextColor(getColor(R.color.colorGrisTexto))
            txtFichaTPos.setTextColor(getColor(R.color.colorGrisTexto))
            txtFichaContra.setTextColor(getColor(R.color.colorGrisTexto))
            txtFichaInterac.setTextColor(getColor(R.color.colorGrisTexto))
            txtFichaFertilidad.setTextColor(getColor(R.color.colorGrisTexto))
            txtFichaReacciones.setTextColor(getColor(R.color.colorGrisTexto))
            txtProxpecto.setTextColor(getColor(R.color.colorGrisTexto))

            ftNoAccesible()
        }

        txtConduccion.text = if (miM.conduc) {
            getString(R.string.puede_afectar_cond)
        } else {
            getString(R.string.no_afecta_conduc)
        }

        if (miM.psum) {
            with(txtPsum){
                text = getString(R.string.hay_problemas_suminstro)
                setTextColor(getColor(R.color.colorPrimaryDark))
            }
        } else {
            txtPsum.text = getString(R.string.no_problemas_suministro)
        }

    }


    //<editor-folder desc = " Conexión a Internet ">


    /**
     * Comprueba la conexión a Internet y permite reintentar la conexión antes del diálogo de
     * salir de la aplicación
     */
    private fun compruebaConexionInternet(activity: DetalleFarmaco) {
        if (!Comun.hasNetworkAvailable(activity)) {
            val builder = AlertDialog.Builder(activity)
                .setTitle(R.string.sinConexion)
                .setMessage(R.string.reintentar)
                .setPositiveButton(R.string.strSi) { _, _ ->
                    miComprobacion(0, activity)
                }
                .setNegativeButton(R.string.strNo) { _, _ -> activity.finish() }
            builder.create().show()

        }
    }

    /**
     * Permite inintentar la conexión hasta 3 veces
     * @param n número de veces que se repite el bucle
     * @param activity Actividad a la que se aplica
     */
    private fun miComprobacion(n: Int, activity: DetalleFarmaco) {
        if (!Comun.hasNetworkAvailable(activity)) {
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
                        miComprobacion(n + 1, activity)}}
                .create().show()
        } else {
            getFarmacoById(Comun.service, nRegistro)
        }
    }

    //</editor-folder>

}

