package com.skynetbee.neuralengine

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.net.toUri


//
//  DevEnvironment
//  ContactOurDevelopers.kt
//
//  Created by Gowtham Bharath N on 3/24/2025.
//

data class Quintuple<T1, T2, T3, T4, T5>(val first: T1, val second: T2, val third: T3, val fourth: T4, val fifth: T5)

@Composable
fun ContactOurDev() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textcolour = if (isSystemInDarkTheme()){Color.White}else{Color.Black}
        val bgcolour = if (isSystemInDarkTheme()){Color.DarkGray}else{Color.White}
        val devs = listOf(
            Quintuple("Nithishaanandh A", "Software Architect", "+91 9361370969", "nithishaanand.developer@gmail.com", "nithishaanand.a"),
            Quintuple("Gowtham Bharath N", "Frontend & Ui Designer", "+91 7418802809", "barathinfo28@gamil.com", "gowthambharath.n"),
            Quintuple("Gayathri Mam", "Director", "+91 7373733020", "infi@rdcollege.in", "rdnationalcollegeerode"),
            Quintuple("Harini S", "Full Stack Developer", "+91 8072537016", "skynetharini@gmail.com", "axx._.harini._.10"),
            Quintuple("Megavarshini K", "Backend Developer", "+91 9384555815", "skynetmega01@gmail.com", "_mega_zz"),
            Quintuple("Shaliha", "Database Scientist", "+91 8531956905", "skynetshaliha@gmail.com", "shaliha._.s")
        )


        val profilePics = listOf(
            R.drawable.nithish_profile_pic,
            R.drawable.gowtham_profile_pic,
            R.drawable.gayathrimam_profile_pic,
            R.drawable.harini_profile_pic,
            R.drawable.mega_profile_pic,
            R.drawable.shaliha_profile_pic
        )
        val boxborder = if(isSystemInDarkTheme()){
            Color.Transparent
        }else{
            Color.LightGray
        }

        devs.forEachIndexed { index, (name, role, mobile, email, instagram)  ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(180.dp)
                    .border(1.dp, boxborder, shape = RoundedCornerShape(15.dp))
                    .background(bgcolour.copy(alpha = 0.4f), shape = RoundedCornerShape(15.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Wrap profile picture in a Box with fixed width
                        Box(
                            modifier = Modifier
                                .width(100.dp), // Fixed width to keep space consistent
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = profilePics[index]),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                            )
                        }

                        // Column for Text Details
                        Column() {
                            Text(text = name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textcolour)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = role, fontSize = 16.sp, color = textcolour)
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text(text = mobile, fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        color = textcolour,
                        thickness = 1.dp
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Spacer(modifier = Modifier.padding(4.dp))
                        Image(
                            painter = painterResource(id = R.drawable.phone_logo),
                            contentDescription = "Call",
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .size(47.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL)
                                    intent.data = Uri.parse("tel:$mobile")
                                    context.startActivity(intent)
                                }
                        )
                        Spacer(modifier = Modifier.padding(7.dp))
                        // WhatsApp Button
                        Image(
                            painter = painterResource(id = R.drawable.whats_app_logo),
                            contentDescription = "WhatsApp",
                            modifier = Modifier
                                .padding(top = 3.dp)
                                .size(50.dp)
                                .clickable {
                                    val uri = "https://wa.me/${mobile.replace("+", "").replace(" ", "")}".toUri()
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    context.startActivity(intent)
                                }
                        )
                        Spacer(modifier = Modifier.padding(3.dp))
                        // Email Button
                        Image(
                            painter = painterResource(id = R.drawable.apple_mail_logo),
                            contentDescription = "Email",
                            modifier = Modifier
                                .size(70.dp)
                                .clickable {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:$email")
                                        putExtra(Intent.EXTRA_SUBJECT, "Inquiry from SkynetBee App")
                                    }
                                    context.startActivity(emailIntent)
                                }
                        )
                        //Spacer(modifier = Modifier.padding(2.dp))
                        // Instagram Button
                        Image(
                            painter = painterResource(id = R.drawable.instagram_old_logo),
                            contentDescription = "Instagram",
                            modifier = Modifier
                                .size(80.dp)
                                .clickable {
                                    val instagramIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.instagram.com/$instagram/")
                                    )
                                    context.startActivity(instagramIntent)
                                }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(10.dp))
        }
    }
}

