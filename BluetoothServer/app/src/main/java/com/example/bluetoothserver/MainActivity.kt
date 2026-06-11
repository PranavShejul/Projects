package com.example.bluetoothserver

import android.Manifest
import android.bluetooth.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.bluetoothserver.databinding.ActivityMainBinding
import java.io.IOException
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private var serverSocket: BluetoothServerSocket? = null
    private var clientSocket: BluetoothSocket? = null
    private var isRunning = false

    private val SPP_UUID: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val APP_NAME = "BluetoothApp"

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

    private val enableBtLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            updateStatus("Bluetooth ON — tap Start Server")
        } else {
            showToast("Bluetooth is required")
        }
    }

    private val discoverableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        startServer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
        setupBluetooth()
        setupButtons()
    }

    private fun setupBluetooth() {
        val manager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = manager.adapter

        val supported = packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
        Log.d("DEBUG", "Bluetooth supported: $supported")

        if (!supported) {
            showToast("Bluetooth not supported")
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            enableBtLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        } else {
            updateStatus("Bluetooth ON — tap Start Server")
        }
    }

    private fun setupButtons() {
        binding.btnStartStop.setOnClickListener {
            if (checkPermissions()) {
                if (isRunning) stopServer() else makeDiscoverable()
            }
        }

        binding.btnSend.setOnClickListener {
            val msg = binding.etMessage.text.toString()
            if (msg.isNotEmpty()) {
                sendMessage(msg)
                binding.etMessage.text?.clear()
            }
        }

        binding.btnClearLog.setOnClickListener {
            binding.tvLog.text = ""
        }
    }

    private fun makeDiscoverable() {
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        discoverableLauncher.launch(intent)
    }

    private fun startServer() {
        isRunning = true
        updateStatus("Waiting for client...")
        appendLog("Server started")

        Thread {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    APP_NAME,
                    SPP_UUID
                )

                val socket = serverSocket?.accept()

                if (socket != null && isRunning) {
                    clientSocket = socket
                    serverSocket?.close()

                    val clientName = try {
                        socket.remoteDevice.name ?: "Unknown"
                    } catch (e: SecurityException) {
                        "Unknown"
                    }

                    runOnUiThread {
                        updateStatus("Connected: $clientName")
                        appendLog("Client connected: $clientName")
                        binding.btnSend.isEnabled = true
                    }

                    receiveMessages()
                }

            } catch (e: Exception) {
                Log.e("BTServer", "Error", e)
                runOnUiThread {
                    appendLog("Error: ${e.message}")
                    resetServerUI()
                }
            }
        }.start()
    }

    private fun stopServer() {
        isRunning = false
        try {
            serverSocket?.close()
            clientSocket?.close()
        } catch (e: IOException) {
            Log.e("BTServer", "Stop error", e)
        }
        resetServerUI()
        appendLog("Server stopped")
    }

    private fun receiveMessages() {
        val inputStream = clientSocket?.inputStream ?: return
        val buffer = ByteArray(1024)

        while (isRunning) {
            try {
                val bytes = inputStream.read(buffer)
                val message = String(buffer, 0, bytes)
                runOnUiThread { appendLog("Received: $message") }
            } catch (e: IOException) {
                runOnUiThread {
                    appendLog("Client disconnected")
                    resetServerUI()
                }
                break
            }
        }
    }

    private fun sendMessage(message: String) {
        Thread {
            try {
                clientSocket?.outputStream?.write(message.toByteArray())
                runOnUiThread { appendLog("Sent: $message") }
            } catch (e: IOException) {
                runOnUiThread { appendLog("Send failed") }
            }
        }.start()
    }

    private fun resetServerUI() {
        runOnUiThread {
            isRunning = false
            binding.btnStartStop.text = "Start Server"
            binding.btnSend.isEnabled = false
            updateStatus("Server stopped")
        }
    }

    private fun updateStatus(msg: String) {
        binding.tvStatus.text = msg
    }

    private fun appendLog(msg: String) {
        binding.tvLog.text = "$msg\n${binding.tvLog.text}"
    }

    private fun checkPermissions(): Boolean {
        val missing = requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        return if (missing.isEmpty()) true
        else {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 100)
            false
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {

                showToast("Permissions granted")

                makeDiscoverable()

            } else {
                showToast("Permissions required")
            }
        }
    }

    private fun showToast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    override fun onDestroy() {
        super.onDestroy()
        stopServer()
    }
}