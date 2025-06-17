package com.skynetbee.neuralengine

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.skynetbee.neuralengine.DeveloperFramework.ViewModel

//  
// LoginWithOTP_Tablet.kt
// DevEnvironment
//
// Created by A. Nithish on 13-03-2025


@Composable
fun LoginWithOTP_Tablet(viewModel : ViewModel = viewModel()) {
//    var otp by remember { mutableStateOf("") }
//    var isError by remember { mutableStateOf(false) }
//    var loading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(bottom = 100.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.skynetbee_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SkyneTBee",
                fontSize = 75.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700),
                            Color(0xFFFFA500),
                            Color(0xFFFFFFE0),
                            Color(0xFFFFD700)
                        )
                    ),
                    lineHeight = 50.sp
                ),
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(40.dp))

            OTPInputField_Tablet(
                onOtpChange = { viewModel.otp = it },
                onErrorAnimationComplete = { viewModel.isError = false },
                onLoadingChange = { viewModel.loading = it }
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (viewModel.loading) {
                PulsatingDotsLoadingIndicator_Tablet()
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Communicating with Skynet...",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA500),
                                Color(0xFFFFFFE0),
                                Color(0xFFFFD700)
                            )
                        ),
                        lineHeight = 13.sp
                    ),
                    maxLines = 1,
                )
            } else if (viewModel.navToPage) {
                PulsatingDotsLoadingIndicator_Mobile()
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Navigating ...",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA500),
                                Color(0xFFFFFFE0),
                                Color(0xFFFFD700)
                            )
                        ),
                        fontSize = 13.sp,
                        lineHeight = 13.sp
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            } else {
                Button(
                    onClick = {
                        viewModel.loading = true
                        sendOtpToServer(viewModel.otp, onOtpValidation = { isValid ->
                            viewModel.loading = true
                            viewModel.isError = !isValid
                        },
                            onSecondCall = {},
                            onResponseReceived ={})
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFD700),
                                        Color(0xFFFFA500),
                                        Color(0xFFFCFC6E),
                                        Color(0xFFFFD700)
                                    )
                                ),
                                shape = RoundedCornerShape(30.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Verify OTP",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PulsatingDotsLoadingIndicator_Tablet() {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val scale3 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Dot_Tablet(scale = scale1, color = Color(0xFFFFA500)) // Orange
            Dot_Tablet(scale = scale2, color = Color(0xFFFFD700)) // Gold
            Dot_Tablet(scale = scale3, color = Color(0xFFFFFFE0)) // Light Yellow
        }
    }
}

@Composable
fun Dot_Tablet(scale: Float, color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .scale(scale)
            .background(color, shape = CircleShape)
    )
}


@Composable
fun OTPInputField_Tablet(
    onOtpChange: (String) -> Unit,
    onErrorAnimationComplete: () -> Unit,
    onLoadingChange: (Boolean) -> Unit,
    viewModel: ViewModel = viewModel()
) {

    val focusManager = LocalFocusManager.current
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val density = LocalDensity.current

    LaunchedEffect(viewModel.otpisError) {
        if (viewModel.otpisError) {
            val shake = with(density) { 10.dp.toPx() }
            viewModel.otpErrorAnimation(
                shakeDistance = shake,
                onOtpChange = onOtpChange,
                onErrorAnimationComplete = onErrorAnimationComplete
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .graphicsLayer {
                    translationX = viewModel.offsetX.value
                }
        ) {
            viewModel.textFields.forEachIndexed { index, state ->
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .background(Color.Transparent)
                        .border(1.dp, textColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = state.value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1) {
                                state.value = newValue.filter { it.isDigit() }
                                val newOtp = viewModel.textFields.joinToString("") { it.value }
                                onOtpChange(newOtp)

                                if (newValue.isNotEmpty() && index < 6 - 1) {
                                    viewModel.focusRequesters[index + 1].requestFocus()
                                }

                                if (index == 6 - 1 && newValue.isNotEmpty()) {
                                    onLoadingChange(true)
                                    sendOtpToServer(newOtp, onOtpValidation = { isValid ->
                                        onLoadingChange(true)
                                        viewModel.otpisError = !isValid
                                    },
                                        onSecondCall = {
                                            onLoadingChange(true)
                                        },
                                        onResponseReceived ={
                                            onLoadingChange(false)
                                        }
                                    )
                                }

                                viewModel.otpisError = false
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            color = if (viewModel.otpisError) Color.Red else textColor,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(viewModel.focusRequesters[index])
                            .onKeyEvent { event ->
                                if (event.key == Key.Backspace && state.value.isEmpty() && index > 0) {
                                    viewModel.textFields[index - 1].value = ""
                                    viewModel.focusRequesters[index - 1].requestFocus()
                                    true
                                } else {
                                    false
                                }
                            },
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}