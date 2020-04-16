package com.example.vademecum.models

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vademecum.dataclass.MiFarmaco
import com.example.vademecum.dataclass.MiObjeto

/**
 * @author José Ramón Laperal Mur
 * Clase ViewModel que mantiene el RecyclerView al rotar la pantalla.
 */
class MainViewModel : ViewModel() {

    val miRecycle: MutableLiveData<MiObjeto>? by lazy {
        MutableLiveData<MiObjeto>()
    }

    val miMenu: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    /**
     * Muestra un Toast con el número de items del total de entradas
     * @param m Objeto que muestra los resultados de la consulta
     * @param context Contexto donde se aplica la función
     */
    fun contador(m: MiObjeto?, context: Context) {
        val lista: List<MiFarmaco>? = m?.resultados

        /*
        * El comando Let ejecuta el código entre corchetes solo si el valor de m no es null.
        * Es mejora que hacer un if(!=null) porque controla el acceso desde otros hilos
        * */
        m.let {
            val toast: Toast = Toast.makeText(
                context,
                """${lista?.count().toString()} entradas de un total de ${m?.totalFilas}""", // Mas eficiente usar conunt que size
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.show()
        }
    }


}