package com.skynetbee.neuralengine

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.bouncycastle.math.raw.Mod

@Composable
fun SmartButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val gradientOffset = remember { Animatable(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            // Reset to center (golden shine)
            gradientOffset.snapTo(0f)

            gradientOffset.snapTo(-1f)

            gradientOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = LinearEasing)
            )

            isAnimating = false
        }
    }

    val shift = gradientOffset.value.coerceIn(-1f, 1f)
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF77530A),
            Color(0xFFFFD277),
            Color(0xFF77530A),
            Color(0xFFFFD277),
            Color(0xFF77530A)
        ),
        start = Offset(x = shift * 800f, y = shift * 800f),
        end = Offset(x = shift * 800f + 800f, y = shift * 800f + 800f)
    )

    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .wrapContentSize()
            .defaultMinSize(minHeight = 50.dp, minWidth = 160.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(brush)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!isAnimating) {
                    scope.launch {
                        isAnimating = true
                        onClick()
                    }
                }
            }
            .border(
                2.dp,
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFB8860B),
                        Color(0xFFFFD700),
                        Color(0xFFB8860B)
                    )
                ),
                RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .defaultMinSize(minHeight = 50.dp, minWidth = 160.dp)
                .padding(3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xD6000000)),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides Color(0xFFFFD700)) {
                content()
            }
        }
    }
}

@Composable
fun GreenButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val gradientOffset = remember { Animatable(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            gradientOffset.snapTo(0f)

            gradientOffset.snapTo(-1f)

            gradientOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = LinearEasing)
            )

            isAnimating = false
        }
    }

    val shift = gradientOffset.value.coerceIn(-1f, 1f)
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF77530A),
            Color(0xFFFFD277),
            Color(0xFF77530A),
            Color(0xFFFFD277),
            Color(0xFF77530A)
        ),
        start = Offset(x = shift * 800f, y = shift * 800f),
        end = Offset(x = shift * 800f + 800f, y = shift * 800f + 800f)
    )

    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .wrapContentSize()
            .defaultMinSize(minHeight = 50.dp, minWidth = 160.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(brush)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!isAnimating) {
                    scope.launch {
                        isAnimating = true
                        onClick()
                    }
                }
            }
            .border(
                2.dp,
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFB8860B),
                        Color(0xFFFFD700),
                        Color(0xFFB8860B)
                    )
                ),
                RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .defaultMinSize(minHeight = 50.dp, minWidth = 160.dp)
                .padding(3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xAA0B3D0B)),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides Color(0xFFFFD700)) {
                content()
            }
        }
    }
}

@Composable
fun RedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val gradientOffset = remember { Animatable(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {

            gradientOffset.snapTo(0f)

//            // 1. Shine moves from Center → Right (1f)
//            gradientOffset.animateTo(
//                targetValue = 1f,
//                animationSpec = tween(durationMillis = 800, easing = LinearEasing)
//            )

            gradientOffset.snapTo(-1f)

            gradientOffset.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 800, easing = LinearEasing)
            )

            isAnimating = false
        }
    }

    val shift = gradientOffset.value.coerceIn(-1f, 1f)
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF77530A),
            Color(0xFFFFD277),
            Color(0xFF77530A),
            Color(0xFFFFD277),
            Color(0xFF77530A)
        ),
        start = Offset(x = shift * 800f, y = shift * 800f),
        end = Offset(x = shift * 800f + 800f, y = shift * 800f + 800f)
    )

    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .wrapContentSize()
            .defaultMinSize(minHeight = 50.dp, minWidth = 160.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(brush)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!isAnimating) {
                    scope.launch {
                        isAnimating = true
                        onClick()
                    }
                }
            }
            .border(
                2.dp,
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFFB8860B),
                        Color(0xFFFFD700),
                        Color(0xFFB8860B)
                    )
                ),
                RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .defaultMinSize(minHeight = 50.dp, minWidth = 160.dp)
                .padding(3.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xAA6F0404)),
            contentAlignment = Alignment.Center
        ) {
            CompositionLocalProvider(LocalContentColor provides Color(0xFFFFD700)) {
                content()
            }
        }
    }
}