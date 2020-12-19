package com.example.vademecum.models

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vademecum.R
import com.example.vademecum.dataclass.MiObjeto

/**
 * @author José Ramón Laperal Mur
 * Clase ViewModel que mantiene el RecyclerView al rotar la pantalla.
 */
class MainViewModel : ViewModel() {

    private val _miRecycle: MutableLiveData<MiObjeto>? by lazy { MutableLiveData<MiObjeto>() }
    fun getmiRecycle() = _miRecycle as LiveData<MiObjeto>
    fun setmiRecycle(o: MiObjeto?){
        _miRecycle?.value = o
    }

    private val _miPosicion: MutableLiveData<Int>? by lazy { MutableLiveData<Int>() }
    fun getmiPosicion() = _miPosicion as LiveData<Int>
    fun setmiPosicion(i: Int){
        _miPosicion?.value = i
    }


    private val _miMenu: MutableLiveData<Int>? by lazy { MutableLiveData<Int>() }
    fun getmiMenu() = _miMenu as LiveData<Int>
    fun setmiMenu(i: Int){
        _miMenu?.value = i
    }


    private val _textoBHint: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    fun getTextoBHint() = _textoBHint as LiveData<String>
    fun setTextoBHint(s: String){
        _textoBHint.value = s
    }



    /**
     * Muestra un Toast con el número de items del total de entradas
     * @param m Objeto que muestra los resultados de la consulta
     * @param context Contexto donde se aplica la función
     */
    fun contador(m: MiObjeto?, context: Context) {

        /**
         * El comando Let ejecuta el código entre corchetes solo si el valor de m no es null.
         * Es mejora que hacer un if(!=null) porque controla el acceso desde otros hilos
         */
            m.let {
                Toast.makeText(
                    context,
                    """  ${
                        it?.resultados?.count().toString()
                    } entradas de un total de ${it?.totalFilas}  """, // Mas eficiente usar count que size
                    Toast.LENGTH_SHORT
                ).apply {
                    setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
                    val toastView: View = view
                    toastView.setBackgroundColor(
                        ContextCompat.getColor(
                            toastView.context,
                            R.color.colorAccent
                        )
                    )
                    show()
                }
            }

    }

}