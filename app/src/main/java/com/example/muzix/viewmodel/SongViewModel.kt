package com.example.muzix.viewmodel

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzix.R
import com.example.muzix.model.Song
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.model.Playlist
import com.example.muzix.view.library.AddSongPlaylistFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SongViewModel:ViewModel() {
    private var dataSong : MutableLiveData<ArrayList<Song>> = MutableLiveData()
    private var listSong : List<Song> = ArrayList()
    private var listSongValue : ArrayList<Song> = ArrayList()
    private var dataAllSong : MutableLiveData<List<Song>> = MutableLiveData()
    var listAllSong : MutableList<Song> = mutableListOf()
    private set
    var listSongAdded : MutableList<Song> = mutableListOf()
    private set
    private var dataSongAdded : MutableLiveData<MutableList<Song>> = MutableLiveData()
    fun getSong(idPlaylist : String): MutableLiveData<ArrayList<Song>> {
        viewModelScope.launch {
            FirebaseService.apiService.getSong().enqueue(object : Callback<Map<String,Song>>{
                override fun onResponse(
                    call: Call<Map<String, Song>>,
                    response: Response<Map<String, Song>>
                ) {
                    listSong = response.body()?.values?.toList() ?: emptyList()
                    for (i in listSong){
                        if (i.idPlaylist == idPlaylist) { listSongValue.add(i) }
                    }
                    dataSong.postValue(listSongValue)
                }
                override fun onFailure(call: Call<Map<String, Song>>, t: Throwable) {
                    Log.e("getSong","error")
                }
            })
        }
        return dataSong
    }
    fun getAllSong() : MutableLiveData<List<Song>>{
        viewModelScope.launch {
            FirebaseService.apiService.getSong().enqueue(object : Callback<Map<String,Song>>{
                override fun onResponse(
                    call: Call<Map<String, Song>>,
                    response: Response<Map<String, Song>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        listAllSong = list as MutableList<Song>
                        dataAllSong.postValue(listAllSong)
                    }
                }

                override fun onFailure(call: Call<Map<String, Song>>, t: Throwable) {
                    Log.e("getAllSong","error")
                }

            })
        }
        return dataAllSong
    }

    //add song to playlist
    fun setSongPlaylist(list : MutableList<Song>?){
        listSongAdded = list!!
    }
    fun deleteSongAll(song: Song){
        listAllSong.remove(song)
        dataAllSong.postValue(listAllSong)
    }
    fun shuffle(){
        dataAllSong.postValue(listAllSong.shuffled())
    }
    fun addSongPlaylist(song: Song){
        listSongAdded.add(song)
        dataSongAdded.postValue(listSongAdded)
    }
    fun getSongAdded(): MutableLiveData<MutableList<Song>>{
        return dataSongAdded
    }
    fun backUpSongAll(song: Song){
        listAllSong.add(song)
        dataAllSong.postValue(listAllSong)
    }
    fun removeSongAdded(song: Song,playlist: Playlist?){
        listSongAdded.remove(song)
        removeSongInPlaylist(song,playlist)
        dataSongAdded.postValue(listSongAdded)
    }

    private fun removeSongInPlaylist(song: Song, playlist: Playlist?) {
        viewModelScope.launch {
            FirebaseService.apiService.getPlaylistFromId(playlist?.id.toString()).enqueue(object :Callback<Playlist>{
                override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                    if (response.isSuccessful && response.body() != null){
                        var listSong : ArrayList<Song> = arrayListOf()
                        if (response.body()?.tracks != null) {
                            listSong = response.body()?.tracks!!
                        }
                        if (listSong.contains(song)){listSong.remove(song)}
                        playlist.apply {
                            this?.tracks = listSong
                        }
                        FirebaseService.apiService.addPlaylist(playlist?.id.toString(), playlist!!)
                            .enqueue(object : Callback<Playlist>{
                                override fun onResponse(
                                    call: Call<Playlist>,
                                    response2: Response<Playlist>
                                ) {
                                    if (response2.isSuccessful){
                                        Log.e("remove_song","success")
                                    }
                                }

                                override fun onFailure(call: Call<Playlist>, t: Throwable) {

                                }

                            })
                    }
                }

                override fun onFailure(call: Call<Playlist>, t: Throwable) {

                }

            })
        }
    }

    fun addSongToPlaylist(song: Song, playlist : Playlist?) {
        viewModelScope.launch {
            FirebaseService.apiService.getPlaylistFromId(playlist?.id.toString()).enqueue(object :Callback<Playlist>{
                override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                    if (response.isSuccessful && response.body() != null){
                        var listSong : ArrayList<Song> = arrayListOf()
                        if (response.body()?.tracks != null) {
                            listSong = response.body()?.tracks!!
                        }
                        listSong.add(song)
                        playlist.apply {
                            this?.tracks = listSong
                        }
                        FirebaseService.apiService.addPlaylist(playlist?.id.toString(), playlist!!)
                            .enqueue(object : Callback<Playlist>{
                                override fun onResponse(
                                    call: Call<Playlist>,
                                    response2: Response<Playlist>
                                ) {
                                    if (response2.isSuccessful){
                                        Log.e("add_song","success")
                                    }
                                }

                                override fun onFailure(call: Call<Playlist>, t: Throwable) {

                                }

                            })
                    }
                }

                override fun onFailure(call: Call<Playlist>, t: Throwable) {

                }

            })
        }
    }
}