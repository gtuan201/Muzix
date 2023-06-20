package com.example.muzix.view.register

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.muzix.R
import com.example.muzix.databinding.FragmentNameBinding
import com.example.muzix.view.main.MainActivity
import com.example.muzix.viewmodel.RegisterViewModel
import com.example.muzix.viewmodel.RegisterViewModel.Companion.NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class NameFragment : Fragment() {

    private lateinit var binding : FragmentNameBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNameBinding.inflate(LayoutInflater.from(context),container,false)
        auth = FirebaseAuth.getInstance()
        binding.btnNext.apply {
            isEnabled = false
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.grey))
        }
        onTextChangeListener()
        viewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]
        viewModel.getData().observe(viewLifecycleOwner){
            if (it.name != null) binding.edt.setText(it.name)
        }
        binding.btnNext.setOnClickListener {
            val name = binding.edt.text.toString().trim()
            register(name)
        }
        return binding.root
    }

    private fun register(s: String) {
        viewModel.getData().observe(viewLifecycleOwner){
            it.apply { name = s }
            auth.createUserWithEmailAndPassword(it.email.toString(),it.password.toString())
                .addOnCompleteListener(requireActivity()){task->
                    if (task.isSuccessful){
                        Toast.makeText(requireContext(),"Đăng ký thành công !",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireContext(),MainActivity::class.java))
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(s)
                            .build()
                        user?.updateProfile(profileUpdates)
                    }
                    else Toast.makeText(requireContext(),"Đăng ký thất bại !",Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun onTextChangeListener() {
        binding.edt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null){
                    binding.btnNext.apply {
                        isEnabled = true
                        backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white))
                    }
                }
                else {
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

}