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
import com.example.vademecum.R
import com.example.vademecum.adaptadores.Adaptador
import com.example.vademecum.adaptadores.ApiService
import com.example.vademecum.adaptadores.OnFarItemClickListner
import com.example.vademecum.dataclass.MiFarmaco
import com.example.vademecum.dataclass.MiObjeto
import com.example.vademecum.models.MainViewModel
import com.example.vademecum.objetos.CFecha
import com.example.vademecum.objetos.Comun
import com.example.vademecum.objetos.Comun.nMIFIRMA
import com.example.vademecum.objetos.Comun.nVADEMECUM
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * @author Jose Ramón Laperal Mur
 * Clase prinicpal que filtra los fármacos por nombre y llena el RecyclerView
 */
class MainActivity : AppCompatActivity(),
    OnFarItemClickListner {

    //<editor-folder desc = " Variables ">

    private lateinit var mButton: Button
    private lateinit var mEditText: EditText
    private lateinit var chkActivo: CheckBox
    private lateinit var chkOrdenNombre: CheckBox
    private lateinit var chkOrdenLaboratorio: CheckBox

    private lateinit var miViewModel: MainViewModel
    private lateinit var recyclerFarmacos: RecyclerView

    private var getNumPA: Int =0


    //</editor-folder>

    //<editor-folder desc = " Menu ">

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /*
     *   Junto a invalidateOptionsMenu permite modificar los datos del menu
     *
     **/
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        when(getNumPA){
            0 -> menu?.findItem(R.id.mnuTodosPA)?.isChecked = true
            1 -> menu?.findItem(R.id.mnuUnPA)?.isChecked = true
            2 -> menu?.findItem(R.id.mnuDosPA)?.isChecked = true
            3 -> menu?.findItem(R.id.mnuMasDosPA)?.isChecked = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.version -> {
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
                        "$nVADEMECUM$version$nMIFIRMA",
                        Toast.LENGTH_SHORT
                    )
                toast.setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
                toast.show()
            }
            R.id.action_settings ->{
                val intent = Intent(this, Acercade::class.java)
                startActivity(intent)
            }

            R.id.mnuTodosPA->{
                item.isChecked = !item.isChecked
                getNumPA =0
                miViewModel.miMenu.value = getNumPA
                return true
            }
            R.id.mnuUnPA->{
                item.isChecked = !item.isChecked
                getNumPA =1
                miViewModel.miMenu.value = getNumPA
                return true
            }
            R.id.mnuDosPA->{
                item.isChecked = !item.isChecked
                getNumPA =2
                miViewModel.miMenu.value = getNumPA
                return true
            }
            R.id.mnuMasDosPA->{
                item.isChecked = !item.isChecked
                getNumPA =3
                miViewModel.miMenu.value = getNumPA
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    //</editor-folder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Comprueba fecha límite
        if(CFecha.comprueba("01/01/2022")){
            val builder = AlertDialog.Builder(this)
                .setTitle(CFecha.ATENCION)
                .setMessage(CFecha.PROGRAMA_PASADO)
                .setIcon(R.mipmap.ic_launcher_foreground)
                .setPositiveButton(CFecha.ACEPTAR) { _, _ -> finish() }
            builder.create().show()
        }

        compruebaConexionInternet(this)

        miViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        inicializa()

        //      Se activa el botón cuando el número de letras es mayor a 2
        mEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mButton.isEnabled = mEditText.text.length > 2
            }

        })

        //      Borra el contenido del EditTexBox y limpia el RecyclerView
        mEditText.setOnLongClickListener {
            mEditText.text.clear()
            getComun(null)
            UIUtil.showKeyboard(this, mEditText)
            true
        }

        //        Bloquea que se puedan activar a la vez el orden por nombre y laboratorio
        chkOrdenNombre.setOnClickListener {
            if (chkOrdenNombre.isChecked) chkOrdenLaboratorio.isChecked = false
        }

        //        Bloquea que se puedan activar a la vez el orden por nombre y laboratorio
        chkOrdenLaboratorio.setOnClickListener {
            if (chkOrdenLaboratorio.isChecked) chkOrdenNombre.isChecked = false
        }

        //      Busca los fármacos en la API dependiendo de si está marcado el Principio activo o no.
        mButton.setOnClickListener {
            if (Comun.hasNetworkAvailable(this)) {
                val miS: String = mEditText.text.toString()
                if (chkActivo.isChecked) {
                    when(getNumPA){
                        0 -> getPactivos(Comun.service, miS)
                        1 -> getPactivosUnPA(Comun.service, miS)
                        2-> getPactivosDosPA(Comun.service, miS)
                        else -> getPactivosMasDeDosPA(Comun.service, miS)
                    }
                } else {
                    when(getNumPA){
                        0 -> getMedicamentos(Comun.service, miS)
                        1 -> getMedicamentosUnPa(Comun.service, miS)
                        2-> getMedicamentosDosPa(Comun.service, miS)
                        else -> getMedicamentosMasDeDos(Comun.service, miS)
                    }
                }
            } else {
                compruebaConexionInternet(this)
            }
        }

    }

    /**
     * Actualiza la pantalla con el ViewModel al volver a cargar la actividad
     */
    override fun onResume() {
        super.onResume()

        //        Comprba que la caja de búsqueda no esté vacía para cargar el recyclerview

        if (mEditText.text.toString() != "") {
            getComun(miViewModel.miRecycle?.value)
        } else {
            getComun(null)
        }
        getNumPA = miViewModel.miMenu.value?.toInt()?:0 // El operador Elvis me permite poner a 0 la variable si es null
        mEditText.requestFocus()
        UIUtil.showKeyboard(this, mEditText)
    }

    //<editor-folder desc = " Consultas ">

    //<editor-folder desc = " GET Principios Activos ">

    /**
     * Función que filtra los fármcos por el Principio Activo
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private fun getPactivos(ser: ApiService, miS: String) {
        //        Filtra solo los comercializados
        ser.getPActivos(miS).enqueue(object : Callback<MiObjeto> {
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                funcionListado(response)
            }

            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * Función que filtra los fármcos por el Principio Activo
     * Filtra un solo principio activo
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private fun getPactivosUnPA(ser: ApiService, miS: String) {
        //        Filtra solo los comercializados
        ser.getPactivosUnPA(miS).enqueue(object : Callback<MiObjeto> {
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                funcionListado(response)
            }

            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * Función que filtra los fármcos por el Principio Activo
     * Filtra un solo principio activo
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private fun getPactivosDosPA(ser: ApiService, miS: String) {
        //        Filtra solo los comercializados
        ser.getPactivosDosPA(miS).enqueue(object : Callback<MiObjeto> {
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                funcionListado(response)
            }

            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * Función que filtra los fármcos por el Principio Activo
     * Filtra más dos principios activos
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private fun getPactivosMasDeDosPA(ser: ApiService, miS: String) {
        //        Filtra solo los comercializados
        ser.getPactivosTresPA(miS).enqueue(object : Callback<MiObjeto> {
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                funcionListado(response)
            }

            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    //</editor-folder>

    //<editor-folder desc = " GET Medicamentos ">

    /**
     * Función que filtra los fármcos por el nombre del mismo comercial o genérico
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private fun getMedicamentos(ser: ApiService, miS: String) {
        ser.getMedicamentos(miS).enqueue(object : Callback<MiObjeto> {
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                funcionListado(response)
            }
            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * Función que filtra los fármcos por el nombre del mismo comercial o genérico
     * Filtra aquellos con solo un principio activo
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     *
     */
    private fun getMedicamentosUnPa(ser: ApiService, miS: String) {
        ser.getMedicamentosUnPA(miS).enqueue(object : Callback<MiObjeto> {
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                funcionListado(response)
            }
            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * Función que filtra los fármcos por el nombre del mismo comercial o genérico
     * Filtra aquellos con dos principios activo
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     *
     */
    private fun getMedicamentosDosPa(ser: ApiService, miS: String) {
        ser.getMedicamentosDosPA(miS).enqueue(object : Callback<MiObjeto> {
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                funcionListado(response)
            }
            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    /**
     * Función que filtra los fármcos por el nombre del mismo comercial o genérico
     * Filtra aquellos con más de dos principios activos
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private fun getMedicamentosMasDeDos(ser: ApiService, miS: String) {
        ser.getMedicamentosTresPA(miS).enqueue(object : Callback<MiObjeto> {
            override fun onResponse(call: Call<MiObjeto>, response: Response<MiObjeto>) {
                funcionListado(response)
            }
            override fun onFailure(call: Call<MiObjeto>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    //</editor-folder>

    /**
     * Llena el RecyclerView con los fármacos filtrados
     * @author José Ramón Laperal Mur
     * @param mG lista de objetos que llena el RecyclerView
     */
    private fun getComun(mG: MiObjeto?) {
        val miLista: List<MiFarmaco>? = mG?.resultados

//      Oredena por nombre y laboratorio
        val sortList: MutableList<MiFarmaco>?

        sortList = when {
            chkOrdenNombre.isChecked && !chkOrdenLaboratorio.isChecked -> miLista?.sortedWith(
                compareBy { it.nombre })?.toMutableList()
            !chkOrdenNombre.isChecked && chkOrdenLaboratorio.isChecked -> miLista?.sortedWith(
                compareBy { it.labtitular })?.toMutableList()
            else -> miLista?.toMutableList()
        }


        this.invalidateOptionsMenu()
        getNumPA =0

        val layoutManager = LinearLayoutManager(this@MainActivity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerFarmacos.layoutManager = layoutManager

        val adapter = sortList?.let {
            Adaptador(this@MainActivity, it, this)
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
    private fun inicializa() {
        mButton = findViewById(R.id.botonBuscar)
        mEditText = findViewById(R.id.txtBuscar)
        mEditText.requestFocus()

//      Convierte todas las entradas en mayúsculas
        mEditText.filters += InputFilter.AllCaps()

        chkActivo = findViewById(R.id.chkPActivo)
        chkOrdenNombre = findViewById(R.id.chkOrdenNombre)
        chkOrdenLaboratorio = findViewById(R.id.chkOrdenLaboratorio)

        recyclerFarmacos = findViewById(R.id.recyclerId)
    }

    //<editor-folder desc = " Conexión a Internet ">

    /**
     * Comprueba la conexión a Internet y permite reintentar la conexión antes del diálogo de
     * salir de la aplicación
     * @param activity Actividad a la que se aplica
     */
    private fun compruebaConexionInternet(activity: MainActivity) {
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
    private fun miComprobacion(n: Int, activity: MainActivity) {
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
        }
    }

    //</editor-folder>

    /**
     * Ejecuta el código según la respuesta de Retofit
     * @param res Respuesta de Retrofit
     */
    private fun funcionListado(res: Response<MiObjeto>) {
        if (res.isSuccessful) {
            with(miViewModel){
                miRecycle?.value = res.body()
                miMenu.value = getNumPA
                val miValor: MiObjeto? = miRecycle?.value

                getComun(miValor)
                contador(miValor, applicationContext)
            }
        } else {
            errorToastListado()
        }
    }

    //    Toast que avisa del error en la ejecución de la consulta.
    private fun errorToastListado() {
        val toast: Toast = Toast.makeText(
            applicationContext,
            "No se ha podido el listado de fármacoslos datos",
            Toast.LENGTH_LONG
        )
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        toast.show()
    }

}
