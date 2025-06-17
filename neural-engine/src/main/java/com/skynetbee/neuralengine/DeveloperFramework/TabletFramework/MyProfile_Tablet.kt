package com.skynetbee.neuralengine

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.skynetbee.neuralengine.DeveloperFramework.ViewModel
import com.skynetbee.neuralengine.GlobalNavController.navController
import java.io.File

//
// MyProfile_Tablet.kt
// DevEnvironment
//
// Created by A. Nithish on 13-03-2025





@Composable
fun MyProfile_Tablet( viewModel: ViewModel = viewModel()) {


    val context = LocalContext.current

    if (isTabletInLandscape()) {

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

        Row {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 100.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
//                Rank holderes
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        viewModel.profile.forEach { (imageRes, name, position) ->
                            ProfileWithDetails_Tablet(imageRes, name, position)
                        }
                    }
                }

                Spacer(Modifier.padding(top = 60.dp))
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 50.dp, end = 50.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Your\n${ viewModel.r1}\nRank",
                                fontSize = 35.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                textAlign = TextAlign.Center,
                                lineHeight = 35.sp
                            )

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(start = 30.dp)
                                    .size(if (isTabletInLandscape()) 110.dp else 140.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, Color.Gray, CircleShape)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        viewModel.uphoto ?: R.drawable.proffile_default
                                    ),
                                    contentDescription = "Profile Image",
                                    modifier = Modifier
                                        .size(140.dp)
                                        .clip(CircleShape)
                                        .clickable(
                                            onClick = {
                                                viewModel.moveToWorkAround(context)
                                            }
                                        ),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(Modifier.weight(1f))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(36.dp),
                                modifier = Modifier
                                    .padding(end = 40.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .padding(horizontal = 6.dp, vertical = 5.dp)
                                        .size(100.dp)
                                ) {
                                    Text(
                                        text = "${viewModel.completedProjects}/${viewModel.totalProjects}",
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSystemInDarkTheme()){Color.White}else{Color.Black}
                                    )
                                    val isTablet = isTabletInLandscape()
                                    Canvas(modifier = Modifier.size(if (isTablet) 100.dp else 125.dp)) {
                                        val strokeWidth = if (isTablet) 10.dp.toPx() else 18.dp.toPx()

                                        drawArc(
                                            color = Color.LightGray,
                                            startAngle = -90f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            style = Stroke(
                                                width = strokeWidth,
                                                cap = StrokeCap.Round
                                            )
                                        )

                                        drawArc(
                                            color = Color.Yellow,
                                            startAngle = -90f,
                                            sweepAngle = viewModel.progress,
                                            useCenter = false,
                                            style = Stroke(
                                                width = strokeWidth,
                                                cap = StrokeCap.Round
                                            )
                                        )
                                    }
                                }
                            }

                            Box(
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = "Credit\n${ viewModel.c1}\nPoints",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 35.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(top = 60.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uc( viewModel.name),
                                fontSize = 45.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = if (isSystemInDarkTheme()){Color.White}else{Color.Black}
                            )
                            Spacer(modifier = Modifier.padding(top = 13.dp))

                            StarRating(
                                "personal rating",
                                50.sp,
                                viewModel.rating.toInt()
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                LaunchedEffect(Unit) { // Fetch data only ONCE when the composable is launched

                    viewModel.fetchProject()
                }

                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val isTablet = this.maxWidth > 1000.dp

                    val targetValue1 = if (isTabletInLandscape()) 45.dp else 87.dp
                    val targetValue2 = if (isTabletInLandscape()) 333.dp else 455.dp
                    val tabIndicatorOffset by animateDpAsState(
                        targetValue = if (viewModel.currentPage == "In Progress") targetValue1 else targetValue2,
                        animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
                    )

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxHeight()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier.height(70.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxSize()
                                    ) {

                                        val isNotSelect = if (isSystemInDarkTheme()) Color.Yellow else Color(0xFF6200EA)

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
                                                    0xFFD50000
                                                ) else isNotSelect,
                                                fontSize = 38.sp
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
                                                    0xFFD50000
                                                ) else isNotSelect,
                                                fontSize = 38.sp
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 60.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .offset(x = tabIndicatorOffset)
                                                .width(195.dp)
                                                .height(4.dp)
                                                .background(
                                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                                )
                                        )
                                    }
                                }

                                if (viewModel.currentPage == "In Progress") {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp)
                                    ) {

                                        itemsIndexed(viewModel.inProgressProjects.sortedBy { it.remainingDays.toInt() }) { i, project ->

                                            ProjectItem_Tablet(
                                                project = project.name,
                                                deadline = formatChanger(project.deadline),
                                                remainingDays = project.remainingDays,
                                                progress = project.progress,
                                                level = project.level,
                                                index = i
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
                                            CompletedProjectItem_Tablet(
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
    } else {

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

        Column(
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
//                Rank holderes
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    viewModel.profile.forEach { (imageRes, name, position) ->
                        ProfileWithDetails_Tablet(imageRes, name, position)
                    }
                }
            }

// Safely get rating value
//            val rating =  viewModel.rate1.getOrNull(0)?.toString()?.toIntOrNull() ?: 0

            Spacer(Modifier.padding(top = 70.dp))
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 70.dp, end = 70.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Your\n${ viewModel.r1}\nRank",
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 40.sp
                        )

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(start = 40.dp)
                                .size(140.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.Gray, CircleShape)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    viewModel.uphoto ?: R.drawable.proffile_default
                                ),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape)
                                    .clickable(
                                        onClick = {
                                            viewModel.moveToWorkAround(context)
                                        }
                                    ),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(36.dp),
                            modifier = Modifier
                                .padding(end = 40.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .padding(horizontal = 6.dp, vertical = 5.dp)
                                    .size(140.dp)
                            ) {
                                Text(
                                    text = "${viewModel.completedProjects}/${viewModel.totalProjects}",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                                Canvas(modifier = Modifier.size(125.dp)) {
                                    val strokeWidth = 18.dp.toPx()

                                    drawArc(
                                        color = Color.LightGray,
                                        startAngle = -90f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = Stroke(
                                            width = strokeWidth,
                                            cap = StrokeCap.Round
                                        )
                                    )

                                    drawArc(
                                        color = Color.Yellow,
                                        startAngle = -90f,
                                        sweepAngle = viewModel.progress,
                                        useCenter = false,
                                        style = Stroke(
                                            width = strokeWidth,
                                            cap = StrokeCap.Round
                                        )
                                    )
                                }
                            }
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Credit\n${ viewModel.c1}\nPoints",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                                textAlign = TextAlign.Center,
                                lineHeight = 40.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uc( viewModel.name),
                            fontSize = 55.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                        Spacer(modifier = Modifier.height(13.dp))
                        StarRating(
                            "personal rating",
                            65.sp,
                            if (viewModel.rate1.isEmpty()){
                                0
                            } else {
                                viewModel.rate1.toInt()
                            }
                        )
                    }
                }
            }

