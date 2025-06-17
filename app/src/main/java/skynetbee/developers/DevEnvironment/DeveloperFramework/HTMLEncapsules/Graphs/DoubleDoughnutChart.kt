package skynetbee.developers.DevEnvironment

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun DoubleDoughnutChart(
    title: String,
    shareNames: List<String>,
    percentages: List<Float>,
    size: Dp = 270.dp,
    centerContent: String? = null
) {
    var activeTooltipIndex by remember { mutableStateOf(-1) }

    Box {
        DoubleDoughnutChartInternal(
            title = title,
            shareNames = shareNames,
            percentages = percentages,
            size = size,
            centerText = centerContent,
            centerImage = null,
            activeTooltipIndex = activeTooltipIndex,
            onInfoIconClick = { index ->
                Log.d("DoughnutChart", "Before click - activeTooltipIndex: $activeTooltipIndex, clicked index: $index")
                activeTooltipIndex = if (activeTooltipIndex == index) -1 else index
                Log.d("DoughnutChart", "After click - activeTooltipIndex: $activeTooltipIndex")
            }
        )
    }
}

@Composable
fun DoubleDoughnutChart(
    title: String,
    shareNames: List<String>,
    percentages: List<Float>,
    size: Dp = 270.dp,
    centerContent: Painter? = null
) {
    var activeTooltipIndex by remember { mutableStateOf(-1) }

    Box {
        DoubleDoughnutChartInternal(
            title = title,
            shareNames = shareNames,
            percentages = percentages,
            size = size,
            centerText = null,
            centerImage = centerContent,
            activeTooltipIndex = activeTooltipIndex,
            onInfoIconClick = { index ->
                Log.d("DoughnutChart", "Before click - activeTooltipIndex: $activeTooltipIndex, clicked index: $index")
                activeTooltipIndex = if (activeTooltipIndex == index) -1 else index
                Log.d("DoughnutChart", "After click - activeTooltipIndex: $activeTooltipIndex")
            }
        )
    }
}

@Composable
fun DoubleDoughnutChart(
    title: String,
    shareNames: List<String>,
    percentages: List<Float>,
    size: Dp = 270.dp,
    centerContent: Bitmap?
) {
    val painter = centerContent?.let { remember(it) { BitmapPainter(it.asImageBitmap()) } }

    var activeTooltipIndex by remember { mutableStateOf(-1) }

    Box {
        DoubleDoughnutChartInternal(
            title = title,
            shareNames = shareNames,
            percentages = percentages,
            size = size,
            centerText = null,
            centerImage = painter,
            activeTooltipIndex = activeTooltipIndex,
            onInfoIconClick = { index ->
                activeTooltipIndex = if (activeTooltipIndex == index) -1 else index
            }
        )
    }
}


