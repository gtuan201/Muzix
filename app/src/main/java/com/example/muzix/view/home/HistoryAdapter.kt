package com.example.muzix.view.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemHistoryBinding
import com.example.muzix.model.Playlist

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var listHistory : List<Playlist> = ArrayList()
    class HistoryViewHolder(val binding: ItemHistoryBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return HistoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listHistory.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val playlist = listHistory[position]
        Glide.with(holder.binding.imgHistory).load(playlist.thumbnail).placeholder(R.drawable.thumbnail).into(holder.binding.imgHistory)
        holder.binding.tvNameHistory.text = playlist.name
    }
    fun setData(list : List<Playlist>){
        listHistory = list
    }
}