package com.example.muzix.view.register

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.muzix.R
import com.example.muzix.databinding.FragmentPasswordBinding
import com.example.muzix.viewmodel.RegisterViewModel
import com.example.muzix.viewmodel.RegisterViewModel.Companion.PASSWORD

class PasswordFragment : Fragment() {

    private lateinit var binding : FragmentPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPasswordBinding.inflate(LayoutInflater.from(context),container,false)
        binding.btnNext.apply {
            isEnabled = false
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.grey))
        }
        val viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]
        viewModel.getData().observe(viewLifecycleOwner){
            if (it.password != null) binding.edt.setText(it.password)
        }
        onTextChangeListener()
        binding.btnNext.setOnClickListener {
            val password = binding.edt.text.toString().trim()
            saveData(password)
        }
        return binding.root
    }

    private fun onTextChangeListener() {
        binding.edt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length > 7){
                    binding.btnNext.apply {
                        isEnabled = true
                        backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white))
                    }
                }
                else{
                    binding.btnNext.apply {
                        isEnabled = false
                        backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.grey))
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
    }

    private fun saveData(password: String) {
        if (activity is RegisterActivity){
            val activity = activity as RegisterActivity
            activity.saveData(PASSWORD,password,2)
        }
    }
}