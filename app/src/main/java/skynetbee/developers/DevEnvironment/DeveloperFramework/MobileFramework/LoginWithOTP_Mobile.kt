package skynetbee.developers.DevEnvironment


/**
 * Created by Gowtham Bharath N
 * Kotlin Wing - OTP Page
 *
 * 📄 Description:
 * This Kotlin file handles OTP verification from the server.
 *
 * ✅ Functionality:
 * - Sends OTP to the server for validation.
 * - If the server responds with "noaccess", the app navigates to the "Access Denied" screen.
 * - If the server responds with "success", it navigates to the respective pages.
 * - Downloads and stores:
 *    - Profile photo
 *    - Leaderboard photos
 *
 * 🛠️ Notes:
 * - Uses coroutine for network calls.
 * - Save directories are handled in internal app storage.
 * - Old profile and leaderboard images are deleted to maintain storage.
 */

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import skynetbee.developers.DevEnvironment.DeveloperFramework.ViewModel
import skynetbee.developers.DevEnvironment.GlobalNavController.navController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * This is the Main function
 */

@Composable
fun LoginWithOTP_Mobil(viewModel : ViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.skynetbee_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .fillMaxHeight(0.2f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SkyneTBee",
                fontSize = 45.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Cursive,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD700),
                            Color(0xFFFFA500),
                            Color(0xFFFFFFE0),
                            Color(0xFFFFD700)
                        )
                    ),
                    fontSize = 50.sp,
                    lineHeight = 50.sp
                ),
                maxLines = 1,
                softWrap = false
            )

            Spacer(modifier = Modifier.height(40.dp))

            OTPInputField_Mobile(
                onOtpChange = { viewModel.otp = it },
                onErrorAnimationComplete = { viewModel.isError = true },
                onLoadingChange = { viewModel.loading = it }
            )

            Spacer(modifier = Modifier.height(30.dp))

            if (viewModel.loading) {
                PulsatingDotsLoadingIndicator_Mobile()
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Communicating with Skynet...",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA500),
                                Color(0xFFFFFFE0),
                                Color(0xFFFFD700)
                            )
                        ),
                        fontSize = 13.sp,
                        lineHeight = 13.sp
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            } else if (viewModel.navToPage) {
                PulsatingDotsLoadingIndicator_Mobile()
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Navigating ...",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFFFA500),
                                Color(0xFFFFFFE0),
                                Color(0xFFFFD700)
                            )
                        ),
                        fontSize = 13.sp,
                        lineHeight = 13.sp
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }else {
                Button(
                    onClick = {
                        viewModel.loading = true
                        sendOtpToServer(viewModel.otp, onOtpValidation = { isValid ->
                            viewModel.loading = false
                            viewModel.isError = !isValid
                        },
                        onSecondCall = {},
                        onResponseReceived ={},
                        )
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFFD700),
                                        Color(0xFFFFA500),
                                        Color(0xFFFCFC6E),
                                        Color(0xFFFFD700)
                                    )
                                ),
                                shape = RoundedCornerShape(30.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Verify OTP",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Default,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}

/**
 * This is the function animation
 */

@Composable
fun PulsatingDotsLoadingIndicator_Mobile() {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val scale3 by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Dot_Mobile(scale = scale1, color = Color(0xFFFFA500)) // Orange
            Dot_Mobile(scale = scale2, color = Color(0xFFFFD700)) // Gold
            Dot_Mobile(scale = scale3, color = Color(0xFFFFFFE0)) // Light Yellow
        }
    }
}

/**
 * This is the function for Dot That is used for animation
 */

@Composable
fun Dot_Mobile(scale: Float, color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .scale(scale)
            .background(color, shape = CircleShape)
    )
}

/**
 * This is the function for otp text field
 */

