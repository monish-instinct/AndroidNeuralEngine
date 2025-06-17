package skynetbee.developers.DevEnvironment

import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import skynetbee.developers.DevEnvironment.GlobalNavController.navController

@Composable
fun AccessDenied(onGoBack: () -> Unit) {
    val context = LocalContext.current
    val swordPixelWidth = with(LocalDensity.current) { 355.dp.toPx() }

    val screenWidth = context.resources.displayMetrics.widthPixels

    var leftSwordWidth by remember { mutableIntStateOf(screenWidth) }
    val leftSwordHeight by remember { mutableIntStateOf(0) }

    var rightSwordWidth by remember { mutableIntStateOf(-screenWidth) }
    val rightSwordHeight by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = "swordAnimation") {
        while (leftSwordWidth > screenWidth / 2 - swordPixelWidth / 2 && rightSwordWidth < screenWidth / 2 + swordPixelWidth / 2) {
            leftSwordWidth -= 20
            rightSwordWidth += 20
            delay(4)
        }
    }

    var audioPlayer: MediaPlayer? = null
    LaunchedEffect(key1 = "soundEffect") {
        audioPlayer = MediaPlayer.create(context, skynetbee.developers.DevEnvironment.R.raw.swordsoundeffect).apply {
            setOnCompletionListener { player ->
                player.release()
            }
            start()
        }
    }

    DisposableEffect(key1 = audioPlayer) {
        onDispose {
            audioPlayer?.release()
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(
            brush = Brush.linearGradient(
                colors = listOf(Color.Black,Color.Red)
            )
        )) {
        Image(
            painter = painterResource(id = skynetbee.developers.DevEnvironment.R.drawable.access),
            contentDescription = "Access Denied",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 120.dp)
        )

        // Left Sword (appears from the right)
        Image(
            painter = painterResource(id = skynetbee.developers.DevEnvironment.R.drawable.sword_1),
            contentDescription = "Left Sword",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .offset {
                    IntOffset(
                        leftSwordWidth,
                        leftSwordHeight
                    )
                }
                .graphicsLayer {
                    rotationZ = 0f
                }
                .scale(-1f, 1f)
        )

        // Right Sword (appears from the left)
        Image(
            painter = painterResource(id = skynetbee.developers.DevEnvironment.R.drawable.sword_1),
            contentDescription = "Right Sword",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
                .offset {
                    IntOffset(
                        rightSwordWidth,
                        rightSwordHeight
                    )
                }
                .graphicsLayer {
                    rotationZ = -0f
                }
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 150.dp)
        ) {
            OutlinedButton(
                onClick = {
                    navController?.navigate("contactdev")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(15),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0.4f, 0f, 0f),
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color(0.4f, 0f, 0f))
            ) {
                Text(
                    text = "Contact Our Tech Team",
                    color = Color.White,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.W900
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            OutlinedButton(
                onClick = {
                    // Handle go back action
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(15),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0.4f, 0f, 0f),
                    contentColor = Color.White
                ),
                border = BorderStroke(1.dp, Color(0.4f, 0f, 0f))
            ) {
                Text(
                    text = "Go Back",
                    color = Color.White,
                    fontSize = 15.5.sp,
                    fontWeight = FontWeight.W900
                )
            }
        }
    }
}
