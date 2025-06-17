package com.skynetbee.neuralengine


import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MultiLineGraph(
    title: String,
    shareData: List<Pair<String, List<Float>>>,
    size: Dp = 300.dp
) {
    val isDarkMode = isSystemInDarkTheme()

    // Colors
    val textColor = if (isDarkMode) Color.White else Color.Black
    val axisColor = if (isDarkMode) Color(0xFFB0BEC5) else Color(0xFF455A64)

    // Base line/dot colors
    val baseColors = listOf(
        Color(0xFFE53935), Color(0xFFFFD600), Color(0xFF1E88E5),
        Color(0xFF43A047), Color(0xFF8E24AA), Color(0xFFFB8C00),
        Color(0xFF00ACC1), Color(0xFF8D6E63)
    )
    val lineColors = List(shareData.size) { baseColors[it % baseColors.size] }

    // Value range
    val allValues = shareData.flatMap { it.second }
    val minValue = (allValues.minOrNull() ?: 0f).let { if (it > 0) 0f else it * 1.1f }
    val maxValue = (allValues.maxOrNull() ?: 100f).let { if (it < 0) 0f else it * 1.1f }
    val range = maxValue - minValue

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color(0xFFFFD277),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 30.dp)
        )
        Canvas(modifier = Modifier.size(size)) {
            val padding = 50f
            val graphWidth = size.toPx() - (2 * padding)
            val graphHeight = size.toPx() - (2 * padding)

            val xAxisY = size.toPx() - padding
            val yAxisX = padding

            // Draw Axes
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
            val maxDataPoints = shareData.maxOfOrNull { it.second.size } ?: 0
            if (maxDataPoints > 0) {
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
            }

            // Draw Lines and Dots
            shareData.forEachIndexed { dataIndex, (_, values) ->
                val color = lineColors[dataIndex]
                val points = values.mapIndexed { index, value ->
                    val x = yAxisX + (index.toFloat() / (values.size - 1).coerceAtLeast(1)) * graphWidth
                    val y = xAxisY - ((value - minValue) / range) * graphHeight
                    Offset(x, y)
                }

                // Shadow Line for 3D Effect
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

                // 3D Style Dots
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

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.padding(top = 8.dp).fillMaxWidth(0.8f).horizontalScroll(
        rememberScrollState()
        )) {
            shareData.forEachIndexed { index, (name, _) ->
                if (index > 0) Spacer(Modifier.width(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(16.dp).background(lineColors[index], CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Text(name, color = textColor, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}
fun Color.lighten(factor: Float): Color {
    return Color(
        red = (red + (1 - red) * factor).coerceIn(0f, 1f),
        green = (green + (1 - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1 - blue) * factor).coerceIn(0f, 1f),
        alpha = alpha
    )
}








