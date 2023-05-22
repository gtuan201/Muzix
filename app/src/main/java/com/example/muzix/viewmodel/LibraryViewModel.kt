package com.example.muzix.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.model.Artist
import com.example.muzix.model.Favourite
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
    private var dataFavPlaylist : MutableLiveData<MutableList<Playlist>> = MutableLiveData()

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

        dataFavPlaylist.observeForever { favPlaylistList ->
            val combinedList = mutableListOf<Any>()
            combinedList.addAll(dataLibrary.value ?: emptyList())
            combinedList.addAll(favPlaylistList)
            dataLibrary.value = combinedList
        }
    }

    fun getLibrary(): MutableLiveData<MutableList<Any>>{
        getFavPlaylist()
        getPlaylist()
        getArtist()
        return dataLibrary
    }

    private fun getPlaylist(): MutableLiveData<MutableList<Playlist>>{
        viewModelScope.launch {
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
        }
        return dataPlaylist
    }

    private fun getArtist(): MutableLiveData<MutableList<Artist>>{
        viewModelScope.launch {
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
        }
        return dataArtist
    }
    private fun getFavPlaylist(): MutableLiveData<MutableList<Playlist>>{
        viewModelScope.launch {
            val listValue : MutableList<Playlist> = mutableListOf()
            FirebaseService.apiService.getFavourite().enqueue(object : Callback<Map<String, Favourite>>{
                override fun onResponse(
                    call: Call<Map<String, Favourite>>,
                    response: Response<Map<String, Favourite>>
                ) {
                    if (response.isSuccessful){
                        val list = response.body()!!.values.toList()
                        FirebaseService.apiService.getPlaylist().enqueue(object : Callback<Map<String,Playlist>>{
                            override fun onResponse(
                                call: Call<Map<String, Playlist>>,
                                response: Response<Map<String, Playlist>>
                            ) {
                                if (response.isSuccessful){
                                    val listP = response.body()!!.values.toList()
                                    for (i in list){
                                        for (j in listP){
                                            if (i.idPlaylist != null && i.idPlaylist == j.id){
                                                listValue.add(j)
                                            }
                                        }
                                    }
                                    dataFavPlaylist.postValue(listValue)
                                }
                            }

                            override fun onFailure(
                                call: Call<Map<String, Playlist>>,
                                t: Throwable
                            ) {

                            }

                        })
                    }
                }

                override fun onFailure(call: Call<Map<String, Favourite>>, t: Throwable) {
                    Log.e("getFavourite","error")
                }

            })
        }
        return dataFavPlaylist
    }
    fun addPlaylist(playlist: Playlist){
        viewModelScope.launch {
            FirebaseService.apiService.addPlaylist(playlist.id.toString(),playlist)
                .enqueue(object : Callback<Playlist>{
                    override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                        if (response.isSuccessful && response.body() != null){
                            val list = dataLibrary.value
                            list?.add(playlist)
                            dataLibrary.postValue(list)
                        }
                    }
                    override fun onFailure(call: Call<Playlist>, t: Throwable) {
                        Log.e("addPlaylist", "error")
                    }

                })
        }
    }
}