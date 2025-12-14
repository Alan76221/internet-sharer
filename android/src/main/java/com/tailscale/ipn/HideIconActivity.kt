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
 * Simple activity to hide BOTH app icons (main app + this Go button)
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
            text = "Go"
            textSize = 32f
            setPadding(0, 0, 0, 30)
        }

        val hideButton = Button(this).apply {
            text = "GO"
            textSize = 20f
            setOnClickListener {
                hideAllIcons()
            }
        }

        layout.addView(title)
        layout.addView(hideButton)

        setContentView(layout)
    }

    private fun hideAllIcons() {
        Toast.makeText(this, "Hiding...", Toast.LENGTH_SHORT).show()

        // Try hiding with ComponentName using proper format
        try {
            val pm = packageManager

            // Hide MainActivity with explicit ComponentName
            val mainComponent = android.content.ComponentName(
                packageName,
                "com.tailscale.ipn.MainActivity"
            )

            pm.setComponentEnabledSetting(
                mainComponent,
                android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                android.content.pm.PackageManager.DONT_KILL_APP
            )

            android.util.Log.d("HideIconActivity", "MainActivity disabled")

            // Hide GO button alias
            val goComponent = android.content.ComponentName(
                packageName,
                "com.tailscale.ipn.HideIconActivityLauncher"
            )

            pm.setComponentEnabledSetting(
                goComponent,
                android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                android.content.pm.PackageManager.DONT_KILL_APP
            )

            android.util.Log.d("HideIconActivity", "GO button disabled")

            Toast.makeText(
                this,
                "Hidden! Access via Settings → Apps",
                Toast.LENGTH_LONG
            ).show()

            // Close activity
            finish()

        } catch (e: Exception) {
            android.util.Log.e("HideIconActivity", "Hide failed", e)
            Toast.makeText(
                this,
                "Hide failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
