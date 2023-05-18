package com.example.muzix.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.model.Artist
import com.example.muzix.model.Playlist
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryViewModel : ViewModel() {

    private var dataLibrary : MutableLiveData<MutableList<Any>> = MutableLiveData()
    private var dataPlaylist : MutableLiveData<MutableList<Playlist>> = MutableLiveData()
    private var dataArtist : MutableLiveData<MutableList<Artist>> = MutableLiveData()

    init {
        dataPlaylist.observeForever { playlistList ->
            val combinedList = mutableListOf<Any>()
            combinedList.addAll(dataLibrary.value ?: emptyList())
            combinedList.addAll(playlistList)
            dataLibrary.value = combinedList
        }

        dataArtist.observeForever { artistList ->
            val combinedList = mutableListOf<Any>()
            combinedList.addAll(dataLibrary.value ?: emptyList())
            combinedList.addAll(artistList)
            dataLibrary.value = combinedList
        }
    }

    fun getLibrary(): MutableLiveData<MutableList<Any>>{
        getPlaylist()
        getArtist()
        return dataLibrary
    }

    fun getPlaylist(): MutableLiveData<MutableList<Playlist>>{
        val list : MutableList<Playlist> = mutableListOf()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        FirebaseService.apiService.getPlaylist().enqueue(object : Callback<Map<String,Playlist>>{
            override fun onResponse(
                call: Call<Map<String, Playlist>>,
                response: Response<Map<String, Playlist>>
            ) {
                if (response.isSuccessful && response.body() != null){
                    val listPlaylist = response.body()!!.values.toList()
                    for (i in listPlaylist){
                        if (i.idCollection == uid){
                            list.add(i)
                        }
                    }
                    dataPlaylist.postValue(list)
                }
            }

            override fun onFailure(call: Call<Map<String, Playlist>>, t: Throwable) {

            }

        })
        return dataPlaylist
    }

    private fun getArtist(): MutableLiveData<MutableList<Artist>>{
        FirebaseService.apiService.getArtist().enqueue(object : Callback<Map<String,Artist>>{
            override fun onResponse(
                call: Call<Map<String, Artist>>,
                response: Response<Map<String, Artist>>
            ) {
                if (response.isSuccessful && response.body() != null){
                    dataArtist.postValue(response.body()!!.values.toMutableList().take(2).toMutableList())
                }
            }

            override fun onFailure(call: Call<Map<String, Artist>>, t: Throwable) {

            }

        })
        return dataArtist
    }
}