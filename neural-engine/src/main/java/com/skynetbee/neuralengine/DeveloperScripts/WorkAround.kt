package com.skynetbee.neuralengine

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skynetbee.neuralengine.GlobalNavController.navController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

//
// WorkAround.kt
// DevEnvironment
//
// Created by A. Nithish on 05-03-2025


@Composable
fun WorkAround() {

    LastWorkedOn("work_around")

    InsertFakeData()

    val allRows = mutableListOf<Map<String, String>>()

    sql.reset()
    while (true) {
        val row = sql.select("scheme_name,start_date,end_date,scheme_duration,payment_lastdate,scheme_description from all_chit_schemes")
        if (row == null) break
        allRows.add(row)
    }

    Log.d("asdfghjkl", "$allRows")

    if (allRows.isNotEmpty()) {
        SchemeBoxWithEnroll(schemes = allRows)
    }

    Column(
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(top = 50.dp, start = 20.dp)
    ){
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    navController?.navigate("my_profile_Mobile")
                }
        )
    }
}


@Composable
fun SchemeBoxWithEnroll(schemes: List<Map<String, String>>) {
    var isPresent by remember { mutableStateOf(true) }
    var isSuccesssfull by remember { mutableStateOf(false) }
    var selectedScheme by remember { mutableStateOf<Map<String, String>?>(null) }
    var showAnimation by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            schemes.forEach { scheme ->
                // Only show if scheme has the required keys (basic validation)
                if (scheme.containsKey("scheme_name") && scheme.containsKey("start_date") && scheme.containsKey("end_date")) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .border(1.dp, Color(0xFFBFBFBF) , RoundedCornerShape(10.dp))
                            .background(Color.LightGray.copy(0.1f), RoundedCornerShape(10.dp))
                            .height(150.dp)
                            .fillMaxWidth(0.8f)
                            .clickable { selectedScheme = scheme }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .border(1.dp, Color(0xFFFFD277), CircleShape)
                                    .clip(CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.ironman),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = scheme["scheme_name"] ?: "N/A",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD277),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Divider(
                                    color = Color(0xFFFFD277).copy(alpha = 0.3f),
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                InfoItemsb("Start Date:", scheme["start_date"] ?: "N/A")
                                InfoItemsb("End Date:", scheme["end_date"] ?: "N/A")
                                InfoItemsb("Duration:", scheme["scheme_duration"] ?: "N/A")
                            }
                        }
                    }
                }
            }
        }
        selectedScheme?.let { scheme ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable {
                        selectedScheme = null
                        showAnimation = false
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .shadow(16.dp, shape = RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFBFBFBF) , RoundedCornerShape(10.dp))
                        .background(Color.LightGray.copy(0.7f), RoundedCornerShape(10.dp))
                        .height(380.dp)
                        .fillMaxWidth(0.8f)
                        .padding(8.dp)
                ) {
                    if (showAnimation) {
                        EnrollmentAnimation(
                            onAnimationFinished = {
                                showAnimation = false
                                isSuccesssfull = true
                            }
                        )
                    } else if (isPresent) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = scheme["scheme_name"] ?: "N/A",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700)
                                )
                                Divider(
                                    color = Color(0xFFFFD700).copy(alpha = 0.5f),
                                    thickness = 1.dp,
                                    modifier = Modifier.fillMaxWidth(0.6f)
                                )
                                InfoItem("Start Date:", scheme["start_date"] ?: "N/A")
                                InfoItem("End Date:", scheme["end_date"] ?: "N/A")
                                InfoItem("Duration:", scheme["scheme_duration"] ?: "N/A")
                                InfoItem("Last Payment Date:", scheme["payment_lastdate"] ?: "N/A")

                                Text(
                                    text = scheme["scheme_description"] ?: "",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 20.sp
                                )
                            }

                            SmartButton(
                                onClick = {
                                    val data = mutableMapOf(
                                        "offinam" to "gowtham",
                                        "scheme_name" to (scheme["scheme_name"] ?: ""),
                                        "start_date" to (scheme["start_date"] ?: ""),
                                        "end_date" to (scheme["end_date"] ?: ""),
                                        "scheme_duration" to (scheme["scheme_duration"] ?: ""),
                                        "payment_lastdate" to (scheme["payment_lastdate"] ?: ""),
                                        "scheme_description" to (scheme["scheme_description"] ?: "")
                                    )
                                    val result = sql.insert("enrolled_schemes", data)
                                    val isInserted = result.first.second
                                    Log.d("zxcvbnm", "$isInserted")

                                    isPresent = false
                                    showAnimation = true

                                }
                            ) {
                                Text("Enroll Now", fontWeight = FontWeight.Bold, color = Color(0xFFFFD277))
                            }
                        }
                    }else if (isSuccesssfull) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "You Aready Enrolled",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 25.sp,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun EnrollmentAnimation(onAnimationFinished: () -> Unit) {
    // Start a coroutine for the delay.
    LaunchedEffect(Unit) {
        delay(2500)  // Delay for the duration of your animation (e.g., 4 seconds)
        onAnimationFinished()
    }

    // The animation content inside the dialog.
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//             Lottie animation for the enrollment success.
//             Ensure you have the necessary Lottie dependency added.
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.success_animation))
            LottieAnimation(
                composition = composition,
                iterations = 1,
                modifier = Modifier.size(250.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Enrolled Successfully",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD277),
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Color.Black)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun InfoItemsb(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, color = Color(0xFFBFBFBF), fontSize = 14.sp)
        Text(value, fontWeight = FontWeight.Medium, color = Color(0xFFBFBFBF), fontSize = 14.sp)
    }
}


