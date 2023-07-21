package com.binarydzins.readera.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.binarydzins.readera.fragment.About
import com.binarydzins.readera.fragment.Dashboard
import com.binarydzins.readera.fragment.Favourites
import com.binarydzins.readera.fragment.Profile
import com.binarydzins.readera.R
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        drawerLayout = findViewById(R.id.drawerlayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.framelayout)
        navigationView = findViewById(R.id.navigationView)
        setUpToolBar()
        openDashboard()


        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when (it.itemId) {
                R.id.Dashboard -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, Dashboard())

                        .commit()
                    supportActionBar?.title = "Dashboard"
                    drawerLayout.closeDrawers()
                }

                R.id.Favourite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, Favourites())

                        .commit()
                    supportActionBar?.title = "Favourites"
                    drawerLayout.closeDrawers()
                }

                R.id.Profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, Profile())

                        .commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }

                R.id.About -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.framelayout, About())

                        .commit()

                    supportActionBar?.title = "About"
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    private fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard() {
        val fragment = Dashboard()
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.framelayout, fragment)
        transaction.commit()
        supportActionBar?.title = "Dashboard"


    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.framelayout)
        when (frag) {
            !is Dashboard -> openDashboard()

            else -> super.onBackPressed()
        }
    }
}