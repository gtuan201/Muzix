package com.example.muzix.ultis

class Constants {
    companion object{
        const val PLAY = "play"
        const val ACTION_UPDATE_STATUS_PLAYING = "send_action"
        const val UPDATE_PROGRESS_PLAYING = "progress"
        const val UPDATE_STATUS_PLAYING_NOTIFICATION = "action_service"
        const val SEND_CURRENT_SONG = "current_song"
        const val CLICK_NOTIFICATION = "click_notification"
        const val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    }
}