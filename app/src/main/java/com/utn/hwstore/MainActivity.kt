package com.utn.hwstore

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.navdrawer_header.*


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener,
    DrawerLayout.DrawerListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val navController = findNavController(R.id.nav_host_fragment)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //val appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        //navigationView.setupWithNavController(navController)
        navigationView.setNavigationItemSelectedListener(this)

        drawerLayout.addDrawerListener(this)

        val header = navigationView.getHeaderView(0)
        val headerTitle = header.findViewById<TextView>(R.id.txt_title)
        headerTitle.text = "Usuario"

        if (!checkPermissions()) {
            requestPermissions()
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        Toast.makeText(this, "onNavigationItemSelected", Toast.LENGTH_SHORT).show()
        when(menuItem.itemId) {
            R.id.nav_user_settings -> Toast.makeText(this, "User settings", Toast.LENGTH_SHORT).show()
            R.id.nav_user_logout -> Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
            R.id.nav_add_article -> Toast.makeText(this, "Agregar artículo", Toast.LENGTH_SHORT).show()
            R.id.nav_shopping_cart -> Toast.makeText(this, "Carrito de compras", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this, "Not yet implemented. ID: ${menuItem.itemId}", Toast.LENGTH_SHORT).show()
        }

        drawerLayout.closeDrawer(GravityCompat.START)

        return true
    }

    override fun onDrawerStateChanged(newState: Int) {
        //TODO("Not yet implemented")
    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        //TODO("Not yet implemented")
    }

    override fun onDrawerClosed(drawerView: View) {
        //TODO("Not yet implemented")
    }

    override fun onDrawerOpened(drawerView: View) {
        //TODO("Not yet implemented")
    }


    private fun checkPermissions(): Boolean {
        var permissionState = 0

        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        for (perm in permissions) {
            permissionState = ActivityCompat.checkSelfPermission(this, perm)
        }
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        for (perm in permissions) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, perm)
        }
        startPermissionRequest(permissions)
    }

    private fun startPermissionRequest(perm: Array<String>) {
        ActivityCompat.requestPermissions(this, perm, 0)
    }
}
