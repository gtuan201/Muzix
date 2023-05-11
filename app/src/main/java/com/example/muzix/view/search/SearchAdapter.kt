package com.example.muzix.view.search

import android.graphics.Color
import android.view.LayoutInflater
import android.view.OnReceiveContentListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemCategoryBinding
import com.example.muzix.model.Category
import com.example.muzix.model.Playlist

class SearchAdapter(private val listener: OnCategoryClickListener) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var listCategory: List<Category> = listOf()

    class SearchViewHolder(val binding: ItemCategoryBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listCategory.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val category = listCategory[position]
        holder.binding.tvCategory.text = category.name
//        Glide.with(holder.itemView.context).load(category.img).placeholder(R.drawable.thumbnail)
//            .into(holder.binding.imgCategory)
        holder.binding.layoutBackground.setCardBackgroundColor(Color.parseColor(category.color))
        holder.itemView.setOnClickListener{
            listener.onItemClick(category)
        }
    }

    fun setData(list: List<Category>) {
        listCategory = list
    }
    interface OnCategoryClickListener {
        fun onItemClick(category : Category)
    }
}