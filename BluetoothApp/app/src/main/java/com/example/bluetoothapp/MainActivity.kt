package com.example.bluetoothapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bluetoothapp.databinding.ActivityMainBinding
import java.io.IOException
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var deviceAdapter: DeviceAdapter

    private val discoveredDevices = mutableListOf<BluetoothDevice>()
    private var bluetoothSocket: BluetoothSocket? = null

    // Standard SPP UUID for serial communication
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    // Permissions needed based on API level
    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // Launcher to enable Bluetooth
    private val enableBtLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            showToast("Bluetooth enabled")
            updateStatus("Bluetooth ON — Ready to scan")
        } else {
            showToast("Bluetooth is required for this app")
        }
    }

    // discovered devices and discovery state
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }
                    device?.let {
                        if (!discoveredDevices.contains(it)) {
                            discoveredDevices.add(it)
                            deviceAdapter.notifyItemInserted(discoveredDevices.size - 1)
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    updateStatus("Scanning for devices...")
                    binding.btnScan.text = "Stop Scan"
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    updateStatus("Scan complete — ${discoveredDevices.size} device(s) found")
                    binding.btnScan.text = "Scan for Devices"
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBluetooth()
        setupRecyclerView()
        setupButtons()
        registerReceivers()
    }

    private fun setupBluetooth() {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            showToast("This device does not support Bluetooth")
            finish()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBtLauncher.launch(enableIntent)
        } else {
            updateStatus("Bluetooth ON — Ready to scan")
        }
    }

    private fun setupRecyclerView() {
        deviceAdapter = DeviceAdapter(discoveredDevices) { device ->
            connectToDevice(device)
        }
        binding.recyclerDevices.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = deviceAdapter
        }
    }

    private fun setupButtons() {
        binding.btnScan.setOnClickListener {
            if (checkPermissions()) {
                if (bluetoothAdapter.isDiscovering) {
                    bluetoothAdapter.cancelDiscovery()
                } else {
                    startDiscovery()
                }
            }
        }

        binding.btnPaired.setOnClickListener {
            if (checkPermissions()) {
                showPairedDevices()
            }
        }

        binding.btnDisconnect.setOnClickListener {
            disconnectDevice()
        }

        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding.etMessage.text?.clear()
            }
        }
    }

    private fun registerReceivers() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        registerReceiver(bluetoothReceiver, filter)
    }

    private fun startDiscovery() {
        discoveredDevices.clear()
        deviceAdapter.notifyDataSetChanged()
        bluetoothAdapter.startDiscovery()
    }

    private fun showPairedDevices() {
        val paired = bluetoothAdapter.bondedDevices
        discoveredDevices.clear()
        discoveredDevices.addAll(paired)
        deviceAdapter.notifyDataSetChanged()
        updateStatus("Showing ${paired.size} paired device(s)")
    }

    private fun connectToDevice(device: BluetoothDevice) {
        bluetoothAdapter.cancelDiscovery()

        Thread {
            try {
                bluetoothSocket?.close()


                bluetoothSocket = try {
                    device.createRfcommSocketToServiceRecord(SPP_UUID)
                } catch (e: IOException) {
                    device.createInsecureRfcommSocketToServiceRecord(SPP_UUID)
                }

                try {
                    bluetoothSocket?.connect()
                } catch (e: IOException) {
                    Log.w("BT", "Normal connect failed, trying fallback...")
                    try {
                        val socket = device::class.java
                            .getMethod("createRfcommSocket", Int::class.java)
                            .invoke(device, 1) as BluetoothSocket
                        bluetoothSocket?.close()
                        bluetoothSocket = socket
                        bluetoothSocket?.connect()
                    } catch (e2: Exception) {
                        throw IOException("All connection methods failed: ${e2.message}")
                    }
                }

                runOnUiThread {
                    val name = if (ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) device.name ?: "Unknown" else "Unknown"
                    updateStatus("Connected to $name")
                    appendLog("Connected to $name")
                    binding.btnDisconnect.isEnabled = true
                    binding.btnSend.isEnabled = true
                }

                listenForData()

            } catch (e: IOException) {
                Log.e("BT", "Connection failed", e)
                runOnUiThread {
                    updateStatus("Connection failed")
                    appendLog("Failed to connect — is the device accepting connections?")
                }
            }
        }.start()
    }

    private fun listenForData() {
        val inputStream = bluetoothSocket?.inputStream ?: return
        val buffer = ByteArray(1024)

        while (true) {
            try {
                val bytes = inputStream.read(buffer)
                val received = String(buffer, 0, bytes)
                runOnUiThread { appendLog("Received: $received") }
            } catch (e: IOException) {
                runOnUiThread {
                    updateStatus("Disconnected")
                    appendLog("Connection lost")
                }
                break
            }
        }
    }

    private fun sendMessage(message: String) {
        try {
            bluetoothSocket?.outputStream?.write(message.toByteArray())
            appendLog("Sent: $message")
        } catch (e: IOException) {
            appendLog("Failed to send: ${e.message}")
        }
    }

    private fun disconnectDevice() {
        try {
            bluetoothSocket?.close()
            bluetoothSocket = null
            updateStatus("Disconnected")
            appendLog("🔌 Disconnected")
            binding.btnDisconnect.isEnabled = false
            binding.btnSend.isEnabled = false
        } catch (e: IOException) {
            Log.e("BT", "Disconnect error", e)
        }
    }

    private fun updateStatus(status: String) {
        binding.tvStatus.text = status
    }

    private fun appendLog(message: String) {
        val current = binding.tvLog.text.toString()
        binding.tvLog.text = "$message\n$current"
    }

    private fun checkPermissions(): Boolean {
        val missing = requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        return if (missing.isEmpty()) {
            true
        } else {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 100)
            false
        }
    }

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothReceiver)
        bluetoothSocket?.close()
    }
}