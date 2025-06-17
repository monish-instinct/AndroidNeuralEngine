package skynetbee.developers.DevEnvironment

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val ratings = mutableStateMapOf<String, Int>()

@Composable
fun StarRating(id: String, starSize: TextUnit = 35.sp, defaultRating: Int? = null) {
    val context = LocalContext.current
    if (id !in ratings) ratings[id] = defaultRating ?: 0

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        StarRatingBar(
            rating = ratings[id] ?: 0,
            onRatingChanged = { newRating ->
                if (defaultRating == null) {
                    ratings[id] = newRating
                    playSoundForRating(context, newRating)
                }
            },
            starSize = starSize,
            isFixed = defaultRating != null
        )
    }
}

@Composable
fun StarRatingBar(rating: Int, onRatingChanged: (Int) -> Unit, starSize: TextUnit, isFixed: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            val rotation = remember { Animatable(0f) }
            val scale = remember { Animatable(1f) }

            LaunchedEffect(rating) {
                if (i == rating) {
                    rotation.animateTo(720f, animationSpec = tween(600))
                    scale.animateTo(1.4f, animationSpec = tween(200, easing = EaseOutQuad))
                    scale.animateTo(1f, animationSpec = tween(200, easing = EaseInQuad))
                } else {
                    rotation.animateTo(-360f, animationSpec = tween(600))
                    scale.snapTo(1f)
                }
            }

            val isEmoji = i == rating
            val displayedChar = if (isEmoji) getEmojiForRating(i) else "★"

            Text(
                text = displayedChar,
                fontSize = if (isEmoji) (starSize.value-8).sp else starSize,
                textAlign = TextAlign.Center,
                color = when {
                    isEmoji -> Color.Unspecified
                    i <= rating -> Color(0xFFFFD700)
                    else -> Color.Gray
                },
                modifier = Modifier
                    .rotate(rotation.value)
                    .scale(scale.value)
                    .then(
                        if (isFixed) Modifier else Modifier.clickable(
                            onClick = { onRatingChanged(i) },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                    )
                    .padding(4.dp)
            )
        }
    }
}

fun getEmojiForRating(rating: Int): String {
    return when (rating) {
        1 -> "\uD83E\uDD7A" // 🥺
        2 -> "\uD83D\uDE30" // 😰
        3 -> "😐"
        4 -> "😊"
        5 -> "\uD83D\uDE0D" // 😍
        else -> "★"
    }
}

fun playSoundForRating(context: Context, rating: Int) {
    val soundResId = when (rating) {
        1 -> R.raw.betterlucknxttime
        2 -> R.raw.youcan
        3 -> R.raw.goodjob
        4 -> R.raw.gratwork
        5 -> R.raw.excellent
        else -> null
    }

    soundResId?.let { resId ->
        val mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer.setVolume(0.5f, 0.5f)
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener {
            it.release()
        }
    }
}