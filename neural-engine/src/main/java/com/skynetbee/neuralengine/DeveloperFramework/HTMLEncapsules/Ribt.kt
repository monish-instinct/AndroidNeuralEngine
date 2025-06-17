package com.skynetbee.neuralengine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Ribt(label: String, textState: MutableState<String>) {
    Box(
        modifier = Modifier.wrapContentSize( ),
        contentAlignment = Alignment.Center
    ) {

        // Floating Label Text Fields (Dynamic Labels)
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            FloatingLabelTextField(labelText = label, value = textState)
            Spacer(modifier = Modifier.height(24.dp)) // Space between fields

        }
    }
}

@Composable
fun FloatingLabelTextField(labelText: String, value: MutableState<String>) {
    OutlinedTextField(
        value = value.value, // Use the passed state
        onValueChange = { value.value = it }, // Update state
        label = {
            Text(
                text = labelText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.8f)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.White.copy(alpha = 0.15f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
            focusedIndicatorColor = Color.White.copy(alpha = 0.15f),
            unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
            focusedLabelColor = Color.White.copy(alpha = 0.9f),
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(30.dp), // Rounded corners
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        singleLine = true,
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    )
}

//val userLabels = listOf("Name", "Address")
//Ribt(labels = userLabels)
