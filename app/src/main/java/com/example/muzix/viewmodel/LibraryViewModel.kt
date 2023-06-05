package com.example.muzix.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.model.Artist
import com.example.muzix.model.Favourite
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LibraryViewModel : ViewModel() {

    private var dataLibrary : MutableLiveData<MutableList<Any>> = MutableLiveData()
    private var dataPlaylist : MutableLiveData<MutableList<Playlist>> = MutableLiveData()
    private var dataArtist : MutableLiveData<MutableList<Artist>> = MutableLiveData()
    private var dataFavPlaylist : MutableLiveData<MutableList<Playlist>> = MutableLiveData()
    private var dataFavArtist : MutableLiveData<MutableList<Artist>> = MutableLiveData()
    private var dataFavSong : MutableLiveData<MutableList<Song>> = MutableLiveData()

    init {
        dataPlaylist.observeForever { playlistList ->
            val combinedList = mutableListOf<Any>()
            combinedList.addAll(dataLibrary.value ?: emptyList())
            combinedList.addAll(playlistList)
            dataLibrary.value = combinedList
        }
    }

    fun getLibrary(): MutableLiveData<MutableList<Any>>{
        getPlaylist()
        return dataLibrary
    }

    fun getPlaylist(): MutableLiveData<MutableList<Playlist>>{
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
    fun getFavPlaylist(): MutableLiveData<MutableList<Playlist>>{
        viewModelScope.launch {
            val listValue : MutableList<Playlist> = mutableListOf()
            FirebaseService.apiService.getFavourite().enqueue(object : Callback<Map<String, Favourite>>{
                override fun onResponse(
                    call: Call<Map<String, Favourite>>,
                    response: Response<Map<String, Favourite>>
                ) {
                    if (response.isSuccessful && response.body() != null){
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
    fun addPlaylist(list: MutableList<Any>, playlist: Playlist){
        viewModelScope.launch {
            FirebaseService.apiService.addPlaylist(playlist.id.toString(),playlist)
                .enqueue(object : Callback<Playlist>{
                    override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                        if (response.isSuccessful && response.body() != null){
                          dataLibrary.value = list
                        }
                    }
                    override fun onFailure(call: Call<Playlist>, t: Throwable) {
                        Log.e("addPlaylist", "error")
                    }

                })
        }
    }

    fun updatePlaylistLib(playlist: Playlist){
        viewModelScope.launch(Dispatchers.IO) {
            FirebaseService.apiService.addPlaylist(playlist.id.toString(),playlist)
                .enqueue(object :Callback<Playlist>{
                    override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                        if (response.isSuccessful){
                            Log.e("updatePlaylist","ok")
                        }
                    }

                    override fun onFailure(call: Call<Playlist>, t: Throwable) {
                        Log.e("updatePlaylist","error")
                    }

                })
        }
    }
    fun getFavArtist(): MutableLiveData<MutableList<Artist>> {
        viewModelScope.launch {
            val listValue : MutableList<Artist> = mutableListOf()
            FirebaseService.apiService.getFavourite().enqueue(object : Callback<Map<String, Favourite>>{
                override fun onResponse(
                    call: Call<Map<String, Favourite>>,
                    response: Response<Map<String, Favourite>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        FirebaseService.apiService.getArtist().enqueue(object : Callback<Map<String,Artist>>{
                            override fun onResponse(
                                call: Call<Map<String, Artist>>,
                                response: Response<Map<String, Artist>>
                            ) {
                                if (response.isSuccessful){
                                    val listA = response.body()!!.values.toList()
                                    for (i in list){
                                        for (j in listA){
                                            if (i.idArtist != null && i.idArtist == j.id){
                                                listValue.add(j)
                                            }
                                        }
                                    }
                                    dataFavArtist.postValue(listValue)
                                }
                            }

                            override fun onFailure(
                                call: Call<Map<String, Artist>>,
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
        return dataFavArtist
    }

    fun getFavSong(): MutableLiveData<MutableList<Song>> {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val listValue : MutableList<Song> = mutableListOf()
            FirebaseService.apiService.getFavourite().enqueue(object : Callback<Map<String, Favourite>>{
                override fun onResponse(
                    call: Call<Map<String, Favourite>>,
                    response: Response<Map<String, Favourite>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        if (list[0].uid == uid)
                        {
                            FirebaseService.apiService.getSong().enqueue(object : Callback<Map<String,Song>>{
                                override fun onResponse(
                                    call: Call<Map<String, Song>>,
                                    response: Response<Map<String, Song>>
                                ) {
                                    if (response.isSuccessful){
                                        val listA = response.body()!!.values.toList()
                                        for (i in list){
                                            for (j in listA){
                                                if (i.idSong != null && i.idSong == j.id){
                                                    listValue.add(j)
                                                }
                                            }
                                        }
                                        dataFavSong.postValue(listValue)
                                    }
                                }

                                override fun onFailure(
                                    call: Call<Map<String, Song>>,
                                    t: Throwable
                                ) {

                                }

                            })
                        }
                    }
                }

                override fun onFailure(call: Call<Map<String, Favourite>>, t: Throwable) {
                    Log.e("getFavourite","error")
                }

            })
        }
        return dataFavSong
    }
    fun removePlaylist(playlist: Playlist){
        viewModelScope.launch(Dispatchers.IO){
            FirebaseService.apiService.removePlaylist(playlist.id.toString())
                .enqueue(object : Callback<Playlist>{
                    override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                        if (response.isSuccessful){
                            val list = dataLibrary.value
                            list?.remove(playlist)
                            dataLibrary.postValue(list)
                            Log.e("remove","ok")
                        }
                    }

                    override fun onFailure(call: Call<Playlist>, t: Throwable) {
                        Log.e("removePlaylist","error")
                    }

                })
        }
    }
    fun updateLib(listLib : MutableList<Any>){
        dataLibrary.value = mutableListOf()
        dataLibrary.postValue(listLib)
    }
}