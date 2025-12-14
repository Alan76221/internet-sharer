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
    private const val MAIN_ACTIVITY = "com.tailscale.ipn.MainActivity"  // OLD working method!
    private const val GO_LAUNCHER_ALIAS = "com.tailscale.ipn.HideIconActivityLauncher"

    fun hideIcon(context: Context): Boolean {
        // Use OLD working method - hide MainActivity directly (not alias)
        return hideComponent(context, MAIN_ACTIVITY, "Main App")
    }

    fun hideGoIcon(context: Context): Boolean {
        // Hide the GO button alias
        return hideComponent(context, GO_LAUNCHER_ALIAS, "Go Button")
    }

    private fun hideComponent(context: Context, alias: String, name: String): Boolean {
        return try {
            val componentName = ComponentName(context, alias)

            Log.d(TAG, "=== HIDING $name ===")
            Log.d(TAG, "Alias: $alias")
            Log.d(TAG, "Before: ${context.packageManager.getComponentEnabledSetting(componentName)}")

            context.packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )

            val newState = context.packageManager.getComponentEnabledSetting(componentName)
            Log.d(TAG, "After: $newState")
            Log.d(TAG, "Success: ${newState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED}")
            Log.d(TAG, "=== $name HIDE DONE ===")

            true
        } catch (e: Exception) {
            Log.e(TAG, "=== HIDE $name FAILED ===", e)
            false
        }
    }

    fun showIcon(context: Context): Boolean {
        return try {
            val componentName = ComponentName(context, MAIN_ACTIVITY)

            Log.d(TAG, "Attempting to show launcher icon: $MAIN_ACTIVITY")

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
            val componentName = ComponentName(context, MAIN_ACTIVITY)
            val state = context.packageManager.getComponentEnabledSetting(componentName)
            state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check icon state: ${e.message}", e)
            false
        }
    }
}
