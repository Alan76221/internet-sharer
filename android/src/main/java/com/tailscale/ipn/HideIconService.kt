// Copyright (c) Tailscale Inc & AUTHORS
// SPDX-License-Identifier: BSD-3-Clause

package com.tailscale.ipn

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log

/**
 * Service that hides HideIconActivity after a delay
 * This allows MainActivity to be hidden first, then this service hides the hide button
 */
class HideIconService : IntentService("HideIconService") {

    companion object {
        private const val TAG = "HideIconService"
        private const val ACTION_HIDE_BUTTON = "com.tailscale.ipn.HIDE_BUTTON"
        private const val EXTRA_DELAY_MS = "delay_ms"

        fun startHideButton(context: Context, delayMs: Long = 2000) {
            val intent = Intent(context, HideIconService::class.java).apply {
                action = ACTION_HIDE_BUTTON
                putExtra(EXTRA_DELAY_MS, delayMs)
            }
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent?.action == ACTION_HIDE_BUTTON) {
            val delayMs = intent.getLongExtra(EXTRA_DELAY_MS, 2000)

            Log.d(TAG, "=== BACKGROUND SERVICE STARTED ===")
            Log.d(TAG, "Waiting $delayMs ms before hiding HideIconActivity...")
            SystemClock.sleep(delayMs)

            Log.d(TAG, "Now hiding HideIconActivity...")
            val success = IconHideHelper.hideHideButton(applicationContext)

            if (success) {
                Log.d(TAG, "HideIconActivity hidden successfully by background service!")
            } else {
                Log.e(TAG, "Failed to hide HideIconActivity from background service")
            }
            Log.d(TAG, "=== BACKGROUND SERVICE COMPLETED ===")
        }
    }
}
