package com.capstone.aiyam.presentation.auth.splash

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.capstone.aiyam.databinding.FragmentSplashBinding
import com.capstone.aiyam.domain.model.AuthorizationResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val action = when(viewModel.getUser()) {
            is AuthorizationResponse.Success -> {
                SplashFragmentDirections.actionSplashFragmentToHomeFragment()
            }
            is AuthorizationResponse.Error -> {
                SplashFragmentDirections.actionSplashFragmentToSigninFragment()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(action)
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
