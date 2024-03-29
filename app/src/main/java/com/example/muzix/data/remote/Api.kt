package com.example.muzix.data.remote

import com.example.muzix.model.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface FirebaseService {
    companion object{
        private val gson: Gson = GsonBuilder().create()
        val apiService: FirebaseService = Retrofit.Builder()
            .baseUrl("https://muzix-b3bea-default-rtdb.firebaseio.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FirebaseService::class.java)
    }
    @PUT("artist/{artistId}.json")
    fun addArtist(@Path("artistId") artistId : String,@Body artist: Artist) : Call<Artist>

    @PUT("history/{historyId}.json")
    fun addHistory(@Path("historyId") historyId : String,@Body history: History) : Call<History>

    @GET("playlists/{id}.json")
    fun getPlaylistFromId(@Path("id") id : String) : Call<Playlist>

    @GET("history.json")
    fun getHistory(): Call<Map<String,History>>

    @GET("artist.json")
    fun getArtist(): Call<Map<String, Artist>>

    @PUT("songs/{songId}.json")
    fun addSong(@Path("songId") songId : String, @Body song: Song) : Call<Song>

    @GET("songs.json")
    fun getSong(): Call<Map<String, Song>>

    @GET("songs/{id}.json")
    fun getSongFromId(@Path("id") id: String) : Call<Song>

    @POST("collections.json")
    fun addUser(@Body collection: PlaylistCollection) : Call<PlaylistCollection>

    @PUT("playlists/{playlistId}.json")
    fun addPlaylist(@Path("playlistId") playlistId: String, @Body playlist: Playlist): Call<Playlist>

    @GET("playlists.json")
    fun getPlaylist(): Call<Map<String, Playlist>>

    @GET("users.json")
    fun getUsers(): Call<Map<String, User>>

    @GET("collections.json")
    fun getCollection(): Call<Map<String, PlaylistCollection>>

    @POST("collections.json")
    fun addCollection(@Body collection: PlaylistCollection) : Call<PlaylistCollection>

    @PUT("song_lib/{id}.json")
    fun addSongToPlaylist(@Path("id") id: String, @Body songPlaylistLib: SongPlaylistLib) : Call<SongPlaylistLib>

    @GET("song_lib.json")
    fun getSongLib() : Call<Map<String,SongPlaylistLib>>

    @PUT("favourite/{id}.json")
    fun addToFavourite(@Path("id") id: String,@Body favourite: Favourite) : Call<Favourite>

    @DELETE("favourite/{id}.json")
    fun removeFavourite(@Path("id") id: String) : Call<Favourite>
    @GET("favourite.json")
    fun getFavourite() : Call<Map<String,Favourite>>
    @GET("favourite.json")
    fun getFavouriteFromId() : Call<Map<String,Favourite>>

    @DELETE("playlists/{id}.json")
    fun removePlaylist(@Path("id") id: String) : Call<Playlist>
}