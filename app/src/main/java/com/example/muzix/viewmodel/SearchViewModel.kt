package com.example.muzix.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.model.Artist
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {
    private var dataSearch : MutableLiveData<String> = MutableLiveData()
    private var dataListPlaylist : MutableLiveData<List<Playlist>> = MutableLiveData()
    private var dataListSong : MutableLiveData<List<Song>> = MutableLiveData()
    private var dataListArtist : MutableLiveData<List<Artist>> = MutableLiveData()

    fun setDataSearch(s : String){
        dataSearch.value = s
    }
    fun getDataSearch() : MutableLiveData<String>{
        return dataSearch
    }
    fun getListPlaylist(key : String) : MutableLiveData<List<Playlist>>{
        viewModelScope.launch {
            val listValue = mutableListOf<Playlist>()
            FirebaseService.apiService.getPlaylist().enqueue(object : Callback<Map<String,Playlist>>{
                override fun onResponse(
                    call: Call<Map<String, Playlist>>,
                    response: Response<Map<String, Playlist>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        for (i in list){
                            if (i.name?.lowercase()?.contains(key.lowercase()) == true){
                                listValue.add(i)
                            }
                        }
                        dataListPlaylist.postValue(listValue)
                    }
                }

                override fun onFailure(call: Call<Map<String, Playlist>>, t: Throwable) {
                    Log.e("getListPlaylist","error")
                }

            })
        }
        return dataListPlaylist
    }
    fun getListSong(key : String): MutableLiveData<List<Song>>{
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
                            if (i.name?.lowercase()?.contains(key.lowercase()) == true){
                                listValue.add(i)
                            }
                        }
                        dataListSong.postValue(listValue)
                    }
                }

                override fun onFailure(call: Call<Map<String, Song>>, t: Throwable) {
                    Log.e("getListSong","error")
                }

            })
        }
        return dataListSong
    }
    fun getListArtist(key : String): MutableLiveData<List<Artist>>{
        viewModelScope.launch {
            val listValue : MutableList<Artist> = mutableListOf()
            FirebaseService.apiService.getArtist().enqueue(object : Callback<Map<String,Artist>>{
                override fun onResponse(
                    call: Call<Map<String, Artist>>,
                    response: Response<Map<String, Artist>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        for (i in list){
                            if (i.name?.lowercase()?.contains(key.lowercase()) == true){
                                listValue.add(i)
                            }
                        }
                        dataListArtist.postValue(listValue)
                    }
                }

                override fun onFailure(call: Call<Map<String, Artist>>, t: Throwable) {

                }

            })
        }
        return dataListArtist
    }
}