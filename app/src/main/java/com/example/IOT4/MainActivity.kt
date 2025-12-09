package com.example.sensorlab

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAccelerometer: MaterialCardView = findViewById(R.id.btnAccelerometer)
        val btnBluetooth: MaterialCardView = findViewById(R.id.btnBluetooth)
        val btnWifi: MaterialCardView = findViewById(R.id.btnWifi)
        val btnLocation: MaterialCardView = findViewById(R.id.btnLocation)

        btnAccelerometer.setOnClickListener {
            startActivity(Intent(this, AccelerometerActivity::class.java))
        }

        btnBluetooth.setOnClickListener {
            startActivity(Intent(this, BluetoothActivity::class.java))
        }

        btnWifi.setOnClickListener {
            startActivity(Intent(this, WifiActivity::class.java))
        }

        btnLocation.setOnClickListener {
            startActivity(Intent(this, LocationActivity::class.java))
        }
    }
}