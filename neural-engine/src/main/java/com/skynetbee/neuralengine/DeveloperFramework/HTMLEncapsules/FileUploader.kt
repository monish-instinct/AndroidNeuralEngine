package com.skynetbee.neuralengine


import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


var getFile = mutableStateMapOf<String, MutableList<String>>()
@Composable
fun FileUploader(id: String) { // Accepts only 'id'
    val fileNameArray = listOf(
        "Folderkajolzarazyaana.zip", "File1File1File1File1File1File1File1File1File1File1.pdf", "File2.docx",
        "File3.pptx", "File4.txt", "file5.pages", "file6.key", "file7.numbers", "file8.xlsx",
        "file9.zip", "file10.rar"
    )

    val file = remember { mutableStateListOf<String>() }

    val selectedFileNames = remember { mutableStateListOf<String>() }
    var deleteVisibleFile by remember { mutableStateOf<String?>(null) }
    var currentFileIndex by remember { mutableStateOf(0) }
    var showUploadText by remember { mutableStateOf(true) } // State to track text visibility

    Column(
        modifier = Modifier
            .backgroundCard()
            .padding(bottom = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Upload Button
        Icon(
            painter = painterResource(id = R.drawable.uploadicon),
            contentDescription = "Upload",
            tint = Color.White,
            modifier = Modifier
                .padding(top = 10.dp)
                .size(100.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (currentFileIndex < fileNameArray.size) {
                        val newFile = fileNameArray[currentFileIndex]
                        selectedFileNames.add(newFile)
                        deleteVisibleFile = null // Reset delete visibility
                        getFile[id] = getFile[id]?.toMutableList()?.apply { add(newFile) } ?: mutableListOf(newFile)
                        currentFileIndex++
                        showUploadText = false // Hide the text after first file upload
                    }
                }
        )

        // Show the text only if no files are uploaded
        if (showUploadText) {
            Text(
                text = "Click the upload icon to add files.",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        // File List
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(100.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            itemsIndexed(selectedFileNames) { index, fileName ->
                FileItem(
                    fileName = fileName,
                    isDeleteVisible = false,
                    removeAction = {
                        selectedFileNames.remove(fileName)
                        getFile[id] = getFile[id]?.toMutableList()?.apply { remove(fileName) } ?: mutableListOf()
                    }
                )
            }
        }
    }
}

@Composable
fun FileItem(
    fileName: String,
    isDeleteVisible: Boolean,
    removeAction: () -> Unit
) {
    var isDeleteButtonVisible by remember { mutableStateOf(isDeleteVisible) }

    Box(
        modifier = Modifier
            .size(100.dp)
            .background(if (isSystemInDarkTheme()) Color.LightGray.copy(0.2f) else Color.Black.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isDeleteButtonVisible = true // Show delete button only on long press
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        // File Name Display
        Text(
            text = if (fileName.length <= 10) fileName else "${fileName.take(10)}\n...\n${fileName.takeLast(10)}",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = 14.sp
        )

        // Delete Button (Appears only on long press)
        if (isDeleteButtonVisible) {
            IconButton(
                onClick = {
                    removeAction() // Remove file
                    isDeleteButtonVisible = false // Hide delete button after deletion
                },
                modifier = Modifier.align(Alignment.TopEnd).size(30.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.deleteicon),
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}