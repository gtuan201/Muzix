package com.example.muzix.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.model.Favourite
import com.example.muzix.model.Playlist
import com.example.muzix.model.Song
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavouriteViewModel : ViewModel() {
    private var dataFavourite : MutableLiveData<Favourite> = MutableLiveData()
    private var dataFavSong : MutableLiveData<Favourite> = MutableLiveData()

    // favourite song
    fun addToFavourite(song: Song) : MutableLiveData<Favourite>{
        viewModelScope.launch {
            val id = System.currentTimeMillis().toString()
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val favourite = Favourite(id,song.id,null,uid)
            FirebaseService.apiService.addToFavourite(id,favourite).enqueue(object : Callback<Favourite>{
                override fun onResponse(call: Call<Favourite>, response: Response<Favourite>) {
                    if (response.isSuccessful && response.body() != null){
                        val favouriteData = response.body()
                        dataFavSong.postValue(favouriteData)
                    }
                }

                override fun onFailure(call: Call<Favourite>, t: Throwable) {
                    Log.e("addToFavourite","error")
                }

            })
        }
        return dataFavSong
    }

    fun getFavFromId(song: Song?) : MutableLiveData<Favourite>{
        viewModelScope.launch {
            FirebaseService.apiService.getFavouriteFromId().enqueue(object : Callback<Map<String,Favourite>>{
                override fun onResponse(
                    call: Call<Map<String, Favourite>>,
                    response: Response<Map<String, Favourite>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        var fav : Favourite? = null
                        for (i in list){
                            if (i.idSong == song?.id){
                                fav = i
                                break
                            }
                        }
                        dataFavSong.postValue(fav)
                    }
                }

                override fun onFailure(call: Call<Map<String, Favourite>>, t: Throwable) {
                    Log.e("getFavFromId","error")
                }

            })
        }
        return dataFavSong
    }
    fun removeFavouriteSong(favourite: Favourite): MutableLiveData<Favourite>{
        viewModelScope.launch {
            FirebaseService.apiService.removeFavourite(favourite.id.toString())
                .enqueue(object : Callback<Favourite>{
                    override fun onResponse(call: Call<Favourite>, response: Response<Favourite>) {
                        if (response.isSuccessful){
                            dataFavSong.postValue(null)
                        }
                    }

                    override fun onFailure(call: Call<Favourite>, t: Throwable) {
                        Log.e("removeFavourite","error")
                    }
                })
        }
        return dataFavSong
    }

    // favourite playlist
    fun addToFavourite(playlist: Playlist?): MutableLiveData<Favourite>{
        viewModelScope.launch {
            val id = System.currentTimeMillis().toString()
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val favourite = Favourite(id,null,playlist?.id,uid)
            FirebaseService.apiService.addToFavourite(id,favourite).enqueue(object : Callback<Favourite>{
                override fun onResponse(call: Call<Favourite>, response: Response<Favourite>) {
                    if (response.isSuccessful && response.body() != null){
                        val favouriteData = response.body()
                        dataFavourite.postValue(favouriteData)
                    }
                }

                override fun onFailure(call: Call<Favourite>, t: Throwable) {
                    Log.e("addToFavourite","error")
                }

            })
        }
        return dataFavourite
    }

    fun getFavFromId(playlist: Playlist?) : MutableLiveData<Favourite>{
        viewModelScope.launch {
            FirebaseService.apiService.getFavouriteFromId().enqueue(object : Callback<Map<String,Favourite>>{
                override fun onResponse(
                    call: Call<Map<String, Favourite>>,
                    response: Response<Map<String, Favourite>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        var fav : Favourite? = null
                        for (i in list){
                            if (i.idPlaylist == playlist?.id){
                                fav = i
                                break
                            }
                        }
                        dataFavourite.postValue(fav)
                    }
                }

                override fun onFailure(call: Call<Map<String, Favourite>>, t: Throwable) {
                    Log.e("getFavFromId","error")
                }

            })
        }
        return dataFavourite
    }

    fun removeFavourite(favourite: Favourite): MutableLiveData<Favourite>{
        viewModelScope.launch {
                FirebaseService.apiService.removeFavourite(favourite.id.toString())
                    .enqueue(object : Callback<Favourite>{
                        override fun onResponse(call: Call<Favourite>, response: Response<Favourite>) {
                            if (response.isSuccessful){
                                dataFavourite.postValue(null)
                            }
                        }

                        override fun onFailure(call: Call<Favourite>, t: Throwable) {
                            Log.e("removeFavourite","error")
                        }
                    })
            }
        return dataFavourite
    }
}