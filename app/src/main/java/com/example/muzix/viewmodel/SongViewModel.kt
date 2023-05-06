package com.example.muzix.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.muzix.model.Song
import com.example.muzix.data.remote.FirebaseService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SongViewModel:ViewModel() {
    private var dataSong : MutableLiveData<ArrayList<Song>> = MutableLiveData()
    private var listSong : List<Song> = ArrayList()
    private var listSongValue : ArrayList<Song> = ArrayList()
    fun getSong(idPlaylist : String): MutableLiveData<ArrayList<Song>> {
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
        return dataSong
    }
}