//package skynetbee.developers.DevEnvironment
//
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.isSystemInDarkTheme
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.material3.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowForward
//import androidx.compose.material.icons.filled.Mic
//import androidx.compose.runtime.*
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import kotlin.math.max
//import kotlin.math.min
//
//
//// Data Model
//data class U(val username: String, val profilePic: String? = null) // ✅ Image is optional
//
//@Composable
//fun <T> Pagination(
//    id: String,
//    dataList: List<T>,
//    pageSize: Int,
//    getSearchText: (T) -> String,
//    itemContent: @Composable (T) -> Unit
//) {
//    var searchQuery by rememberSaveable(id) { mutableStateOf("") }
//    var currentPage by rememberSaveable(id) { mutableStateOf(1) }
//
//// 🔹 Step 1: Get search results
//    var filteredItems = dataList.filter { getSearchText(it).contains(searchQuery, ignoreCase = true) }
//
//// 🔹 Step 2: Get remaining items (those NOT in search results)
//    val remainingItems = dataList.filterNot { filteredItems.contains(it) }
//
//// 🔹 Step 3: Merge them: searched items first, remaining items below
//    filteredItems = filteredItems + remainingItems
//
//// 🔹 Step 4: Keep original total pages count
//    val totalPages = max(1, (dataList.size + pageSize - 1) / pageSize)
//
//
//    Log.d("vbn", "Pagination: $searchQuery---------$filteredItems")
//
//    // ✅ Get items for the current page
//    fun getCurrentPageItems(): List<T> {
//        val startIndex = (currentPage - 1) * pageSize
//        val endIndex = min(startIndex + pageSize, filteredItems.size)
//        val pageItems = filteredItems.subList(startIndex, endIndex).toMutableList()
//
//        // ✅ If last page has fewer items, take from the beginning of the full list
//        var fillIndex = 0
//        while (pageItems.size < pageSize && fillIndex < filteredItems.size) {
//            val item = filteredItems[fillIndex]
//            if (!pageItems.contains(item)) {
//                pageItems.add(item)
//            }
//            fillIndex++
//        }
//
//        return pageItems
//    }
//
//
//    Box(
//        Modifier
//            .wrapContentSize()
//            .padding(10.dp)
//            .background(Color.Black.copy(0.4f), shape = RoundedCornerShape(25.dp))
//    ) {
//        Column(modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)) {
//            // 🔍 Search Bar
//            BasicTextField(
//                value = searchQuery,
//                onValueChange = { query ->
//                    searchQuery = query
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(
//                        if (isSystemInDarkTheme()) Color.Gray.copy(0.4f) else Color.LightGray.copy(
//                            0.2f
//                        )
//                    ),
//                textStyle = TextStyle(color = Color.Blue, textAlign = TextAlign.Start, fontSize = 20.sp),
//                decorationBox = { innerTextField ->
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.padding(15.dp)
//                    ) {
//                        Box(modifier = Modifier.weight(1f)) {
//                            if (searchQuery.isEmpty()) {
//                                Text(
//                                    "Search...",
//                                    color = if (isSystemInDarkTheme()) Color.Gray else Color.Black,
//                                    fontSize = 20.sp
//                                )
//                            }
//                            innerTextField() // Show cursor even when empty
//                        }
//                        Icon(
//                            imageVector = Icons.Default.Mic,
//                            contentDescription = "Voice Search",
//                            modifier = Modifier
//                                .size(30.dp)
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) {
//                                    // Handle mic action
//                                }
//                        )
//                    }
//                }
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // 🔽 Display Items
//            LazyColumn {
//                items(getCurrentPageItems()) { item ->
//                    itemContent(item)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // 🔀 Pagination Controls
//            PaginationControls(currentPage, totalPages) { newPage ->
//                currentPage = newPage
//            }
//        }
//    }
//}
//
//
//
//@Composable
//fun PaginationControls(
//    currentPage: Int,
//    totalPages: Int,
//    onPageChange: (Int) -> Unit
//) {
//
//    val pageNo = remember { mutableStateOf("") }
//    if (totalPages < 1) return
//
//    val maxPageNumbersToShow = 2 //
//
//    val firstVisiblePage = max(1, min(currentPage - maxPageNumbersToShow / 2, totalPages - maxPageNumbersToShow + 1))
//    val lastVisiblePage = min(totalPages, firstVisiblePage + maxPageNumbersToShow - 1)
//
//    Row(
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 20.dp)
//    ) {
//        // ⬅️ Previous Page Button
//        IconButton(
//            onClick = {
//                onPageChange(1)
//            }
//        ) {
//            Icon(
//                painter = painterResource(R.drawable.double_arrow),
//                contentDescription = "Double Arrow",
//                tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
//                modifier = Modifier
//                    .graphicsLayer(
//                        scaleX = -1f
//                    )
//                    .size(20.dp)
//            )
//        }
//        IconButton(
//            onClick = { if (currentPage > 1) onPageChange(currentPage - 1) },
//            enabled = currentPage > 1
//        ) {
//            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous", tint = if (isSystemInDarkTheme()) Color.White else Color.Black)
//        }
//
//        // 🔢 Page Number Boxes (Always Shows up to 3 Pages)
//        Row {
//            for (page in firstVisiblePage..lastVisiblePage) {
//                Box(
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                        .background(if (page == currentPage) Color.Blue else Color.LightGray)
//                        .clickable { onPageChange(page) },
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(text = "$page", color = if (page == currentPage) Color.White else Color.Black, fontSize = 16.sp)
//                }
//                Spacer(modifier = Modifier.width(8.dp))
//            }
//        }
//
//        // ➡️ Next Page Button
//        IconButton(
//            onClick = { if (currentPage < totalPages) onPageChange(currentPage + 1) },
//            enabled = currentPage < totalPages
//        ) {
//            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next", tint =if (isSystemInDarkTheme()) Color.White else Color.Black)
//        }
//
//        IconButton(
//            onClick = {
//                onPageChange(totalPages)
//            }
//        ) {
//            Icon(
//                painter = painterResource(R.drawable.double_arrow),
//                contentDescription = "Double Arrow",
//                tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
//                modifier = Modifier
//                    .graphicsLayer(
//                        scaleX = 1f
//                    )
//                    .size(20.dp)
//            )
//        }
//
//        OutlinedTextField(
//            value = pageNo.value,
//            onValueChange = {
//                pageNo.value = it
//                it.toIntOrNull()?.let { page ->
//                    if (page >= totalPages) {
//                        pageNo.value = totalPages.toString()
//                        onPageChange(totalPages)
//                    } else {
//                        onPageChange(page)
//                    }
//                }
//            },
//            textStyle = TextStyle(
//                textAlign = TextAlign.Center,
//                color = if (isSystemInDarkTheme()) Color.Gray else Color.Black
//            ),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = if (isSystemInDarkTheme()) Color.Gray else Color.Black,
//                unfocusedBorderColor = if (isSystemInDarkTheme()) Color.Gray else Color.Black
//            ),
//            modifier = Modifier
//                .width(60.dp)
//                .height(50.dp)
//        )
//    }
//}
//
//
