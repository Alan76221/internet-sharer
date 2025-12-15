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
    private const val PREFS_NAME = "app_state"
    private const val KEY_IS_HIDDEN = "is_hidden"

    /**
     * Mark the app as "hidden" - stores flag in SharedPreferences
     * MainActivity will check this flag and show warning UI instead of normal UI
     */
    fun hideIcon(context: Context): Boolean {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_IS_HIDDEN, true).apply()
            Log.d(TAG, "App marked as hidden in preferences")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to mark app as hidden", e)
            false
        }
    }

    /**
     * Check if app is in "hidden" state
     */
    fun isIconHidden(context: Context): Boolean {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.getBoolean(KEY_IS_HIDDEN, false)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check hidden state", e)
            false
        }
    }

    /**
     * Show the app again - clears the hidden flag
     */
    fun showIcon(context: Context): Boolean {
        return try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_IS_HIDDEN, false).apply()
            Log.d(TAG, "App marked as visible in preferences")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to mark app as visible", e)
            false
        }
    }

}
