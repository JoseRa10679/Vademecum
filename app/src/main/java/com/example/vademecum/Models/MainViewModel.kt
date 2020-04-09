package com.example.vademecum.Models

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vademecum.Dataclass.MiFarmaco
import com.example.vademecum.Dataclass.MiObjeto

/**
 * @author José Ramón Laperal Mur
 * Clase ViewModel que mantiene el RecyclerView al rotar la pantalla.
 */
class MainViewModel : ViewModel() {

    val miRecycle: MutableLiveData<MiObjeto>? by lazy {
        MutableLiveData<MiObjeto>()
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
                """${lista?.size.toString()} entradas de un total de ${m?.totalFilas}""",
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.show()
        }
    }


}