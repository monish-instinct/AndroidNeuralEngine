package com.skynetbee.neuralengine

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.min
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateListOf


// Format time as mm:ss
fun formatTime(ms: Int): String {
    val seconds = ms / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%02d:%02d".format(minutes, remainingSeconds)
}

// Data class for audio message with waveform & duration.
data class AudioItem(
    val uri: Uri,
    val timestamp: Long,
    val waveformHeights: List<Int>,
    val duration: Int // in milliseconds
)

data class Audio(
    val name: String,
    val audio: AudioItem
)

// Generate random waveform heights (21 bars, values between 6 and 30)
fun generateWaveformHeights(): List<Int> {
    return List(21) { (6..30).random() }
}

// WhatsApp-style bubble shape.
val whatsappBubbleShape: Shape = GenericShape { size, _ ->
    val tailSize = 16f
    val cornerRadius = 12f
    moveTo(cornerRadius, 0f)
    lineTo(size.width - cornerRadius - tailSize, 0f)
    quadraticBezierTo(size.width - tailSize, 0f, size.width - tailSize, cornerRadius)
    lineTo(size.width - tailSize, size.height - cornerRadius)
    quadraticBezierTo(size.width - tailSize, size.height, size.width - tailSize - cornerRadius, size.height)
    lineTo(cornerRadius, size.height)
    quadraticBezierTo(0f, size.height, 0f, size.height - cornerRadius)
    lineTo(0f, cornerRadius)
    quadraticBezierTo(0f, 0f, cornerRadius, 0f)
    close()
    moveTo(size.width - tailSize, size.height / 2 - tailSize)
    lineTo(size.width, size.height / 2)
    lineTo(size.width - tailSize, size.height / 2 + tailSize)
    close()
}

