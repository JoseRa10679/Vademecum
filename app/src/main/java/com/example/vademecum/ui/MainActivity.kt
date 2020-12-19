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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vademecum.R
import com.example.vademecum.adaptadores.Adaptador
import com.example.vademecum.adaptadores.ApiService
import com.example.vademecum.adaptadores.OnFarItemClickListner
import com.example.vademecum.databinding.ActivityMainBinding
import com.example.vademecum.dataclass.MiFarmaco
import com.example.vademecum.dataclass.MiObjeto
import com.example.vademecum.models.MainViewModel
import com.example.vademecum.objetos.CFecha
import com.example.vademecum.objetos.Comun
import com.example.vademecum.objetos.Comun.nMIFIRMA
import com.example.vademecum.objetos.Comun.nVADEMECUM
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import retrofit2.Response


/**
 * @author Jose Ramón Laperal Mur
 * Clase prinicpal que filtra los fármacos por nombre y llena el RecyclerView
 */
class MainActivity : AppCompatActivity(),
    OnFarItemClickListner {

    //<editor-folder desc = " Variables ">

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    private val miViewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private var getNumPA: Int = 0


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
        when (getNumPA) {
            0 -> menu?.findItem(R.id.mnuTodosPA)?.isChecked = true
            1 -> menu?.findItem(R.id.mnuUnPA)?.isChecked = true
            2 -> menu?.findItem(R.id.mnuDosPA)?.isChecked = true
            3 -> menu?.findItem(R.id.mnuMasDosPA)?.isChecked = true
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.version -> {
                lateinit var version: String
                var packageInfo: PackageInfo? = null
                try {
                    packageInfo = packageManager.getPackageInfo(packageName, 0)
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }

                if (packageInfo != null) version = packageInfo.versionName
                Toast.makeText(
                    this@MainActivity,
                    "$nVADEMECUM$version$nMIFIRMA",
                    Toast.LENGTH_SHORT
                ).apply {
                    setGravity(Gravity.CENTER or Gravity.CENTER_HORIZONTAL, 0, 0)
                    show()
                }

            }
            R.id.action_settings -> {
                Intent(this, Acercade::class.java).apply {
                    startActivity(this)
                }

            }

            R.id.mnuTodosPA -> {
                item.isChecked = !item.isChecked
                getNumPA = 0
                miViewModel.setmiMenu(getNumPA)
                return true
            }
            R.id.mnuUnPA -> {
                item.isChecked = !item.isChecked
                getNumPA = 1
                miViewModel.setmiMenu(getNumPA)
                return true
            }
            R.id.mnuDosPA -> {
                item.isChecked = !item.isChecked
                getNumPA = 2
                miViewModel.setmiMenu(getNumPA)
                return true
            }
            R.id.mnuMasDosPA -> {
                item.isChecked = !item.isChecked
                getNumPA = 3
                miViewModel.setmiMenu(getNumPA)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    //</editor-folder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Comprueba fecha límite
        if (CFecha.comprueba("01/01/2024")) {
            CFecha.alertaMSG(this)
        }

        Comun.compruebaConexionInternet(this)

        observa()

        inicializa()


        //      Se activa el botón cuando el número de letras es mayor a 2
        binding.txtBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.botonBuscar.isEnabled = binding.txtBuscar.text.length > 2
            }
        })

        //      Borra el contenido del EditTexBox y limpia el RecyclerView
        binding.txtBuscar.setOnLongClickListener {
            binding.txtBuscar.text.clear()
            this.invalidateOptionsMenu()
            getNumPA = 0
            miViewModel.setmiMenu(getNumPA)
            miViewModel.setmiRecycle(null)
            getComun(null)
            UIUtil.showKeyboard(this, txtBuscar)
            true
        }

        //        Cambia el Hint del editTextBox al marcar el chkActivo
        binding.chkPActivo.setOnClickListener {
            miViewModel.setTextoBHint(
                if (binding.chkPActivo.isChecked) {
                    getString(R.string.p_activo)
                } else {
                    getString(R.string.nombre)
                }
            )
        }

        //        Bloquea que se puedan activar a la vez el orden por nombre y laboratorio
        binding.chkOrdenNombre.setOnClickListener {
            if (binding.chkOrdenNombre.isChecked) binding.chkOrdenLaboratorio.isChecked = false
        }

        //        Bloquea que se puedan activar a la vez el orden por nombre y laboratorio
        binding.chkOrdenLaboratorio.setOnClickListener {
            if (binding.chkOrdenLaboratorio.isChecked) binding.chkOrdenNombre.isChecked = false
        }

        //      Busca los fármacos en la API dependiendo de si está marcado el Principio activo o no.
        binding.botonBuscar.setOnClickListener {
            if (Comun.hasNetworkAvailable(this)) {

                /**
                 * Utilizo una Corutina en el hilo principal para visualizar la
                 * progressBarr sin que bloquee la aplicación
                 */
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.progressBar0.visibility = View.VISIBLE
                }

                /**
                 * Utilizo una Corutina en el hilo IO para hacer la consulta a la base de datos online
                 * y que, no bloquee el hilo principal
                 */
                lifecycleScope.launch(Dispatchers.IO) {
                    val miS: String = binding.txtBuscar.text.toString()
                    if (binding.chkPActivo.isChecked) {
                        when (getNumPA) {
                            0 -> getPactivos(Comun.service, miS)
                            1 -> getPactivosUnPA(Comun.service, miS)
                            2 -> getPactivosDosPA(Comun.service, miS)
                            else -> getPactivosMasDeDosPA(Comun.service, miS)
                        }
                    } else {
                        when (getNumPA) {
                            0 -> getMedicamentos(Comun.service, miS)
                            1 -> getMedicamentosUnPa(Comun.service, miS)
                            2 -> getMedicamentosDosPa(Comun.service, miS)
                            else -> getMedicamentosMasDeDos(Comun.service, miS)
                        }
                    }
                }

            } else {
                errorSinConexion()
            }
        }

    }

    private fun observa() {

        miViewModel.run{
            getmiMenu().observe(this@MainActivity, {
                getNumPA = it ?: 0
            })

            getTextoBHint().observe(this@MainActivity, {
                binding.txtBuscar.hint = it ?: "Nombre"
            })

            getmiRecycle().observe(this@MainActivity, {
                val miObjeto: MiObjeto? = it
                binding.txtBuscar.text?.toString()?.let {
                    getComun(miObjeto)
                } ?: getComun(null)

            })

            getmiPosicion().observe(this@MainActivity, {
                binding.recyclerId.smoothScrollToPosition(it)
            })
        }

    }

    /**
     * Actualiza la pantalla con el ViewModel al volver a cargar la actividad
     */
    override fun onResume() {
        super.onResume()
        binding.txtBuscar.requestFocus()
//        UIUtil.showKeyboard(this, binding.txtBuscar)
    }

    //<editor-folder desc = " Consultas ">

    //<editor-folder desc = " GET Principios Activos ">

    /**
     * Función que filtra los fármcos por el Principio Activo
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private suspend fun getPactivos(ser: ApiService, miS: String) {
        respondiendo(ser.getPActivos(miS))
    }


    /* ********** SIN CORUTINAS ****************
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
     }*/

    /**
     * Función que filtra los fármcos por el Principio Activo
     * Filtra un solo principio activo
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */

    private suspend fun getPactivosUnPA(ser: ApiService, miS: String) {
        respondiendo(ser.getPactivosUnPA(miS))
    }


    /**
     * Función que filtra los fármcos por el Principio Activo
     * Filtra un solo principio activo
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private suspend fun getPactivosDosPA(ser: ApiService, miS: String) {
        //        Filtra solo los comercializados
        respondiendo(ser.getPactivosDosPA(miS))
    }


    /**
     * Función que filtra los fármcos por el Principio Activo
     * Filtra más dos principios activos
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private suspend fun getPactivosMasDeDosPA(ser: ApiService, miS: String) {
        //        Filtra solo los comercializados
        respondiendo(ser.getPactivosTresPA(miS))
    }


    //</editor-folder>

    //<editor-folder desc = " GET Medicamentos ">

    /**
     * Función que filtra los fármcos por el nombre del mismo comercial o genérico
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private suspend fun getMedicamentos(ser: ApiService, miS: String) {
        respondiendo(ser.getMedicamentos(miS))
    }


    /**
     * Función que filtra los fármcos por el nombre del mismo comercial o genérico
     * Filtra aquellos con solo un principio activo
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     *
     */
    private suspend fun getMedicamentosUnPa(ser: ApiService, miS: String) {
        respondiendo(ser.getMedicamentosUnPA(miS))
    }


    /**
     * Función que filtra los fármcos por el nombre del mismo comercial o genérico
     * Filtra aquellos con dos principios activo
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     *
     */
    private suspend fun getMedicamentosDosPa(ser: ApiService, miS: String) {
        respondiendo(ser.getMedicamentosDosPA(miS))
    }


    /**
     * Función que filtra los fármcos por el nombre del mismo comercial o genérico
     * Filtra aquellos con más de dos principios activos
     * @author José Ramón Laperal Mur
     * @param ser instancia del ApiServide de Retrofit
     */
    private suspend fun getMedicamentosMasDeDos(ser: ApiService, miS: String) {
        respondiendo(ser.getMedicamentosTresPA(miS))
    }


    //</editor-folder>

    private fun respondiendo(res: Response<MiObjeto>){
        if (res.isSuccessful) {
            funcionListado(res)
        } else {
            Comun.noSePuedenCargarDatos(
                applicationContext,
                getString(R.string.no_cargar_datos)
            )
        }
    }

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
            binding.chkOrdenNombre.isChecked && !binding.chkOrdenLaboratorio.isChecked -> miLista?.sortedWith(
                compareBy { it.nombre })?.toMutableList()
            !binding.chkOrdenNombre.isChecked && binding.chkOrdenLaboratorio.isChecked -> miLista?.sortedWith(
                compareBy { it.labtitular })?.toMutableList()
            else -> miLista?.toMutableList()
        }

        val layoutManager = LinearLayoutManager(this@MainActivity).apply {
            orientation = LinearLayoutManager.VERTICAL
        }

        binding.recyclerId.run {
            this.layoutManager = layoutManager
            val adapter = sortList?.let {
                Adaptador(this@MainActivity, it, this@MainActivity)
            }
            this.adapter = adapter
        }

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
        if (Comun.hasNetworkAvailable(this)) {
            miViewModel.setmiPosicion(position)

            lifecycleScope.launch(Dispatchers.Main) {
                Intent(this@MainActivity, DetalleFarmaco::class.java).apply {
                    putExtra("REGISTRO", item.nregistro)
                    startActivity(this)
                }
            }
        } else {
            errorSinConexion()
        }
    }

    /**
     * Inicializa los controles
     */
    private fun inicializa() {
        binding.txtBuscar.run{
            requestFocus()

            //      Convierte todas las entradas en mayúsculas
            filters += InputFilter.AllCaps()
        }
        
        this.invalidateOptionsMenu()
        getNumPA = 0

    }

    /**
     * Ejecuta el código según la respuesta de Retofit
     * @param res Respuesta de Retrofit
     */
    private fun funcionListado(res: Response<MiObjeto>) {
        lifecycleScope.launch(Dispatchers.Main) {
            delay(1000L)
            if (res.isSuccessful) {
                miViewModel.run{
                    setmiRecycle(res.body())
                    setmiMenu(getNumPA)
                    val miValor: MiObjeto? = getmiRecycle().value
                    getComun(miValor)

                    lifecycleScope.launch(Dispatchers.Main) {
                        contador(miValor, applicationContext)
                        progressBar0.visibility = View.GONE
                    }
                }
            } else {
                errorToastListado()
            }
        }
    }

    //    Toast que avisa del error en la ejecución de la consulta.
    private fun errorToastListado() {
        Toast.makeText(
            applicationContext,
            "No se ha podido el listado de fármacoslos datos",
            Toast.LENGTH_LONG
        ).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
            show()
        }

    }

    //    Toast que avisa de falta de conexión a Internet
    private fun errorSinConexion() {
        Toast.makeText(
            applicationContext,
            "No hay conexión a Internet",
            Toast.LENGTH_LONG
        ).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorAccent))
            show()
        }
        
    }
}
