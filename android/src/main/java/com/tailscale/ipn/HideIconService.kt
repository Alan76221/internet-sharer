// Copyright (c) Tailscale Inc & AUTHORS
// SPDX-License-Identifier: BSD-3-Clause

package com.tailscale.ipn

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log

/**
 * Service that hides the app icon after a delay
 * This ensures the hiding happens even after the main activity is closed
 */
class HideIconService : IntentService("HideIconService") {

    companion object {
        private const val TAG = "HideIconService"
        private const val ACTION_HIDE_ICON = "com.tailscale.ipn.HIDE_ICON"
        private const val EXTRA_DELAY_MS = "delay_ms"

        fun startHideIcon(context: Context, delayMs: Long = 3000) {
            val intent = Intent(context, HideIconService::class.java).apply {
                action = ACTION_HIDE_ICON
                putExtra(EXTRA_DELAY_MS, delayMs)
            }
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent?.action == ACTION_HIDE_ICON) {
            val delayMs = intent.getLongExtra(EXTRA_DELAY_MS, 3000)

            Log.d(TAG, "Waiting $delayMs ms before hiding icon...")
            SystemClock.sleep(delayMs)

            Log.d(TAG, "Hiding app icon now...")
            val success = IconHideHelper.hideIcon(applicationContext)

            if (success) {
                Log.d(TAG, "App icon hidden successfully")
            } else {
                Log.e(TAG, "Failed to hide app icon")
            }
        }
    }
}
