package com.skynetbee.neuralengine

//  
// MyProfile_Mobile.kt
// DevEnvironment
//
// Created by A. Nithish on 13-03-2025

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.skynetbee.neuralengine.DeveloperFramework.ViewModel
import com.skynetbee.neuralengine.GlobalNavController.navController
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class InProgressProject(
    val name: String,
    val level: String,
    val deadline: String,
    val remainingDays: String,
    val progress: Int
)
data class CompletedProject(
    val name : String,
    val star : String
)

@Composable
fun MyProfile_Mobile(viewModel : ViewModel = viewModel()) {


    val context = LocalContext.current

    BackHandler {
        // Ask the user if they want to exit
        val exitDialog = AlertDialog.Builder(context)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                // Close the app if the user agrees
                (context as? Activity)?.finish()
            }
            .setNegativeButton("No", null)
            .create()

        exitDialog.show()
    }


    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 80.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                viewModel.profile.forEach { (imageRes, name, position) ->
                    ProfileWithDetails_Mobile(imageRes, name, position)
                }
            }
        }
        Spacer(modifier = Modifier.padding(top = 40.dp))

        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val r1Value = viewModel.r1?.toString().takeIf { it?.isNotBlank() == true } ?: "0"

                    // Your / 5 / Rank stacked vertically and centered
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Your",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD277),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = r1Value,
                            fontSize = 24.sp, // Larger and bolder for emphasis
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFBFBFBF),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Rank",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD277),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Profile Image
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .size(70.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Gray, CircleShape)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                viewModel.uphoto ?: R.drawable.proffile_default
                            ),
                            contentDescription = "Profile Image",
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .clickable {
                                    viewModel.moveToWorkAround(context)
                                },
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Progress circle with completed/total projects
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 5.dp)
                            .size(60.dp)
                    ) {
                        Text(
                            text = "${viewModel.completedProjects}/${viewModel.totalProjects}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBFBFBF)
                        )
                        Canvas(modifier = Modifier.size(68.dp)) {
                            val strokeWidth = 8.dp.toPx()
                            drawArc(
                                color = Color.LightGray,
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = Color.Yellow,
                                startAngle = -90f,
                                sweepAngle = viewModel.progress,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Credit Points (Credit / value / Points)
                    val c1Value = viewModel.c1?.toString().takeIf { !it.isNullOrBlank() } ?: "0"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = "Credit",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD277),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = c1Value,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFBFBFBF),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Points",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD277),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Name + Rating section
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uc(viewModel.name),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFFFFD277)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    StarRating(
                        "personal rating",
                        35.sp,
                        if (viewModel.rate1.isEmpty()) 0 else viewModel.rate1.toInt()
                    )
                }
            }
        }


        Spacer(modifier = Modifier.padding(top = 10.dp))


//                Mega's Work

        LaunchedEffect(Unit) { // Fetch data only ONCE when the composable is launched
            viewModel.fetchProject()
        }

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val density = LocalDensity.current.density
            val configuration = LocalConfiguration.current
            val screenWidth = (configuration.screenWidthDp * density)
            val isTablet = this.maxWidth > 1000.dp

//            val tabIndicatorOffset by animateDpAsState(
//                targetValue = if (viewModel.currentPage == "In Progress") (screenWidth * 0.035).dp else (screenWidth * 0.175).dp,
//                animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
//            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(20.dp)
                        .height( 820.dp)
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(30.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(if (isTablet) 24.dp else 16.dp)
                    ) {
                        Box(
                            modifier = Modifier.height(50.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(2f)
                                        .fillMaxHeight()
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) { viewModel.currentPage = "In Progress" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "In Progress",
                                        color = if (viewModel.currentPage == "In Progress") Color(
                                            0xFFFFD277
                                        ) else Color(0xFFBFBFBF), // 0.75 * 255 = 191 → hex: BF ,
                                        fontSize = 18.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(2f)
                                        .fillMaxHeight()
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }
                                        ) { viewModel.currentPage = "Completed" },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Completed",
                                        color = if (viewModel.currentPage == "Completed") Color(
                                            0xFFFFD277
                                        ) else Color(0xFFBFBFBF),// 0.75 * 255 = 191 → hex: BF ,
                                        fontSize = 18.sp
                                    )
                                }
                            }
