package com.capstone.aiyam.presentation.auth.splash

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.capstone.aiyam.databinding.FragmentSplashBinding
import com.capstone.aiyam.domain.model.AuthorizationResponse
import kotlinx.coroutines.launch

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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getUser().collect {
                    when(it) {
                        is AuthorizationResponse.Success -> {
                            Handler(Looper.getMainLooper()).postDelayed({
                                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
                            }, 3000)
                        }
                        is AuthorizationResponse.Error -> {
                            Handler(Looper.getMainLooper()).postDelayed({
                                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToSigninFragment())
                            }, 3000)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}