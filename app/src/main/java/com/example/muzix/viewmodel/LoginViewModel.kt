package com.example.muzix.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.muzix.repository.LoginRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : LoginRepository = LoginRepository()
    fun addInforUser(){
       repository.uploadData()
    }
}