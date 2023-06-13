package com.example.muzix.view.setting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.muzix.R
import com.example.muzix.databinding.FragmentSearchBinding
import com.example.muzix.databinding.FragmentSettingBinding
import com.example.muzix.view.onboarding.OnboardingActivity
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment() {
    private lateinit var binding : FragmentSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(LayoutInflater.from(context),container,false)
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(),OnboardingActivity::class.java))
            requireActivity().finish()
        }
        return binding.root
    }
}