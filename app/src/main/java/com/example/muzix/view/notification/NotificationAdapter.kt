package com.example.muzix.view.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.muzix.R
import com.example.muzix.databinding.ItemNotificationBinding
import com.example.muzix.listener.ClickNotification
import com.example.muzix.model.Notification

class NotificationAdapter(private val listener : ClickNotification) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private var listNoti: List<Notification> = listOf()

    class NotificationViewHolder(val binding: ItemNotificationBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listNoti.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = listNoti[position]
        Glide.with(holder.binding.image).load(notification.image).placeholder(R.drawable.thumbnail)
            .into(holder.binding.image)
        holder.binding.tvTitle.text = notification.title
        holder.binding.tvBody.text = notification.body
        holder.binding.tvTime.text = notification.date
        holder.itemView.setOnClickListener { listener.clickNoti(notification) }
    }
    fun setData(list: List<Notification>){
        listNoti = list
    }
}