@Composable
fun OTPInputField_Mobile(
    onOtpChange: (String) -> Unit,
    onErrorAnimationComplete: () -> Unit,
    onLoadingChange: (Boolean) -> Unit,
    viewModel: ViewModel = viewModel()
) {

    val focusManager = LocalFocusManager.current
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val density = LocalDensity.current

    LaunchedEffect(viewModel.otpisError) {
        if (viewModel.otpisError) {
            val shake = with(density) { 10.dp.toPx() }
            viewModel.otpErrorAnimation(
                shakeDistance = shake,
                onOtpChange = onOtpChange,
                onErrorAnimationComplete = onErrorAnimationComplete
            )
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .graphicsLayer {
                    translationX = viewModel.offsetX.value
                }
        ) {
            viewModel.textFields.forEachIndexed { index, state ->
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(Color.Transparent)
                        .border(1.dp, textColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = state.value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1) {
                                state.value = newValue.filter { it.isDigit() }
                                val newOtp = viewModel.textFields.joinToString("") { it.value }
                                onOtpChange(newOtp)

                                if (newValue.isNotEmpty() && index < 6 - 1) {
                                    viewModel.focusRequesters[index + 1].requestFocus()
                                }

                                if (index == 6 - 1 && newValue.isNotEmpty()) {
                                    onLoadingChange(true)
                                    sendOtpToServer(newOtp, onOtpValidation = { isValid ->
                                        onLoadingChange(true)
                                        viewModel.otpisError = !isValid
                                    },
                                    onSecondCall = {
                                        onLoadingChange(true)
                                    },
                                    onResponseReceived ={
                                        onLoadingChange(false)
                                    })
                                }

                                viewModel.otpisError = false
                            }
                        },
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            color = if (viewModel.otpisError) Color.Red else Color(0xFFBFBFBF),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(viewModel.focusRequesters[index])
                            .onKeyEvent { event ->
                                if (event.key == Key.Backspace && state.value.isEmpty() && index > 0) {
                                    viewModel.textFields[index - 1].value = ""
                                    viewModel.focusRequesters[index - 1].requestFocus()
                                    true
                                } else {
                                    false
                                }
                            },
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * This is the core function of this page because it checks the otp to the server
 */


@OptIn(DelicateCoroutinesApi::class)
fun sendOtpToServer(otp: String, onOtpValidation: (Boolean) -> Unit,onSecondCall: (Boolean) -> Unit, onResponseReceived: (String) -> Unit ){
    if (otp.length != 6) {
        Log.e("OTPError", "Invalid OTP Length")
        onOtpValidation(false)
        onResponseReceived("invalid")
        return
    }
    val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()

    val url = "https://www.skynetbee.com/skynetbee/api/developer-environment/login-with-otp.php?otp=$otp"
    Log.d("OTPRequest", "Request URL: $url")

    val otprequest = Request.Builder()
        .url(url)
        .get()
        .addHeader("User-Agent", "Mozilla/5.0")
        .build()

    client.newCall(otprequest).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("OTPError", "Request failed: ${e.message}")
            onOtpValidation(false)
        }

        @SuppressLint("NewApi")
        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("OTPResponse", "Server Response: $responseBody")

                    //<----Execute When Otp is Send---->

                    if (responseBody?.contains("noaccess") == true) {

                        Log.e("OTPError", "No access: $responseBody")
                        onOtpValidation(false)
                        onResponseReceived("noaccess")

                    } else if (responseBody?.contains("INSERT INTO") == true) {
                        onOtpValidation(true)
                        val splitfirstres = responseBody.split("[s~s]")

                        val delQueryforprojects = "DELETE FROM all_system_projects_assigned_to_developers"
                        DF.executeQuery(delQueryforprojects)

                        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val updateQueryforleaderboard = "UPDATE all_system_leaderboard SET todat = '$currentDate'"
                        val updateQueryfordetails = "UPDATE all_system_developer_details SET todat = '$currentDate'"

                        val resultforupdatelb = DF.executeQuery(updateQueryforleaderboard)
                        val resultforupdatedt = DF.executeQuery(updateQueryfordetails)

                        if (resultforupdatelb.first && resultforupdatedt.first) {
                            Log.d("DB_UPDATE", "todat updated successfully in both tables!")
                        } else {
                            Log.e("DB_UPDATE", "Failed to update one or both tables!")
                        }
                        for (i in splitfirstres.indices) {
                            val secondSplit = splitfirstres[i].split("[n~p]")

                            for (j in secondSplit.indices) {

                                DF.executeQuery(secondSplit[j].trim())
                                Log.d("splitquery", secondSplit[j].trim())
                            }
                        }

                        //<----End of Execution When OTP Send---->
                        //<----Insert Last Contact with Server----->

                        val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                        val selectOtpQuery = "SELECT otp FROM all_system_developer_details WHERE todat = '0000-00-00'"
                        val otpExe = DF.executeQuery(selectOtpQuery)
                        val otpFromusers = otpExe.second?.first()?.get("otp")
                        val otpUser = if (otpFromusers?.length == 5) "0$otpFromusers" else otpFromusers

                        Log.d("DEBUG123", "SeperateOtp size: ${otpExe}")

                        val insertLastDateTimeQuery = "INSERT INTO last_communication_with_server (otp, doe, toe) VALUES ('${otpUser}', '$currentDate', '$currentTime')"
                        DF.executeQuery(insertLastDateTimeQuery)


                        //<----End of Last Contact of Server---->
                        //<----Download Image to Private Dir---->

                        Log.d("splitquery",splitfirstres.last().trim())
                        val imageUrl = splitfirstres.last().trim()
                        downloadAndSaveImages(
                            imageUrls = listOf(imageUrl),
                            saveDirPath = "/data/data/skynetbee.developers.DevEnvironment/files/Photos/Profile",
                            defaultPrefix = "profile",
                            onComplete = { files ->
                                files.forEach { Log.d("SavedFile", it.name) }
                            }
                        )

                        val selectQueryforImg = "SELECT fphoto, sphoto, tphoto FROM all_system_leaderboard WHERE todat = '0000-00-00'"
                        val queryResultforImg = DF.executeQuery(selectQueryforImg)

                        if (queryResultforImg.first && queryResultforImg.second != null && queryResultforImg.second!!.isNotEmpty()) {
                            val row = queryResultforImg.second!![0]
                            val fphoto = row["fphoto"] ?: ""
                            val sphoto = row["sphoto"] ?: ""
                            val tphoto = row["tphoto"] ?: ""

                            downloadAndSaveImages(
                                imageUrls = listOf(fphoto, sphoto, tphoto),
                                saveDirPath = "/data/data/skynetbee.developers.DevEnvironment/files/photos/faceid",
                                defaultPrefix = "leaderboard", // still used for normal paths
                                onComplete = { files ->
                                    files.forEach { Log.d("SavedFile", it.name) }
                                }
                            )

                        } else {
                            Log.e("DB_SELECT", "No matching data found!")
                        }

                        //<----End of Download photo---->
                        //<----Send All Project code to server----->

                        val projectCodeList = mutableListOf<String>()
                        val selectProjectQuery = "SELECT projectcode FROM all_system_projects_assigned_to_developers"
                        val selectprojectexe = DF.executeQuery(selectProjectQuery)

                        if (selectprojectexe.first) {
                            selectprojectexe.second?.forEach { row ->
                                row["projectcode"]?.let { projectCodeList.add(it) }
                            }
                        }
                        var howmanyreq = 0
                        var howmanyres = 0
                        val allresponse = mutableListOf<String?>()

                        for (projectCode in projectCodeList) {
                            val projectUrl = "https://www.skynetbee.com/skynetbee/api/developer-environment/get-tables-with-project-code.php?projectcode=$projectCode"
                            Log.d("ProjectCodeRequest", "Request URL: $projectUrl")

                            val projectRequest = Request.Builder()
                                .url(projectUrl)
                                .get()
                                .addHeader("User-Agent", "Mozilla/5.0")
                                .build()

                            howmanyreq++

                            client.newCall(projectRequest).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    Log.e("ProjectCodeError", "Failed to send project code: ${e.message}")
                                    onSecondCall(true)
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    onSecondCall(true)
                                    response.use {
                                        if (response.isSuccessful) {
                                            val responseBody = response.body?.string()
                                            Log.d("ProjectCodeResponse", "Project Code: $projectCode Server Response: $responseBody")

                                            // Store response in the array (matching request order)

                                            synchronized(allresponse) {
                                                allresponse.add(responseBody)
                                                howmanyres++
                                            }

                                            // <----If all responses are received, proceed with SQL operations---->

                                            Log.d("allresponse", "$allresponse")
                                            if (howmanyres == howmanyreq) {
                                                val deleteTableResponses = mutableListOf<String?>()
                                                val otherResponses = mutableListOf<String?>()

                                                for (response in allresponse) {
                                                    if (response != null && response.contains("Delete Tables")) {
                                                        deleteTableResponses.add(response)
                                                    } else {
                                                        otherResponses.add(response)
                                                    }
                                                }
                                                Log.d("seperateResponse", "$deleteTableResponses")
                                                Log.d("seperateResponse", "$otherResponses")

                                                // <----Process delete table responses first, passing each index separately---->

                                                for (response in deleteTableResponses) {
                                                    processResponses(listOf(response), sql)
                                                }

                                                for (response in otherResponses) {
                                                    processResponses(listOf(response), sql)
                                                }
                                            }else{
                                                Log.d("ProjectCodeError", "Error")
                                            }

                                        } else {
                                            Log.e("ProjectCodeError", "Failed with code: ${response.code}")
                                        }
                                    }
                                }
                            })
                        }
                    } else {
                        Log.e("OTPError", "Invalid OTP or unexpected response: $responseBody")
                        onOtpValidation(false)
                    }

                    GlobalScope.launch(Dispatchers.Main) {

                        if (responseBody?.contains("noaccess") == true){
                            cl()
                        } else {
                            var dataa = DF.select("unique_member_id, offinam from all_system_developer_details where todat='0000-00-00'")
                            cl()
                            dataa?.get("offinam")?.let { dataa["unique_member_id"]?.let { it1 ->
                                cl()
                                user.setUserInfo(it,
                                    it1
                                )
                                cl()
                            }
                            }
                            var lvp = LasteVisitedPage()
                            if (lvp == null) {
                                navController?.navigate(myProf)
                            } else {
                                when (lvp) {
                                    "1" -> navController?.navigate("project_1")
                                    "2" -> navController?.navigate("project_2")
                                    "3" -> navController?.navigate("project_3")
                                    "4" -> navController?.navigate("project_4")
                                    "5" -> navController?.navigate("project_5")
                                    "6" -> navController?.navigate("project_6")
                                    "7" -> navController?.navigate("project_7")
                                    "8" -> navController?.navigate("project_8")
                                    "9" -> navController?.navigate("project_9")
                                    "10" -> navController?.navigate("project_10")
                                    else -> null
                                }
                            }
                        }
                    }
                } else {
                    Log.e("OTPError", "Request failed with code: ${response.code}")
                    onOtpValidation(false)
                }
            }
        }
    })
    onResponseReceived("")
}

