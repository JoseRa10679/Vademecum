package com.example.vademecum

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vademecum.Models.DetalleViewModel
import com.example.vademecum.objetos.Docs
import com.example.vademecum.objetos.EsteFarmaco
import com.example.vademecum.objetos.Excipiente
import com.example.vademecum.objetos.Presentacion
import kotlinx.android.synthetic.main.activity_detalle_farmaco.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DetalleFarmaco : AppCompatActivity() {

    private companion object{
        const val DOBLE_SALTO = "\n\n"
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
    private lateinit var fichaTecnicaReac: TextView

    private lateinit var miProspecto: TextView
    private lateinit var miPsum: TextView

    private lateinit var miViewModel: DetalleViewModel

    private lateinit var service: ApiService

    //</editor-folder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_farmaco)

        inicializa()

        miViewModel = ViewModelProvider(this).get(DetalleViewModel::class.java)

        Observer<String>{
            nombre.text = miViewModel.nombreV.value
            laboratorio.text = miViewModel.laboratorioV.value
            pactivos.text = miViewModel.pactivosV.value
            cpresc.text = miViewModel.cprescV.value
            conduccion.text = miViewModel.conduccionV.value
            miPsum.text = miViewModel.miPsumV.value
            presentaciones.text = miViewModel.presentacionesV.value
            excipiente.text = miViewModel.excipienteV.value
            fichaTecnica.text = miViewModel.fichaTecnicaV.value
            fichaTecnicaPos.text = miViewModel.fichaTecnicaPosV.value
            fichaTecnicaCont.text = miViewModel.fichaTecnicaContV.value
            fichaTecnicaReac.text = miViewModel.fichaTecnicaReacV.value
            miProspecto.text = miViewModel.miProspectoV.value
        }

        val nRegistro = intent.getStringExtra(getString(R.string.regsitro))

        //<editor-folder desc = " Retrofit ">

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(ApiService::class.java)

        //</editor-folder>

        getFarmacoById(service, nRegistro)

    }


    /**
     * Nos da un fármaco con el número de registro
     */
    private fun getFarmacoById(ser: ApiService, reg: String){

        ser.getDetalleFarmaco(reg).enqueue((object: Callback<EsteFarmaco>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<EsteFarmaco>, response: Response<EsteFarmaco>) {
                val miM: EsteFarmaco? = response.body()
                val miPr: List<Presentacion>? = miM?.presentaciones
                var miPresent =""
                miPr?.forEach {
                    miPresent = miPresent + it.nombre + DOBLE_SALTO
                }

                val miE: List<Excipiente>? = miM?.excipientes
                var miExcip = ""
                miE?.forEach {
                    miExcip = miExcip + it.nombre + " " + it.cantidad + " " + it.unidad + DOBLE_SALTO
                }


                val misDocs: List<Docs>? = miM?.docs
                if(misDocs!!.isNotEmpty()) {
                    val fTecnica: String? = misDocs[0].urlHtml
                    val prospecto: String? = misDocs[1].urlHtml

                    fichaTecnica.text = getString(R.string.abrir_ficha_tecnica)
                    fichaTecnica.setOnClickListener {
                        if(fTecnica!=null) {
                            val url: String = fTecnica
                            val int = Intent(Intent.ACTION_VIEW)
                            int.data = Uri.parse(url)
                            startActivity(int)
                        }else{
                            val toast: Toast = Toast.makeText(applicationContext, getString(R.string.ft_no_accesible), Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
                            toast.show()
                        }
                    }


                    fichaTecnicaPos.text = "Posología"
                    fichaTecnicaPos.setOnClickListener{
                        val posRuta =
                            "https://cima.aemps.es/cima/dochtml/ft/$reg/4.2/FichaTecnica.html"
                        val int = Intent(Intent.ACTION_VIEW)
                        int.data = Uri.parse(posRuta)
                        startActivity(int)
                    }

                    fichaTecnicaCont.text = "Contraindicaciones"
                    fichaTecnicaCont.setOnClickListener{
                        val posRutaC =
                            "https://cima.aemps.es/cima/dochtml/ft/$reg/4.3/FichaTecnica.html"
                        val int = Intent(Intent.ACTION_VIEW)
                        int.data = Uri.parse(posRutaC)
                        startActivity(int)
                    }

                    fichaTecnicaReac.text = "Reacciones Adversas"
                    fichaTecnicaReac.setOnClickListener{
                        val posRutaC =
                            "https://cima.aemps.es/cima/dochtml/ft/$reg/4.8/FichaTecnica.html"
                        val int = Intent(Intent.ACTION_VIEW)
                        int.data = Uri.parse(posRutaC)
                        startActivity(int)
                    }

                    miProspecto.text = getString(R.string.abrir_prospecto)
                    miProspecto.setOnClickListener {
                        if(prospecto!=null) {
                            val url: String? = prospecto
                            val int = Intent(Intent.ACTION_VIEW)
                            int.data = Uri.parse(url)
                            startActivity(int)
                        }else{
                            val toast: Toast = Toast.makeText(applicationContext, getString(R.string.p_no_accesible), Toast.LENGTH_SHORT)
                            toast.setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
                            toast.show()
                        }
                    }
                }

                nombre.text = miM.nombre
                laboratorio.text = miM.labtitular
                pactivos.text = DOBLE_SALTO + miM.pactivos + DOBLE_SALTO
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
        fichaTecnicaReac = findViewById(R.id.txtFichaReacciones)

        miProspecto = findViewById(R.id.txtProxpecto)

    }
}

