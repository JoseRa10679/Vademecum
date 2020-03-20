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
import com.example.vademecum.Adaptadores.ApiService
import com.example.vademecum.R
import com.example.vademecum.objetos.*
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

    private companion object{
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
    private lateinit var fichaTecnicaPos: TextView
    private lateinit var fichaTecnicaCont: TextView
    private lateinit var fichaTecnicaInter: TextView
    private lateinit var fichaTecnicaReac: TextView
    private lateinit var fichaFertilidad: TextView

    private lateinit var miProspecto: TextView
    private lateinit var miPsum: TextView

    private val nRegistro: String by lazy {intent.getStringExtra(getString(R.string.regsitro))}

    //</editor-folder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_farmaco)

        inicializa()

        if(!Comun.hasNetworkAvailable(this)){
            val builder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.volver))
                .setMessage(getString(R.string.no_conexion))
                .setPositiveButton(getString(R.string.aceptar)){ _, _ -> this.finish()}
            builder.create().show()
        }

        getFarmacoById(Comun.service, nRegistro)

    }

    /**
     * Función que muestra las características del fármaco seleccionado
     * @param ser instancia del ApiServide de Retrofit
     * @param reg número de registro del fármco en cuestión
     */
    private fun getFarmacoById(ser: ApiService, reg: String){
        ser.getDetalleFarmaco(reg).enqueue((object: Callback<EsteFarmaco>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<EsteFarmaco>, response: Response<EsteFarmaco>) {
                val miM: EsteFarmaco? = response.body()
                val miPr: List<Presentacion>? = miM?.presentaciones
                val miPa: List<PActivos>? = miM?.pactivos
                val miE: List<Excipiente>? = miM?.excipientes
                val misDocs: List<Docs>? = miM?.docs

                var miPAct = ""
                miPa?.forEach {
                    miPAct = miPAct + it.nombre + " " + it.cantidad + " " + it.unidad + DOBLE_SALTO
                }

                var miPresent =""
                miPr?.forEach {
                    miPresent = miPresent + it.nombre + DOBLE_SALTO
                }


                var miExcip = ""
                miE?.forEach {
                    miExcip = miExcip + it.nombre + " " + it.cantidad + " " + it.unidad + DOBLE_SALTO
                }


                if(misDocs!!.isNotEmpty()) {
                    val fTecnica: String? = misDocs[0].urlHtml
                    val prospecto = if(misDocs.size>1){misDocs[1].urlHtml}else{null}

                    fichaTecnica.text = getString(R.string.abrir_ficha_tecnica)
                    fichaTecnica.setOnClickListener {
                        if(fTecnica!=null) {
                            val url: String = fTecnica
                            val int = Intent(Intent.ACTION_VIEW)
                            int.data = Uri.parse(url)
                            startActivity(int)
                        }else{
                            ftNoAccesible()
                        }
                    }

                    fichaTecnicaPos.text = getString(R.string.posolog_a)
                    fichaTecnicaPos.setOnClickListener{
                        if(fTecnica!=null){
                            val posRuta =
                                "$RUTA$reg/4.2/$FICHA"
                            val int = Intent(Intent.ACTION_VIEW)
                            int.data = Uri.parse(posRuta)
                            startActivity(int)
                        }else{
                            ftNoAccesible()
                        }

                    }

                    fichaTecnicaCont.text = getString(R.string.contraindicaciones)
                    fichaTecnicaCont.setOnClickListener{
                        if(fTecnica!=null) {
                            val posRutaC =
                                "$RUTA$reg/4.3/$FICHA"
                            val int = Intent(Intent.ACTION_VIEW)
                            int.data = Uri.parse(posRutaC)
                            startActivity(int)
                        }else{
                            ftNoAccesible()
                        }
                    }

                    fichaTecnicaInter.text = getString(R.string.interacciones)
                    fichaTecnicaInter.setOnClickListener{
                        if(fTecnica!=null) {
                            val posRutaC =
                                "$RUTA$reg/4.5/$FICHA"
                            val int = Intent(Intent.ACTION_VIEW)
                            int.data = Uri.parse(posRutaC)
                            startActivity(int)
                        }else{
                            ftNoAccesible()
                        }
                    }

                    fichaFertilidad.text = getString(R.string.fertilidad)
                    fichaFertilidad.setOnClickListener{
                        if(fTecnica!=null) {
                            val posRutaC =
                                "$RUTA$reg/4.6/$FICHA"
                            val int = Intent(Intent.ACTION_VIEW)
                            int.data = Uri.parse(posRutaC)
                            startActivity(int)
                        }else{
                            ftNoAccesible()
                        }
                    }

                    fichaTecnicaReac.text = getString(R.string.reacciones_adversas)
                    fichaTecnicaReac.setOnClickListener{
                        if(fTecnica!=null) {
                            val posRutaC =
                                "$RUTA$reg/4.8/$FICHA"
                            val int = Intent(Intent.ACTION_VIEW)
                            int.data = Uri.parse(posRutaC)
                            startActivity(int)
                        }else{
                            ftNoAccesible()
                        }
                    }

                    miProspecto.text = getString(R.string.abrir_prospecto)
                    miProspecto.setOnClickListener {
                        if(prospecto!=null){
                            val url: String? = prospecto
                            val int = Intent(Intent.ACTION_VIEW)
                            int.data = Uri.parse(url)
                            startActivity(int)
                        }else{
                            proNoAccesible()
                        }
                    }
                }

                nombre.text = miM.nombre
                laboratorio.text = miM.labtitular
                pactivos.text = DOBLE_SALTO + miPAct
                cpresc.text = miM.cpresc
                if(miM.conduc){
                    txtConduccion.text=getString(R.string.puede_afectar_cond)
                }else{
                    txtConduccion.text=getString(R.string.no_afecta_conduc)
                }

                if(miM.psum){
                    miPsum.text = getString(R.string.hay_problemas_suminstro)
                    miPsum.setTextColor(getColor(R.color.colorPrimaryDark))
                }else{
                    miPsum.text = getString(R.string.no_problemas_suministro)
                }
                presentaciones.text = DOBLE_SALTO + miPresent
                excipiente.text = DOBLE_SALTO + miExcip
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
    fun ftNoAccesible(){
        val toast: Toast = Toast.makeText(applicationContext, getString(R.string.ft_no_accesible), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

    /**
     * Toast que informa que el prospecto no está accesible
     */
    fun proNoAccesible(){
        val toast: Toast = Toast.makeText(applicationContext, getString(R.string.proNoAccesible), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

    //</editor-folder>

    /**
     * Inicializa las variables de la interfase
     */
    private fun inicializa(){
        nombre = findViewById(R.id.txtNombre)
        laboratorio = findViewById(R.id.txtLaboratorio)
        pactivos = findViewById(R.id.txtPactivos)
        cpresc = findViewById(R.id.txtCpresc)
        conduccion = findViewById(R.id.txtConduccion)
        miPsum = findViewById(R.id.txtPsum)
        presentaciones = findViewById(R.id.txtPresentaciones)
        excipiente = findViewById(R.id.txtExcipientes)
        fichaTecnica = findViewById(R.id.txtFichaT)
        fichaTecnicaPos = findViewById(R.id.txtFichaTPos)
        fichaTecnicaCont = findViewById(R.id.txtFichaContra)
        fichaTecnicaInter = findViewById(R.id.txtFichaInterac)

        fichaTecnicaReac = findViewById(R.id.txtFichaReacciones)
        fichaFertilidad = findViewById(R.id.txtFichaFertilidad)

        miProspecto = findViewById(R.id.txtProxpecto)

    }
}