/**
 * - This function converts mySQL commend into sqlite commends.
 */

fun processResponses(allresponse: List<String?>, neural: NeuralMemoryConnectionEstablisher) {
    for (responseBody in allresponse) {
        val splitSecondres = responseBody?.split("[s~~t]")

        if (splitSecondres != null) {
            val cleanedsplitSecondres = splitSecondres.toString()
                .replace("[", "").replace("]", "").trim()

            if ("Delete Tables" in cleanedsplitSecondres) {
                Log.d("DeleteQuery", "Detected table deletion request: $cleanedsplitSecondres")
                val delquery = generateDeleteQuery(cleanedsplitSecondres)
                DF.executeQuery(delquery.toString())
            }
        }

        if (splitSecondres != null) {
            for (query in splitSecondres) {
                var cleanedQuery = query.trim()

                cleanedQuery = cleanedQuery.replace("`", "")
                    .replace("varchar(", "TEXT(")
                    .replace("longtext", "TEXT")
                    .replace("float", "REAL")
                    .replace("int NOT NULL AUTO_INCREMENT", "INTEGER PRIMARY KEY AUTOINCREMENT")
                    .replace("AUTO_INCREMENT", "")
                    .replace("DEFAULT CHARSET=utf8mb3;", "")
                    .replace("DEFAULT CHARSET=latin1", "")
                    .replace("PRIMARY KEY (counti)", "")
                    .replace("DEFAULT '0000-00-00'", "")
                    .replace("DEFAULT '00:00:00'", "")
                    .replace("time", "TEXT")
                    .replace("date", "TEXT")
                    .replace("AUTOINCREMENT=1", "")
                    .replace("=1", "")
                    .replace("ENGINE=InnoDB", "")
                    .replace("TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci", "TEXT(500)")
                    .replace("CHARACTER SET latin1 COLLATE latin1_swedish_ci ", "")
//                    .replace("INTEGER PRIMARY KEY AUTOINCREMENT", "int NOT NULL")
//                    .replace("mcounti int NOT NULL,", "mcounti int DEFAULT -1,localcounti INTEGER PRIMARY KEY AUTOINCREMENT,")
                cleanedQuery = cleanedQuery.replace(",\\s*\\)".toRegex(), "\n)")

                try {
                    neural.executeQuery(cleanedQuery)
                    Log.d("QueryExecution", "Executed: $cleanedQuery")
                } catch (e: Exception) {
                    Log.e("QueryExecution", "Error executing query: $cleanedQuery", e)
                }
            }
        }
    }
}

