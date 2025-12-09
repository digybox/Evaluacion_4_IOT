package com.example.sensorlab

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class WifiActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnScan: Button
    private lateinit var wifiManager: WifiManager
    private lateinit var wifiNetworkAdapter: WifiNetworkAdapter
    private var scanResults = mutableListOf<ScanResult>()

    private val REQUEST_LOCATION_PERMISSION = 2001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.rvWifiNetworks)
        btnScan = findViewById(R.id.btnWifiScan)

        wifiNetworkAdapter = WifiNetworkAdapter(scanResults) { 
            Toast.makeText(this, "Abriendo configuración de WiFi...", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }
        recyclerView.adapter = wifiNetworkAdapter

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        btnScan.setOnClickListener {
            if (hasLocationPermission()) {
                startWifiScan()
            } else {
                requestLocationPermission()
            }
        }
    }

    private fun startWifiScan() {
        if (!wifiManager.isWifiEnabled) {
            Toast.makeText(this, "Por favor, activa el WiFi", Toast.LENGTH_SHORT).show()
            return
        }

        val intentFilter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiScanReceiver, intentFilter)

        scanResults.clear()
        wifiNetworkAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Escaneando redes...", Toast.LENGTH_SHORT).show()

        @Suppress("DEPRECATION")
        val success = wifiManager.startScan()
        if (!success) {
            Toast.makeText(this, "No se pudo iniciar el escaneo de WiFi.", Toast.LENGTH_SHORT).show()
            unregisterReceiver(wifiScanReceiver)
        }
    }

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                context.unregisterReceiver(this)
            } catch (e: IllegalArgumentException) {}

            @Suppress("DEPRECATION")
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            handleScanResults()
        }
    }

    private fun handleScanResults() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        @Suppress("DEPRECATION")
        val results = wifiManager.scanResults
        scanResults.clear()
        scanResults.addAll(results.filter { it.SSID.isNotEmpty() })
        wifiNetworkAdapter.notifyDataSetChanged()

        if (scanResults.isEmpty()) {
            Toast.makeText(this, "No se encontraron redes WiFi.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            startWifiScan()
        } else {
            Toast.makeText(this, "Permiso de ubicación es necesario para escanear redes WiFi.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}