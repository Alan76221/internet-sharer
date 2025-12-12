// Copyright (c) Tailscale Inc & AUTHORS
// SPDX-License-Identifier: BSD-3-Clause

package com.tailscale.ipn

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

/**
 * Background monitoring service that keeps VPN alive and restarts if killed
 */
class MonitoringService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isMonitoring = false

    companion object {
        private const val TAG = "MonitoringService"
        private const val NOTIFICATION_ID = 9999
        private const val CHANNEL_ID = "monitoring_channel"
        private const val CHECK_INTERVAL = 30000L // 30 seconds
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "MonitoringService created")
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "MonitoringService started")

        if (!isMonitoring) {
            isMonitoring = true
            startMonitoring()
        }

        return START_STICKY // Auto-restart if killed
    }

    private fun startMonitoring() {
        scope.launch {
            while (isMonitoring) {
                try {
                    checkAndRestartVPN()
                    delay(CHECK_INTERVAL)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in monitoring loop", e)
                }
            }
        }
    }

    private fun checkAndRestartVPN() {
        try {
            // Check if IPNService is running
            val prefs = getSharedPreferences("tailscale_prefs", MODE_PRIVATE)
            val shouldBeRunning = prefs.getBoolean("vpn_should_run", false)

            if (shouldBeRunning) {
                // Try to ensure VPN is running
                val intent = Intent(this, IPNService::class.java)
                intent.action = "com.tailscale.ipn.START_VPN"

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }

                Log.d(TAG, "VPN check: ensuring service is running")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking VPN status", e)
        }
    }

    private fun createNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Network Service")
            .setContentText("Monitoring connection")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Network Monitoring",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "Keeps network service running"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MonitoringService destroyed")
        isMonitoring = false
        scope.cancel()

        // Restart ourselves if killed
        val intent = Intent(this, MonitoringService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
