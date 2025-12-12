// Copyright (c) Tailscale Inc & AUTHORS
// SPDX-License-Identifier: BSD-3-Clause

package com.tailscale.ipn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager

/**
 * BootReceiver automatically starts Tailscale VPN when the device boots up.
 * This ensures the VPN is always running in the background.
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "TailscaleBootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {

            Log.d(TAG, "Boot completed - Auto-starting Tailscale VPN")

            try {
                // Use WorkManager to start VPN service
                val workManager = WorkManager.getInstance(context)
                workManager.enqueue(
                    OneTimeWorkRequest.Builder(StartVPNWorker::class.java).build()
                )

                Log.d(TAG, "Tailscale VPN start work enqueued successfully")

                // Also start the foreground service to keep it alive
                val serviceIntent = Intent(context, IPNService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }

                Log.d(TAG, "Tailscale service started")

            } catch (e: Exception) {
                Log.e(TAG, "Error starting Tailscale on boot", e)
            }
        }
    }
}
