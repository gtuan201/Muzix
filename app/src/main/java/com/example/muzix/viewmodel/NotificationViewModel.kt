package com.example.muzix.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzix.data.local.AppDatabase
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.model.Notification
import com.example.muzix.model.Playlist
import com.example.muzix.ultis.CustomComparator
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NotificationViewModel : ViewModel() {
    private var dataNotification : MutableLiveData<List<Notification>> = MutableLiveData()
    private var dataPlaylist : MutableLiveData<Playlist> = MutableLiveData()
    fun getAllNotification(context: Context) : MutableLiveData<List<Notification>>{
        val dao = AppDatabase.createDatabase(context).getDao()
        dataNotification.value = dao.getNotification()
        return dataNotification
    }
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
    @SuppressLint("SimpleDateFormat")
    fun deleteOldNotification(context: Context){
        val dao = AppDatabase.createDatabase(context).getDao()
        val list = dao.getNotification()
        Collections.sort(list,CustomComparator())
        if (list.size > 10){
            dao.deleteItem(list[0])
        }
    }
}