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
        // Hide MainActivity FIRST (Tailscale app)
        val mainHidden = IconHideHelper.hideIcon(this)

        android.util.Log.d("HideIconActivity", "MainActivity hide result: $mainHidden")

        // Small delay before hiding this activity
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // Hide HideIconActivity (this button) after MainActivity
            val hideButtonHidden = IconHideHelper.hideHideButton(this)

            android.util.Log.d("HideIconActivity", "HideButton hide result: $hideButtonHidden")

            if (mainHidden && hideButtonHidden) {
                Toast.makeText(
                    this,
                    "Both hidden! Access via Settings → Apps",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Result: Tailscale=$mainHidden Hide=$hideButtonHidden",
                    Toast.LENGTH_LONG
                ).show()
            }

            // Close activity
            finish()
        }, 500) // Wait 500ms between hiding
    }
}