@Composable
private fun DoubleDoughnutChartInternal(
    title: String,
    shareNames: List<String>,
    percentages: List<Float>,
    size: Dp = 270.dp,
    centerText: String? = null,
    centerImage: Painter? = null,
    activeTooltipIndex: Int = -1,
    onInfoIconClick: (Int) -> Unit = { _ -> }
) {
    require(shareNames.size == percentages.size) { "shareNames and percentages must have the same size" }

    val baseColors = listOf(
        Color(0xFF4CAF50),  // Green
        Color(0xFF2196F3),  // Blue
        Color(0xFFFFC107),  // Amber
        Color(0xFFE91E63),  // Pink
        Color(0xFF9C27B0)   // Purple
    )

    val titleColor = Color(0xFFFFD277)
    val shareColor = Color(0xFFBFBFBF)

    val sumPercent = percentages.sum()
    val normalizedPercentages = if (sumPercent != 100f && sumPercent != 0f) {
        percentages.map { it / sumPercent * 100f }
    } else {
        percentages
    }

    // Track icon positions and their corresponding indices
    val iconPositions = remember { mutableStateMapOf<Int, Offset>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = (size.value * 0.1).sp
            ),
            fontWeight = FontWeight.Bold,
            color = titleColor,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size)
        ) {
            val density = LocalDensity.current

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            iconPositions.forEach { (index, position) ->
                                val iconSize = size.toPx() * 0.05f
                                val distance = (tapOffset - position).getDistance()
                                if (distance <= iconSize * 1.5f) {
                                    onInfoIconClick(index)
                                    return@detectTapGestures
                                }
                            }
                        }
                    }
            ) {
                val canvasWidth = size.toPx()
                val canvasHeight = size.toPx()
                val centerX = canvasWidth / 2
                val centerY = canvasHeight / 2
                val minDimension = min(canvasWidth, canvasHeight)

                val outerStrokeWidth = minDimension / 6f
                val innerStrokeWidth = minDimension / 9f
                val outerRadius = minDimension * 0.45f
                val innerRadius = outerRadius * 0.7f

                var startAngle = -90f
                iconPositions.clear()

                // Draw inner semi-transparent layer
                normalizedPercentages.forEachIndexed { i, percent ->
                    val sweep = (percent / 100f) * 360f
                    val color = baseColors[i % baseColors.size].copy(alpha = 0.4f)

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(innerStrokeWidth),
                        topLeft = Offset(centerX - innerRadius, centerY - innerRadius),
                        size = Size(innerRadius * 2, innerRadius * 2)
                    )

                    // Draw percentage
                    val percentageTextSize = minDimension * 0.04f
                    val midAngle = startAngle + sweep / 2
                    val textRadius = innerRadius - innerStrokeWidth * 0.1f
                    val x = textRadius * cos(midAngle * PI.toFloat() / 180f)
                    val y = textRadius * sin(midAngle * PI.toFloat() / 180f)

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            this.color = shareColor.toArgb()
                            textSize = percentageTextSize
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        val text = "${percent.toInt()}%"
                        val textBounds = Rect()
                        paint.getTextBounds(text, 0, text.length, textBounds)
                        val textOffset = (textBounds.top + textBounds.bottom) / 2f

                        drawText(
                            text,
                            centerX + x,
                            centerY + y - textOffset,
                            paint
                        )
                    }
                    startAngle += sweep
                }

                // Draw outer fully colored layer
                startAngle = -90f
                normalizedPercentages.forEachIndexed { i, percent ->
                    val sweep = (percent / 100f) * 360f
                    val color = baseColors[i % baseColors.size]

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = Stroke(outerStrokeWidth),
                        topLeft = Offset(centerX - outerRadius, centerY - outerRadius),
                        size = Size(outerRadius * 2, outerRadius * 2)
                    )

                    if (i < shareNames.size) {
                        val iconPosition = drawOuterLabel(
                            canvas = drawContext.canvas.nativeCanvas,
                            center = Offset(centerX, centerY),
                            outerRadius = outerRadius,
                            outerStrokeWidth = outerStrokeWidth,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            label = shareNames[i],
                            color = color,
                            textSize = minDimension * 0.06f,
                            minDimension = minDimension,
                            showTooltip = activeTooltipIndex == i
                        )
                        if (iconPosition != null) {
                            iconPositions[i] = iconPosition
                        }
                    }
                    startAngle += sweep
                }
            }

            // Draw tooltips for active icons
            iconPositions.forEach { (index, position) ->
                if (activeTooltipIndex == index) {
                    Box(
                        modifier = Modifier
                            .offset(
                                x = with(density) { position.x.toDp() - 48.dp },
                                y = with(density) { position.y.toDp() - 48.dp }
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .zIndex(1f)
                    ) {
                        Text(
                            text = shareNames[index],
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFFFD277),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(size * 0.35f)
            ) {
                when {
                    centerImage != null -> {
                        Image(
                            painter = centerImage,
                            contentDescription = "Center image",
                            modifier = Modifier
                                .fillMaxSize(0.85f)
                                .clip(CircleShape)
                        )
                    }
                    centerText != null -> {
                        Text(
                            text = centerText,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = (size.value * 0.08).sp
                            ),
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private fun drawOuterLabel(
    canvas: android.graphics.Canvas,
    center: Offset,
    outerRadius: Float,
    outerStrokeWidth: Float,
    startAngle: Float,
    sweepAngle: Float,
    label: String,
    color: Color,
    textSize: Float,
    minDimension: Float,
    showTooltip: Boolean = false
): Offset? {
    val textPaint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
        this.color = if (color.isLightColor()) Color.Black.toArgb() else Color.White.toArgb()
        this.textSize = textSize * 0.9f
    }

    val textRadius = outerRadius - outerStrokeWidth * 0.1f
    val midAngle = startAngle + sweepAngle / 2
    val x = textRadius * cos(midAngle * PI.toFloat() / 180f)
    val y = textRadius * sin(midAngle * PI.toFloat() / 180f)
    val textPosition = Offset(center.x + x, center.y + y)

    // Calculate available space
    val arcLength = (2 * PI * textRadius * sweepAngle / 360).toFloat()
    val maxTextWidth = arcLength * 0.9f // Leave some margin

    // Function to measure text width
    fun measureText(text: String): Float = textPaint.measureText(text)

    // Find the maximum length that fits
    var displayText = label
    if (measureText(label) > maxTextWidth) {
        var truncatedText = label
        while (measureText("$truncatedText...") > maxTextWidth && truncatedText.length > 1) {
            truncatedText = truncatedText.dropLast(1)
        }
        displayText = if (truncatedText.length > 1) "$truncatedText..." else truncatedText
    }

    // Draw text on arc
    val path = Path()
    val oval = RectF(
        center.x - textRadius,
        center.y - textRadius,
        center.x + textRadius,
        center.y + textRadius
    )

    // Flip text if it lies on the bottom half (midAngle in 90° to 270°)
    val isFlipped = midAngle in 90f..120f
    if (isFlipped) {
        path.addArc(oval, startAngle + sweepAngle * 0.95f, -sweepAngle * 0.9f)
    } else {
        path.addArc(oval, startAngle + sweepAngle * 0.05f, sweepAngle * 0.9f)
    }

    canvas.drawTextOnPath(displayText, path, 0f, textPaint.textSize / 3f, textPaint)

    // Return position for tooltip if text was truncated
    return if (displayText.endsWith("...")) textPosition else null
}

private fun Color.isLightColor(): Boolean {
    val brightness = (red * 299 + green * 587 + blue * 114) / 1000
    return brightness > 0.5
}