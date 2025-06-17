package com.skynetbee.neuralengine

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skynetbee.neuralengine.GlobalNavController.navController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer


//
// 1.kt
// DevEnvironment
//
// Created by A. Nithish on 05-03-2025

val clsList = mutableListOf<String>()
val examList = mutableListOf<String>()
var Std = "1"
var username = "hariharan a"

class viewModel : ViewModel() {
    var standardExpanded by mutableStateOf(false)
    var examExpanded by mutableStateOf(false)
    var showSecondCard by mutableStateOf(true)
    // Add selected values
    var selectedSemester by mutableStateOf("")
    var selectedExamFromDropdown by mutableStateOf("")
    var showChartCard by mutableStateOf(false)
        private set

    val percentages = mutableListOf<Float>()
    val subjectNames = mutableListOf<String>()

    fun fetchDataAndShowChart(exam: String, semester: String) {
        percentages.clear()
        subjectNames.clear()

        val query = "subjectname, percentage FROM edu_students_marks_sheet_for_primary_and_secondary_exams " +
                "WHERE exam = '$exam' AND cls = '$semester'"

        while (true) {
            val row = sql.select(query)
            if (row != null) {
                row["percentage"]?.toFloatOrNull()?.let {
                    percentages.add(it)
                    subjectNames.add(row["subjectname"].toString())
                }
            } else break
        }

        showChartCard = percentages.isNotEmpty()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun Project1(viewModel : viewModel = viewModel()) {
    val isTablet = isTablet()
    LastWorkedOn("1")
    val context = LocalContext.current

//    Box(
//        modifier = Modifier
//        .fillMaxSize()
//    ) {
//        Column(
//            verticalArrangement = Arrangement.Top
//        ) {
//            SmartTable("* from edu_students_marks_sheet_for_primary_and_secondary_exams where examtype = 'semester'")
//        }
//    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        item {
            Spacer(modifier = Modifier.height(20.dp))
            MainCard(viewModel)
        }
        if (viewModel.showSecondCard) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                if (viewModel.showChartCard) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ExamPieChart1(viewModel)
                    ExamBarChart(viewModel)
                    ExamPieChart2(viewModel)
                    SmartButton(
                        onClick = {
                            val queue = "n3crp0"

                            val query = """
                                subjectname, scored, outof, percentage
                                FROM edu_students_marks_sheet_for_primary_and_secondary_exams
                                WHERE queue = '$queue' AND term = '1'
                            """.trimIndent()

                            val paragraphText = "Marksheet for Term: 1"

                            generatePdf(context = context, id = "n3crp0", query = query, paragraphText = paragraphText, fileName = "marksheet_1.pdf")

                        }

                    ) {
                        Text(
                            text = "Generate PDF",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.padding(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCard(viewModel : viewModel = viewModel()) {
    val boxColor = Color.LightGray.copy(0.1f)
    val names = remember { mutableStateListOf<Map<String, String>>() }
    var showDropdown by remember { mutableStateOf(false) }
    var preselectedValue by remember { mutableStateOf("") }


    val semquery = """
        DISTINCT area, term
        FROM edu_students_marks_sheet_for_primary_and_secondary_exams
        WHERE todat = '0000-00-00'
        AND offinam = '$username';
    """.trimIndent()

    LaunchedEffect(Unit) {
        val developers: MutableList<Map<String, String>?> = mutableListOf()
        sql.reset(lineNumber = 193)
        while (true) {
            val result = sql.select(semquery) ?: break
            developers.add(result)
        }

        if (developers.isEmpty()) {
            names.add(mapOf("No Data" to "nd"))
            showDropdown = true
        } else {
            developers.forEach { developer ->
                val values = developer?.values?.toList()
                val name = values?.getOrNull(0)
                val value = values?.getOrNull(1)
                if (name != null && value != null) {
                    names.add(mapOf(name to value))
                }
            }
            showDropdown = names.size > 1
            if (!showDropdown) {
                // Auto-select if only one value
                preselectedValue = names.first().values.first()
            }
        }
    }


    Box(
        modifier = Modifier
            .padding(5.dp)
            .background(boxColor, RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .border(2.dp, Color(0xFFDADADA), CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ironman),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Name
                Text(
                    text = uc(username),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD277),
                    style = MaterialTheme.typography.bodyMedium
                )
                // Course
                Text(
                    text = "Computer Science",
                    fontSize = 12.sp,
                    color =Color(0xFFBFBFBF),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            // Horizontal Divider
            Divider(
                color = Color(0xFFBFBFBF).copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Dropdowns Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                if (showDropdown) {
                    val semquery = """
                        DISTINCT term, area
                        FROM edu_students_marks_sheet_for_primary_and_secondary_exams
                        WHERE todat = '0000-00-00'
                        AND offinam = '$username';
                    """.trimIndent()

                    fillFromDatabase(
                        query = semquery,
                        ndval = "--Semester--",
                        modifier = Modifier.width(160.dp),
                        onchange = { selectedValue ->
                            viewModel.selectedSemester = selectedValue
                        },
                        fill = "data",
                        errorcode = "ethu ella"
                    )
                } else {
                    viewModel.selectedSemester = preselectedValue

                    var expanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = preselectedValue,
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text("--Semester--", fontWeight = FontWeight.Bold, color = Color(0xFFBFBFBF))
                            },
                            textStyle = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBFBFBF)
                            ),
                            modifier = Modifier
                                .width(130.dp)
                                .menuAnchor(),
                            shape = RoundedCornerShape(16.dp),
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(10.dp))
                var examquery =  "DISTINCT exam AS name, exam AS value FROM edu_students_marks_sheet_for_primary_and_secondary_exams WHERE todat = '0000-00-00' AND cls = '$Std';"
                fillFromDatabase(
                    fill = "data",
                    query = examquery,
                    ndval = "--Exam--",
                    errorcode = "ethu ella",
                    modifier = Modifier
                        .width(160.dp),
                    onchange = { selectedValue ->
                        viewModel.selectedExamFromDropdown = selectedValue
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            val context = LocalContext.current

            LaunchedEffect(viewModel.selectedSemester, viewModel.selectedExamFromDropdown) {
                if (viewModel.selectedSemester.isNotBlank() && viewModel.selectedExamFromDropdown.isNotBlank()) {
                    // Optional: log for debug
                    Log.d("MainCard", "Auto running fetchDataAndShowChart with: ${viewModel.selectedSemester}, ${viewModel.selectedExamFromDropdown}")

                    viewModel.standardExpanded = false
                    viewModel.examExpanded = false

                    clsList.clear()
                    examList.clear()
                    viewModel.fetchDataAndShowChart(viewModel.selectedExamFromDropdown, viewModel.selectedSemester)
                }
            }
        }
    }
}
@Composable
fun ExamPieChart1(viewModel: viewModel = viewModel()) {
    val boxColor = Color.LightGray.copy(0.1f)
    val total = viewModel.percentages.sum()
    val normalizedPercentages = viewModel.percentages.map { it / total * 100f }


    Box(
        modifier = Modifier
            .padding(15.dp)
            .background(boxColor, RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(440.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (viewModel.percentages.isNotEmpty()) {

                Log.d("zxcvbnm", "${viewModel.subjectNames}")
                Log.d("zxcvbnm", "${viewModel.percentages}")

                DoubleDoughnutChart(
                    title = "",
                    shareNames = viewModel.subjectNames,
                    percentages = normalizedPercentages,
                    size = 250.dp,
                    centerContent = "Total Score" // String center content
                )
            } else {
                Text("Please select an exam to view the chart.")
            }
        }
    }
}
@Composable
fun ExamPieChart2(viewModel: viewModel = viewModel()) {
    val boxColor = Color.LightGray.copy(0.1f)

    Box(
        modifier = Modifier
            .padding(15.dp)
            .background(boxColor, RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(440.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (viewModel.percentages.isNotEmpty()) {

                Log.d("zxcvbnm", "${viewModel.subjectNames}")
                Log.d("zxcvbnm", "${viewModel.percentages}")

                DoubleDoughnutChart(
                    title = "Exam Scores",
                    shareNames = listOf("core 4 data structures", "core 5 java programming", "allied 3 paper 1 microprocessor & alp"),
                    percentages = listOf(33f, 25f, 28f),
                    size = 250.dp,
                    centerContent = painterResource(id = R.drawable.ironman)
                )
            } else {
                Text("Please select an exam to view the chart.")
            }
        }
    }
}

@Composable
fun ExamBarChart(viewModel: viewModel = viewModel()) {
    val boxColor = Color.LightGray.copy(0.1f)

    Box(
        modifier = Modifier
            .padding(15.dp)
            .background(boxColor, RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .height(440.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (viewModel.percentages.isNotEmpty()) {
                Log.d("zxcvbnm", "${viewModel.subjectNames}")
                Log.d("zxcvbnm", "${viewModel.percentages}")

                // ✅ FIXED HERE
                val filteredData = viewModel.subjectNames.zip(viewModel.percentages)
                    .filterNot { (subject, _) ->
                        subject.startsWith("language", ignoreCase = true) || subject.startsWith("english", ignoreCase = true)
                    }

                BarGraph(
                    title = "Exam Scores",
                    shareData = filteredData,
                    size = 280.dp
                )

            } else {
                Text("Please select an exam to view the chart.")
            }
        }
    }
}
