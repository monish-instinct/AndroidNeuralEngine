package com.skynetbee.neuralengine.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skynetbee.neuralengine.NeuralEngine

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Neural Engine
        NeuralEngine.initialize(this)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SampleApp()
                }
            }
        }
    }
}

@Composable
fun SampleApp() {
    var selectedTab by remember { mutableStateOf(0) }
    val neuralEngine = NeuralEngine.getInstance()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Neural Engine Sample App",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Tab buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Button(
                onClick = { selectedTab = 0 },
                colors = if (selectedTab == 0) {
                    ButtonDefaults.buttonColors()
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                Text("Engine Starter")
            }
            
            Button(
                onClick = { selectedTab = 1 },
                colors = if (selectedTab == 1) {
                    ButtonDefaults.buttonColors()
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                Text("Login/OTP")
            }
            
            Button(
                onClick = { selectedTab = 2 },
                colors = if (selectedTab == 2) {
                    ButtonDefaults.buttonColors()
                } else {
                    ButtonDefaults.outlinedButtonColors()
                }
            ) {
                Text("Profile")
            }
        }
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        // Content based on selected tab
        when (selectedTab) {
            0 -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Engine Starter",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        // Neural Engine Tablet Framework component
                        NeuralEngine.TabletFramework.EngineStarter()
                    }
                }
            }
            1 -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Login with OTP",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        // Neural Engine Login component
                        NeuralEngine.TabletFramework.LoginWithOTP()
                    }
                }
            }
            2 -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "My Profile",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        // Neural Engine Profile component
                        NeuralEngine.TabletFramework.MyProfile()
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Payment engine example
        Button(
            onClick = {
                val paymentEngine = neuralEngine.getPaymentEngine()
                // Use payment functionality here
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Payment Engine")
        }
        
        // View model example
        Button(
            onClick = {
                val viewModel = neuralEngine.getViewModel()
                // Use view model functionality here
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Test View Model")
        }
    }
}

