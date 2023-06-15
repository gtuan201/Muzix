package com.example.muzix.listener

import com.example.muzix.model.Notification

interface ClickNotification {
    fun clickNoti(notification: Notification)
}