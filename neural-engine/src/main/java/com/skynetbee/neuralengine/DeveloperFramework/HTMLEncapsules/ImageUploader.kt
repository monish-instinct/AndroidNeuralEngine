package com.skynetbee.neuralengine

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

var getImage = mutableStateMapOf<String, MutableList<String>>()

@Composable
fun ImageUploader(id: String) {
    val context = LocalContext.current
    val originalImages = listOf(
        R.drawable.ironman,
        R.drawable.doreamon,
        R.drawable.thor,
        R.drawable.batman,
        R.drawable.captain_america,
        R.drawable.avengers,
        R.drawable.antman,
        R.drawable.hulk,
        R.drawable.spiderman,
        R.drawable.shinchan
    )

    var imageId = mutableListOf<String>()

    originalImages.forEach { value ->
        imageId.add("${value}")
    }

    val images = remember { mutableStateListOf(*originalImages.toTypedArray()) }
    var removedImages by remember { mutableStateOf<List<Pair<Int, Int>>>(emptyList()) }
    var selectedIndex by remember { mutableStateOf(0) }
    // Remember image IDs so they persist across recompositions
    val imageid = remember { mutableStateListOf<String>() }

    // Populate imageid only once when the composable is first launched or when images change

    Column(
        modifier = Modifier.wrapContentSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.Companion
                .backgroundCard()
                .height(450.dp)
                .width(400.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -30) {
                            selectedIndex =
                                (selectedIndex + 1).coerceAtMost(images.size - 1)
                        } else if (dragAmount > 40) {
                            selectedIndex = (selectedIndex - 1).coerceAtLeast(0)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {

            images.forEachIndexed { index, imageResId ->
                val bitmap = remember(imageResId) {
                    BitmapFactory.decodeResource(context.resources, imageResId)
                }

                // The IDs are no longer added here
                ImageCover(
                    imageBitmap = bitmap,
                    offset = index - selectedIndex,
                    onClick = { selectedIndex = index }
                )
                imageid.add(imageResId.toString())
                Log.d("123poiuy", "Image ID stored: ${imageResId}") // This will log for each image
            }

            // Add Image Button
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
            ) {
                SmartButton(
                    onClick = {
                        if (removedImages.isNotEmpty()) {
                            val (restoreIndex, restoreImage) = removedImages.last()
                            images.add(restoreIndex, restoreImage)
                            // Update imageid accordingly

                            removedImages = removedImages.dropLast(1)
                            selectedIndex = restoreIndex

                        }
                        imageId.add(originalImages[selectedIndex].toString())

                        getImage[id] = imageId

                        imageId.forEach { value ->
                            Log.d("VideoGlob", "ADDED: ${value}")
                        }
                    },
                    content = {
                        Text(
                            text = "Add Image",
                            color = Color(0xFFE2A440),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    },
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .padding(bottom = 20.dp)
                )
            }

            // Remove Image Button
            if (images.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                ) {
                    SmartButton(
                        onClick = {
                            val removedImageResId = images[selectedIndex]
                            removedImages = removedImages + (selectedIndex to removedImageResId)
                            images.removeAt(selectedIndex)
                            // Remove the corresponding ID from the list
                            if (imageid.size > selectedIndex) {
                                imageid.removeAt(selectedIndex)
                            }
                            selectedIndex = selectedIndex.coerceAtMost(images.size - 1)

                            imageId.removeAt(selectedIndex)
                            imageId.forEach { value ->
                                Log.d("VideoGlob", "REMOVED: ${value}")
                            }

                            getImage[id] = imageId
                            Log.d("123poiuy", "$removedImageResId")
                            Log.d("123poiuy", "=================")
                        },
                        content = {
                            Text(
                                text = "Remove Image",
                                color = Color(0xFFE2A440),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            )
                        },
                    )
                }
            }
        }
    }
}



@Composable
fun ImageCover(
    imageBitmap: Bitmap,
    offset: Int,
    onClick: () -> Unit,
) {
    val rotation by animateFloatAsState(if (offset == 0) 0f else offset * -30f)
    val opacity by animateFloatAsState(if (offset == 0) 1f else 0.5f)
    val xOffset = (offset * 100).dp

    Column(
        modifier = Modifier
            .padding(top = 2.dp)
            .offset(x = xOffset)
            .scale(1f) // Keep all images same size
            .graphicsLayer(rotationY = rotation, alpha = opacity)
            .clickable(indication = null,
                interactionSource = remember { MutableInteractionSource() }) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Main Image
        Image(
            bitmap = imageBitmap.asImageBitmap(),
            contentDescription = "Uploaded Image",
            modifier = Modifier
                .width(110.dp)
                .height(150.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(1.5.dp))

        // Reflection
        Box(
            modifier = Modifier
                .width(110.dp)
                .height(80.dp) // Reduce reflection height
                .graphicsLayer {
                    scaleY = -1f // Flip vertically
                    alpha = 0.8f  // Slightly faded
                }
                .drawWithCache{
                    val fade = Brush.verticalGradient(colors = listOf(Color.Transparent, Color.White))
                    onDrawWithContent {
                        drawContent()
                        drawRect(fade, blendMode = BlendMode.DstIn)
                    }
                },
        ) {
            // Reflection Image
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = "Reflection",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay for Smooth Fade
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = if (isSystemInDarkTheme()) {
                                listOf(
                                    Color.Black.copy(alpha = 1f), // Dark theme - stronger fade
                                    Color.Black.copy(alpha = 1f),
                                    Color.Black.copy(alpha = 0.9f),
                                    Color.Black.copy(alpha = 0.8f),
                                    Color.Black.copy(alpha = 0.7f), // Completely fades out
                                )
                            } else {
                                listOf(
                                    Color.Black.copy(alpha = 0.7f), // Top reflection visible
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.1f), // Near bottom fades out smoothly
                                    Color.Transparent // Card fully visible
                                )
                            }
                        )
                    )
            )
        }
    }
}
