package com.example.vademecum.Models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vademecum.objetos.MiObjeto

/**
 * @author José Ramón Laperal Mur
 * Clase ViewModel que mantiene el RecyclerView al rotar la pantalla.
 */
class MainViewModel: ViewModel() {
    val miRecycle: MutableLiveData<MiObjeto>? by lazy {
        MutableLiveData<MiObjeto>()
    }

}