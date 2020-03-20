package com.example.vademecum.ui

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vademecum.Adaptadores.Adaptador
import com.example.vademecum.Adaptadores.ApiService
import com.example.vademecum.Adaptadores.OnFarItemClickListner
import com.example.vademecum.Models.MainViewModel
import com.example.vademecum.R
import com.example.vademecum.objetos.Comun
import com.example.vademecum.objetos.MiFarmaco
import com.example.vademecum.objetos.MiObjeto
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author Jose Ramón Laperal Mur
 * Clase prinicpal que filtra los fármacos por nombre y llena el RecyclerView
 */
class MainActivity: AppCompatActivity(),
    OnFarItemClickListner {

    //<editor-folder desc = " Variables ">
   private lateinit var mButton: Button
   private lateinit var mEditText: EditText
   private lateinit var chkActivo: CheckBox

    private lateinit var miViewModel: MainViewModel

   private lateinit var recyclerFarmacos: RecyclerView


    //</editor-folder>

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
                    this@MainActivity,
                    "Vademecum versión: $version\n@Josera. Marzo 2020",
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

    //Comprueba si hay Internet
        if(!Comun.hasNetworkAvailable(this)){

            val builder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.salir))
                .setMessage(getString(R.string.salir_aplicacion))
                .setPositiveButton(getString(R.string.aceptar)){ _, _ -> this.finish() }
            builder.create().show()

        }

        miViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        inicializa()


        // Se activa el botón cuando el número de letras es mayor a 2
        mEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mButton.isEnabled = mEditText.text.length>2
            }

        })

        //Borra el contenido del EditTexBox y limpia el RecyclerView
        mEditText.setOnLongClickListener{
            mEditText.text.clear()
            getComun(null)
            UIUtil.showKeyboard(this, mEditText)
            true
        }

//        Busca los fármacos en la API dependiendo de si está marcado el Principio activo o no.
        mButton.setOnClickListener{
            if(Comun.hasNetworkAvailable(this)) {
                val miS: String = mEditText.text.toString()
                if(chkActivo.isChecked ){
                    getPactivos(Comun.service, miS)
                }else{
                    getMedicamentos(Comun.service, miS)
                }
            }else{
                val builder = AlertDialog.Builder(this)
                    .setTitle(getString(R.string.salir))
                    .setMessage(getString(R.string.salir_aplicacion))
                    .setPositiveButton(getString(R.string.aceptar)){ _, _ -> this.finish() }
                builder.create().show()
            }
        }

    }

    override fun onResume() {
        super.onResume()

        if(mEditText.text.toString() !=""){
            getComun(miViewModel.miRecycle?.value)
        }else{
            getComun(null)
        }
        mEditText.requestFocus()
        UIUtil.showKeyboard(this, mEditText)
    }

    //<editor-folder desc = " Consultas ">

    /**
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     * Función que filtra los fármcos por el Principio Activo
     */
    private fun getPactivos(ser: ApiService, miS: String){
        ser.getPActivos(miS, 1).enqueue(object: Callback<MiObjeto>{
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                miViewModel.miRecycle?.value  = response.body()
                val miValor: MiObjeto? = miViewModel.miRecycle?.value
                val miLista: List<MiFarmaco>? = miViewModel.miRecycle?.value?.resultados
                getComun(miValor, miLista)
                contador(miValor, miLista)
            }
            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     * Función que filtra los fármcos por el nombre del mismo comercial o genérico
     */
    private fun getMedicamentos(ser: ApiService, miS: String) {
        ser.getMedicamentos(miS,1).enqueue(object : Callback<MiObjeto> {
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                miViewModel.miRecycle?.value = response.body()
                getComun(miViewModel.miRecycle?.value)
                contador(miViewModel.miRecycle?.value)
            }
            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * @author José Ramón Laperal Mur
     * @param mG lista de objetos que llena el RecyclerView
     * Llena el RecyclerView con los fármacos filtrados
     */
    private fun getComun(mG: MiObjeto?, miLista: List<MiFarmaco>? = mG?.resultados){

        recyclerFarmacos = findViewById(R.id.recyclerId)
        val layoutManager = LinearLayoutManager(this@MainActivity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerFarmacos.layoutManager = layoutManager

        val adapter = miLista?.let {
            Adaptador(this@MainActivity,it,this)
        }
        recyclerFarmacos.adapter = adapter

        UIUtil.hideKeyboard(this@MainActivity)

    }

    //</editor-folder>

    /**
     * Envía a la vista DetalleFarmaco el número de registro del
     * fármaco seleccionado en el RecyclerView
     * @param item objeto que tiene los datos del fármaco seleccionado
     * @param position representa la posición en el RecyclerView
     */
    override fun onItemClick(item: MiFarmaco, position: Int) {
        val intent = Intent(this, DetalleFarmaco::class.java)
        intent.putExtra("REGISTRO", item.nregistro)
        startActivity(intent)
    }

    /**
     * Inicializa los controles
     */
    private fun inicializa(){
        mButton = findViewById(R.id.botonBuscar)
        mEditText = findViewById(R.id.txtBuscar)
        mEditText.requestFocus()

//      Convierte todas las entradas en mayúsculas
        mEditText.filters = mEditText.filters + InputFilter.AllCaps()

        chkActivo = findViewById(R.id.chkPActivo)
    }

    /**
     * Muestra un Toast con el número de items del total de entradas
     * @param m Objeto que muestra los resultados de la consulta
     */
    private fun contador(m: MiObjeto?, lista: List<MiFarmaco>? = m?.resultados){
        if(m!=null) {
            val toast: Toast = Toast.makeText(
                applicationContext,
                """${lista?.size.toString()} entradas de un total de ${m.totalFilas}""",
                Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.show()
        }
    }

}
