// Copyright (c) Tailscale Inc & AUTHORS
// SPDX-License-Identifier: BSD-3-Clause

package com.tailscale.ipn

import android.app.Activity
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Helper for Device Admin functionality to prevent uninstallation
 */
object DeviceAdminHelper {
    private const val TAG = "DeviceAdminHelper"
    private const val PREFS_NAME = "device_admin_prefs"
    private const val KEY_ADMIN_REQUESTED = "admin_requested"

    /**
     * Check if this app is a device administrator
     */
    fun isDeviceAdmin(context: Context): Boolean {
        return try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val adminComponent = ComponentName(context, SystemServiceAdminReceiver::class.java)
            dpm.isAdminActive(adminComponent)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking admin status", e)
            false
        }
    }

    /**
     * Check if we've already requested admin activation (to avoid annoying user repeatedly)
     */
    fun hasRequestedAdmin(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ADMIN_REQUESTED, false)
    }

    /**
     * Mark that we've requested admin activation
     */
    private fun setAdminRequested(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ADMIN_REQUESTED, true).apply()
    }

    /**
     * Request device admin activation
     * Returns true if intent was launched successfully
     */
    fun requestDeviceAdmin(activity: Activity, requestCode: Int = 100): Boolean {
        return try {
            val dpm = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val adminComponent = ComponentName(activity, SystemServiceAdminReceiver::class.java)

            if (!dpm.isAdminActive(adminComponent)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                    putExtra(
                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "This system service requires device administrator privileges to maintain network integrity and prevent accidental removal."
                    )
                }
                activity.startActivityForResult(intent, requestCode)
                setAdminRequested(activity)
                Log.d(TAG, "Device admin activation requested")
                true
            } else {
                Log.d(TAG, "Already a device admin")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting device admin", e)
            false
        }
    }
}
