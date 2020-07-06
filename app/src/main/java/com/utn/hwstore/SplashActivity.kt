package com.utn.hwstore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT:Long = 3000 // 1 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        this.window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)

        val txtName = findViewById<TextView>(R.id.txt_name)
        val imageView = findViewById<ImageView>(R.id.img_logo)

        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in)
        imageView.startAnimation(animation)
        txtName.startAnimation(animation)

        Handler().postDelayed({
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)

    }
}
