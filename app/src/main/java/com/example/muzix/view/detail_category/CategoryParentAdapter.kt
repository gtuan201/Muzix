package com.example.muzix.view.detail_category


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.muzix.data.remote.FirebaseService
import com.example.muzix.databinding.ItemCategoryParentBinding
import com.example.muzix.model.Playlist
import com.example.muzix.model.PlaylistCollection
import com.example.muzix.ultis.OnItemClickListener
import com.example.muzix.view.home.HomeChildAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryParentAdapter(private val listener: OnItemClickListener) : RecyclerView.Adapter<CategoryParentAdapter.CategoryParentVH>() {

    private var listCollectionOfCategory : List<PlaylistCollection> = listOf()
    class CategoryParentVH(val binding : ItemCategoryParentBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryParentVH {
        val binding = ItemCategoryParentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CategoryParentVH(binding)
    }

    override fun getItemCount(): Int {
        return listCollectionOfCategory.size
    }

    override fun onBindViewHolder(holder: CategoryParentVH, position: Int) {
        val collection = listCollectionOfCategory[position]
        holder.binding.tvNameCollection.text = collection.name
        holder.binding.rcvChild.layoutManager = LinearLayoutManager(holder.itemView.context,LinearLayoutManager.HORIZONTAL,false)
        holder.binding.rcvChild.setHasFixedSize(true)
        val adapter = HomeChildAdapter(listener)
        holder.binding.rcvChild.adapter = adapter
        setDataPlaylist(adapter, collection.id.toString())
    }
    fun setDataCollection(listCol : List<PlaylistCollection>){
        listCollectionOfCategory = listCol
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