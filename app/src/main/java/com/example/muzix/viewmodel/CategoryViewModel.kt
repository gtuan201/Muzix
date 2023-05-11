package com.example.muzix.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.model.Category
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel : ViewModel() {
    private var dataCategory : MutableLiveData<List<Category>> = MutableLiveData()
    private var dataOneCategory : MutableLiveData<Category> = MutableLiveData()

    fun getCategory(): MutableLiveData<List<Category>>{
        viewModelScope.launch {
            FirebaseService.apiService.getCategory().enqueue(object : Callback<Map<String,Category>>{
                override fun onResponse(
                    call: Call<Map<String, Category>>,
                    response: Response<Map<String, Category>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        dataCategory.postValue(list)
                    }
                }

                override fun onFailure(call: Call<Map<String, Category>>, t: Throwable) {
                    Log.e("getCategory","error")
                }

            })
        }
        return dataCategory
    }
}