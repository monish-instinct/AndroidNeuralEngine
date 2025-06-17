package com.skynetbee.neuralengine

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skynetbee.neuralengine.GlobalNavController.navController

//  
// 1.kt
// DevEnvironment
//
// Created by A. Nithish on 05-03-2025


@Composable
fun Project9() {
    val isTablet = isTablet()
    LastWorkedOn("9")
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(top = 50.dp, start = 20.dp)
    ){
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    navController?.navigate(if (isTablet)"my_profile_Tablet" else "my_profile_Mobile")
                }
        )
    }

    Column (modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            "Welcome to Project 9!",
            color = MaterialTheme.colorScheme.background
        )
    }
}