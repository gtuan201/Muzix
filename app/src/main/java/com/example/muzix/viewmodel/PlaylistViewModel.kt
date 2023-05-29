package com.example.muzix.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzix.model.*
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.ultis.HistoryComparator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class PlaylistViewModel : ViewModel() {
    private var dataPlaylist: MutableLiveData<Playlist> = MutableLiveData()
    private var dataCollection: MutableLiveData<List<PlaylistCollection>> = MutableLiveData()
    private var listCol: List<PlaylistCollection> = ArrayList()
    private var dataArtist: MutableLiveData<List<Artist>> = MutableLiveData()
    private var listArtist: List<Artist> = ArrayList()
    private var dataRandomSong: MutableLiveData<Song> = MutableLiveData()
    private var dataCurrentSong: MutableLiveData<Song> = MutableLiveData()
    private var dataRandomPlaylist : MutableLiveData<List<Playlist>> = MutableLiveData()
    private var dataPlaylistHistory : MutableLiveData<MutableList<Playlist>> = MutableLiveData()
    var currentSong: Song? = null
        private set
    private var dataIsPlaying: MutableLiveData<Boolean> = MutableLiveData()
    var isPlaying: Boolean = false
        private set

    fun getPlaylist(idPlaylist: String): MutableLiveData<Playlist> {
        viewModelScope.launch {
            val call = FirebaseService.apiService.getPlaylistFromId(idPlaylist)
            call.enqueue(object : Callback<Playlist> {
                override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                    if (response.isSuccessful && response.body() != null) {
                        val playlist = response.body()
                        dataPlaylist.postValue(playlist)
                    }
                }

                override fun onFailure(call: Call<Playlist>, t: Throwable) {
                    Log.e("getPlaylist", "error")
                }

            })
        }
        return dataPlaylist
    }

    fun getRandomPlaylist() : MutableLiveData<List<Playlist>> {
        viewModelScope.launch {
            FirebaseService.apiService.getPlaylist().enqueue(object : Callback<Map<String,Playlist>>{
                override fun onResponse(
                    call: Call<Map<String, Playlist>>,
                    response: Response<Map<String, Playlist>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList().shuffled().take(6)
                        dataRandomPlaylist.postValue(list)
                    }
                }

                override fun onFailure(call: Call<Map<String, Playlist>>, t: Throwable) {
                   Log.e("getRandomPlaylist","error")
                }

            })
        }
        return dataRandomPlaylist
    }

    fun getCollection(): MutableLiveData<List<PlaylistCollection>> {
        viewModelScope.launch {
            FirebaseService.apiService.getCollection()
                .enqueue(object : Callback<Map<String, PlaylistCollection>> {
                    override fun onResponse(
                        call: Call<Map<String, PlaylistCollection>>,
                        response: Response<Map<String, PlaylistCollection>>
                    ) {
                        if (response.isSuccessful) {
                            listCol = response.body()?.values?.toList()!!
                            dataCollection.postValue(listCol)
                        }
                    }

                    override fun onFailure(call: Call<Map<String, PlaylistCollection>>, t: Throwable) {
                        Log.e(TAG, "Failed to get collections: ${t.message}")
                    }

                })
        }
        return dataCollection
    }
    fun getTopArtist(): MutableLiveData<List<Artist>> {
        viewModelScope.launch {
            FirebaseService.apiService.getArtist().enqueue(object : Callback<Map<String, Artist>> {
                override fun onResponse(
                    call: Call<Map<String, Artist>>,
                    response: Response<Map<String, Artist>>
                ) {
                    listArtist = response.body()?.values?.toList()!!.take(9)
                    dataArtist.postValue(listArtist)
                }

                override fun onFailure(call: Call<Map<String, Artist>>, t: Throwable) {
                    Log.e("getArtist", "error")
                }

            })
        }
        return dataArtist
    }

    fun getRandomSong(position : Int): MutableLiveData<Song> {
        viewModelScope.launch {
            FirebaseService.apiService.getSong().enqueue(object : Callback<Map<String, Song>> {
                override fun onResponse(
                    call: Call<Map<String, Song>>,
                    response: Response<Map<String, Song>>
                ) {
                    if (response.isSuccessful) {
                        val songs = response.body()?.values?.toList()
                        if (songs != null) {
                            dataRandomSong.postValue(songs[position])
                        }
                    }
                }

                override fun onFailure(call: Call<Map<String, Song>>, t: Throwable) {
                    Log.e("getRandomSong", "error")
                }

            })
        }
        return dataRandomSong
    }

    fun getPlaylistHistory() : MutableLiveData<MutableList<Playlist>>{
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val listPlaylistHistory : MutableList<Playlist> = mutableListOf()
            FirebaseService.apiService.getHistory().enqueue(object : Callback<Map<String,History>>{
                override fun onResponse(
                    call: Call<Map<String, History>>,
                    response: Response<Map<String, History>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val listHistory = response.body()!!.values.toList()
                        Collections.sort(listHistory,HistoryComparator())
                        FirebaseService.apiService.getPlaylist().enqueue(object : Callback<Map<String,Playlist>>{
                            override fun onResponse(
                                call: Call<Map<String, Playlist>>,
                                response: Response<Map<String, Playlist>>
                            ) {
                                if (response.isSuccessful && response.body() != null){
                                    val listPlaylist = response.body()!!.values.toList()
                                    for (i in listHistory){
                                        for (j in listPlaylist){
                                            if (i.idPlaylist == j.id && i.uid == uid){
                                                listPlaylistHistory.add(j)
                                            }
                                        }
                                    }
                                    dataPlaylistHistory.postValue(listPlaylistHistory)
                                }
                            }

                            override fun onFailure(call: Call<Map<String, Playlist>>, t: Throwable) {

                            }

                        })
                    }
                }

                override fun onFailure(call: Call<Map<String, History>>, t: Throwable) {

                }

            })
        }
        return dataPlaylistHistory
    }

    fun addHistoryPlaylist(playlist: Playlist){
        viewModelScope.launch {
            val id = System.currentTimeMillis().toString()
            val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val history = History(id,playlist.id,uid)
            FirebaseService.apiService.addHistory(playlist.id.toString(),history).enqueue(object :Callback<History>{
                override fun onResponse(call: Call<History>, response: Response<History>) {
                    if (response.isSuccessful && response.body() != null){
                        val list = dataPlaylistHistory.value?.toMutableList()
                        list?.add(playlist)
                        dataPlaylistHistory.value = list
                    }
                }

                override fun onFailure(call: Call<History>, t: Throwable) {

                }

            })
        }
    }

    fun setCurrentSong(song: Song?) {
        currentSong = song
        dataCurrentSong.postValue(song)
    }

    fun getCurrentSong(): MutableLiveData<Song> {
        return dataCurrentSong
    }

    fun setIsPlaying(playing: Boolean) {
        isPlaying = playing
        dataIsPlaying.postValue(isPlaying)
    }

    fun getIsPlaying(): MutableLiveData<Boolean> {
        return dataIsPlaying
    }
}