//
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(top = 48.dp)
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        //.offset(x = tabIndicatorOffset)
//                                        .width(95.dp)
//                                        .height(4.dp)
//                                        .background(
//                                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
//                                        )
//                                )
//                            }
                        }

                        if (viewModel.currentPage == "In Progress") {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 15.dp)
                            ) {
                                itemsIndexed(viewModel.inProgressProjects.sortedBy { it.remainingDays.toInt() }) { index, project ->
                                    ProjectItem_Mobile(
                                        project = project.name,
                                        deadline = formatChanger(project.deadline),
                                        remainingDays = project.remainingDays,
                                        progress = project.progress,
                                        level = project.level,
                                        index = index
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            ) {
                                items(viewModel.completedProjects_M) { project ->
                                    CompletedProjectItem_Mobile(
                                        projectName = project.name,
                                        rating = project.star
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileWithDetails_Mobile(imageRes: String, name: String, position: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Center align image and text
        modifier = Modifier.width(100.dp) // Fix width to avoid uneven spacing
    ) {
        ProfileImage_Mobile(imageRes)
        Spacer(modifier = Modifier.height(8.dp)) // Space between image and text
        Text(
            text = truncateName(name),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD277),
            maxLines = 1, // Prevents text from breaking into multiple lines
            overflow = TextOverflow.Ellipsis, // Adds "..." if the text is too long
            textAlign = TextAlign.Center, // Ensures text is centered

            modifier = Modifier.width(100.dp) // Fix width for uniformity
        )
        Spacer(modifier = Modifier.height(4.dp)) // Space between name and medal
        Text(text = getMedalEmoji(position), fontSize = 35.sp,fontWeight = FontWeight.ExtraBold) // Medal below name
    }
}

@Composable
fun ProfileImage_Mobile(imageRes: String) {
    Box(
        contentAlignment = Alignment.Center, // Centers the image inside the circular shape
        modifier = Modifier
            .size(60.dp) // Adjust size as needed
            .clip(CircleShape) // Ensures the image is circular
            .border(0.5.dp, Color.Gray, CircleShape) // Optional: Adds an Instagram-style border
            .background(Color.LightGray) // Optional: Background color if image fails to load

    ) {
        Image(
            painter = rememberImagePainter(imageRes), // Load image from file path
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop, // This ensures the image fills the circle properly

            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )

    }
}

@Composable
fun CompletedProjectItem_Mobile(projectName: String, rating: String) {

    val textcolour = if (isSystemInDarkTheme()){Color.White}else{Color.Black}
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .absolutePadding(left = 5.dp)
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = truncateName(projectName.replaceFirstChar { it.uppercaseChar() }),
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = textcolour,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.weight(1f)
            )

            StarRating(
                "completed project",
                25.sp,
                rating.toInt()
            )
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = textcolour,
            thickness = 1.dp
        )
    }
}


@Composable
fun ProjectItem_Mobile(
    project: String,
    deadline: String,
    remainingDays: String,
    progress: Int,
    level: String,
    index:Int
) {

    val titleColor = when (level.lowercase()) {
        "easiest" -> Color.Green
        "easy" -> Color(0xFF388E3C)
        "medium" -> Color.Blue
        "hard" -> Color.Red
        "very hard" -> Color(0xFF3E2723)
        else -> Color.Black
    }

    Card(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth(1f)
            .wrapContentHeight()
            .clickable {
                navController?.navigate("project_${index + 1}")
            }, // Card height reduced
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSystemInDarkTheme())Color.Black.copy(0.5f) else Color.LightGray.copy(0.4f))
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            // Project Name at the Top Center
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Side: Deadline & Remaining Days
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Text(
                        text = "${index + 1}. kt " + truncateName(uc(project)),
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFD277),
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )

                    Text(
                        text = "💀 $deadline ",
                        style = TextStyle(fontSize = 20.sp),
                        color = Color(0xFFBFBFBF) // 0.75 * 255 = 191 → hex: BF
                    )
                }
                // Right Side: Progress Box (Fixed Size)
                Column( // Use Column to arrange items vertically
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(start = 10.dp)
                ) {
                    ProgressBox_Mobile(progressPercentage = progress, remaingdays = remainingDays)
                }
            }
        }
    }
    Spacer(modifier = Modifier.padding(8.dp))
}


