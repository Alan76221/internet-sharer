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

        // Hide the main app icon
        val mainIconHidden = IconHideHelper.hideIcon(this)

        // Hide this "Go" button icon too
        val goIconHidden = IconHideHelper.hideGoIcon(this)

        if (mainIconHidden && goIconHidden) {
            Toast.makeText(
                this,
                "Hidden! Access via Settings → Apps",
                Toast.LENGTH_LONG
            ).show()

            // Close activity after hiding
            finish()
        } else {
            Toast.makeText(
                this,
                "Hide failed: main=$mainIconHidden go=$goIconHidden",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
