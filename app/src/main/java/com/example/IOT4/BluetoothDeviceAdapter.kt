package com.example.sensorlab

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BluetoothDeviceAdapter(
    private val devices: List<BluetoothDevice>,
    private val onItemClick: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bluetooth_device, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]
        holder.bind(device, onItemClick)
    }

    override fun getItemCount() = devices.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val deviceName: TextView = itemView.findViewById(R.id.tvDeviceName)
        private val deviceAddress: TextView = itemView.findViewById(R.id.tvDeviceAddress)

        @SuppressLint("MissingPermission")
        fun bind(device: BluetoothDevice, onItemClick: (BluetoothDevice) -> Unit) {
            deviceName.text = device.name ?: "Dispositivo Desconocido"
            deviceAddress.text = device.address
            itemView.setOnClickListener { onItemClick(device) }
        }
    }
}