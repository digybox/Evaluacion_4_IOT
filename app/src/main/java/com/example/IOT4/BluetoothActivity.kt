package com.example.sensorlab

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("MissingPermission")
class BluetoothActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnScan: Button
    private lateinit var deviceListAdapter: BluetoothDeviceAdapter
    private val foundDevices = mutableListOf<BluetoothDevice>()

    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val bluetoothLeScanner by lazy { bluetoothAdapter?.bluetoothLeScanner }
    private var isScanning = false
    private val handler = Handler(Looper.getMainLooper())
    private val SCAN_PERIOD: Long = 10000

    private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION)
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_ADMIN)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.rvBluetoothDevices)
        btnScan = findViewById(R.id.btnBtList)
        btnScan.text = "Escanear Dispositivos"

        deviceListAdapter = BluetoothDeviceAdapter(foundDevices) { device ->
            Toast.makeText(this, "Abriendo configuración de Bluetooth...", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            startActivity(intent)
        }
        recyclerView.adapter = deviceListAdapter

        btnScan.setOnClickListener {
            if (hasRequiredPermissions()) {
                scanLeDevice()
            } else {
                requestRequiredPermissions()
            }
        }
    }

    private fun scanLeDevice() {
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            Toast.makeText(this, "Por favor, activa el Bluetooth.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isScanning) {
            handler.postDelayed({
                isScanning = false
                bluetoothLeScanner?.stopScan(leScanCallback)
                btnScan.text = "Escanear de Nuevo"
                if (foundDevices.isEmpty()) {
                    Toast.makeText(this, "No se encontraron dispositivos.", Toast.LENGTH_SHORT).show()
                }
            }, SCAN_PERIOD)

            isScanning = true
            btnScan.text = "Escaneando..."
            foundDevices.clear()
            deviceListAdapter.notifyDataSetChanged()
            Toast.makeText(this, "Buscando dispositivos...", Toast.LENGTH_SHORT).show()
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            isScanning = false
            btnScan.text = "Escanear Dispositivos"
            bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            runOnUiThread {
                val device = result.device
                if (device.name != null && foundDevices.none { it.address == device.address }) {
                    foundDevices.add(device)
                    deviceListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestRequiredPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 1001)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            scanLeDevice()
        } else {
            Toast.makeText(this, "Los permisos son necesarios para escanear.", Toast.LENGTH_LONG).show()
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