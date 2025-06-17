package com.skynetbee.neuralengine

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FiveLineGraph(
    title: String,
    shareData1: Pair<String, List<Float>>,
    shareData2: Pair<String, List<Float>>,
    shareData3: Pair<String, List<Float>>,
    shareData4: Pair<String, List<Float>>,
    shareData5: Pair<String, List<Float>>,
    size: Dp = 300.dp
) {
    val isDarkMode = isSystemInDarkTheme()

    // Colors - matching MultiLineGraph exactly
    val textColor = if (isDarkMode) Color.White else Color.Black
    val axisColor = if (isDarkMode) Color(0xFFB0BEC5) else Color(0xFF455A64)
    val lineColors = listOf(
        Color(0xFFE53935),  // Red
        Color(0xFFFFD600),  // Yellow
        Color(0xFF1E88E5),  // Blue
        Color(0xFF43A047),  // Green
        Color(0xFF8E24AA)   // Purple
    )

    // Combine all values for range calculation
    val allValues = shareData1.second + shareData2.second + shareData3.second + shareData4.second + shareData5.second
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

                // Draw Axes (same as MultiLineGraph)
                drawLine(color = axisColor, start = Offset(yAxisX, xAxisY - graphHeight), end = Offset(yAxisX, xAxisY), strokeWidth = 4f)
                drawLine(color = axisColor, start = Offset(yAxisX, xAxisY), end = Offset(yAxisX + graphWidth, xAxisY), strokeWidth = 4f)

                // Y-axis Labels
                val yStepCount = 5
                for (step in 0..yStepCount) {
                    val percentage = (step * 100 / yStepCount).toFloat()
                    val yPosition = xAxisY - ((percentage - minValue) / range) * graphHeight

                    drawContext.canvas.nativeCanvas.drawText(
                        "${percentage.toInt()}%",
                        yAxisX - 45f, yPosition + 15f,
                        Paint().apply {
                            color = textColor.toArgb()
                            textSize = 24f
                            textAlign = Paint.Align.RIGHT
                        }
                    )

                    drawLine(color = axisColor.copy(alpha = 0.2f), start = Offset(yAxisX, yPosition), end = Offset(yAxisX + graphWidth, yPosition), strokeWidth = 1f)
                }

                // X-axis Labels
                val maxDataPoints = maxOf(
                    shareData1.second.size,
                    shareData2.second.size,
                    shareData3.second.size,
                    shareData4.second.size,
                    shareData5.second.size
                )
                for (index in 0 until maxDataPoints) {
                    val xPosition = yAxisX + (index.toFloat() / (maxDataPoints - 1).coerceAtLeast(1)) * graphWidth

                    drawContext.canvas.nativeCanvas.drawText(
                        "${index + 1}",
                        xPosition, xAxisY + 40f,
                        Paint().apply {
                            color = textColor.toArgb()
                            textSize = 24f
                            textAlign = Paint.Align.CENTER
                        }
                    )
                }

                // Draw Lines and Dots (with 3D effects)
                listOf(shareData1, shareData2, shareData3, shareData4, shareData5).forEachIndexed { dataIndex, (_, values) ->
                    val color = lineColors[dataIndex]
                    val points = values.mapIndexed { index, value ->
                        val x = yAxisX + (index.toFloat() / (values.size - 1).coerceAtLeast(1)) * graphWidth
                        val y = xAxisY - ((value - minValue) / range) * graphHeight
                        Offset(x, y)
                    }

                    // Shadow Line for 3D Effect (same as MultiLineGraph)
                    if (points.size >= 2) {
                        val shadowPath = Path().apply {
                            moveTo(points.first().x + 2f, points.first().y + 2f)
                            points.drop(1).forEach {
                                lineTo(it.x + 2f, it.y + 2f)
                            }
                        }
                        drawPath(shadowPath, color = Color.Black.copy(alpha = 0.15f), style = Stroke(width = 6f, cap = StrokeCap.Round))

                        val linePath = Path().apply {
                            moveTo(points.first().x, points.first().y)
                            points.drop(1).forEach {
                                lineTo(it.x, it.y)
                            }
                        }
                        drawPath(linePath, color = color, style = Stroke(width = 4f, cap = StrokeCap.Round))
                    }

                    // 3D Style Dots (same as MultiLineGraph)
                    points.forEach { point ->
                        // Shadow dot
                        drawCircle(color = Color.Black.copy(alpha = 0.2f), radius = 10f, center = point + Offset(2f, 2f))

                        // Gradient-filled dot
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(color.lighten(0.4f), color),
                                center = point,
                                radius = 8f
                            ),
                            radius = 8f,
                            center = point
                        )
                    }
                }
            }

                    // Legend (same styling as MultiLineGraph)
                    Row(
                    horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(0.8f)
                .horizontalScroll(rememberScrollState())
        ) {
            listOf(shareData1, shareData2, shareData3, shareData4, shareData5).forEachIndexed { index, (name, _) ->
                if (index > 0) Spacer(Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(16.dp).background(lineColors[index], CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = name,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
