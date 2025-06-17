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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.MotionScene

@Composable
fun BarGraph(
    title: String,
    shareData: List<Pair<String, Float>>,
    size: Dp = 280.dp
) {
    val isDarkMode = isSystemInDarkTheme()
    val textColor = Color(0xFFBFBFBF)
    val axisColor = if (isDarkMode) Color(0xFFB0BEC5) else Color(0xFF455A64)

    val baseColors = listOf(
        Color(0xFF008EFF),
        Color(0xFF00FF0D),
        Color(0xFFFF9700),
        Color(0xFFD900FF)
    )

    val filteredData = shareData.take(4)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color =  Color(0xFFFFD277),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (shareData.size > 4) {
            Text(
                text = "Only top 4 subjects shown.",
                color = textColor,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Canvas(modifier = Modifier.size(size)) {
            val padding = 50f
            val canvasWidth = size.toPx()
            val canvasHeight = size.toPx()
            val graphWidth = canvasWidth - 2 * padding
            val graphHeight = canvasHeight - 2 * padding
            val xAxisY = canvasHeight - padding
            val yAxisX = padding

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

            // Y-axis steps
            val yStepCount = 5
            for (step in 0..yStepCount) {
                val value = 100f * step / yStepCount
                val y = xAxisY - (value / 100f) * graphHeight

                drawContext.canvas.nativeCanvas.drawText(
                    "${value.toInt()}%",
                    yAxisX - 45f,
                    y + 10f,
                    Paint().apply {
                        color = textColor.toArgb()
                        textSize = 24f
                        textAlign = Paint.Align.RIGHT
                    }
                )

                drawLine(
                    color = axisColor.copy(alpha = 0.2f),
                    start = Offset(yAxisX, y),
                    end = Offset(yAxisX + graphWidth, y),
                    strokeWidth = 1f
                )
            }

            val barWidth = if (filteredData.isNotEmpty()) graphWidth / filteredData.size * 0.6f else 0f
            val spacing = (graphWidth / filteredData.size * 0.4f) / 2f

            filteredData.forEachIndexed { index, (label, percentage) ->
                val barHeight = (percentage / 100f) * graphHeight
                val leftX = yAxisX + index * (graphWidth / filteredData.size) + spacing
                val topY = xAxisY - barHeight
                val barColor = baseColors[index % baseColors.size]

                // Main bar
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(barColor.copy(alpha = 0.9f), barColor.copy(alpha = 0.7f))
                    ),
                    topLeft = Offset(leftX, topY),
                    size = Size(width = barWidth, height = barHeight)
                )

                // Top bevel (3D look)
                val topShade = Path().apply {
                    moveTo(leftX, topY)
                    lineTo(leftX + barWidth, topY)
                    lineTo(leftX + barWidth - 10f, topY - 10f)
                    lineTo(leftX + 10f, topY - 10f)
                    close()
                }
                drawPath(path = topShade, color = barColor.copy(alpha = 0.3f))

                // Draw value above bar
                drawContext.canvas.nativeCanvas.drawText(
                    "${percentage.toInt()}%",
                    leftX + barWidth / 2,
                    topY - 20f,
                    Paint().apply {
                        color = textColor.toArgb()
                        textSize = 26f
                        textAlign = Paint.Align.CENTER
                    }
                )
//
//                // Draw wrapped label below bar
//                val wrappedLines = wrapLabelLines(label)
//                val labelX = leftX + barWidth / 2
//                val baseY = xAxisY + 20f
//
//                wrappedLines.forEachIndexed { lineIndex, line ->
//                    drawContext.canvas.nativeCanvas.drawText(
//                        line,
//                        labelX,
//                        baseY + (lineIndex * 25f),
//                        Paint().apply {
//                            color = textColor.toArgb()
//                            textSize = 34f
//                            textAlign = Paint.Align.CENTER
//                        }
//                    )
//                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Legend
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .wrapContentWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            filteredData.forEachIndexed { index, (name, _) ->
                if (index > 0) Spacer(Modifier.width(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(baseColors[index % baseColors.size], CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = name,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// Splits label into max 2 lines based on length and words
fun wrapLabelLines(label: String): List<String> {
    val words = label.split(" ")
    if (words.size <= 1) return listOf(label)

    val firstLine = mutableListOf<String>()
    val secondLine = mutableListOf<String>()

    var lineLength = 0
    for (word in words) {
        if (lineLength + word.length <= 12) {
            firstLine.add(word)
            lineLength += word.length + 1
        } else {
            secondLine.add(word)
        }
    }

    return listOf(firstLine.joinToString(" "), secondLine.joinToString(" ")).filter { it.isNotBlank() }
}
