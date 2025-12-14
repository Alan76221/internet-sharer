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

            Log.d(TAG, "Attempting to hide launcher icon: $LAUNCHER_ALIAS")

            context.packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )

            Log.d(TAG, "Launcher icon hidden successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to hide launcher icon: ${e.message}", e)
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
