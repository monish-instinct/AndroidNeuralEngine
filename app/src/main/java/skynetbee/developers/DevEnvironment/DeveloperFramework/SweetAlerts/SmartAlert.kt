package skynetbee.developers.DevEnvironment

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay

// State Management
object SmartAlertManager {

    var message by mutableStateOf("")
    var isVisible by mutableStateOf(false)

    fun SmartAlert(msg: String) {
        message = msg
        isVisible = true
    }

    fun hide() {
        isVisible = false
    }
}

// Composable Component
@Composable
fun SmartAlert() {
    if (SmartAlertManager.isVisible) {
        val alpha = remember { Animatable(0f) }

        LaunchedEffect(SmartAlertManager.message) {
            // Smoothly pop up
            alpha.animateTo(1f, animationSpec = tween(1000))
//            scale.animateTo(1f, animationSpec = tween(1000, easing = EaseOutBack))

            // Stay for 2 seconds
            delay(2000)

            // Smoothly fade out
            alpha.animateTo(0f, animationSpec = tween(1000))
//            scale.animateTo(0.8f, animationSpec = tween(1000))

            // Hide alert after animation
            SmartAlertManager.hide()
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
                    .padding(16.dp),

                )
            {
                Text(
                    text = SmartAlertManager.message,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp, // Change font size
                        letterSpacing = 1.sp // Adjust letter spacing
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}