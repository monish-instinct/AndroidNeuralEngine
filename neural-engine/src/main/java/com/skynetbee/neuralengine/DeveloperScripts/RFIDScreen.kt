package com.skynetbee.neuralengine

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT])
@Composable
fun RFIDScreen(viewModel: RfidViewModel = viewModel()) {
    val context = LocalContext.current
    var serialNumber by remember { mutableStateOf("") }
    var isBluetoothEnabled by remember { mutableStateOf(false) }

    // Check Bluetooth status on launch
    LaunchedEffect(Unit) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        isBluetoothEnabled = adapter?.isEnabled == true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Enter RFID Reader S/N", fontSize = 18.sp)
        TextField(
            value = serialNumber,
            onValueChange = { serialNumber = it },
            placeholder = { Text("e.g. 123ABC456") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    val adapter = BluetoothAdapter.getDefaultAdapter()
                    if (adapter != null && !adapter.isEnabled) {
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        if (context is Activity) {
                            context.startActivityForResult(enableBtIntent, 1001)
                        }
                    } else {
                        isBluetoothEnabled = true
                        viewModel.connectToReader(serialNumber)
                    }
                },
                enabled = serialNumber.isNotBlank()
            ) {
                Text("Connect")
            }

            Button(
                onClick = { viewModel.startInventory() },
                enabled = true
            ) {
                Text("Start Reading")
            }

            Button(
                onClick = { viewModel.stopInventory() },
                enabled = true
            ) {
                Text("Stop Reading")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("RFID Log:", fontWeight = FontWeight.Bold)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(viewModel.logMessages.reversed()) { log ->
                Text(log, fontSize = 14.sp)
            }
        }
    }
}