//                Mega's Work

            LaunchedEffect(Unit) { // Fetch data only ONCE when the composable is launched

                viewModel.fetchProject()
            }

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                val isTablet = this.maxWidth > 1000.dp

                val tabIndicatorOffset by animateDpAsState(
                    targetValue = if (viewModel.currentPage == "In Progress") 87.dp else 455.dp,
                    animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .padding(20.dp)
                            .height(820.dp)
                            .background(
                                color = Color.Transparent,
                                shape = RoundedCornerShape(30.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier.height(70.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxSize()
                                ) {

                                    val isNotSelect = if (isSystemInDarkTheme()) Color.Yellow else Color(0xFF6200EA)

                                    Box(
                                        modifier = Modifier
                                            .weight(2f)
                                            .fillMaxHeight()
                                            .clickable(
                                                indication = null,
                                                interactionSource = remember { MutableInteractionSource() }
                                            ) {viewModel. currentPage = "In Progress" },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "In Progress",
                                            color = if (viewModel.currentPage == "In Progress") Color(
                                                0xFFD50000
                                            ) else {
                                               isNotSelect
                                            },
                                            fontSize = 38.sp
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
                                                0xFFD50000
                                            ) else {
                                              isNotSelect
                                            },
                                            fontSize = 38.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 60.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .offset(x = tabIndicatorOffset)
                                            .width(190.dp)
                                            .height(4.dp)
                                            .background(
                                                color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                            )
                                    )
                                }
                            }

                            if (viewModel.currentPage == "In Progress") {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    itemsIndexed(viewModel.inProgressProjects.sortedBy { it.remainingDays.toInt() }) { i, project ->
                                        ProjectItem_Tablet(
                                            project = project.name,
                                            deadline = formatChanger(project.deadline),
                                            remainingDays = project.remainingDays,
                                            progress = project.progress,
                                            level = project.level,
                                            index = i
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
                                        CompletedProjectItem_Tablet(
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
}

@Composable
fun ProfileWithDetails_Tablet(imageRes: String, name: String, position: Int) {

    val textcolour = if (isSystemInDarkTheme()){Color.White}else{Color.Black}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, // Center align image and text
    ) {
        ProfileImage_Tablet(imageRes)
        Spacer(modifier = Modifier.height(18.dp)) // Space between image and text
        Text(
            text = truncateName(name),
            fontSize = if (isTabletInLandscape())22.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            color = textcolour,
            maxLines = 1, // Prevents text from breaking into multiple lines
            overflow = TextOverflow.Ellipsis, // Adds "..." if the text is too long
            textAlign = TextAlign.Center, // Ensures text is centered

            modifier = Modifier.width(100.dp) // Fix width for uniformity
        )
        Spacer(modifier = Modifier.height(4.dp)) // Space between name and medal
        Text(text = getMedalEmoji(position), fontSize = 55.sp,fontWeight = FontWeight.ExtraBold) // Medal below name
    }
}


@Composable
fun ProfileImage_Tablet(imageRes: String) {
    Box(
        contentAlignment = Alignment.Center, // Centers the image inside the circular shape
        modifier = Modifier
            .size(120.dp) // Adjust size as needed
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
fun CompletedProjectItem_Tablet(projectName: String, rating: String) {

    val textcolour = if (isSystemInDarkTheme()){Color.White}else{Color.Black}
    Row(
        modifier = Modifier
            .padding(16.dp)
            .height(100.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = truncateName( uc(projectName)),
            style = TextStyle(
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                color = textcolour,
                textAlign = TextAlign.Start
            ),
            modifier = Modifier.weight(1f)
        )

        StarRating(
            "completed project",
            45.sp,
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


@Composable
fun ProjectItem_Tablet(
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

    Box(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .backgroundCard()
            .clip(RoundedCornerShape(15.dp))
            .clickable {
                navController?.navigate("project_${index + 1}")
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .absolutePadding(left = 15.dp)
        ) {
            // Project Name at the Top Center
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Side: Deadline & Remaining Days
                Column(
                    modifier = Modifier.weight(2f)
                ) {
                    Text(
                        text = truncateName(uc(project)),
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    Text(
                        text = "💀 $deadline  / ${index+1}.kt",
                        style = TextStyle(fontSize = 26.sp),
                        color = if(isSystemInDarkTheme())Color.White else Color.Black
                    )
                }
                // Right Side: Progress Box (Fixed Size)
                Column( // Use Column to arrange items vertically
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.size(100.dp)
                ) {
                    ProgressBox_Tablet(progressPercentage = progress, remaingdays = remainingDays)
                }
            }
        }
    }
}


@Composable
fun ProgressBox_Tablet(
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
            .size(120.dp)
            .absolutePadding(left = 10.dp, top = 10.dp),
        contentAlignment = Alignment.Center // Ensures text is centered
    ) {
        Canvas(
            modifier = Modifier.size(70.dp) // Increased Canvas size
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
            text = remaingdays,
            color = if(isSystemInDarkTheme())Color.White else Color.Black,
            fontSize = 22.sp, // Slightly larger text
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(x = (-4).dp, y = (-3).dp)
        )
    }
}