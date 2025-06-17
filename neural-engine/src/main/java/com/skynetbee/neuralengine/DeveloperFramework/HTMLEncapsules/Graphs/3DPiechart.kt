package com.skynetbee.neuralengine

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas as AndroidCanvas
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.graphics.toArgb

val Orange = Color(0xFFFFA500) // Hexadecimal for the color orange
val LightBlue = Color(0xFFADD8E6) // Hexadecimal for Light Blue
val DarkBlue = Color(0xFF00008B)  // Hexadecimal for Dark Blue
val Violet = Color(0xFFEE82EE)    // Hexadecimal for Violet


@Composable
fun ThreeDPieChart(
    title: String,
    shareNames: List<String>,
    percentage: List<Float>,
    size: Int = 300
) {
    val width = size
    val height = (size * 0.8).toInt() // Slightly shorter height for 3D effect
    val depth = (size * 0.1).toFloat() // Depth for 3D effect
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val androidCanvas = AndroidCanvas(bitmap)

    val predefinedColors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow,
         Orange,LightBlue,DarkBlue,Violet
    )

    val colors =predefinedColors.take(shareNames.size)
    var startAngle = -90f
    val total = percentage.sum()

    val rect = RectF(0f, 0f, width.toFloat(), height.toFloat() - depth)
    val baseRect = RectF(0f, depth, width.toFloat(), height.toFloat())

    // Draw the base slices (depth)
    percentage.forEachIndexed { index, percentage ->
        val sweepAngle = (percentage / total) * 360f
        val paint = Paint().apply {
            color = colors[index].toArgb()
            style = Paint.Style.FILL
        }
        androidCanvas.drawArc(baseRect, startAngle, sweepAngle, true, paint)
        startAngle += sweepAngle
    }

    // Reset start angle for the top slices
    startAngle = -90f

    // Draw the top slices
    percentage.forEachIndexed { index, percentage ->
        val sweepAngle = (percentage / total) * 360f
        val paint = Paint().apply {
            color = predefinedColors[index].toArgb()
            style = Paint.Style.FILL
        }
        androidCanvas.drawArc(rect, startAngle, sweepAngle, true, paint)
        startAngle += sweepAngle
    }

    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(5.dp)
            .background(Color.Black.copy(0.4f), shape = RoundedCornerShape(25.dp))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title inside the box, adapting to theme
            Text(
                text = title,
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 10.dp) // Spacing between title & graph
            )

            Row {
                // Display the graph
                bitmap.let { bmp ->
                    AndroidView(factory = { context ->
                        ImageView(context).apply {
                            setImageBitmap(bmp)
                        }
                    }, modifier = Modifier.size(300.dp))
                }

                // Legend for the graph
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp, end = 15.dp)
                        .height(200.dp)
                        .verticalScroll(rememberScrollState()) // Enable scrolling
                ) {
                    colors.forEachIndexed { index,color ->
                        Row {
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .background(color = color)
                            )
                            Spacer(Modifier.padding(start = 5.dp))
                            Text(
                                shareNames[index],
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp)) // Use height() instead of padding() for spacing
                    }
                }
            }
        }
    }
}

