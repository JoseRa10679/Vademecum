package com.example.vademecum.Models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetalleViewModel: ViewModel(){

    val nombreV = MutableLiveData<String>()
    val laboratorioV = MutableLiveData<String>()
    val pactivosV = MutableLiveData<String>()
    val cprescV = MutableLiveData<String>()
    val conduccionV = MutableLiveData<String>()
    val presentacionesV = MutableLiveData<String>()
    val excipienteV = MutableLiveData<String>()
    val fichaTecnicaV = MutableLiveData<String>()
    val fichaTecnicaPosV = MutableLiveData<String>()
    val fichaTecnicaContV = MutableLiveData<String>()
    val fichaTecnicaReacV = MutableLiveData<String>()

    val miProspectoV = MutableLiveData<String>()
    val miPsumV = MutableLiveData<String>()
}