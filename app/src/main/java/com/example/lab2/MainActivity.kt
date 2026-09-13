package com.example.lab2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.lab2.ui.dashboard.DashboardFragment
import com.example.lab2.ui.home.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private val homeFragment = HomeFragment()
    private val dashboardFragment = DashboardFragment()
    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        bottomNav = findViewById(R.id.bottomNav)

        if (savedInstanceState == null) {
            switchFragment(homeFragment)
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calculator -> {
                    switchFragment(homeFragment)
                    true
                }
                R.id.nav_dashboard -> {
                    switchFragment(dashboardFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(target: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(
            R.anim.fragment_slide_in,
            R.anim.fragment_slide_out,
            R.anim.fragment_slide_in_right,
            R.anim.fragment_slide_out_left
        )

        if (activeFragment != null) {
            activeFragment?.let { transaction.hide(it) }
        }

        if (target.isAdded) {
            transaction.show(target)
        } else {
            transaction.add(R.id.fragmentContainer, target)
        }

        transaction.commit()
        activeFragment = target
    }
}
