package com.capstone.aiyam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.capstone.aiyam.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())
    private val exitRunnable = Runnable { doubleBackToExitPressedOnce = false }

    private val multiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }

        if (allGranted) {
            showToast("All permissions granted")
        } else {
            val deniedPermissions = permissions.filter { !it.value }.keys
            showToast("Denied permissions: ${deniedPermissions.joinToString()}")
        }
    }

    private fun checkAllPermissions(): Boolean {
        val permissions = mutableListOf(
            CAMERA_PERMISSION, AUDIO_PERMISSION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(NOTIFICATION_PERMISSION)
        }

        return permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun requestAllPermissions() {
        val permissionsToRequest = mutableListOf(
            CAMERA_PERMISSION, AUDIO_PERMISSION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(NOTIFICATION_PERMISSION)
        }

        multiplePermissionsLauncher.launch(permissionsToRequest.toTypedArray())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.splashCondition.value
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        handleBackPress(navController)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isAuthenticated.collect {
                    val navGraph = navController.navInflater.inflate(R.navigation.navigation_root)
                    val startDestination = if (it) R.id.homeFragment else R.id.signinFragment

                    navGraph.setStartDestination(startDestination)
                    navController.graph = navGraph

                    val navView: BottomNavigationView = binding.navView
                    setNavigation(navView, navController)
                }
            }
        }

        if (!checkAllPermissions()) { requestAllPermissions() }
    }

    private fun handleBackPress(navController: NavController) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTopLevelDestination(navController)) {
                    if (doubleBackToExitPressedOnce) {
                        finish()
                    } else {
                        doubleBackToExitPressedOnce = true
                        handler.postDelayed(exitRunnable, 3000)
                        showToast("Tap again to exit")
                    }
                } else {
                    if (!navController.popBackStack()) {
                        finish()
                    }
                }
            }
        })
    }

    private fun setNavigation(navView: BottomNavigationView, navController: NavController) {
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    onBottomNavigated(navController, R.id.homeFragment)
                    true
                }
                R.id.profileFragment -> {
                    onBottomNavigated(navController, R.id.profileFragment)
                    true
                }
                R.id.historyFragment -> {
                    onBottomNavigated(navController, R.id.historyFragment)
                    true
                }
                R.id.classificationFragment -> {
                    onBottomNavigated(navController, R.id.classificationFragment)
                    true
                }
                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.signinFragment -> navView.visibility = View.GONE
                R.id.signupFragment -> navView.visibility = View.GONE
                R.id.detailFragment -> navView.visibility = View.GONE
                R.id.alertDetailFragment -> navView.visibility = View.GONE
                R.id.phoneFragment -> navView.visibility = View.GONE
                else -> navView.visibility = View.VISIBLE
            }
        }
    }

    private fun onBottomNavigated(navController: NavController, destination: Int) {
        navController.popBackStack()
        navController.navigate(destination)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp() || super.onSupportNavigateUp()
    }

    private fun isTopLevelDestination(navController: NavController): Boolean {
        val topLevelDestinations = setOf(
            R.id.homeFragment,
            R.id.profileFragment,
            R.id.historyFragment,
            R.id.classificationFragment,
            R.id.signinFragment,
            R.id.signupFragment
        )
        return navController.currentDestination?.id in topLevelDestinations
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(exitRunnable)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS

        private const val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
    }
}
