package com.project.phycare

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.phycare.MainActivityFragments.DashboardFragment
import com.project.phycare.MainActivityFragments.ProfileFragment
import com.project.phycare.MainActivityFragments.ThoughSharingFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceWithFragment(DashboardFragment())
        val navdashboard=findViewById<LinearLayout>(R.id.nav_dashboard)
        val navthoughts=findViewById<LinearLayout>(R.id.nav_thoughts)
        val navprofile=findViewById<LinearLayout>(R.id.nav_profile)
        navdashboard.setOnClickListener{
            replaceWithFragment(DashboardFragment())
        }
        navthoughts.setOnClickListener{
            replaceWithFragment(ThoughSharingFragment())
        }

        navprofile.setOnClickListener{
            replaceWithFragment(ProfileFragment())
        }
        }
    fun replaceWithFragment(fragment: Fragment){
        val fm=supportFragmentManager
        val ft=fm.beginTransaction()
        ft.replace(R.id.framelayout,fragment)
        ft.commit()
    }
}