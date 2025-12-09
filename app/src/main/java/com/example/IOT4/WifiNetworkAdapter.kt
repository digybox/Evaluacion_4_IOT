package com.example.sensorlab

import android.net.wifi.ScanResult
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WifiNetworkAdapter(
    private var networks: List<ScanResult>,
    private val onItemClick: (ScanResult) -> Unit
) : RecyclerView.Adapter<WifiNetworkAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wifi_network, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val network = networks[position]
        holder.bind(network, onItemClick)
    }

    override fun getItemCount() = networks.size

    fun updateData(newNetworks: List<ScanResult>) {
        this.networks = newNetworks
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wifiName: TextView = itemView.findViewById(R.id.tvWifiName)
        private val wifiStrength: TextView = itemView.findViewById(R.id.tvWifiStrength)

        fun bind(network: ScanResult, onItemClick: (ScanResult) -> Unit) {
            wifiName.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                network.wifiSsid.toString().removePrefix("\"").removeSuffix("\"")
            } else {
                @Suppress("DEPRECATION")
                network.SSID
            }
            if(wifiName.text.isEmpty()) wifiName.text = "Red Oculta"

            wifiStrength.text = "Intensidad: ${network.level} dBm"
            itemView.setOnClickListener { onItemClick(network) }
        }
    }
}