/**
 * This is the function generates delete query
 */

fun generateDeleteQuery(response: String): String? {
    val prefix = "Delete Tables : "
    return if (response.startsWith(prefix)) {
        val tableName = response.removePrefix(prefix).trim()
        "DROP TABLE $tableName;"
    } else {
        null
    }
}

/**
 * This is the function that downloads image for profile
 */

@SuppressLint("SdCardPath")
fun downloadAndSaveImages(
    imageUrls: List<String?>,
    saveDirPath: String,
    defaultPrefix: String,
    onComplete: (List<File>) -> Unit
) {
    val saveDir = File(saveDirPath)
    if (!saveDir.exists()) saveDir.mkdirs()

    val client = OkHttpClient()
    val timestamp = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault()).format(Date())
    val savedFiles = mutableListOf<File>()

    val useSimpleName = !saveDirPath.endsWith("/Photos/Profile")
    val faceIdPrefixes = listOf("fphoto", "sphoto", "tphoto", "uphoto", "vphoto") // extend as needed

    CoroutineScope(Dispatchers.IO).launch {
        imageUrls.forEachIndexed { index, imageUrl ->
            if (!imageUrl.isNullOrBlank()) {
                val fileName = if (useSimpleName && index < faceIdPrefixes.size) {
                    "${faceIdPrefixes[index]}.jpeg"
                } else {
                    "${defaultPrefix}_${timestamp}_$index.jpeg"
                }

                val saveFile = File(saveDir, fileName)

                try {
                    val request = Request.Builder().url(imageUrl).build()
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            response.body?.byteStream()?.use { input ->
                                FileOutputStream(saveFile).use { output ->
                                    input.copyTo(output)
                                }
                                savedFiles.add(saveFile)
                                Log.i("ImageDownload", "Image saved at: ${saveFile.absolutePath}")
                            }
                        } else {
                            Log.e("ImageDownload", "Failed to download image $index: ${response.message}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ImageDownload", "Exception while saving image $index", e)
                }
            }
        }

        deleteOldImages(saveDir, savedFiles)

        withContext(Dispatchers.Main) {
            onComplete(savedFiles)
        }
    }
}

/**
 * This is the function delete old image
 */

private fun deleteOldImages(directory: File, keepFiles: List<File>) {
    directory.listFiles()?.let { files ->
        for (file in files) {
            if (!keepFiles.contains(file)) {
                file.delete()
                Log.d("ImageCleaner", "Deleted old image: ${file.absolutePath}")
            }
        }
    }
}