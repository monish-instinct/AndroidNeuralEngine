package com.skynetbee.neuralengine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import android.view.Surface
import android.graphics.Matrix
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.util.Log
import kotlin.math.log


var getVideo = mutableStateMapOf<String, MutableList<String>>()

@OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun VideoUploader(context: Context , id : String) {

    var videoId = mutableListOf<String>()

    val videoResources = listOf(
        R.raw.spider_man_s,
        R.raw.thor_s,
        R.raw.bat_man_s,
        R.raw.iron_man_s,
        R.raw.captain_america_s,
        R.raw.hulk_s,
        R.raw.infinity_war_s,
        R.raw.fight_s
    )
    videoResources.forEach { value ->
        videoId.add("${value}")
    }

    val videoUris = remember {
        mutableStateListOf(*videoResources.map {
            Uri.parse("android.resource://${context.packageName}/$it")
        }.toTypedArray())
    }
    val deletedVideos = remember { mutableStateListOf<Pair<Uri, Int>>() }
    var selectedIndex by remember { mutableStateOf(0) }
    var playingVideoIndex by remember { mutableStateOf(0) }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(450.dp)
                .border(1.dp, color = Color(0x30B7B4B4), RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -10) {
                            selectedIndex = (selectedIndex + 1).coerceAtMost(videoUris.size - 1)
                        } else if (dragAmount > 50) {
                            selectedIndex = (selectedIndex - 1).coerceAtLeast(0)
                        }
                        playingVideoIndex = selectedIndex
                    }
                }
                .drawBehind {
                    drawRoundRect(
                        color = Color(0x33000000),
                        size = size.copy(width = size.width * 1.02f, height = size.height * 1.02f),
                        topLeft = Offset(4f, 4f),
                        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                    )
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // "Add Video" Button remains at the top.
                SmartButton(
                    onClick = {
                        Log.d("videoGlob", "VideoUploader:$getVideo ")
                        if (deletedVideos.isNotEmpty()) {
                            val (restoredVideo, position) = deletedVideos.removeLast()
                            videoUris.add(position, restoredVideo)
                            selectedIndex = position
                            playingVideoIndex = selectedIndex
                        }
                        videoId.add(videoResources[selectedIndex].toString())

                        getVideo[id] = videoId
                        videoId.forEach { value ->
                            Log.d("VideoGlob", "ADDED: ${value}")
                        }
                    },
                ){
                    Text("Add Video")
                }

                Spacer(modifier = Modifier.height(40.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    videoUris.forEachIndexed { index, videoUri ->
                        android.util.Log.d("qwertyuiop", "$videoUri")
                        key(videoUri) {
                            VideoThumbnailItem(
                                context = context,
                                videoUri = videoUri,
                                index = index,
                                isCentered = index == selectedIndex,
                                offset = index - selectedIndex,
                                isPlaying = playingVideoIndex == index,
                                onClick = {
                                    selectedIndex = it
                                    playingVideoIndex = it
                                }
                            )
                        }
                    }
                    android.util.Log.d("qwertyuiop", "==================")

                    Spacer(modifier = Modifier.height(30.dp))
                    Column (modifier = Modifier
                        .padding(bottom = 20.dp)
                        .align(Alignment.BottomCenter)) {

                        // Overlay the Delete button at the bottom center of the Box.
                        SmartButton(
                            onClick = {
                                if (videoUris.isNotEmpty()) {
                                    val removedVideo = videoUris[selectedIndex]
                                    deletedVideos.add(removedVideo to selectedIndex)
                                    videoUris.removeAt(selectedIndex)
                                    selectedIndex = selectedIndex.coerceAtMost(videoUris.size - 1)
                                    playingVideoIndex = selectedIndex

                                }

                                videoId.remove(videoResources[selectedIndex].toString())

                                getVideo[id] = videoId
                                videoId.forEach { value ->
                                    Log.d("VideoGlob", "REMOVED: ${value}")
                                }
                                //
                            },
                        ) {
                            Text("Delete Video")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoThumbnailItem(
    context: Context,
    videoUri: Uri,
    index: Int,
    isCentered: Boolean,
    offset: Int,
    isPlaying: Boolean,
    onClick: (Int) -> Unit
) {
    val scale by animateFloatAsState(if (isCentered) 1.3f else 0.8f)
    val rotation by animateFloatAsState(if (isCentered) 0f else offset * -30f)
    val opacity by animateFloatAsState(if (isCentered) 1f else 0.5f)
    val xOffset = (offset * 130).dp

    Column(
        modifier = Modifier
            .offset(x = xOffset)
            .scale(scale)
            .graphicsLayer(rotationY = rotation, alpha = opacity)
            .clickable { onClick(index) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val exoPlayerMain = remember {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(videoUri))
                prepare()
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0f  // Mute audio for reflection
            }
        }

        val exoPlayerReflection = remember {
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(videoUri))
                prepare()
                repeatMode = Player.REPEAT_MODE_ONE
            }
        }

        // ✅ Ensure video plays when centered
        LaunchedEffect(videoUri, isCentered) {
            exoPlayerMain.playWhenReady = isCentered
            exoPlayerReflection.playWhenReady = isCentered
        }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_PAUSE -> {
                        exoPlayerMain.pause()
                        exoPlayerReflection.pause()
                    }
                    Lifecycle.Event.ON_RESUME -> {
                        // Resume if this video should be playing.
                        if (isPlaying) {
                            exoPlayerMain.playWhenReady = true
                            exoPlayerReflection.playWhenReady = true
                        }
                    }
                    else -> {}
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        // ✅ Release ExoPlayer properly
        DisposableEffect(Unit) {
            onDispose {
                exoPlayerMain.release()
                exoPlayerReflection.release()
            }
        }

        // 🔹 Main Video Player
        Box(
            modifier = Modifier
                .width(185.dp)
                .height(130.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            if (isPlaying) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayerMain
                            useController = false  // ✅ Ensure NO controls appear
                            controllerAutoShow = false  // ✅ Prevents controls from auto-showing
                            controllerShowTimeoutMs = 0 // ✅ Disables timeout animation
                            hideController()  // ✅ Ensures controller is immediately hidden
                            useController = false
                            controllerAutoShow = false
                            controllerShowTimeoutMs = 0
                            // Optionally, disable focus to prevent any accidental controller activation.
                            isFocusable = false
                            isClickable = false
                        }
                    }
                )
            } else {
                val bitmap by remember(videoUri) { mutableStateOf(getVideoThumbnail(context, videoUri)) }
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Video Thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } ?: Text(
                    text = "No Thumbnail",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(1.dp))

        // 🔹 Reflection Video Player (Stable)
        Box(
            modifier = Modifier
                .width(if (isCentered) 105.dp else 175.dp) // Adjust width dynamically
                .height(130.dp) // Same as the main video
                .graphicsLayer {
                    scaleY = -1f  // Flips the reflection
                    rotationX = 180f  // Fully rotates it upside down
                    alpha = 0.8f
                    clip = true
                }
                .drawWithCache {
                    val fade = Brush.verticalGradient(
                        colors = listOf(Color.White, Color.Transparent, Color.Transparent)
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(fade, blendMode = BlendMode.DstIn)
                    }
                }
        ) {
            if (isPlaying) {
                // Use the already created exoPlayerReflection instance
                AndroidView(
                    factory = { ctx ->
                        TextureView(ctx).apply {
                            val textureView = this

                            surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                                override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                                    val surface = Surface(surfaceTexture)
                                    exoPlayerReflection.setVideoSurface(surface)

                                    // Apply matrix to flip video vertically (upside down)
                                    textureView.post {
                                        val matrix = Matrix().apply {
                                            postScale(1f, -1f, width / 2f, height / 2f)
                                        }
                                        textureView.setTransform(matrix)
                                    }
                                }

                                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
                                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                                    exoPlayerReflection.setVideoSurface(null)
                                    return true
                                }
                                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                            }
                        }
                    }
                )
            } else {
                val reflectionBitmap = remember { getVideoThumbnail(context, videoUri) }
                reflectionBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Reflection",
                        modifier = Modifier
                            .width(if (isCentered) 105.dp else 175.dp)
                            .graphicsLayer { rotationX = 180f },
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // 🔹 Gradient Overlay for Realistic Reflection
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.0f), // ✨ Top - Fully Bright
                                Color.Black.copy(alpha = 0.3f), // 🔆 Middle - Slight Dim
                                Color.Black.copy(alpha = 0.7f), // 🌒 Bottom - Darker Shadow
                                Color.Black.copy(alpha = 0.9f)  // 🌑 Last - Almost Black
                            )
                        )
                    )
            )
        }
    }
}

fun getVideoThumbnail(context: Context, uri: Uri): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val bitmap = retriever.getFrameAtTime(1000000L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        retriever.release()
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}