package com.example.muzix.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.muzix.model.Artist
import com.example.muzix.model.Playlist
import com.example.muzix.model.PlaylistCollection
import com.example.muzix.model.Song
import com.example.muzix.ultis.FirebaseService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlaylistViewModel : ViewModel() {
    private var dataPlaylist: MutableLiveData<List<Playlist>> = MutableLiveData()
    private var dataCollection: MutableLiveData<List<PlaylistCollection>> = MutableLiveData()
    private var listPlaylist: List<Playlist> = ArrayList()
    private var listCol: List<PlaylistCollection> = ArrayList()
    private var dataArtist : MutableLiveData<List<Artist>> = MutableLiveData()
    private var listArtist : List<Artist> = ArrayList()
    private var dataRandomSong : MutableLiveData<Song> = MutableLiveData()

//        fun getPlaylist(idCollection : String) : MutableLiveData<List<Playlist>>{
//        val call = FirebaseService.apiService.getPlaylist("idCollection",idCollection)
//        call.enqueue(object : Callback<Map<String, Playlist>> {
//            override fun onResponse(
//                call: Call<Map<String, Playlist>>,
//                response: Response<Map<String, Playlist>>
//            ) {
//                listPlaylist = response.body()?.values?.toList()!!
//                dataPlaylist.postValue(listPlaylist)
//            }
//            override fun onFailure(call: Call<Map<String, Playlist>>, t: Throwable) {
//                Log.e("getPlaylist","error")
//            }
//        })
//        return dataPlaylist
//    }
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
                    val songs = response.body()
                    if (songs != null) {
                        val randomKey = songs.keys.random()
                        val songRandom = songs[randomKey]
                        dataRandomSong.postValue(songRandom)
                    }
                }
            }

            override fun onFailure(call: Call<Map<String, Song>>, t: Throwable) {
                Log.e("getRandomSong","error")
            }

        })
        return dataRandomSong
    }
}