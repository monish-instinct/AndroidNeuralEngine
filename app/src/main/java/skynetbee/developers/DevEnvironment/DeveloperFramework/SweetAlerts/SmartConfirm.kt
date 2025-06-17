package skynetbee.developers.DevEnvironment

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

// State Management
object SmartConfirmManager {
    var message by mutableStateOf("")
    var isVisible by mutableStateOf(false)
    var Confirm: () -> Unit = {}
    var Cancel: () -> Unit = {}

    fun SmartConfirm(msg: String, onconfirm: () -> Unit, oncancel: () -> Unit) {
        message = msg
        isVisible = true
        Confirm = onconfirm
        Cancel = oncancel

    }
    fun hide() {
        isVisible = false
    }
}

@Composable
fun SmartConfirm() {
    if (SmartConfirmManager.isVisible) {
        val alpha = remember { Animatable(0f) }

        LaunchedEffect(SmartConfirmManager.message) {
            alpha.animateTo(1f, animationSpec = tween(300))
            delay(2000) // Display duration
            alpha.animateTo(0f, animationSpec = tween(300))
            SmartConfirmManager.isVisible = false
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1000f)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(16.dp)) // ✅ Rounded Corners

                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFD18203), // Equivalent to Color(red: 0.82, green: 0.50, blue: 0.02)
                                Color(0xFFFACB69), // Equivalent to Color(red: 0.98, green: 0.8, blue: 0.41)
                                Color(0xFFD18203)  // Equivalent to Color(red: 0.82, green: 0.50, blue: 0.02)
                            ),
                        )
                    )
                    .padding(16.dp)
            )
            {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // ✅ Alert Message Text
                    Text(
                        text = SmartConfirmManager.message,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp, // Change font size
                            letterSpacing = 1.sp // Adjust letter spacing
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )

                    // ✅ Buttons Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                SmartConfirmManager.Confirm()
                                SmartConfirmManager.hide()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "OK", color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = {
                                SmartConfirmManager.Cancel()
                                SmartConfirmManager.hide()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Cancel", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

fun cl (msg:String) {
    Log.d("print", msg)
}