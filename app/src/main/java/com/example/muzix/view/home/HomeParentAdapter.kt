package com.example.muzix.view.home

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.muzix.databinding.ItemHomeParentBinding
import com.example.muzix.model.Playlist
import com.example.muzix.model.PlaylistCollection
import com.example.muzix.ultis.FirebaseService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeParentAdapter : RecyclerView.Adapter<HomeParentAdapter.HomeParentViewHolder>() {

    private var list : List<PlaylistCollection> = ArrayList()

    class HomeParentViewHolder(val binding: ItemHomeParentBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeParentViewHolder {
        val binding = ItemHomeParentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HomeParentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeParentViewHolder, position: Int) {
        val playlistCollection = list[position]
        holder.binding.tvNameCollection.text = playlistCollection.name
        holder.binding.rcvChild.layoutManager = LinearLayoutManager(holder.itemView.context,LinearLayoutManager.HORIZONTAL,false)
        val adapter = HomeChildAdapter()
        holder.binding.rcvChild.adapter = adapter
        holder.binding.rcvChild.setHasFixedSize(true)
        setDataPlaylist(adapter, playlistCollection.id.toString())
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setDataCollection(listCol : List<PlaylistCollection>){
        list = listCol
    }

    private fun setDataPlaylist(adapter: HomeChildAdapter, id: String) {
        val listFinal : ArrayList<Playlist> = ArrayList()
        FirebaseService.apiService.getPlaylist()
            .enqueue(object : Callback<Map<String, Playlist>> {
                override fun onResponse(
                    call: Call<Map<String, Playlist>>,
                    response: Response<Map<String, Playlist>>
                ) {
                    val listPlay = response.body()?.values?.toList() ?: emptyList()
                    for (i in listPlay){
                        if (i.idCollection == id) listFinal.add(i)
                    }
                    adapter.setDataPlaylist(listFinal)
                }

                override fun onFailure(call: Call<Map<String, Playlist>>, t: Throwable) {
                    Log.e("setDataPlaylist", "Error: ${t.message}")
                }
            })
    }
}