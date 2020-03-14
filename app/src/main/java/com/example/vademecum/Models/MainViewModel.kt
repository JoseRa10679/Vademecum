package com.example.vademecum.Models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.vademecum.objetos.MiObjeto

class MainViewModel: ViewModel() {
    val miRecycle: MutableLiveData<MiObjeto>? by lazy {
        MutableLiveData<MiObjeto>()
    }
}