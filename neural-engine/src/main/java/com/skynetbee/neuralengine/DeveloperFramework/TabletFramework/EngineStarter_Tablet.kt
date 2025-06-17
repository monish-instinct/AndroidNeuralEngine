package com.skynetbee.neuralengine

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavHostController

//  
// EngineStarter_Tablet.kt
// DevEnvironment
//
// Created by A. Nithish on 13-03-2025


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun EngineStarter_Tablet(navController: NavHostController) {

    if (OTPNeverVerified()) {
        LoginWithOTP_Tablet()
    } else if (OTPNotVerifiedToday()){
        if (reVerifyOTP() == "noaccess"){
            AccessDenied(
                onGoBack = {
                    navController?.popBackStack()
                }
            )
        } else {
            var lvp = LasteVisitedPage()
            if (lvp == null){
                MyProfile_Tablet()
            } else {
                when (lvp) {
                    "1" -> Project1()
                    "2" -> Project2()
                    "3" -> Project3()
                    "4" -> Project4()
                    "5" -> Project5()
                    "6" -> Project6()
                    "7" -> Project7()
                    "8" -> Project8()
                    "9" -> Project9()
                    "10" -> Project10()
                    else -> null
                }
            }
        }
    } else {
        var lvp = LasteVisitedPage()
        if (lvp == null){
            MyProfile_Tablet()
        } else {
            when (lvp) {
                "1" -> Project1()
                "2" -> Project2()
                "3" -> Project3()
                "4" -> Project4()
                "5" -> Project5()
                "6" -> Project6()
                "7" -> Project7()
                "8" -> Project8()
                "9" -> Project9()
                "10" -> Project10()
                else -> null
            }
        }
    }
}

@Composable
fun isTabletInLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp > configuration.screenHeightDp
}
