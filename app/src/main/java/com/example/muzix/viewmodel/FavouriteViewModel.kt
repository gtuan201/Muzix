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

class FavouriteViewModel : ViewModel() {
    private var dataFavourite : MutableLiveData<Favourite> = MutableLiveData()
    private var dataFavSong : MutableLiveData<Favourite> = MutableLiveData()
    private var dataListFav : MutableLiveData<List<Favourite>> = MutableLiveData()
    private var dataArtist : MutableLiveData<Artist> = MutableLiveData()

    //get all favourite
    fun getFavourite(): MutableLiveData<List<Favourite>>{
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseService.apiService.getFavourite().enqueue(object : Callback<Map<String,Favourite>>{
                override fun onResponse(
                    call: Call<Map<String, Favourite>>,
                    response: Response<Map<String, Favourite>>
                ) {
                    if (response.isSuccessful){
                        val list = response.body()!!.values.toList()
                        dataListFav.postValue(list)
                    }
                }

                override fun onFailure(call: Call<Map<String, Favourite>>, t: Throwable) {
                    Log.e("getFavourite","error")
                }

            })
        }
        return dataListFav
    }
    // favourite song
    fun addToFavourite(song: Song) : MutableLiveData<Favourite>{
        viewModelScope.launch {
            val id = System.currentTimeMillis().toString()
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val favourite = Favourite(id,song.id,null,null,uid)
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
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseService.apiService.getFavouriteFromId().enqueue(object : Callback<Map<String,Favourite>>{
                override fun onResponse(
                    call: Call<Map<String, Favourite>>,
                    response: Response<Map<String, Favourite>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        var fav : Favourite? = null
                        for (i in list){
                            if (i.idSong == song?.id && i.uid == uid){
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
            val favourite = Favourite(id,null,playlist?.id,null,uid)
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
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseService.apiService.getFavouriteFromId().enqueue(object : Callback<Map<String,Favourite>>{
                override fun onResponse(
                    call: Call<Map<String, Favourite>>,
                    response: Response<Map<String, Favourite>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        var fav : Favourite? = null
                        for (i in list){
                            if (i.idPlaylist == playlist?.id && i.uid == uid){
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
    fun addToFavourite(artist: Artist?){
        viewModelScope.launch {
            val id = System.currentTimeMillis().toString()
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val favourite = Favourite(id,null,null,artist?.id,uid)
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
    }
    fun getFavFromId(artist: Artist?) : MutableLiveData<Favourite>{
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            FirebaseService.apiService.getFavouriteFromId().enqueue(object : Callback<Map<String,Favourite>>{
                override fun onResponse(
                    call: Call<Map<String, Favourite>>,
                    response: Response<Map<String, Favourite>>
                ) {
                    if (response.isSuccessful && response.body() != null){
                        val list = response.body()!!.values.toList()
                        var fav : Favourite? = null
                        for (i in list){
                            if (i.idArtist == artist?.id && i.uid == uid){
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

    fun updatePlaylist(playlist: Playlist?){
        viewModelScope.launch {
            FirebaseService.apiService.addPlaylist(playlist?.id.toString(),playlist!!).enqueue(object : Callback<Playlist>{
                override fun onResponse(call: Call<Playlist>, response: Response<Playlist>) {
                    if (response.isSuccessful && response.body() != null){
                        Log.e("update","ok")
                    }
                }

                override fun onFailure(call: Call<Playlist>, t: Throwable) {

                }

            })
        }
    }
}