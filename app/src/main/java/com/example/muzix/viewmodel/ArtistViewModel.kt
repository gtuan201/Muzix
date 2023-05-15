package com.example.muzix.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.model.Artist
import com.example.muzix.model.Song
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtistViewModel : ViewModel() {

    private var dataSong : MutableLiveData<List<Song>> = MutableLiveData()
    private var dataRandomArtist : MutableLiveData<List<Artist>> = MutableLiveData()

    fun getSongOfArtist(name : String): MutableLiveData<List<Song>>{
        viewModelScope.launch {
            val listValue : MutableList<Song> = mutableListOf()
            FirebaseService.apiService.getSong().enqueue(object : Callback<Map<String,Song>>{
                override fun onResponse(
                    call: Call<Map<String, Song>>,
                    response: Response<Map<String, Song>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        for (i in list){
                            if (i.artist?.lowercase()?.contains(name) == true){
                                listValue.add(i)
                            }
                        }
                        dataSong.postValue(listValue)
                    }
                }

                override fun onFailure(call: Call<Map<String, Song>>, t: Throwable) {
                    Log.e("getSongOfArtist","error")
                }

            })
        }
        return dataSong
    }
    fun getRandomArtist(): MutableLiveData<List<Artist>>{
        viewModelScope.launch {
            FirebaseService.apiService.getArtist().enqueue(object : Callback<Map<String,Artist>>{
                override fun onResponse(
                    call: Call<Map<String, Artist>>,
                    response: Response<Map<String, Artist>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList().shuffled().take(10)
                        dataRandomArtist.postValue(list)
                    }
                }

                override fun onFailure(call: Call<Map<String, Artist>>, t: Throwable) {
                    Log.e("getRandomArtist","error")
                }

            })
        }
        return dataRandomArtist
    }
}