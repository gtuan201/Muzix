package com.example.muzix.view.splash

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.muzix.view.main.MainActivity
import com.example.muzix.R
import com.example.muzix.data.local.AppDatabase
import com.example.muzix.model.Notification
import com.example.muzix.view.onboarding.OnboardingActivity
import com.example.muzix.viewmodel.SongViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var intentUser : Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val viewModel = ViewModelProvider(this)[SongViewModel::class.java]
        viewModel.getAllSong().observe(this){
            randomSong(it.size)
        }
        val user = FirebaseAuth.getInstance().currentUser
        intentUser = if (user == null){
            Intent(this, OnboardingActivity::class.java)
        } else Intent(this,MainActivity::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intentUser)
            finish()
        },2000)
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//            // Get new FCM registration token
//            val token = task.result
//            Log.e("token",token)
//        })
        saveNotificationClicked()
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.createDatabase(applicationContext).getDao()
            Log.e("list","${dao.getNotification()}")
        }
    }

    private fun saveNotificationClicked() {
        val data  = intent.extras
        CoroutineScope(Dispatchers.IO).launch {
            if (data != null){
                val title = data.getString("title")
                val body = data.getString("body")
                val image = data.getString("image")
                val id = data.getString("id_playlist")
                val dao = AppDatabase.createDatabase(applicationContext).getDao()
                dao.insertNotification(Notification(null,title, body, image,id))
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun randomSong(size: Int) {
        val currentDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (sharedPreferences.contains("lastDate")){
            val lastDate = sharedPreferences.getInt("lastDate",0)
            if (currentDate > lastDate){
                putValuePreferences(size,editor,currentDate)
            }
        }
        else {
            putValuePreferences(size,editor,currentDate)
        }
    }
    private fun putValuePreferences(size: Int, editor: SharedPreferences.Editor, currentDate: Int) {
        val newRandomId = Random.nextInt(size)
        editor.putInt("id",newRandomId)
        editor.putInt("lastDate",currentDate)
        editor.apply()
    }
}