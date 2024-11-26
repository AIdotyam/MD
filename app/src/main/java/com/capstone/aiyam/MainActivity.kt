package com.capstone.aiyam

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.capstone.aiyam.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var doubleBackToExitPressedOnce = false
    private val handler = Handler(Looper.getMainLooper())
    private val exitRunnable = Runnable { doubleBackToExitPressedOnce = false }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

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
                R.id.alertsFragment -> {
                    onBottomNavigated(navController, R.id.alertsFragment)
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
                R.id.splashFragment -> navView.visibility = View.GONE
                R.id.signinFragment -> navView.visibility = View.GONE
                R.id.signupFragment -> navView.visibility = View.GONE
                R.id.detailFragment -> navView.visibility = View.GONE
                R.id.alertDetailFragment -> navView.visibility = View.GONE
                R.id.phoneFragment -> navView.visibility = View.GONE
                else -> navView.visibility = View.VISIBLE
            }

            navView.menu.forEach { item ->
                if (destination(destination, item.itemId)) {
                    item.isChecked = true
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTopLevelDestination(navController)) {
                    if (doubleBackToExitPressedOnce) {
                        finish()
                    } else {
                        doubleBackToExitPressedOnce = true
                        Toast.makeText(this@MainActivity, "Press again to exit", Toast.LENGTH_SHORT).show()
                        handler.postDelayed(exitRunnable, 3000)
                    }
                } else {
                    if (!navController.popBackStack()) {
                        finish()
                    }
                }
            }
        })
    }

    private fun destination(destination: NavDestination, destId: Int): Boolean {
        return destination.hierarchy.any { it.id == destId }
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
            R.id.alertsFragment,
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
}
