package skynetbee.developers.DevEnvironment

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import skynetbee.developers.DevEnvironment.GlobalNavController.navController

//  
// 1.kt
// DevEnvironment
//
// Created by A. Nithish on 05-03-2025

@Composable
fun Project2() {
    val isTablet = isTablet()

    LastWorkedOn("2")
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

            SmartTable("* from edu_students_marks_sheet_for_primary_and_secondary_exams where examtype = 'semester'")
    }
}