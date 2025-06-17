package skynetbee.developers.DevEnvironment


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.text.style.TextForegroundStyle.Unspecified.brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt



//@Composable
//fun BalloonSlider( id: String, startingValue: Float, endingValue: Float, stepValue: Float) {
//    var value by rememberSaveable { mutableStateOf(startingValue) }
//    var isDragging by remember { mutableStateOf(false) }
//    var balloonOffset by remember { mutableStateOf(0f) }
//    var balloonRotation by remember { mutableStateOf(0f) }
//    val trackWidth = 320.dp
//
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentSize(), // ✅ Allow dynamic positioning
//        contentAlignment = Alignment.Center // ✅ Keeps sliders independently centered
//    ) {
//        // Track previous value to calculate movement direction
//        var previousValue by remember { mutableStateOf(value) }
//
//        // Update previous value
//        LaunchedEffect(value) {
//            previousValue = value
//        }
//        var balloonVisible by remember { mutableStateOf(false) }
//        val balloonTiltAnim = remember { Animatable(0f) }
//        val coroutineScope = rememberCoroutineScope() // Needed for animation
//        var lastValue by remember { mutableStateOf(value) } // Store previous value
//        var lastDragTime by remember { mutableStateOf(System.currentTimeMillis()) } // Track drag speed
//
//        // Balloon visibility with fade-in effect
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier
//                .height(80.dp)
//                .offset(
//                    x = ((value - startingValue) / (endingValue - startingValue) * trackWidth.value - trackWidth.value / 2).dp, // ✅ Real-time movement
//                    y = animateDpAsState( // ✅ Smooth transition for better appearance
//                        targetValue = if (isDragging) (-50).dp else (-30).dp,
//                        animationSpec = tween(800), // ✅ Prevents sudden disappearance
//                        label = "Balloon Offset Y"
//                    ).value
//                )
//                .graphicsLayer {
//
//                    rotationZ = balloonTiltAnim.value // ✅ No direct @Composable call here
//                    transformOrigin = TransformOrigin(0.5f, 1f) // Pivot at bottom
//
//                }
//                .alpha(
//                    animateFloatAsState(
//                        targetValue = if (isDragging) 1f else 0f, // Fade-in effect
//                        animationSpec = tween(900),
//                        label = "Balloon Alpha"
//                    ).value
//                )
//
//                .animateContentSize(animationSpec = tween(300))
//        ) {
//
//            if (balloonVisible ) { // ✅ Ensures smooth appearance only when needed
//                BalloonView((value / stepValue).roundToInt() * stepValue, stepValue)
//            }
//
//        }
//        // ✅ Animate balloon tilt opposite to drag direction & consider speed
//        LaunchedEffect(value, isDragging) {
//            coroutineScope.launch {
//                if (isDragging) {
//                    val currentTime = System.currentTimeMillis()
//                    val deltaTime = (currentTime - lastDragTime).coerceAtLeast(1).toFloat() // Avoid division by zero
//                    lastDragTime = currentTime
//
//                    val speedFactor = (abs(value - lastValue) / deltaTime) * 700 // Scale speed impact
//                    val limitedSpeedFactor = speedFactor.coerceIn(10f, 20f) // Prevent extreme tilts
//
//                    val isAtStartOrEnd = value <= startingValue || value >= endingValue
//                    val dragDirection = if (value > lastValue) -1 else 1 // ✅ Opposite tilt
//
//                    val tiltValue = if (isAtStartOrEnd) 0f else dragDirection * limitedSpeedFactor // ✅ Opposite & speed-based tilt
//
//                    balloonTiltAnim.animateTo(tiltValue, animationSpec = tween(30)) // ✅ Faster update
//                    lastValue = value // ✅ Update last value for next comparison
//                } else {
//                    balloonTiltAnim.animateTo(0f, animationSpec = tween(150)) // ✅ Smooth reset
//                }
//            }
//        }
//
//        // ✅ Update visibility state
//        LaunchedEffect(isDragging) {
//            if (isDragging) {
//                balloonVisible = true
//            } else {
//                delay(1000) // Small delay for smooth disappearance
//                balloonVisible = false
//            }
//        }
//
//        Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Box(
//                modifier = Modifier
//                    .height(5.dp)
//                    .width(trackWidth)
//                    .background(Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(2.dp))
//            ) {
//                // Active Track
//                Box(
//                    modifier = Modifier
//                        .height(5.dp)
//                        .width(((value - startingValue) / (endingValue - startingValue) * trackWidth.value).dp)
//                        .background(
//                            brush = Brush.linearGradient(
//                                colors = listOf(
//                                    Color(0xFFFFD700), // Gold
//                                    Color(0xFFEEB422), // Slightly darker gold
//                                    Color(0xFFD4A017)  // Deeper golden hue
//                                )
//                            ),
//                            shape = RoundedCornerShape(1.dp)
//                        )
//
//                )
//            }
//            val animatableValue = remember { Animatable(value) } // For smooth movement
//            val coroutineScope = rememberCoroutineScope() // ✅ Use CoroutineScope to launch animations
//            val steps = ((endingValue - startingValue) / stepValue).toInt()  // Number of steps
//
//            Box (
//                modifier = Modifier
//                    .size(30.dp)
//                    .offset(
//                        x = (((value - startingValue) / (endingValue - startingValue)) * trackWidth.value - trackWidth.value / 2).dp,
//                        y = -0.5.dp
//                    )
//                    .background(Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500))), shape = CircleShape)
//                    .pointerInput(Unit) {
//                        detectDragGestures(onDragStart = {
//                            isDragging = true
//                        }, onDrag = { change, dragAmount ->
//                            change.consume()
//
//                            // ✅ Scale down drag amount to slow down movement
//                            val scaledDragAmount = dragAmount.x * 0.8f // Reducing speed by 70%
//
//                            val rawNewValue = value + (scaledDragAmount / trackWidth.value) * (endingValue - startingValue)
//                            val newValue = rawNewValue.coerceIn(startingValue, endingValue) // ✅ Restrict within range
//
//                            value = newValue
//                            // ✅ Use CoroutineScope instead of LaunchedEffect
//                            coroutineScope.launch {
//                                animatableValue.animateTo(
//                                    newValue,
//                                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing) // ✅ Slow animation
//                                )
//                                value = animatableValue.value
//                            }
//
//                            val dragDirection = newValue - value
//                            balloonOffset = if (dragDirection > 0) -15f else 15f
//                            balloonOffset += dragAmount.x / 2 // Adjust to move smoothly
//                            balloonRotation = if (dragDirection > 0) -15f else 15f
//
//
//                        }, onDragEnd = {
//                            isDragging = false
//                            // ✅ Snap to nearest step after release
//                            coroutineScope.launch {
//                                val snappedValue = (value / stepValue).roundToInt() * stepValue
//                                val finalValue = snappedValue.coerceAtMost(endingValue) // ✅ Prevents exceeding end value
//
//                                animatableValue.animateTo(
//                                    finalValue,
//                                    animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing) // ✅ Smoother snap
//                                )
//                                value = animatableValue.value
//                            }
//                            // ✅ Round value only at the end for smooth movement
//                            value = (value / stepValue).roundToInt() * stepValue
//                            balloonOffset = -0f
//                            balloonRotation = -10f
//                        })
//
//                    }
//            ) {
//                Box(
//                    modifier = Modifier
//                        .size(27.dp)
//                        .background(
//                            Color.White
//
//                            , shape = CircleShape)
//                        .align(Alignment.Center),
//                    contentAlignment = Alignment.Center
//                ) {
//                    val formattedValue = if (stepValue % 1 == 0f) {
//                        value.toInt().toString()  // Show as whole number if stepValue is whole
//                    } else {
//                        String.format("%.1f", value)  // Show one decimal if stepValue is fractional
//                    }
//
//                    val maxTextSize = 17.sp
//                    val minTextSize = 8.sp
//                    val textLength = formattedValue.length
//                    val fontSize = when (textLength) {
//                        1 -> maxTextSize
//                        2 -> (maxTextSize.value - 2).sp
//                        3 -> (maxTextSize.value - 4).sp
//                        else -> minTextSize
//                    }
//
//                    if (!isDragging && !balloonVisible) {
//                        Text(
//                            text = formattedValue,
//                            fontSize = fontSize,
//                            color = Color.Black,
//                            fontWeight = FontWeight.ExtraBold,  // Make it bolder
//
//                            textAlign = TextAlign.Center,
//                            maxLines = 1,  // Ensure text stays in one line
//                            softWrap = false, // Prevents breaking into multiple lines
//                            overflow = TextOverflow.Clip, // Handles overflow text
//                            modifier = Modifier
//                                .padding(2.dp)
//                                .wrapContentSize()  // Ensures text stays clear and centered
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun BalloonView(value: Float, stepValue: Float) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        // Balloon Circle
//        Box(
//            modifier = Modifier
//                .size(60.dp)
//                .background(
//                    brush = Brush.linearGradient(
//                        colors = listOf(
//                            Color(0xFFFFD700), // Gold
//                            Color(0xFFEEB422), // Slightly darker gold
//                            Color(0xFFD4A017)  // Deeper golden hue
//                        ),
//                        start = Offset(0f, 0f),
//                        end = Offset(100f, 100f)
//                    ),
//                    shape = CircleShape
//                ),
//            contentAlignment = Alignment.Center
//        ) {
//            val textValue = if (stepValue % 1 == 0f) {
//                value.toInt().toString()  // Whole number format
//            } else {
//                String.format("%.1f", value)  // One decimal place format
//            }
//
//            // Adjust font size dynamically
//            val maxTextSize = 18.sp
//            val minTextSize = 8.sp
//            val textLength = textValue.length
//            val fontSize = when (textLength) {
//                1 -> maxTextSize
//                2 -> (maxTextSize.value - 2).sp
//                3 -> (maxTextSize.value - 4).sp
//                else -> minTextSize
//            }
//
//            Text(
//                text = textValue,
//                fontSize = fontSize,
//                fontWeight = FontWeight.Medium,
//                color = Color.White
//            )
//        }
//        Spacer(modifier = Modifier.height(2.dp))
//        Triangle()
//    }
//
//}
//
//@Composable
//fun Triangle() {
//    Canvas(
//        modifier = Modifier.size(12.dp, 6.dp)
//    ) {
//        val path = Path().apply {
//            moveTo(size.width / 2, 0f)   // Top center
//            lineTo(0f, size.height)     // Bottom left
//            lineTo(size.width, size.height) // Bottom right
//            close()
//        }
//        drawPath(
//            path = path,
//            brush = Brush.linearGradient(
//                colors = listOf(
//                    Color(0xFFFFD700), // Gold
//                    Color(0xFFEEB422), // Slightly darker gold
//                    Color(0xFFD4A017)  // Deeper golden hue
//                ),
//                start = Offset(0f, 0f),
//                end = Offset(size.width, size.height)
//            )
//        )
//    }
//}
//


