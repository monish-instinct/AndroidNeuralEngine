package skynetbee.developers.DevEnvironment

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import kotlin.math.abs
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("SuspiciousIndentation", "UnrememberedMutableState")
@Composable
fun SmartTable(Query :String, kvp: Map<String, String>?= null) {

    //<---Fetch Data From Database--->

    val allrows = sql.executeQuery("SELECT $Query")
    val columnList = allrows.second?.firstOrNull()?.keys?.toList() ?: emptyList()
    val tabledatalist = mutableListOf<List<String>>()

    allrows.second?.forEach { rowData ->
        val rowvalues = columnList.map { columnName ->
            val original = rowData[columnName]?.toString() ?: ""
            kvp?.get(original) ?: original  // replace if key exists in kvp, else keep original
        }

        tabledatalist.add(rowvalues)
    }

    val headers = columnList
    val data = tabledatalist

    Log.d("qwertyuiop", "$headers")
    Log.d("qwertyuiop", "$data")

    // <---Pagenation Variables--->

    val rowsPerPage = 5
    val totalItems = tabledatalist.size
    val totalPages = (totalItems + rowsPerPage - 1) / rowsPerPage
    var currentPage by remember { mutableIntStateOf(1) }
    val textfieldpage = remember { mutableStateOf("") }

    val startIndex = (currentPage - 1) * rowsPerPage
    val endIndex = minOf(startIndex + rowsPerPage, totalItems)
    val currentPageData = tabledatalist.subList(startIndex, endIndex)

    val pageNumber = textfieldpage.value.toIntOrNull() ?: currentPage

    // <---UI Variables--->

    val listState = rememberLazyListState()
    val density = LocalDensity.current

    val configuration = LocalConfiguration.current
    var isMinimized by remember { mutableStateOf(false) }

    val fontScale = configuration.fontScale
    val pinnedColumnIndices = remember { mutableStateListOf<Int>() }
    var columnwidth = 0.dp

    if (fontScale > 1.2f) { columnwidth = 220.dp } else { columnwidth = 120.dp }

    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    var showCard by remember { mutableStateOf(false) }
    var selectedColumnTitle by remember { mutableStateOf("") }
    var selectedDataList by remember { mutableStateOf<List<String>>(emptyList()) }
    var searchbarshow by remember { mutableStateOf(true) }

    // <---Filter Logic--->

    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedColumns by remember { mutableStateOf(columnList.indices.toSet()) }
    var tempSelectedColumns by remember { mutableStateOf(selectedColumns) }
    var checkboxStates by remember { mutableStateOf(MutableList(columnList.size) { false }) }

    var searchText by remember { mutableStateOf("") }

    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Select Columns") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LocalConfiguration.current.screenHeightDp.dp * 0.3f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    val selectAll = !checkboxStates.all { it }
                                    checkboxStates = MutableList(columnList.size) { selectAll }
                                    tempSelectedColumns =
                                        if (selectAll) columnList.indices.toSet() else emptySet()
                                }
                        ) {
                            Checkbox(
                                checked = checkboxStates.all { it },
                                onCheckedChange = {
                                    checkboxStates = MutableList(columnList.size) { _ -> it }
                                    tempSelectedColumns =
                                        if (it) columnList.indices.toSet() else emptySet()
                                }
                            )
                            Text("All Columns")
                        }

                        columnList.forEachIndexed { index, columnName ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        checkboxStates = checkboxStates.toMutableList()
                                            .apply { this[index] = !this[index] }
                                        tempSelectedColumns =
                                            if (checkboxStates[index]) tempSelectedColumns + index else tempSelectedColumns - index
                                    }
                            ) {
                                Checkbox(
                                    checked = checkboxStates[index],
                                    onCheckedChange = {
                                        checkboxStates = checkboxStates.toMutableList()
                                            .apply { this[index] = it }
                                        tempSelectedColumns =
                                            if (it) tempSelectedColumns + index else tempSelectedColumns - index
                                    }
                                )
                                Text(columnName)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SmartButton(
                        onClick = { showFilterDialog = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                    SmartButton(
                        onClick = {
                            selectedColumns = tempSelectedColumns
                            checkboxStates =
                                MutableList(columnList.size) { index -> index in selectedColumns }
                            showFilterDialog = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Apply", color = Color.White)
                    }
                }
            }
        )
    }

    if (showCard) {
        searchbarshow = false
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    showCard = false
                    searchbarshow = true
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.7f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFA500))
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = selectedColumnTitle,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            items(selectedDataList) { value ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(
                                            Color(0xFFE3F2FD),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = value.toString(),
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Tap to Exit",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }

    Column(modifier =
        Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxHeight(0.75f)
                .fillMaxWidth(0.94f),
            shape = RoundedCornerShape(25.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(0.1f))
        ) {

            if (searchbarshow) {
                SearchBar(
                    hint = "Search...",
                    onSearch = { query ->
                        searchText = query
                        currentPage = 1
                    }
                )
            }

            // <---Minimized Logic--->

            if (isMinimized) {

                val columnWidth = 40.dp
                val rowsPerPage = 20
                val totalPages = (data.size + rowsPerPage - 1) / rowsPerPage
                var currentPage by remember { mutableIntStateOf(1) }
                val paginatedData = data.drop((currentPage - 1) * rowsPerPage).take(rowsPerPage)

                var scale by remember { mutableFloatStateOf(1f) }
                var offsetX by remember { mutableFloatStateOf(0f) }
                var offsetY by remember { mutableFloatStateOf(0f) }

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    var isTwoFingerGesture by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.Transparent)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        scale = 1f
                                        offsetX = 0f
                                        offsetY = 0f
                                        isTwoFingerGesture = false
                                    }
                                )
                            }
                            .pointerInput(Unit) {
                                detectTransformGestures { centroid, pan, zoom, _ ->
                                    if (zoom != 1f) {
                                        isTwoFingerGesture = true
                                        val newScale = (scale * zoom).coerceIn(1f, 5f)
                                        val scaleFactor = newScale / scale
                                        offsetX =
                                            (offsetX - centroid.x) * scaleFactor + centroid.x + pan.x
                                        offsetY =
                                            (offsetY - centroid.y) * scaleFactor + centroid.y + pan.y
                                        scale = newScale
                                    } else if (pan.x != 0f && !isTwoFingerGesture) {
                                        offsetX += pan.x
                                    }
                                }
                            }
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offsetX,
                                translationY = offsetY
                            )
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Column(
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Row(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .background(Color.DarkGray)
                                    .padding(vertical = 1.dp)
                            ) {
                                headers.forEach { header ->

                                    Log.d("qwertyuiop", ".....${header}")
                                    Box(
                                        modifier = Modifier
                                            .width(columnWidth)
                                            .height(20.dp)
                                            .background(Color(0xFFFF9800))
                                            .combinedClickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                                onClick = {},
                                                onLongClick = { isMinimized = false }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = header,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 6.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            paginatedData.forEach { row ->
                                Log.d("qwertyuiop", ".........$row")
                                Row(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .background(Color.White)
                                ) {
                                    row.forEach { cell ->
                                        Box(
                                            modifier = Modifier
                                                .width(columnWidth)
                                                .height(25.dp)
                                                .background(Color.LightGray),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val annotatedString = buildAnnotatedString {
                                                val startIndex =
                                                    cell.indexOf(searchText, ignoreCase = true)
                                                if (startIndex != -1) {
                                                    append(cell.substring(0, startIndex))
                                                    withStyle(
                                                        SpanStyle(
                                                            color = Color.Red,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    ) {
                                                        append(
                                                            cell.substring(
                                                                startIndex,
                                                                startIndex + searchText.length
                                                            )
                                                        )
                                                    }
                                                    append(cell.substring(startIndex + searchText.length))
                                                } else {
                                                    append(cell)
                                                }
                                            }
                                            Text(
                                                text = annotatedString,
                                                maxLines = 1,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Black,
                                                textAlign = TextAlign.Center,
                                                fontSize = 6.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp)) // Space between table and pagination

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PaginationForTable(
                            currentPage = currentPage,
                            totalPages = totalPages,
                            onPageClick = { newPage -> currentPage = newPage },
                            onPrevious = { if (currentPage > 1) currentPage-- },
                            onNext = { if (currentPage < totalPages) currentPage++ }
                        )
                    }
                }
            } else {

                //<---Pinned Column Logic--->

                var isUserScrolling by remember { mutableStateOf(false) }

                Row(modifier = Modifier.fillMaxWidth()) {

                    pinnedColumnIndices.forEach { pinnedIndex ->
                        val columnData =
                            currentPageData.map { row -> row.getOrNull(pinnedIndex) ?: "" }

                        Column(
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .height(373.dp)
                                .width(columnwidth)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f)), // Apply background color here
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFF9800))
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = columnList.getOrNull(pinnedIndex) ?: "N/A",
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp
                                )
                            }

                            columnData.forEach { cell ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(Color.LightGray.copy(alpha = 0.5f))
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {

                                    val annotatedString = buildAnnotatedString {
                                        val startIndex = cell.indexOf(searchText, ignoreCase = true)
                                        if (startIndex != -1) {
                                            append(
                                                cell.substring(
                                                    0,
                                                    startIndex
                                                )
                                            )  // Before the match
                                            withStyle(
                                                SpanStyle(
                                                    color = Color.Red,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ) {
                                                append(
                                                    cell.substring(
                                                        startIndex,
                                                        startIndex + searchText.length
                                                    )
                                                )  // Highlight match
                                            }
                                            append(cell.substring(startIndex + searchText.length))  // After the match
                                        } else {
                                            append(cell)
                                        }
                                    }

                                    Text(
                                        text = annotatedString,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black,
                                        textAlign = TextAlign.Center,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .wrapContentWidth(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                IconButton(
                                    onClick = {
                                        pinnedColumnIndices.remove(pinnedIndex)
                                    },
                                    modifier = Modifier
                                        .padding(bottom = 5.dp)
                                        .size(30.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Remove",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }

                    //<---Coverflow table Logic

                    val expandedColumns by remember { mutableStateOf(setOf<Int>()) }

                    LazyRow(
                        state = listState,
                        flingBehavior = rememberSnapFlingBehavior(listState),
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { isUserScrolling = true },
                                    onDrag = { _, _ -> },
                                    onDragEnd = { isUserScrolling = false }
                                )
                            }
                    ) {

                        itemsIndexed(columnList) { index, header ->

                            if (index in selectedColumns) {

                                if (pinnedColumnIndices.contains(index)) return@itemsIndexed

                                val columnData = currentPageData.map { it.getOrNull(index) ?: "" }
                                var columnX by remember { mutableFloatStateOf(0f) }
                                val isExpanded = expandedColumns.contains(index)

                                val distanceFromCenter =
                                    remember { derivedStateOf { columnX - screenWidthPx / 2 } }
                                val targetScale = remember {
                                    derivedStateOf {
                                        if (pinnedColumnIndices.isNotEmpty()) {
                                            0.9f + (0.1f * (abs(distanceFromCenter.value) / (screenWidthPx / 2))).coerceIn(
                                                0f,
                                                0.2f
                                            )
                                        } else {
                                            1f - (0.2f * (abs(distanceFromCenter.value) / (screenWidthPx / 2))).coerceIn(
                                                0f,
                                                0.2f
                                            )
                                        }
                                    }
                                }
                                val targetRotation = remember {
                                    derivedStateOf {
                                        if (pinnedColumnIndices.isNotEmpty()) {
                                            1f
                                        } else {
                                            (10f * (distanceFromCenter.value / (screenWidthPx / 2))).coerceIn(
                                                -10f,
                                                10f
                                            )
                                        }
                                    }
                                }

                                val animatedScale by animateFloatAsState(
                                    targetValue = targetScale.value,
                                    animationSpec = spring(
                                        stiffness = Spring.StiffnessLow,
                                        dampingRatio = 0.75f
                                    )
                                )

                                val animatedRotation by animateFloatAsState(
                                    targetValue = targetRotation.value,
                                    animationSpec = spring(
                                        stiffness = Spring.StiffnessLow,
                                        dampingRatio = 0.75f
                                    )
                                )

                                Column(
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .height(380.dp)
                                        .graphicsLayer {
                                            rotationY = animatedRotation
                                            scaleX = animatedScale
                                            scaleY = animatedScale
                                        }
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.LightGray.copy(alpha = 0.3f))
                                        .width(columnwidth)
                                        .onGloballyPositioned { layoutCoordinates ->
                                            columnX =
                                                layoutCoordinates.positionInParent().x + layoutCoordinates.size.width / 2
                                        }
                                        .clickable(enabled = columnData.toString().length > 15) {
                                            selectedDataList =
                                                columnData
                                            selectedColumnTitle = header
                                            showCard = true
                                        },
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFFF9800))
                                            .padding(vertical = 12.dp)
                                            .combinedClickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null,
                                                onClick = {},
                                                onLongClick = { isMinimized = true }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = header,
                                            maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center,
                                            fontSize = 18.sp
                                        )
                                    }

                                    columnData.forEach { cell ->

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .background(Color.LightGray.copy(alpha = 0.3f))
                                                .padding(12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val annotatedString = buildAnnotatedString {
                                                val startIndex =
                                                    cell.indexOf(searchText, ignoreCase = true)
                                                if (startIndex != -1) {
                                                    append(
                                                        cell.substring(
                                                            0,
                                                            startIndex
                                                        )
                                                    )  // Before the match
                                                    withStyle(
                                                        SpanStyle(
                                                            color = Color.Red,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    ) {
                                                        append(
                                                            cell.substring(
                                                                startIndex,
                                                                startIndex + searchText.length
                                                            )
                                                        )  // Highlight match
                                                    }
                                                    append(cell.substring(startIndex + searchText.length))  // After the match
                                                } else {
                                                    append(cell)
                                                }
                                            }

                                            Text(
                                                text = annotatedString,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Black,
                                                textAlign = TextAlign.Center,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .wrapContentWidth(),
                                        contentAlignment = Alignment.BottomCenter
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (index !in pinnedColumnIndices) {
                                                    pinnedColumnIndices.add(index)
                                                }
                                            },
                                            modifier = Modifier
                                                .padding(bottom = 5.dp)
                                                .size(30.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.plus),
                                                contentDescription = "Pin Column",
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                LaunchedEffect(pinnedColumnIndices) {
                    if (pinnedColumnIndices.isNotEmpty()) {
                        val firstPinnedIndex = pinnedColumnIndices.first()
                        listState.animateScrollToItem(firstPinnedIndex)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SmartButton(
                            onClick = {
                                tempSelectedColumns = checkboxStates
                                    .mapIndexedNotNull { index, isChecked -> if (isChecked) index else null }
                                    .toSet()
                                showFilterDialog = true
                            },
                            modifier = Modifier
                                .width(120.dp)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Filter",
                                color = Color.White
                            )
                        }
                        val context = LocalContext.current
                        OutlinedTextField(
                            value = textfieldpage.value,
                            onValueChange = { input ->
                                textfieldpage.value = input

                                val enteredPage = input.toIntOrNull()
                                if (enteredPage != null) {
                                    if (enteredPage in 1..totalPages) {
                                        currentPage = enteredPage
                                    } else {
                                        Toast.makeText(context, "Page not found", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(120.dp)
                                .height(54.dp),
                            maxLines = 1,
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = Color.White
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.DarkGray,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.DarkGray
                            )
                        )
                    }

                    //<---Pagenation--->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        PaginationForTable(
                            currentPage = pageNumber,
                            totalPages = totalPages,
                            onPageClick = { newPage -> currentPage = newPage },
                            onPrevious = { if (currentPage > 1) currentPage-- },
                            onNext = { if (currentPage < totalPages) currentPage++ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    hint: String = "Search...",
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text(text = hint, fontSize = 14.sp, color = Color.Gray) },
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
            .padding(16.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        trailingIcon = {
            IconButton(onClick = { onSearch(searchQuery.text) }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.Gray,
            focusedTextColor = Color.Gray
        )
    )
}

@Composable
fun PaginationForTable(
    currentPage: Int,
    totalPages: Int,
    visiblePageCount: Int = 3,
    onPageClick: (Int) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val currentRangeStart = ((currentPage - 1) / visiblePageCount) * visiblePageCount + 1
    val currentRangeEnd = minOf(currentRangeStart + visiblePageCount - 1, totalPages)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = { if (currentPage > 1) onPrevious() },
            enabled = currentPage > 1,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Previous",
                tint = if (currentPage > 1) Color.Blue else Color.Gray
            )
        }

        for (page in currentRangeStart..currentRangeEnd) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        color = if (page == currentPage) Color.Blue else Color.Gray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onPageClick(page) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$page",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        IconButton(
            onClick = { if (currentPage < totalPages) onNext() },
            enabled = currentPage < totalPages,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Next",
                tint = if (currentPage < totalPages) Color.Blue else Color.Gray
            )
        }
    }
}