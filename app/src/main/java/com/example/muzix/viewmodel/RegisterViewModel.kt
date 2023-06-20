package com.example.muzix.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.muzix.model.User

class RegisterViewModel : ViewModel() {
    private val dataAccount : MutableLiveData<User> = MutableLiveData()
    companion object{
        const val EMAIL = 0
        const val PASSWORD = 1
        const val NAME = 2
    }

    fun setData(key : Int,value : String){
        val user = dataAccount.value ?: User()
        when(key){
            EMAIL -> user.apply { email = value }
            PASSWORD -> user.apply { password = value }
            NAME -> user.apply { name = value }
        }
        dataAccount.value = user

    }
    fun getData() : MutableLiveData<User>{
        return dataAccount
    }
}