@Composable
fun BalloonSlider( id: String, startingValue: Float, endingValue: Float, stepValue: Float) {
    var value by rememberSaveable { mutableStateOf(startingValue) }
    var isDragging by remember { mutableStateOf(false) }
    var balloonOffset by remember { mutableStateOf(0f) }
    var balloonRotation by remember { mutableStateOf(0f) }
    var containerWidth by remember { mutableStateOf(1) } // ✅ Start with 1 to avoid division by zero
    var containerHeight by remember { mutableStateOf(0) }
    var balloonVisible by remember { mutableStateOf(false) }
    val balloonTiltAnim = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .onGloballyPositioned { coordinates ->
                containerWidth = coordinates.size.width
                containerHeight = coordinates.size.height
            },
        contentAlignment = Alignment.Center
        // ✅ Keeps sliders independently centered
    ) {
        // Track previous value to calculate movement direction
        var previousValue by remember { mutableStateOf(value) }

        // Update previous value
        LaunchedEffect(value) {
            previousValue = value
        }
        var lastValue by remember { mutableStateOf(value) } // Store previous value
        var lastDragTime by remember { mutableStateOf(System.currentTimeMillis()) } // Track drag speed
        val density = LocalDensity.current

        // Balloon visibility with fade-in effect
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(80.dp)
                .offset(
                    x = with(density) {
                        ((value - startingValue) / (endingValue - startingValue) * containerWidth - containerWidth / 2).toDp()
                    },
                    y = animateDpAsState( // ✅ Smooth transition for better appearance
                        targetValue = if (isDragging) (-50).dp else (-30).dp,
                        animationSpec = tween(800), // ✅ Prevents sudden disappearance
                        label = "Balloon Offset Y"
                    ).value
                )
                .graphicsLayer {

                    rotationZ = balloonTiltAnim.value // ✅ No direct @Composable call here
                    transformOrigin = TransformOrigin(0.5f, 1f) // Pivot at bottom

                }
                .alpha(
                    animateFloatAsState(
                        targetValue = if (isDragging) 1f else 0f, // Fade-in effect
                        animationSpec = tween(900),
                        label = "Balloon Alpha"
                    ).value
                )

                .animateContentSize(animationSpec = tween(300))
        ) {
            if (balloonVisible ) { // ✅ Ensures smooth appearance only when needed
                BalloonView(
                    (value / stepValue).roundToInt() * stepValue,
                    stepValue
                )
            }
        }
        // ✅ Animate balloon tilt opposite to drag direction & consider speed
        LaunchedEffect(value, isDragging) {
            coroutineScope.launch {
                if (isDragging) {
                    val currentTime = System.currentTimeMillis()
                    val deltaTime = (currentTime - lastDragTime).coerceAtLeast(1).toFloat() // Avoid division by zero
                    lastDragTime = currentTime

                    val speedFactor = (abs(value - lastValue) / deltaTime) * 700 // Scale speed impact
                    val limitedSpeedFactor = speedFactor.coerceIn(10f, 20f) // Prevent extreme tilts

                    val isAtStartOrEnd = value <= startingValue || value >= endingValue
                    val dragDirection = if (value > lastValue) -1 else 1 // ✅ Opposite tilt

                    val tiltValue = if (isAtStartOrEnd) 0f else dragDirection * limitedSpeedFactor // ✅ Opposite & speed-based tilt

                    balloonTiltAnim.animateTo(tiltValue, animationSpec = tween(30)) // ✅ Faster update
                    lastValue = value // ✅ Update last value for next comparison
                } else {
                    balloonTiltAnim.animateTo(0f, animationSpec = tween(150)) // ✅ Smooth reset
                }
            }
        }

        // ✅ Update visibility state
        LaunchedEffect(isDragging) {
            if (isDragging) {
                balloonVisible = true
            } else {
                delay(1000) // Small delay for smooth disappearance
                balloonVisible = false
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .height(5.dp)
                    .fillMaxWidth()
                    .background(Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(2.dp))
            ) {
                // Active Track
                Box(
                    modifier = Modifier
                        .height(5.dp)
                        .width(
                            with(density) {
                                ((value - startingValue) / (endingValue - startingValue) * containerWidth).toDp()
                            }
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFD700), // Gold
                                    Color(0xFFEEB422), // Slightly darker gold
                                    Color(0xFFD4A017)  // Deeper golden hue
                                )
                            ),
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }

            val animatableValue = remember { Animatable(value) } // For smooth movement
            val coroutineScope = rememberCoroutineScope() // ✅ Use CoroutineScope to launch animations

            Box (
                modifier = Modifier
                    .size(30.dp)
                    .offset(
                        x = with(density) {
                            ((value - startingValue) / (endingValue - startingValue) * containerWidth - containerWidth / 2).toDp()
                        },
                        y = -0.5.dp
                    )
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500))),
                        shape = CircleShape
                    )
                    .pointerInput(Unit) {
                        detectDragGestures(onDragStart = {
                            isDragging = true
                        }, onDrag = { change, dragAmount ->
                            change.consume()
                            // ✅ Scale down drag amount to slow down movement
                            val scaledDragAmount = dragAmount.x * 0.8f // Reducing speed by 70%
                            val rawNewValue =
                                value + (scaledDragAmount / containerWidth) * (endingValue - startingValue)
                            val newValue = rawNewValue.coerceIn(
                                startingValue,
                                endingValue
                            ) // ✅ Restrict within range
                            value = newValue
                            // ✅ Use CoroutineScope instead of LaunchedEffect
                            coroutineScope.launch {
                                animatableValue.animateTo(
                                    newValue,
                                    animationSpec = tween(
                                        durationMillis = 1000,
                                        easing = FastOutSlowInEasing
                                    ) // ✅ Slow animation
                                )
                                value = animatableValue.value
                            }
                            val dragDirection = newValue - value
                            balloonOffset = if (dragDirection > 0) -15f else 15f
                            balloonOffset += dragAmount.x / 2 // Adjust to move smoothly
                            balloonRotation = if (dragDirection > 0) -15f else 15f
                        }, onDragEnd = {
                            isDragging = false
                            // ✅ Snap to nearest step after release
                            coroutineScope.launch {
                                val snappedValue = (value / stepValue).roundToInt() * stepValue
                                val finalValue =
                                    snappedValue.coerceAtMost(endingValue) // ✅ Prevents exceeding end value
                                animatableValue.animateTo(
                                    finalValue,
                                    animationSpec = tween(
                                        durationMillis = 700,
                                        easing = FastOutSlowInEasing
                                    ) // ✅ Smoother snap
                                )
                                value = animatableValue.value
                            }
                            // ✅ Round value only at the end for smooth movement
                            value = (value / stepValue).roundToInt() * stepValue
                            balloonOffset = -0f
                            balloonRotation = -10f
                        })
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(27.dp)
                        .background(Color.White, shape = CircleShape)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    val formattedValue = if (stepValue % 1 == 0f) {
                        value.toInt().toString()  // Show as whole number if stepValue is whole
                    } else {
                        String.format("%.1f", value)  // Show one decimal if stepValue is fractional
                    }

                    val maxTextSize = 17.sp
                    val minTextSize = 8.sp
                    val textLength = formattedValue.length
                    val fontSize = when (textLength) {
                        1 -> maxTextSize
                        2 -> (maxTextSize.value - 2).sp
                        3 -> (maxTextSize.value - 4).sp
                        else -> minTextSize
                    }

                    if (!isDragging && !balloonVisible) {
                        Text(
                            text = formattedValue,
                            fontSize = fontSize,
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,  // Make it bolder
                            textAlign = TextAlign.Center,
                            maxLines = 1,  // Ensure text stays in one line
                            softWrap = false, // Prevents breaking into multiple lines
                            overflow = TextOverflow.Clip, // Handles overflow text
                            modifier = Modifier
                                .padding(2.dp)
                                .wrapContentSize()  // Ensures text stays clear and centered
                        )
                    }
                }
            }
        }
    }
}

