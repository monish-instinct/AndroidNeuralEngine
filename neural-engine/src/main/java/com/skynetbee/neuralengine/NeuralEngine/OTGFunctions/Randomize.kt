package com.skynetbee.neuralengine

//  
// Randomize.kt
// skynetbee.developers.DevEnvironment.OTGFunctions
//
// Created by A. Nithish  on 02-03-2025

fun generateRandomAlphaNumeric(length: Int): String {
    val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}

fun generateRandomString(length: Int): String {
    val chars = "abcdefghijklmnopqrstuvwxyz"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}

fun generateRandomNumber(length: Int): String {
    val chars = "0123456789"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}