var getAudio = mutableStateMapOf<String,List<String>>()

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@SuppressLint("RememberReturnType")
@Composable
fun AudioUploader(mediaPlayer: MediaPlayer, id : String) {
    val context = LocalContext.current


    // Main list and backup for deleted items.
    var audioId = remember { mutableStateListOf<String>() }

    
    var savedAudioList by rememberSaveable { mutableStateOf(listOf<AudioItem>()) }
    var deletedAudioList by remember { mutableStateOf(listOf<AudioItem>()) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPlayingUri: Uri? by remember { mutableStateOf(null) }

    // Global playback state.
    var currentProgress by remember { mutableStateOf(0) }
    var totalDuration by remember { mutableStateOf(0) }
    val audioProgressMap = remember { mutableStateMapOf<Uri, Int>() }

    val itemsPerPage = 3
    var currentPage by remember { mutableStateOf(0) }

    LaunchedEffect(savedAudioList.size) {
        val maxPage = (savedAudioList.size - 1) / itemsPerPage
        if (currentPage > maxPage) {
            currentPage = maxPage.coerceAtLeast(0)
        }
    }

    // Sample audio resources.
    val audioResources = listOf(
        R.raw.audio1_ben10,
        R.raw.audio2_doremon,
        R.raw.audio3_shianchan,
        R.raw.audio4_myringtone,
        R.raw.audio5_idontknow,
        R.raw.audio6_saafiringtone,
        R.raw.audio7_bachelorbgm,
        R.raw.audio8_mulumadhibgm,
        R.raw.audio9_uandi,
        R.raw.audio10_squidringtone
    )
    var currentAudioIndex by rememberSaveable { mutableStateOf(0) }


    // State for deletion mode. When non-null, it stores the index of the audio item in delete mode.
    var audioToDeleteIndex by remember { mutableStateOf<Int?>(null) }

    fun stopAudio() {
        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer.reset()
        currentPlayingUri?.let { uri ->
            audioProgressMap[uri] = currentProgress
        }
        isPlaying = false
        currentPlayingUri = null
        totalDuration = 0
     }

    // When "Record/Upload" is clicked:
    // First clear any deletion mode, then if there's deleted audio restore it,
    // otherwise add new audio from resources.
    fun handleChooseAudio() {
        // Clear deletion mode from any audio.
        audioToDeleteIndex = null

        if (isPlaying) stopAudio()
        if (deletedAudioList.isNotEmpty()) {
            // Restore one deleted audio at a time.
            val restoredAudio = deletedAudioList.first()
            savedAudioList = listOf(restoredAudio) + savedAudioList
            deletedAudioList = deletedAudioList.drop(1)
        } else if (currentAudioIndex < audioResources.size) {
            val resourceId = audioResources[currentAudioIndex]
            audioId.add(audioResources[currentAudioIndex].toString())
            currentAudioIndex++
            val resourceUri = Uri.parse("android.resource://${context.packageName}/$resourceId")
            val timestamp = System.currentTimeMillis()
            val tempMP = MediaPlayer.create(context, resourceId)
            val audioDuration = tempMP.duration
            tempMP.release()
            val waveform = generateWaveformHeights()
            savedAudioList = listOf(
                AudioItem(
                    uri = resourceUri,
                    timestamp = timestamp,
                    waveformHeights = waveform,
                    duration = audioDuration
                )
            ) + savedAudioList
            currentPage = 0
        } else {
            Toast.makeText(context, "No more audios", Toast.LENGTH_SHORT).show()
        }
    }

    fun formatTimestamp(time: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date(time))
    }
    Column(
        modifier = Modifier
            .backgroundCard()
            .height(350.dp)
            .fillMaxWidth(0.9f)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Replace IconButton with GradientButton
        SmartButton(
            onClick = {
                audioToDeleteIndex = null
                handleChooseAudio()

                audioId.forEach { value ->
                    Log.d("VideoGlob", "ADDED: ${value}")
                }

                getAudio[id] = audioId
            },
            content = {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Record/Upload",
                        color = Color(0xFFFFD966), // Your desired color
                        fontSize = 16.sp, // Text size
                        fontWeight = FontWeight.Bold // Optional: Bold text
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Mic",
                        tint = Color(0xFFFFD966),//Icon colour
                        modifier = Modifier.size(20.dp) // Icon size
                    )
                }
            },
//            modifier = Modifier
//                .padding(bottom = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // List of saved audio items.
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            userScrollEnabled = false
        ) {

            val startIndex = currentPage * itemsPerPage
            val endIndex = min(startIndex + itemsPerPage, savedAudioList.size)

            items(endIndex - startIndex){ index ->
                val originalIndex = startIndex + index
                val audioItem = savedAudioList[originalIndex]
                // Wrap each audio item in a Box to allow overlaying the delete UI.
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),

                        horizontalArrangement = Arrangement.End
                    ) {
                        if (isPlaying && currentPlayingUri == audioItem.uri) {
                            LaunchedEffect(isPlaying) {
                                while (isPlaying && mediaPlayer.isPlaying) {
                                    currentProgress = mediaPlayer.currentPosition
                                    delay(100)
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    2.dp,
                                    color = Color(0x30B7B4B4),
                                    RoundedCornerShape(16.dp)
                                )
                                .background(Color.Transparent, whatsappBubbleShape)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left: Play/Pause controls and progress text.
                                Column(
                                    modifier = Modifier.width(48.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    IconButton(onClick = {
                                        if (currentPlayingUri == audioItem.uri) {
                                            if (isPlaying) {
                                                mediaPlayer.pause()
                                                isPlaying = false
                                                audioProgressMap[audioItem.uri] =
                                                    currentProgress
                                            } else {
                                                mediaPlayer.start()
                                                isPlaying = true
                                            }
                                        } else {
                                            try {
                                                stopAudio()
                                                mediaPlayer.apply {
                                                    setDataSource(context, audioItem.uri)
                                                    prepare()
                                                    totalDuration = duration
                                                    currentProgress =
                                                        audioProgressMap[audioItem.uri] ?: 0
                                                    seekTo(currentProgress)
                                                    start()
                                                    setOnCompletionListener {
                                                        currentProgress = totalDuration
                                                        isPlaying = false
                                                    }
                                                }
                                                isPlaying = true
                                                currentPlayingUri = audioItem.uri
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                Toast.makeText(
                                                    context,
                                                    "Failed to play audio",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (isPlaying && currentPlayingUri == audioItem.uri)
                                                Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                            contentDescription = null,
                                            tint = Color(0xFF5EC1F9),
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }

                                val progressForThisAudio =
                                    if (currentPlayingUri == audioItem.uri && totalDuration > 0)
                                        currentProgress.toFloat() / totalDuration
                                    else
                                        ((audioProgressMap[audioItem.uri]
                                            ?: 0).toFloat() / audioItem.duration).coerceIn(0f, 1f)

                                // Determine if audio has finished.
                                val finished = if (currentPlayingUri == audioItem.uri) {
                                    totalDuration > 0 && currentProgress >= totalDuration
                                } else {
                                    (audioProgressMap[audioItem.uri] ?: 0) >= audioItem.duration
                                }

                                // Middle: Waveform area.
                                // Tap on it seeks; long press enters deletion mode.

                                // ... Waveform area code remains the same
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .pointerInput(Unit) {
                                            detectTapGestures(
                                                onTap = { offset ->
                                                    // 1. Delete மோட் ஐ கேன்சல் செய்ய
                                                    if (audioToDeleteIndex == index) {
                                                        audioToDeleteIndex = null
                                                    }
                                                    // 2. Seek செயல்பாடு
                                                    else if (totalDuration > 0) {
                                                        // 3. Seek அனுமதிக்க நிபந்தனைகள்:
                                                        //    - இந்த ஆடியோ தற்போது பிளே செய்துகொண்டிருக்கும் (currentPlayingUri == audioItem.uri)
                                                        //    - அல்லது எந்த ஆடியோவும் பிளே செய்யப்படவில்லை (currentPlayingUri == null)
                                                        if (currentPlayingUri == null || currentPlayingUri == audioItem.uri) {
                                                            val newFraction = offset.x / size.width
                                                            currentProgress =
                                                                (newFraction * totalDuration).toInt()
                                                            mediaPlayer.seekTo(currentProgress)


                                                            // தானாக பிளே தொடங்கும் (optional)
                                                            if (!isPlaying) {
                                                                mediaPlayer.start()
                                                                isPlaying = true
                                                            }
                                                        }
                                                    }
                                                },
                                                onLongPress = {
                                                    audioToDeleteIndex = index
                                                }
                                            )
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val waveformHeights = audioItem.waveformHeights
                                        val totalBars = waveformHeights.size
                                        waveformHeights.forEachIndexed { idx, height ->
                                            val barPositionFraction = (idx + 0.5f) / totalBars
                                            val targetColor = if (finished)
                                                Color.Gray else if (barPositionFraction <= progressForThisAudio)
                                                Color(0xFF5EC1F9) else Color.Gray
                                            val animatedBarColor by animateColorAsState(
                                                targetValue = targetColor,
                                                animationSpec = tween(durationMillis = 100)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .width(3.dp)
                                                    .height(height.dp)
                                                    .background(animatedBarColor, CircleShape)
                                            )
                                        }
                                    }

                                    val dotSize = 12.dp
                                    val density = LocalDensity.current
                                    var containerWidth by remember { mutableStateOf(0f) }
                                    var containerHeight by remember { mutableStateOf(0f) }
                                    var dotOffsetXState by remember { mutableStateOf<Float?>(null) }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .align(Alignment.Center)
                                            .onGloballyPositioned { coords ->
                                                containerWidth = coords.size.width.toFloat()
                                                containerHeight = coords.size.height.toFloat()
                                            }
                                    ) {
                                        val computedDotOffsetX = dotOffsetXState
                                            ?: (progressForThisAudio * containerWidth).coerceIn(
                                                0f,
                                                containerWidth
                                            )

                                        LaunchedEffect(
                                            progressForThisAudio,
                                            finished,
                                            containerWidth
                                        ) {
                                            if (!finished) {
                                                dotOffsetXState =
                                                    progressForThisAudio * containerWidth
                                            } else {
                                                dotOffsetXState = 0f
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .offset {
                                                    val dotPx = with(density) { dotSize.toPx() }
                                                    IntOffset(
                                                        x = (computedDotOffsetX - dotPx / 2).toInt(),
                                                        y = ((containerHeight - dotPx) / 2).toInt()
                                                    )
                                                }
                                                .size(dotSize)
                                                .background(Color(0xFF5EC1F9), CircleShape)
                                                .pointerInput(
                                                    currentPlayingUri,
                                                    isPlaying
                                                ) { // Add relevant keys
                                                    // Enable drag only if this is the currently playing audio AND player is active
                                                    if (isPlaying && currentPlayingUri == audioItem.uri) {
                                                        detectHorizontalDragGestures(
                                                            onDragStart = {
                                                                dotOffsetXState = computedDotOffsetX
                                                            },
                                                            onHorizontalDrag = { _, dragAmount ->
                                                                val newX = ((dotOffsetXState
                                                                    ?: computedDotOffsetX) + dragAmount)
                                                                    .coerceIn(0f, containerWidth)
                                                                dotOffsetXState = newX
                                                                val newFraction =
                                                                    newX / containerWidth
                                                                currentProgress =
                                                                    (newFraction * totalDuration).toInt()
                                                                mediaPlayer.seekTo(currentProgress)
                                                            },
                                                            onDragEnd = {
                                                                mediaPlayer.start()
                                                                dotOffsetXState = null
                                                            }
                                                        )
                                                    }
                                                }
                                        )
                                    }
                                }

                                // Right: Timestamp.
                                Column(
                                    modifier = Modifier
                                        .padding(14.dp)
                                        .align(Alignment.Bottom)
                                ) {
                                    val displayProgress =
                                        if (isPlaying && currentPlayingUri == audioItem.uri)
                                            currentProgress else (audioProgressMap[audioItem.uri]
                                            ?: audioItem.duration)
                                    Text(
                                        text = formatTime(displayProgress),
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = formatTimestamp(audioItem.timestamp),
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                    // If this audio item is in deletion mode, overlay a semi-transparent layer
                    // with a circular delete button (with an "×" icon) at the top-right corner.
                    if (audioToDeleteIndex == index) {
                        // Full-size overlay that cancels deletion mode on tap.
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFFFD966).copy(alpha = 0.5f))
                                .clickable { audioToDeleteIndex = null }
                        )
                        // Circular delete button.
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f))
                                .border(
                                    width = 1.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable {
                                    val deletedAudio = savedAudioList[index]
                                    deletedAudioList =
                                        listOf(deletedAudio) + deletedAudioList
                                    savedAudioList =
                                        savedAudioList.filterIndexed { i, _ -> i != index }

                                    audioId.removeAt(index)
                                    if (currentPlayingUri == deletedAudio.uri) {
                                        stopAudio()
                                        currentProgress = 0  // Reset progress
                                        totalDuration = 0
                                        currentPlayingUri = null
                                    }

                                    audioToDeleteIndex = null

                                    audioId.remove(deletedAudio.toString())
                                    audioId.forEach { value ->
                                        Log.d("VideoGlob", "REMOVED: ${value}")
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Delete Audio",
                                tint = Color.Black,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    getAudio[id] = audioId
                }
            }
            Log.d("poiuyuiop", "AudioUploader: ${getAudio[id]}")
        }

        val totalPages = ceil(savedAudioList.size.toFloat() / itemsPerPage)
            .toInt()
            .coerceAtLeast(1)

        if (totalPages > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = { currentPage = (currentPage - 1).coerceAtLeast(0) },
                    enabled = currentPage > 0
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous",
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
                Text(
                    text = "Page ${currentPage + 1} of $totalPages",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                IconButton(
                    onClick = { currentPage = (currentPage + 1).coerceAtMost(totalPages - 1) },
                    enabled = currentPage < totalPages - 1
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }
    }
}