// Extension function to easily convert Int pixel to Dp
@Composable
fun Int.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

@Composable
fun BalloonView(value: Float, stepValue: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Balloon Circle
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700), // Gold
                            Color(0xFFEEB422), // Slightly darker gold
                            Color(0xFFD4A017)  // Deeper golden hue
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(100f, 100f)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val textValue = if (stepValue % 1 == 0f) {
                value.toInt().toString()  // Whole number format
            } else {
                String.format("%.1f", value)  // One decimal place format
            }

            // Adjust font size dynamically
            val maxTextSize = 18.sp
            val minTextSize = 8.sp
            val textLength = textValue.length
            val fontSize = when (textLength) {
                1 -> maxTextSize
                2 -> (maxTextSize.value - 2).sp
                3 -> (maxTextSize.value - 4).sp
                else -> minTextSize
            }

            Text(
                text = textValue,
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Triangle()
    }

}

@Composable
fun Triangle() {
    Canvas(
        modifier = Modifier.size(12.dp, 6.dp)
    ) {
        val path = Path().apply {
            moveTo(size.width / 2, 0f)   // Top center
            lineTo(0f, size.height)     // Bottom left
            lineTo(size.width, size.height) // Bottom right
            close()
        }
        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFFD700), // Gold
                    Color(0xFFEEB422), // Slightly darker gold
                    Color(0xFFD4A017)  // Deeper golden hue
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
        )
    }
}