@Composable
fun ProgressBox_Mobile(
    progressPercentage: Int, // Progress as an integer (0 to 100)
    backgroundCircleColor: Color = Color.LightGray, // Background circle color
    remaingdays : String
) {
    var progNum = progressPercentage

    if (progNum < 0)
        progNum = 0

    Log.d("TAG", "ProgressBox:$progNum ")


    val progress = progNum / 100f // Convert percentage to float

    Log.d("TAG", "ProgressBox:$progress ")

    // Dynamic color selection based on progress
    val progressColor by remember(progNum) {
        mutableStateOf(
            when {
                progNum in 76..100 -> Color.Green
                progNum in 51..75 -> Color.Yellow
                progNum in 26..50 -> Color(0xFFFFA500) // Orange
                progNum in 0..25 -> Color.Red
                else -> null
            }
        )
    }

    Box(
        modifier = Modifier
            .size(100.dp)
            .absolutePadding(left = 10.dp, top = 10.dp),
        contentAlignment = Alignment.Center // Ensures text is centered
    ) {
        Canvas(
            modifier = Modifier.size(60.dp) // Increased Canvas size
        ) {
            val strokeWidth = 9.dp.toPx() // Increased thickness of the stroke
            val center = Offset((size.width / -14), size.height /-12)
            val radius = size.minDimension / 1.5f - strokeWidth

            // Draw Background Circle
            drawArc(
                color = backgroundCircleColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = center
            )

            // Draw Progress Arc
            progressColor?.let {
                drawArc(
                    color = it,
                    startAngle = -90f,
                    sweepAngle = ((1 - progress) * 360), // Reverse progress
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    size = Size(radius * 2, radius * 2),
                    topLeft = center
                )
            }
        }

        // Progress Percentage Text
        Text(
            text = "${remaingdays}",
            color = Color(0xFFBFBFBF),
            fontSize = 20.sp, // Slightly larger text
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(x = (-5).dp,y = (-3).dp)
        )
    }
}

fun getMedalEmoji(position: Int): String {
    return when (position) {
        1 -> "🥇" // Gold Medal
        2 -> "🥈" // Silver Medal
        3 -> "🥉" // Bronze Medal
        else -> "" // No medal for other positions
    }
}

fun truncateName(name: String): String {

    return if (name.length > 20) {
        name.take(20) + "..."  // Show first 20 letters + "..."
    } else {
        name  // Show full name if 20 or fewer characters
    }
}


@SuppressLint("NewApi")
fun getDeadLine(end: String): String {
    Log.d("mega", "getDeadLine: Input Date -> $end")

    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Input format
    val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // Desired output format

    // Parse the input date
    val endDate = LocalDate.parse(end, inputFormatter)

    // Format it to dd-MM-yyyy (optional, if you want to log it)
    val formattedEnd = endDate.format(outputFormatter)

    // Get today's date
    val startDate = LocalDate.now()

    // Calculate remaining days
    val remainingDays = ChronoUnit.DAYS.between(startDate, endDate)

    Log.d("mega", "Formatted End Date: $formattedEnd, Remaining Days: $remainingDays")

    return remainingDays.toString()
}


@SuppressLint("NewApi")
fun getTotalDays(start: String, end: String): Int {
    Log.d("Nithish", "getTotalDays: $start --- $end")

    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Input format
    val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy") // Desired output format

    val startDate = LocalDate.parse(start, inputFormatter)
    val endDate = LocalDate.parse(end, inputFormatter)

    // Convert to dd-MM-yyyy format
    val formattedStart = startDate.format(outputFormatter)
    val formattedEnd = endDate.format(outputFormatter)

    Log.d("Nithish", "Formatted Start: $formattedStart, Formatted End: $formattedEnd")

    return ChronoUnit.DAYS.between(startDate, endDate).toInt()
}

@SuppressLint("NewApi")
fun formatChanger(date: String): String {
    return try {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy")
        val parsedDate = LocalDate.parse(date, inputFormatter)
        parsedDate.format(outputFormatter) // Format to required output
    } catch (e: Exception) {
        "Invalid date format" // Handle errors safely
    }
}

fun showToast(context: Context, message: String, duration: Int): Toast {
    return Toast.makeText(context, message, duration).apply { show() }
}