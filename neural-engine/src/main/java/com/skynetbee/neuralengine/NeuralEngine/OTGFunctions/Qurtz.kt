package com.skynetbee.neuralengine

import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//  
// Qurtz.kt
// DevEnvironment
//
// Created by Megavarshni K S on 05-03-2025

fun formatDate(inputString: String): String {
    val containsColon = inputString.contains(":")
    val containsDash = inputString.contains("-")
    val containsSpace = inputString.contains(" ")

    return when {
        containsDash && containsColon -> {
            val parts = inputString.split(" ", "&t").filter { it.isNotEmpty() }
            val fDate = fDate(parts[0])
            val fTime = fTime(parts[1])
            "$fDate at $fTime"
        }
        (containsColon && !containsDash) || containsSpace -> fTime(inputString)
        else -> fDate(inputString)
    }
}

private fun fTime(time24: String): String {
    return try {
        val inputFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormatter = SimpleDateFormat("h:mm a", Locale.getDefault()).apply {
            dateFormatSymbols = DateFormatSymbols().apply {
                amPmStrings = arrayOf("AM", "PM") // Set custom AM/PM
            }
        }
        val date = inputFormatter.parse(time24)
        date?.let { outputFormatter.format(it) } ?: ""
    } catch (e: Exception) {
        ""
    }
}

private fun fDate(inputString: String): String {
    val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    )
    val outputFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    for (format in formats) {
        try {
            val date = format.parse(inputString)
            if (date != null) {
                return outputFormatter.format(date)
            }
        } catch (_: Exception) {
        }
    }
    return inputString
}

fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}