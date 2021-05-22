package com.utn.hwstore

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.utn.hwstore.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.navdrawer_header.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var messageReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupNavigation()

        setupNotificationsReceiver()
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, IntentFilter("MyData"))

        val user = auth.currentUser

        user?.let {
            Toast.makeText(this, "User: ${user.displayName}", Toast.LENGTH_LONG).show()

            if (user.displayName == "Matías Silveiro") {
                subscribeToNotificationTopic("notebooks", true)
            } else {
                subscribeToNotificationTopic("notebooks", false)
            }

            val header = binding.navigationView.getHeaderView(0)
            val headerTitle = header.findViewById<TextView>(R.id.txt_title)
            headerTitle.text = user.displayName
        }

        if (!checkPermissions()) {
            requestPermissions()
        }
    }

    override fun onStop() {
        super.onStop()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = true

        binding.drawerLayout.closeDrawers()

        try {
            when(item.itemId) {
                R.id.nav_user_logout -> { logoutDialog() }
                else -> { navController.navigate(item.itemId) }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Navigation Drawer exception $e")
        }

        return true
    }

    private fun logoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Aceptar") { dialog: DialogInterface, which: Int ->
                auth.signOut()
                finish()
            }
            .setNegativeButton("Cancelar") { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
            }
            .show()
    }

    private fun setupNavigation() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val topLevelDestinations = setOf(
            R.id.HWListFragment,
        )

        appBarConfiguration = AppBarConfiguration.Builder(topLevelDestinations)
            .setDrawerLayout(binding.drawerLayout)
            .build()

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navigationView, navController)
        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupNotificationsReceiver() {
        messageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val title = intent.extras?.getString("title")
                val message = intent.extras?.getString("message")

                if(!message.isNullOrEmpty()) {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("Ok", { dialog, which -> }).show()
                }
            }
        }
    }

    private fun subscribeToNotificationTopic(topic: String, subscribe: Boolean) {
        if (subscribe) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    val msg = if (!task.isSuccessful) {
                        "Subscribed failed to topic notebooks"
                    } else {
                        "Subscribed successfully to topic notebooks"
                    }
                    Log.d(ContentValues.TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener { task ->
                    val msg = if (!task.isSuccessful) {
                        "Unsubscribed failed to topic notebooks"
                    } else {
                        "Unsubscribed successfully to topic notebooks"
                    }
                    Log.d(ContentValues.TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
        }
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
