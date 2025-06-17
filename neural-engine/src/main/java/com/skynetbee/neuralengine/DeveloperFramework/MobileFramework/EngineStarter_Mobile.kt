package com.skynetbee.neuralengine

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.skynetbee.neuralengine.GlobalNavController.navController

//  
// EngineStarter_Mobile.kt
// DevEnvironment
//
// Created by A. Nithish on 12-03-2025



@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun EngineStarter_Mobile(navController: NavHostController) {
    if (OTPNeverVerified()) {
        LoginWithOTP_Mobil()
        Log.d("check1234", "VerifyTheUser_Mobile: 28")
        cl()
    } else if (OTPNotVerifiedToday()){
        cl()
        if (reVerifyOTP() == "noaccess"){
            cl()
            AccessDenied(
                onGoBack = {
                    navController.popBackStack()
                }
            )
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
            cl()
            if (lvp == null){
                cl()
                navController?.navigate("my_profile_Mobile")
            } else {
                cl(lvp,"Nithish123")
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
                    "work_around" -> WorkAround()
                    else -> null
                }
            }
        }
    } else {
        cl()
        var lvp = LasteVisitedPage()
        cl()
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
        cl()
        if (lvp == null){
            cl()
            navController?.navigate("my_profile_Mobile")
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
                "work_around" -> WorkAround()
                else -> null
            }
        }
    }
}