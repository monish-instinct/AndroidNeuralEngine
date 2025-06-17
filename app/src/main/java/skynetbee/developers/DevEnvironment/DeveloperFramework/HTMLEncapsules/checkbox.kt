package skynetbee.developers.DevEnvironment

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition


@Composable
fun SmartCheckBox(
    index: Int,
    selectedIndex: Int,
    onSelectionChange: (Int, String) -> Unit,
    value: String,
    radius: Dp = 40.dp
) {
    val isSelected = index == selectedIndex
    var isPlaying by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.checkmark))
    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = isPlaying,
        restartOnPlay = true
    )

    LaunchedEffect(progress) {
        if (progress == 1f) {
            isPlaying = false
        }
    }

    Box(
        modifier = Modifier.size(radius), // Use radius for overall size
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(radius)
                    .clickable {
                        onSelectionChange(-1, "") // Deselect when clicked
                    }
            )
        } else {
            Box(
                modifier = Modifier
                    .size(radius * 0.6f) // Adjust inner circle size
                    .clip(shape = CircleShape)
                    .border(width = 2.dp, color = Color.Black, shape = CircleShape)
                    .clickable {
                        onSelectionChange(index, value)
                        isPlaying = true
                    }
            )
        }
    }
}






