package com.example.muzix.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.muzix.model.*
import com.example.muzix.data.remote.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaylistViewModel : ViewModel() {
    private var dataPlaylist: MutableLiveData<Playlist> = MutableLiveData()
    private var dataCollection: MutableLiveData<List<PlaylistCollection>> = MutableLiveData()
    private var listCol: List<PlaylistCollection> = ArrayList()
    private var dataArtist : MutableLiveData<List<Artist>> = MutableLiveData()
    private var listArtist : List<Artist> = ArrayList()
    private var dataRandomSong : MutableLiveData<Song> = MutableLiveData()
    private var dataHistory : MutableLiveData<List<History>> = MutableLiveData()
    private var listHistory : MutableList<History> = ArrayList()
    private var dataCurrentSong : MutableLiveData<Song> = MutableLiveData()
    var currentSong : Song? = null
    private set
    private var dataIsPlaying : MutableLiveData<Boolean> = MutableLiveData()
    var isPlaying : Boolean = false
        private set

        fun getPlaylist(idPlaylist : String) : MutableLiveData<Playlist>{
        val call = FirebaseService.apiService.getPlaylistFromId(idPlaylist)
        call.enqueue(object : Callback<Playlist> {
            override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                if (response.isSuccessful && response.body() != null){
                    val playlist = response.body()
                    dataPlaylist.postValue(playlist)
                }
            }

            override fun onFailure(call: Call<Playlist>, t: Throwable) {
                Log.e("getPlaylist","error")
            }

        })
        return dataPlaylist
    }
    fun getCollection(): MutableLiveData<List<PlaylistCollection>> {
        FirebaseService.apiService.getCollection()
            .enqueue(object : Callback<Map<String, PlaylistCollection>> {
                override fun onResponse(
                    call: Call<Map<String, PlaylistCollection>>,
                    response: Response<Map<String, PlaylistCollection>>
                ) {
                    if (response.isSuccessful){
                        listCol = response.body()?.values?.toList()!!
                        dataCollection.postValue(listCol)
                    }
                }

                override fun onFailure(call: Call<Map<String, PlaylistCollection>>, t: Throwable) {
                    Log.e(TAG, "Failed to get collections: ${t.message}")
                }

            })
        return dataCollection
    }
    fun getTopArtist(): MutableLiveData<List<Artist>>{
        FirebaseService.apiService.getArtist().enqueue(object :Callback<Map<String,Artist>>{
            override fun onResponse(
                call: Call<Map<String, Artist>>,
                response: Response<Map<String, Artist>>
            ) {
                listArtist = response.body()?.values?.toList()!!.take(9)
                dataArtist.postValue(listArtist)
            }

            override fun onFailure(call: Call<Map<String, Artist>>, t: Throwable) {
                Log.e("getArtist","error")
            }

        })
        return dataArtist
    }

    fun getRandomSong() : MutableLiveData<Song> {
        FirebaseService.apiService.getSong().enqueue(object :Callback<Map<String,Song>>{
            override fun onResponse(
                call: Call<Map<String, Song>>,
                response: Response<Map<String, Song>>
            ) {
                if (response.isSuccessful) {
                    val songs = response.body()?.values?.toList()
                    if (songs != null) {
                        dataRandomSong.postValue(songs[9])
                    }
                }
            }

            override fun onFailure(call: Call<Map<String, Song>>, t: Throwable) {
                Log.e("getRandomSong","error")
            }

        })
        return dataRandomSong
    }
    fun getHistory() : MutableLiveData<List<History>>{
        val user = FirebaseAuth.getInstance().currentUser
        FirebaseService.apiService.getHistory().enqueue(object : Callback<Map<String,History>>{
            override fun onResponse(
                call: Call<Map<String, History>>,
                response: Response<Map<String, History>>
            ) {
                if (response.isSuccessful){
                    for (i in response.body()?.values!!.toList()){
                        if (i.uid == user?.uid ) listHistory.add(i)
                    }
                    dataHistory.postValue(listHistory.take(4))
                }
            }

            override fun onFailure(call: Call<Map<String, History>>, t: Throwable) {
                Log.e("getHistory","error")
            }
        })
        return dataHistory
    }
    fun setCurrentSong(song: Song?){
        currentSong = song
        dataCurrentSong.postValue(song)
    }
    fun getCurrentSong(): MutableLiveData<Song>{
        return dataCurrentSong
    }
    fun setIsPlaying(playing : Boolean){
        isPlaying = playing
        dataIsPlaying.postValue(isPlaying)
    }
    fun getIsPlaying(): MutableLiveData<Boolean>{
        return dataIsPlaying
    }
}