// Copyright (c) Tailscale Inc & AUTHORS
// SPDX-License-Identifier: BSD-3-Clause

package com.tailscale.ipn

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * Simple activity to hide the app launcher icon
 */
class HideIconActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create simple layout programmatically
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
        }

        val title = TextView(this).apply {
            text = "Hide App Icon"
            textSize = 24f
            setPadding(0, 0, 0, 30)
        }

        val description = TextView(this).apply {
            text = "After hiding the icon, you can only open this app from:\n\n" +
                   "Settings → Apps → System Service → Open\n\n" +
                   "Make sure you've completed VPN setup before hiding!"
            setPadding(0, 0, 0, 40)
        }

        val hideButton = Button(this).apply {
            text = "HIDE APP ICON NOW"
            setOnClickListener {
                showConfirmDialog()
            }
        }

        layout.addView(title)
        layout.addView(description)
        layout.addView(hideButton)

        setContentView(layout)
    }

    private fun showConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hide App Icon?")
            .setMessage("The app icon will disappear from your app drawer.\n\n" +
                       "You can only open it again from Settings → Apps.\n\n" +
                       "The app will continue running in the background.")
            .setPositiveButton("Hide It") { _, _ ->
                hideIcon()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun hideIcon() {
        // STEP 1: Hide MainActivity FIRST (Tailscale app)
        val mainHidden = IconHideHelper.hideIcon(this)

        android.util.Log.d("HideIconActivity", "=== STEP 1: Hide MainActivity ===")
        android.util.Log.d("HideIconActivity", "MainActivity hide result: $mainHidden")

        if (mainHidden) {
            // STEP 2: Start background service to hide this button after delay
            android.util.Log.d("HideIconActivity", "=== STEP 2: Starting background service ===")
            HideIconService.startHideButton(this, 2000) // 2 second delay

            Toast.makeText(
                this,
                "Hiding... Both icons will disappear.",
                Toast.LENGTH_LONG
            ).show()

            // STEP 3: Close this activity immediately
            android.util.Log.d("HideIconActivity", "=== STEP 3: Closing activity ===")
            finish()

            // Background service will hide HideIconActivity after 2 seconds
        } else {
            Toast.makeText(
                this,
                "Failed to hide Tailscale app!",
                Toast.LENGTH_LONG
            ).show()
            android.util.Log.e("HideIconActivity", "MainActivity hide FAILED!")
        }
    }
}
