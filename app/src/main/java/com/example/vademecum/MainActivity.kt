package com.example.vademecum

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
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
import com.example.vademecum.Models.MainViewModel
import com.example.vademecum.objetos.Adaptador
import com.example.vademecum.objetos.MiFarmaco
import com.example.vademecum.objetos.MiObjeto
import com.example.vademecum.objetos.OnFarItemClickListner
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnFarItemClickListner {

    //<editor-folder desc = " Variables ">
   private lateinit var mButton: Button
   private lateinit var mEditText: EditText
   private lateinit var chkActivo: CheckBox

    private lateinit var service: ApiService

    lateinit var miViewModel: MainViewModel

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

        inicializa()

        miViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        if(miViewModel.textoEntrada.value!=null){
            mEditText.setText(miViewModel.textoEntrada.value.toString())
        }else{
            mEditText.setText("")
        }


        if(!hasNetworkAvailable(this)){

            val builder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.salir))
                .setMessage(getString(R.string.salir_aplicacion))
                .setPositiveButton(getString(R.string.aceptar)){ _, _ -> this.finish() }
            builder.create().show()

        }
        //<editor-folder desc = " Retrofit ">

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(ApiService::class.java)

        //</editor-folder>

        // Se activa el botón cuando el número de letras es mayor a 2
        mEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

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

        mButton.setOnClickListener{
            if(chkActivo.isChecked ){
                getPactivos(service)
            }else{
                getMedicamentos(service)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        miViewModel.textoEntrada.value = mEditText.toString()
        if(mEditText.text.toString() !=""){
            if (chkActivo.isChecked) {
                getPactivos(service)
            } else {
                getMedicamentos(service)
            }
        }
        mEditText.requestFocus()
        UIUtil.showKeyboard(this, mEditText)
    }

    /**
     *  Comprueba si Internet está accesible
     */
    @Suppress("DEPRECATION")
    private fun hasNetworkAvailable(context: Context): Boolean {
        val service: String = Context.CONNECTIVITY_SERVICE
        val manager: ConnectivityManager? = context.getSystemService(service) as ConnectivityManager?
        val network = manager?.activeNetworkInfo
        return (network != null)
    }

    private fun inicializa(){
        mButton = findViewById(R.id.botonBuscar)
        mEditText = findViewById(R.id.txtBuscar)
        mEditText.requestFocus()

//      Convierte todas las entradas en mayúsculas
        mEditText.filters = mEditText.filters + InputFilter.AllCaps()

        chkActivo = findViewById(R.id.chkPActivo)
    }

    //<editor-folder desc = " Consultas ">

    private fun getPactivos(ser: ApiService){
        val miS: String = mEditText.text.toString() + "*"
        ser.getPActivos(miS, 1).enqueue(object: Callback<MiObjeto>{
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                miViewModel.miRecycle?.value    = response.body()
                getComun(miViewModel.miRecycle?.value)
            }
            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })

    }

    private fun getMedicamentos(ser: ApiService) {

        val miS: String = mEditText.text.toString() + "*"

        ser.getMedicamentos(miS,1).enqueue(object : Callback<MiObjeto> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                miViewModel.miRecycle?.value = response.body()

                getComun(miViewModel.miRecycle?.value)
            }

            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun getComun(mG: MiObjeto?){
        val miLista = mG?.resultados

        recyclerFarmacos = findViewById(R.id.recyclerId)
        val layoutManager = LinearLayoutManager(this@MainActivity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerFarmacos.layoutManager = layoutManager

        val adapter = miLista?.let { Adaptador(this@MainActivity, it, this) }
        recyclerFarmacos.adapter = adapter

        UIUtil.hideKeyboard(this@MainActivity)

        if(mG!=null) {
            val toast: Toast = Toast.makeText(
                applicationContext,
                """${miLista?.size.toString()} entradas de un total de ${mG.totalFilas}""",
                Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.show()
        }
    }

    override fun onItemClick(item: MiFarmaco, position: Int) {
        val intent = Intent(this, DetalleFarmaco::class.java)
        intent.putExtra(getString(R.string.regsitro), item.nregistro)
        startActivity(intent)
    }

    //</editor-folder>

}
