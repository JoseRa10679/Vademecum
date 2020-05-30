package com.example.vademecum.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.vademecum.R
import com.example.vademecum.adaptadores.ApiService
import com.example.vademecum.dataclass.*
import com.example.vademecum.objetos.Comun
import kotlinx.android.synthetic.main.activity_detalle_farmaco.*
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

    private lateinit var nombre: TextView
    private lateinit var laboratorio: TextView
    private lateinit var pactivos: TextView
    private lateinit var cpresc: TextView
    private lateinit var conduccion: TextView
    private lateinit var presentaciones: TextView
    private lateinit var excipiente: TextView

    private lateinit var fichaTecnica: TextView
    private lateinit var fichaTecnicaIndic: TextView
    private lateinit var fichaTecnicaPos: TextView
    private lateinit var fichaTecnicaCont: TextView
    private lateinit var fichaTecnicaInter: TextView
    private lateinit var fichaTecnicaReac: TextView
    private lateinit var fichaFertilidad: TextView

    private lateinit var miProspecto: TextView
    private lateinit var miPsum: TextView


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
        inicializa()

        getFarmacoById(Comun.service, nRegistro)

    }

    /**
     * Función que muestra las características del fármaco seleccionado
     * @param ser instancia del ApiServide de Retrofit
     * @param reg número de registro del fármco en cuestión
     */
    private fun getFarmacoById(ser: ApiService, reg: String) {
        ser.getDetalleFarmaco(reg).enqueue((object : Callback<EsteFarmaco> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<EsteFarmaco>, response: Response<EsteFarmaco>) {
                if (response.isSuccessful) {
                    val miMS: EsteFarmaco? = response.body()
                    llenaFormulario(miMS, reg)
                } else {
                    val toast: Toast = Toast.makeText(
                        applicationContext,
                        getString(R.string.no_cargar_datos),
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
                    toast.show()
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
        val toast: Toast = Toast.makeText(
            applicationContext,
            getString(R.string.ft_no_accesible),
            Toast.LENGTH_SHORT
        )
        toast.setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

    /**
     * Toast que informa que el prospecto no está accesible
     */
    private fun proNoAccesible() {
        val toast: Toast = Toast.makeText(
            applicationContext,
            getString(R.string.proNoAccesible),
            Toast.LENGTH_SHORT
        )
        toast.setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

    //</editor-folder>

    /**
     * Llena el formulario con los datos de la BD
     * @param miM objeto que recoge los datos de la BD
     */
    @SuppressLint("SetTextI18n")
    private fun llenaFormulario(miM: EsteFarmaco?, reg: String) {
        nombre.text = miM?.nombre
        laboratorio.text = miM?.labtitular
        cpresc.text = miM?.cpresc

        val miPa: List<PActivos>? = miM?.pactivos
        var miPAct = ""
        miPa?.forEach {
            miPAct = miPAct + it.nombre + " " + it.cantidad + " " + it.unidad + DOBLE_SALTO
        }
        pactivos.text = DOBLE_SALTO + miPAct

        val miPr: List<Presentacion>? = miM?.presentaciones
        var miPresent =  ""
        miPr?.forEach {
            val miPsuministro =  getString(R.string.hay_problemas_de_suministro)
            miPresent = if(it.psum) {
                miPresent + it.nombre + SALTO +  miPsuministro + DOBLE_SALTO
            }else{
                miPresent + it.nombre + DOBLE_SALTO
            }

        }
        presentaciones.text = DOBLE_SALTO + miPresent

        val miE: List<Excipiente>? = miM?.excipientes
        var miExcip = ""
        miE?.forEach {
            miExcip = miExcip + it.nombre + " " + it.cantidad + " " + it.unidad + DOBLE_SALTO
        }
        excipiente.text = DOBLE_SALTO + miExcip

        val misDocs: List<Docs>? = miM?.docs

        if (!misDocs!!.isNullOrEmpty()) {
            val fTecnica: String? = misDocs[0].urlHtml
            val prospecto = if (misDocs.size > 1) {misDocs[1].urlHtml} else {null}

            with(fichaTecnica){
                text = getString(R.string.abrir_ficha_tecnica)
                setOnClickListener {
                    if (fTecnica != null) {
                        val url: String = fTecnica
                        val int = Intent(Intent.ACTION_VIEW)
                        int.data = Uri.parse(url)
                        startActivity(int)
                    } else {
                        ftNoAccesible()
                    }
                }
            }

            with(fichaTecnicaIndic){
                text = getString(R.string.indicaciones)
                setOnClickListener{
                    if (fTecnica != null) {
                        val posRuta =
                            "$RUTA$reg/4.1/$FICHA"
                        val int = Intent(Intent.ACTION_VIEW)
                        int.data = Uri.parse(posRuta)
                        startActivity(int)
                    } else {
                        ftNoAccesible()
                    }
                }
            }

            with(fichaTecnicaPos){
                text = getString(R.string.posolog_a)
                setOnClickListener {
                    if (fTecnica != null) {
                        val posRuta =
                            "$RUTA$reg/4.2/$FICHA"
                        val int = Intent(Intent.ACTION_VIEW)
                        int.data = Uri.parse(posRuta)
                        startActivity(int)
                    } else {
                        ftNoAccesible()
                    }

                }
            }

            with(fichaTecnicaCont){
                text = getString(R.string.contraindicaciones)
                setOnClickListener {
                    if (fTecnica != null) {
                        val posRutaC =
                            "$RUTA$reg/4.3/$FICHA"
                        val int = Intent(Intent.ACTION_VIEW)
                        int.data = Uri.parse(posRutaC)
                        startActivity(int)
                    } else {
                        ftNoAccesible()
                    }
                }
            }

            with(fichaTecnicaInter){
                text = getString(R.string.interacciones)
                setOnClickListener {
                    if (fTecnica != null) {
                        val posRutaC =
                            "$RUTA$reg/4.5/$FICHA"
                        val int = Intent(Intent.ACTION_VIEW)
                        int.data = Uri.parse(posRutaC)
                        startActivity(int)
                    } else {
                        ftNoAccesible()
                    }
                }
            }

            with(fichaFertilidad){
                text = getString(R.string.fertilidad)
                setOnClickListener {
                    if (fTecnica != null) {
                        val posRutaC =
                            "$RUTA$reg/4.6/$FICHA"
                        val int = Intent(Intent.ACTION_VIEW)
                        int.data = Uri.parse(posRutaC)
                        startActivity(int)
                    } else {
                        ftNoAccesible()
                    }
                }
            }

            with(fichaTecnicaReac){
                text = getString(R.string.reacciones_adversas)
                setOnClickListener {
                    if (fTecnica != null) {
                        val posRutaC =
                            "$RUTA$reg/4.8/$FICHA"
                        val int = Intent(Intent.ACTION_VIEW)
                        int.data = Uri.parse(posRutaC)
                        startActivity(int)
                    } else {
                        ftNoAccesible()
                    }
                }
            }

            /*
            * Utilizo apply como prueba en lugar de with pero se podía usar cualquiera de los dos.
            * Probablemente sería más lógico usar with pero da igual
            * */
            miProspecto.apply {
                text = getString(R.string.abrir_prospecto)
            }.setOnClickListener{
                if (prospecto != null) {
                    val url: String? = prospecto
                    val int = Intent(Intent.ACTION_VIEW)
                    int.data = Uri.parse(url)
                    startActivity(int)
                } else {
                    proNoAccesible()
                }
            }

        }else{

            fichaTecnica.setTextColor(getColor(R.color.colorGrisTexto))
            fichaTecnicaIndic.setTextColor(getColor(R.color.colorGrisTexto))
            fichaTecnicaPos.setTextColor(getColor(R.color.colorGrisTexto))
            fichaTecnicaCont.setTextColor(getColor(R.color.colorGrisTexto))
            fichaTecnicaInter.setTextColor(getColor(R.color.colorGrisTexto))
            fichaFertilidad.setTextColor(getColor(R.color.colorGrisTexto))
            fichaTecnicaReac.setTextColor(getColor(R.color.colorGrisTexto))
            miProspecto.setTextColor(getColor(R.color.colorGrisTexto))

            ftNoAccesible()
        }

        txtConduccion.text = if (miM.conduc) {
            getString(R.string.puede_afectar_cond)
        } else {
            getString(R.string.no_afecta_conduc)
        }

        if (miM.psum) {
            with(miPsum){
                text = getString(R.string.hay_problemas_suminstro)
                setTextColor(getColor(R.color.colorPrimaryDark))
            }
        } else {
            miPsum.text = getString(R.string.no_problemas_suministro)
        }
    }

    /**
     * Inicializa las variables de la interfase
     */
    private fun inicializa() {
        nombre = findViewById(R.id.txtNombre)
        laboratorio = findViewById(R.id.txtLaboratorio)
        pactivos = findViewById(R.id.txtPactivos)
        cpresc = findViewById(R.id.txtCpresc)
        conduccion = findViewById(R.id.txtConduccion)
        miPsum = findViewById(R.id.txtPsum)
        presentaciones = findViewById(R.id.txtPresentaciones)
        excipiente = findViewById(R.id.txtExcipientes)
        fichaTecnica = findViewById(R.id.txtFichaT)
        fichaTecnicaIndic = findViewById(R.id.txtFichaIndicaciones)
        fichaTecnicaPos = findViewById(R.id.txtFichaTPos)
        fichaTecnicaCont = findViewById(R.id.txtFichaContra)
        fichaTecnicaInter = findViewById(R.id.txtFichaInterac)

        fichaTecnicaReac = findViewById(R.id.txtFichaReacciones)
        fichaFertilidad = findViewById(R.id.txtFichaFertilidad)

        miProspecto = findViewById(R.id.txtProxpecto)

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
                        miComprobacion(
                            n + 1,
                            activity
                        )
                    }
            }
                .create().show()
        } else {
            getFarmacoById(Comun.service, nRegistro)
        }
    }

    //</editor-folder>

}

