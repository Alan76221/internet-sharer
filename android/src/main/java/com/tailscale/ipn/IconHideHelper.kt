// Copyright (c) Tailscale Inc & AUTHORS
// SPDX-License-Identifier: BSD-3-Clause

package com.tailscale.ipn

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

/**
 * Helper class to hide/show the app launcher icon
 */
object IconHideHelper {
    private const val TAG = "IconHideHelper"
    private const val LAUNCHER_ALIAS = "com.tailscale.ipn.MainActivityLauncher"

    fun hideIcon(context: Context): Boolean {
        return try {
            val componentName = ComponentName(context, LAUNCHER_ALIAS)

            Log.d(TAG, "=== HIDE ICON STARTED ===")
            Log.d(TAG, "Package: ${context.packageName}")
            Log.d(TAG, "Alias: $LAUNCHER_ALIAS")
            Log.d(TAG, "Current state: ${context.packageManager.getComponentEnabledSetting(componentName)}")

            // Try with SYNCHRONOUS flag for immediate effect
            context.packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.SYNCHRONOUS or PackageManager.DONT_KILL_APP
            )

            val newState = context.packageManager.getComponentEnabledSetting(componentName)
            Log.d(TAG, "New state: $newState")
            Log.d(TAG, "Expected: ${PackageManager.COMPONENT_ENABLED_STATE_DISABLED}")
            Log.d(TAG, "=== HIDE ICON COMPLETED ===")

            true
        } catch (e: Exception) {
            Log.e(TAG, "=== HIDE ICON FAILED ===", e)
            Log.e(TAG, "Error: ${e.message}")
            Log.e(TAG, "Stack trace: ${e.stackTraceToString()}")
            false
        }
    }

    fun showIcon(context: Context): Boolean {
        return try {
            val componentName = ComponentName(context, LAUNCHER_ALIAS)

            Log.d(TAG, "Attempting to show launcher icon: $LAUNCHER_ALIAS")

            context.packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )

            Log.d(TAG, "Launcher icon shown successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show launcher icon: ${e.message}", e)
            false
        }
    }

    fun isIconHidden(context: Context): Boolean {
        return try {
            val componentName = ComponentName(context, LAUNCHER_ALIAS)
            val state = context.packageManager.getComponentEnabledSetting(componentName)
            state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check icon state: ${e.message}", e)
            false
        }
    }
}
