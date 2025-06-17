package com.skynetbee.neuralengine

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FourLineGraph(
    title: String,
    shareData1: Pair<String, List<Float>>,
    shareData2: Pair<String, List<Float>>,
    shareData3: Pair<String, List<Float>>,
    shareData4: Pair<String, List<Float>>,
    size: Dp = 300.dp
) {
    val isDarkMode = isSystemInDarkTheme()

    val textColor = if (isDarkMode) Color.White else Color.Black
    val axisColor = if (isDarkMode) Color(0xFFB0BEC5) else Color(0xFF455A64)

    val graphLineColors = listOf(
        Brush.linearGradient(listOf(Color(0xFFE53935), Color(0xFFE53935))),
        Brush.linearGradient(listOf(Color(0xFFFFD600), Color(0xFFFFD600))),
        Brush.linearGradient(listOf(Color(0xFF1E88E5), Color(0xFF1E88E5))),
        Brush.linearGradient(listOf(Color(0xFF43A047), Color(0xFF43A047)))
    )

    val dotColors = listOf(
        Color(0xFFE53935),
        Color(0xFFFFD600),
        Color(0xFF1E88E5),
        Color(0xFF43A047)
    )

    val allValues = shareData1.second + shareData2.second + shareData3.second + shareData4.second
    val minValue = (allValues.minOrNull() ?: 0f).let { if (it > 0) 0f else it * 1.1f }
    val maxValue = (allValues.maxOrNull() ?: 100f).let { if (it < 0) 0f else it * 1.1f }
    val range = maxValue - minValue

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = textColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Canvas(modifier = Modifier.size(size)) {
            val padding = 50f
            val graphWidth = size.toPx() - (2 * padding)
            val graphHeight = size.toPx() - (2 * padding)

            val xAxisY = size.toPx() - padding
            val yAxisX = padding

            // Draw Axes
            drawLine(
                color = axisColor,
                start = Offset(yAxisX, xAxisY - graphHeight),
                end = Offset(yAxisX, xAxisY),
                strokeWidth = 4f
            )
            drawLine(
                color = axisColor,
                start = Offset(yAxisX, xAxisY),
                end = Offset(yAxisX + graphWidth, xAxisY),
                strokeWidth = 4f
            )

            // Y-axis Labels
            val yStepCount = 5
            for (step in 0..yStepCount) {
                val percentage = (step * 100 / yStepCount).toFloat()
                val yPosition = xAxisY - ((percentage - minValue) / range) * graphHeight

                drawContext.canvas.nativeCanvas.drawText(
                    "${percentage.toInt()}%",
                    yAxisX - 45f,
                    yPosition + 15f,
                    Paint().apply {
                        color = textColor.toArgb()
                        textSize = 24f
                        textAlign = Paint.Align.RIGHT
                    }
                )

                drawLine(
                    color = axisColor.copy(alpha = 0.2f),
                    start = Offset(yAxisX, yPosition),
                    end = Offset(yAxisX + graphWidth, yPosition),
                    strokeWidth = 1f
                )
            }

            // X-axis Labels
            val maxDataPoints = listOf(
                shareData1.second.size,
                shareData2.second.size,
                shareData3.second.size,
                shareData4.second.size
            ).maxOrNull() ?: 0

            if (maxDataPoints > 0) {
                for (index in 0 until maxDataPoints) {
                    val xPosition = yAxisX + (index.toFloat() / (maxDataPoints - 1).coerceAtLeast(1)) * graphWidth

                    drawContext.canvas.nativeCanvas.drawText(
                        "${index + 1}",
                        xPosition,
                        xAxisY + 40f,
                        Paint().apply {
                            color = textColor.toArgb()
                            textSize = 24f
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }
            }

            val dataList = listOf(shareData1, shareData2, shareData3, shareData4)

            dataList.forEachIndexed { index, (_, values) ->
                val colorBrush = graphLineColors[index]
                val dotColor = dotColors[index]

                val points = values.mapIndexed { i, value ->
                    val x = yAxisX + (i.toFloat() / (values.size - 1).coerceAtLeast(1)) * graphWidth
                    val y = xAxisY - ((value - minValue) / range) * graphHeight
                    Offset(x, y)
                }

                // Shadow Line
                if (points.size >= 2) {
                    val shadowPath = Path().apply {
                        moveTo(points.first().x + 2f, points.first().y + 2f)
                        points.drop(1).forEach {
                            lineTo(it.x + 2f, it.y + 2f)
                        }
                    }
                    drawPath(
                        path = shadowPath,
                        color = Color.Black.copy(alpha = 0.15f),
                        style = Stroke(width = 6f, cap = StrokeCap.Round)
                    )

                    val linePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        points.drop(1).forEach {
                            lineTo(it.x, it.y)
                        }
                    }
                    drawPath(
                        path = linePath,
                        brush = colorBrush,
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )
                }

                // Dots with Shadows
                points.forEach { point ->
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.2f),
                        radius = 10f,
                        center = point + Offset(2f, 2f)
                    )

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(dotColor.lighten(0.4f), dotColor),
                            center = point,
                            radius = 8f
                        ),
                        radius = 8f,
                        center = point
                    )
                }
            }
        }

        // Legend
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            listOf(
                shareData1.first to dotColors[0],
                shareData2.first to dotColors[1],
                shareData3.first to dotColors[2],
                shareData4.first to dotColors[3]
            ).forEachIndexed { index, (name, color) ->
                if (index > 0) {
                    Spacer(Modifier.width(16.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(color, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = name,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