fun InsertFakeData(){
    var createquery = """
        CREATE TABLE IF NOT EXISTS all_chit_schemes (
            id,
            scheme_name TEXT NOT NULL,
            start_date TEXT NOT NULL,
            end_date TEXT NOT NULL,
            scheme_duration TEXT NOT NULL,
            payment_lastdate TEXT NOT NULL,
            scheme_description TEXT,
            area TEXT,
            local_counti INTEGER PRIMARY KEY AUTOINCREMENT,
            counti TEXT,
            mcounti TEXT,
            fromdat TEXT,
            ftodat TEXT,
            ftotim TEXT,
            ftovername  TEXT,
            ftover TEXT,
            ftopid TEXT,
            todat TEXT,
            totim TEXT,
            tovername TEXT,
            tover TEXT,
            topid TEXT,
            ipmac TEXT,
            deviceanduserainfo TEXT,
            basesite TEXT,
            owncomcode TEXT,
            testeridentity TEXT,
            testcontrol TEXT,
            adderpid TEXT,
            addername TEXT,
            adder TEXT,
            syncstatus TEXT,
            syncrejectionreason TEXT,
            doe TEXT,
            toe TEXT
        );
    """.trimIndent()
    var createqueryres = sql.executeQuery(createquery)
    if (createqueryres.first){
        Log.d("Insertfakedata", "InsertFakeData: Schemes table created successfully")
    }else{
        Log.d("Insertfakedata", "InsertFakeData: Schemes table is not created")
    }




    var createquery2 = """
        CREATE TABLE IF NOT EXISTS enrolled_schemes (
            id,
            offinam TEXT NOT NULL,
            scheme_name TEXT NOT NULL,
            start_date TEXT NOT NULL,
            end_date TEXT NOT NULL,
            scheme_duration TEXT NOT NULL,
            payment_lastdate TEXT NOT NULL,
            scheme_description TEXT,
            area TEXT,
            local_counti INTEGER PRIMARY KEY AUTOINCREMENT,
            counti TEXT,
            mcounti TEXT,
            fromdat TEXT,
            ftodat TEXT,
            ftotim TEXT,
            ftovername  TEXT,
            ftover TEXT,
            ftopid TEXT,
            todat TEXT,
            totim TEXT,
            tovername TEXT,
            tover TEXT,
            topid TEXT,
            ipmac TEXT,
            deviceanduserainfo TEXT,
            basesite TEXT,
            owncomcode TEXT,
            testeridentity TEXT,
            testcontrol TEXT,
            adderpid TEXT,
            addername TEXT,
            adder TEXT,
            syncstatus TEXT,
            syncrejectionreason TEXT,
            doe TEXT,
            toe TEXT
        );
    """.trimIndent()
    var createqueryres2 = sql.executeQuery(createquery2)
    if (createqueryres2.first){
        Log.d("Insertfakedata", "InsertFakeData: Schemes table created successfully")
    }else{
        Log.d("Insertfakedata", "InsertFakeData: Schemes table is not created")
    }


    val data1 = mutableMapOf(
        "scheme_name" to "Gold Scheme",
        "start_date" to "2025-08-09",
        "end_date" to "2026-08-09",
        "scheme_duration" to "1 year",
        "payment_lastdate" to "2025-09-09",
        "scheme_description" to "A simple gold saving scheme with monthly payments."
    )

    val data2 = mutableMapOf(
        "scheme_name" to "Silver Savings",
        "start_date" to "2025-07-01",
        "end_date" to "2026-07-01",
        "scheme_duration" to "1 year",
        "payment_lastdate" to "2025-08-01",
        "scheme_description" to "Secure your future with silver savings, paid monthly."
    )

    val data3 = mutableMapOf(
        "scheme_name" to "Diamond Delight",
        "start_date" to "2025-06-15",
        "end_date" to "2026-06-15",
        "scheme_duration" to "1 year",
        "payment_lastdate" to "2025-07-15",
        "scheme_description" to "Exclusive diamond savings for special customers."
    )

    val data4 = mutableMapOf(
        "scheme_name" to "Platinum Plan",
        "start_date" to "2025-05-20",
        "end_date" to "2026-05-20",
        "scheme_duration" to "1 year",
        "payment_lastdate" to "2025-06-20",
        "scheme_description" to "Premium platinum savings plan with flexible options."
    )

    sql.insert("all_chit_schemes",data1)
    sql.insert("all_chit_schemes",data2)
    sql.insert("all_chit_schemes",data3)
    sql.insert("all_chit_schemes",data4)

}