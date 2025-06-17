package com.skynetbee.neuralengine.DeveloperFramework

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import com.skynetbee.neuralengine.CompletedProject
import com.skynetbee.neuralengine.DF
import com.skynetbee.neuralengine.GlobalNavController.navController
import com.skynetbee.neuralengine.InProgressProject
import com.skynetbee.neuralengine.getDeadLine
import com.skynetbee.neuralengine.getTotalDays
import com.skynetbee.neuralengine.showToast
import com.skynetbee.neuralengine.uc
import java.io.File

class ViewModel : ViewModel() {

    var firstname= ""
    var secondname = ""
    var thirdname = ""
    var rank = ""
    var r1 = ""
    var c1 = ""
    var rate1 = ""
    var name =""
    var p1 =""
    var profile: List<Triple<String, String, Int>> = emptyList()



    //        Harini's Work
    val readQuery = "SELECT * FROM all_system_leaderboard WHERE todat = '0000-00-00'"
    val rank1 = DF.executeQuery("SELECT * from all_system_developer_details")
    val result = DF.executeQuery(readQuery)
    val project = DF.executeQuery("SELECT COUNT(projectcode) AS projectcode FROM all_system_projects_assigned_to_developers")
    var totalProjects = 0
    val completedQuery = DF.executeQuery(
        "SELECT COUNT(completedat) AS completedat FROM all_system_projects_assigned_to_developers WHERE completedat != '0000-00-00'"
    )
    var completedProjects = 0
    //leaaderboard
    val directoryPath = "/data/data/skynetbee.developers.DevEnvironment/files/photos/faceid/"
    //profile
    val directoryPath1 = "/data/data/skynetbee.developers.DevEnvironment/files/Photos/Profile/"

    val fphoto = getFirstMatchingFile(directoryPath, "fphoto")
    val photo = getFirstMatchingFile(directoryPath, "sphoto")
    val tphoto = getFirstMatchingFile(directoryPath, "tphoto")
    val uphoto = getFirstMatchingFile(directoryPath1,"profile")


    init {
        profile1()
    }


    fun profile1() {
        if (result.first) {
            result.second?.forEach { row ->

                firstname = row["fname"].toString()
                secondname = row["sname"].toString()
                thirdname = row["tname"].toString()
            }
        }

        Log.d("qwertyuiop", "$fphoto")

        if (rank1.first) {
            rank1.second?.forEach { row ->
                r1 = row["rank"].toString()
                c1 = row["cp"].toString()
                rate1 = row["overallstars"].toString()
                name = row["offinam"].toString()
            }
        }

        if (project.first) {
            project.second?.forEach { row ->
                totalProjects = row["projectcode"].toString().toInt()
            }

        }

        if (completedQuery.first) {
            completedQuery.second?.forEach { row ->
                completedProjects = row["completedat"].toString().toInt()
            }
        }

        profile = listOfNotNull(
            fphoto?.let { Triple(it, uc(firstname) , 1) },
            photo?.let { Triple(it, uc(secondname), 2) },
            tphoto?.let { Triple(it, uc(thirdname), 3) }
        )
    }

    fun moveToWorkAround(context : android.content.Context) {
        count++
        when {
            count == maxTaps -> {
                toast?.cancel() // Cancel previous toast
                toast = showToast(
                    context,
                    "You are now on work around",
                    Toast.LENGTH_SHORT
                )
                navController?.navigate("work_around")
            }

            count < maxTaps -> {
                val stepsLeft = maxTaps - count
                val message =
                    "You're $stepsLeft step${if (stepsLeft == 1) "" else "s"} away from work around"

                toast?.cancel() // Cancel previous toast before showing a new one
                toast =
                    showToast(context, message, Toast.LENGTH_LONG)
            }
        }
    }

    fun fetchProject() {
        // Fetch in-progress projects
        val inProgressQuery =
            "SELECT * FROM all_system_projects_assigned_to_developers WHERE completedat ='0000-00-00'"
        val inProgressResult: Pair<Boolean, List<Map<String, String>>?> =
            DF.executeQuery(inProgressQuery)

        if (inProgressResult.first) {
            inProgressProjects.clear() // Prevent duplicate entries

            inProgressResult.second?.forEach { row ->
                val name = row["pronam"] ?: "Unknown Project"
                val level = row["hardnesslevel"] ?: "Unknown Level"
                val deadline = row["deadlinedat"] ?: "0000-00-00"
                val startDate = row["dat"] ?: "0000-00-00"

                val totalDays = getTotalDays(startDate, deadline)
                val remainingDays = getDeadLine(deadline).toInt()
                val progress =
                    ((remainingDays.toDouble() / totalDays.toDouble()) * 100).toInt()

                inProgressProjects.add(
                    InProgressProject(
                        name = name,
                        level = level,
                        deadline = deadline,
                        remainingDays = remainingDays.toString(),
                        progress = progress
                    )
                )
            }
        }

        // Fetch completed projects
        val completedQuery =
            "SELECT pronam, rating FROM all_system_projects_assigned_to_developers WHERE completedat != '0000-00-00'"
        val completedResult: Pair<Boolean, List<Map<String, String>>?> =
            DF.executeQuery(completedQuery)

        if (completedResult.first) {
            completedProjects_M.clear() // Prevent duplicate entries

            completedResult.second?.forEach { row ->
                val projectName = row["pronam"] ?: "Unnamed Project"
                val rating = row["rating"] ?: "0"

                completedProjects_M.add(
                    CompletedProject(
                        name = projectName,
                        star = rating
                    )
                )
            }
        }
    }
    fun getFirstMatchingFile(directory: String, prefix: String): String? {
        return File(directory).listFiles { file -> file.name.startsWith(prefix) }
            ?.firstOrNull()?.absolutePath
    }

    //        Mega's Work

    val inProgressProjects =  mutableStateListOf<InProgressProject>()
    val completedProjects_M = mutableStateListOf<CompletedProject>()
    var currentPage by mutableStateOf("In Progress")


    // Safely get rating value
    val rating = rate1.getOrNull(0)?.toString()?.toIntOrNull() ?: 0

    var count by   mutableStateOf(0)
    val maxTaps = 5
    var toast: Toast? by mutableStateOf(null)

    val progress =
        if (totalProjects > 0) (completedProjects / totalProjects.toFloat()) * 360f else 0f



    //Login page

    var otp by  mutableStateOf("")
    var isError by mutableStateOf(false)
    var loading by  mutableStateOf(false)
    var navToPage by  mutableStateOf(false)
    var otpisError by  mutableStateOf(false)
    val offsetX =  Animatable(0f)
    val textFields = List(6) { mutableStateOf("") }
    val focusRequesters = List(6) { FocusRequester() }


    suspend fun otpErrorAnimation(
        shakeDistance: Float,
        onOtpChange: (String) -> Unit,
        onErrorAnimationComplete: () -> Unit
    ) {
        if (otpisError) {
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 600
                    0f at 0
                    shakeDistance at 100
                    -shakeDistance at 200
                    shakeDistance at 300
                    -shakeDistance at 400
                    0f at 500
                }
            )

            textFields.forEach { it.value = "" }
            onOtpChange("")
            focusRequesters.first().requestFocus()

            onErrorAnimationComplete()
        }
    }
}