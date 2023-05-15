package com.example.muzix.view.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.example.muzix.view.main.MainActivity
import com.example.muzix.R
import com.example.muzix.view.onboarding.OnboardingActivity
import com.example.muzix.viewmodel.SongViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import kotlin.random.Random

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var intent : Intent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val viewModel = ViewModelProvider(this)[SongViewModel::class.java]
        viewModel.getAllSong().observe(this){
            randomSong(it.size)
        }
        val user = FirebaseAuth.getInstance().currentUser
        intent = if (user == null){
            Intent(this, OnboardingActivity::class.java)
        } else Intent(this,MainActivity::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        },2000)
    }

    @SuppressLint("CommitPrefEdits")
    private fun randomSong(size: Int) {
        val currentDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (sharedPreferences.contains("lastDate")){
            val lastDate = sharedPreferences.getInt("lastDate",0)
            if (lastDate < currentDate){
                putValuePreferences(size,editor,currentDate)
            }
        }
        else {
            putValuePreferences(size,editor,currentDate)
        }
    }
    private fun putValuePreferences(size: Int, editor: SharedPreferences.Editor, currentDate: Int) {
        val newRandomId = Random.nextInt(size - 18)
        editor.putInt("id",newRandomId)
        editor.putInt("lastDate",currentDate)
        editor.apply()
    }
}