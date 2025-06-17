package com.skynetbee.neuralengine

import android.graphics.Canvas as AndroidCanvas
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import coil.compose.AsyncImage

// A simple wrapper to differentiate the image version from the text version.
data class CenterImage(val url: String)

@Composable
fun DonutChart(
    title: String,
    percentage: List<Float>,
    sharenames: List<String>,
    centerText: String,
    size: Int = 200
) {
    val width = size
    val height = size
    val bitmap = createBitmap(width, height)
    val androidCanvas = AndroidCanvas(bitmap)

    val paint = android.graphics.Paint().apply {
        style = android.graphics.Paint.Style.FILL
        isAntiAlias = true
    }

    val padding = 20f
    val radius = (width / 2f) - padding
    val centerX = width / 2f
    val centerY = height / 2f

    val total = percentage.sum()
    var startAngle = -90f

    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow,
        Orange, LightBlue, DarkBlue, Violet
    )

    percentage.forEachIndexed { index, value ->
        val sweepAngle = (value / total) * 360f
        paint.color = colors[index % colors.size].toArgb()
        androidCanvas.drawArc(
            centerX - radius, centerY - radius,
            centerX + radius, centerY + radius,
            startAngle, sweepAngle, true, paint
        )
        startAngle += sweepAngle
    }

    // **Make the center circle transparent instead of white**
    paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
    androidCanvas.drawCircle(centerX, centerY, radius / 2, paint)
    paint.xfermode = null // Reset xfermode after drawing

    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(5.dp)
            .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(25.dp))
            .padding(10.dp) // Padding for spacing
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // **Title inside the box, adapting to theme**
            Text(
                text = title, // Title parameter
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 10.dp) // Spacing between title & graph
            )

            Row {
                Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
                    AndroidView(factory = { context ->
                        ImageView(context).apply {
                            setImageBitmap(bitmap)
                        }
                    }, modifier = Modifier.size(300.dp)
                    )

                    // **Dynamic text scaling logic inside DonutChart**
                    Text(
                        text = centerText,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = with(LocalDensity.current) {
                            val maxSize = radius * 0.3f // Maximum text size (30% of radius)
                            val minSize = 8f // Minimum text size
                            val scaleFactor = centerText.length.coerceAtMost(15) / 15f
                            maxSize * (1 - scaleFactor) + minSize
                        }.sp,
                        modifier = Modifier.width((radius * 1f).dp)
                    )
                }

                LazyColumn(modifier = Modifier.padding(top = 10.dp, end = 15.dp)) {
                    itemsIndexed(sharenames) { index, name ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .background(color = colors[index % colors.size])
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = name,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}



@Composable
fun DonutChart(
    title: String,
    percentage: List<Float>,
    sharenames: List<String>,
    centerImage: CenterImage,
    size: Int = 200
) {
    // Define overall dimensions for the donut chart
    val width = size
    val height = size
    // Create a bitmap to draw the chart on
    val bitmap = createBitmap(width, height)
    val androidCanvas = AndroidCanvas(bitmap)

    // Setup paint for drawing the arcs and inner circle
    val paint = android.graphics.Paint().apply {
        style = android.graphics.Paint.Style.FILL
        isAntiAlias = true
    }

    // Define the outer circle's radius based on a padding value
    val padding = 20f
    val outerRadius = (width / 2f) - padding
    val centerX = width / 2f
    val centerY = height / 2f

    // Calculate total and start angle for arcs
    val total = percentage.sum()
    var startAngle = -90f

    // Predefined list of colors for chart slices
    val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Yellow,
        Orange, LightBlue, DarkBlue, Violet
    )

    // Draw each arc slice based on the provided percentages
    percentage.forEachIndexed { index, value ->
        val sweepAngle = (value / total) * 360f
        paint.color = colors[index % colors.size].toArgb()
        androidCanvas.drawArc(
            centerX - outerRadius, centerY - outerRadius,
            centerX + outerRadius, centerY + outerRadius,
            startAngle, sweepAngle, true, paint
        )
        startAngle += sweepAngle
    }

    // Draw the inner circle (donut hole) that will be covered by the image.
    // The inner circle radius is half of the outer radius.
    val innerRadius = outerRadius / 2
    paint.color = Color.White.toArgb()
    androidCanvas.drawCircle(centerX, centerY, innerRadius, paint)

    // Calculate the inner circle's diameter (the area to be covered by the image)
    val innerDiameter = innerRadius * 2

    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(5.dp)
            .background(Color.Black.copy(alpha = 0.4f), shape = RoundedCornerShape(25.dp))
            .padding(10.dp) // Padding for spacing
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title inside the box, adapting to the theme
            Text(
                text = title, // Title parameter
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 10.dp) // Spacing between title & graph
            )

            Row {
                Box(modifier = Modifier.size(300.dp), contentAlignment = Alignment.Center) {
                    // Display the donut chart bitmap
                    AndroidView(factory = { context ->
                        ImageView(context).apply {
                            setImageBitmap(bitmap)
                        }
                    }, modifier = Modifier.size(200.dp)
                    )

                    // Overlay the image to completely cover the inner circle.
                    // The image will adapt to the size of the inner circle.
                    AsyncImage(
                        model = centerImage.url,
                        contentDescription = "Center Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(innerDiameter.dp)
                            .clip(CircleShape)
                    )
                }

                // Display the legend on the side
                LazyColumn(modifier = Modifier.padding(top = 10.dp, end = 15.dp)) {
                    itemsIndexed(sharenames) { index, name ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .background(color = colors[index % colors.size])
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = name,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }

}

