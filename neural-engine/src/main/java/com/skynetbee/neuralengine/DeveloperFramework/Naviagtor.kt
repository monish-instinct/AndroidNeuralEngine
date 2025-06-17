package com.skynetbee.neuralengine

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

//  
// Naviagtor.kt
// DevEnvironment
//
// Created by A. Nithish on 14-03-2025



object GlobalNavController {
    @SuppressLint("StaticFieldLeak")
    var navController: NavHostController? = null
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun Navigator() {
    val navController = rememberNavController()
    GlobalNavController.navController = navController
    val start = if (isTablet()) "verified_Tablet" else "verified_mobile"

    NavHost(
        navController = navController,
        startDestination = start
    ) {

        composable("verified_Tablet"){

            EngineStarter_Tablet(navController)
        }

        composable("verified_Mobile"){

            EngineStarter_Mobile(navController)
        }

        composable("login_Mobile"){

            LoginWithOTP_Mobil()
        }

        composable("login_Tablet"){

            LoginWithOTP_Tablet()
        }
        composable("access_denied"){

            AccessDenied(
                onGoBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("my_profile_Mobile"){

            MyProfile_Mobile()
        }

        composable("my_profile_Tablet"){
            MyProfile_Tablet()
        }

        composable("project_1"){

            Project1()
        }
        composable("project_2"){

            Project2()
        }
        composable("project_3"){

            Project3()
        }
        composable("project_4"){

            Project4()
        }
        composable("project_5"){

            Project5()
        }
        composable("project_6"){

            Project6()
        }
        composable("project_7"){

            Project7()
        }
        composable("project_8"){

            Project8()
        }
        composable("project_9"){

            Project9()
        }
        composable("project_10"){

            Project10()
        }

        composable("work_around"){

            WorkAround()
        }
        composable("contactdev"){
            ContactOurDev()
        }
    }
}