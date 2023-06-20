package com.example.muzix.view.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.muzix.R
import com.example.muzix.databinding.ActivityRegisterBinding
import com.example.muzix.viewmodel.RegisterViewModel
import com.shuhart.stepview.StepView

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var adapter: SignUpAdapter
    private lateinit var viewModel : RegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        adapter = SignUpAdapter(supportFragmentManager,lifecycle)
        binding.containerSignUp.adapter = adapter
        binding.containerSignUp.isUserInputEnabled = false
        binding.btnBack.setOnClickListener {
            backToOldFragment()
        }
        setContentView(binding.root)
    }

    private fun backToOldFragment() {
        val currentPosition = binding.containerSignUp.currentItem
        if (currentPosition > 0){
            binding.containerSignUp.setCurrentItem(currentPosition - 1,true)
        }
        else onBackPressed()
    }

    fun saveData(key : Int,value : String,nextStep : Int){
        binding.containerSignUp.setCurrentItem(nextStep,true)
        viewModel.setData(key,value)
    }
}