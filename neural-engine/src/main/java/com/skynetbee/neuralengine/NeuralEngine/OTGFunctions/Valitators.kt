package com.skynetbee.neuralengine

//  
// Valitators.kt
// DevEnvironment
//
// Created by Shaliha on 05-03-2025

fun validateAddress(address: String): String {
    if (address.isEmpty()) return "Address cannot be empty."
    if (address.length < 15) return "Address must contain at least 15 characters."
    if (address.count { it == ',' } < 2) return "${address} (must contain at least two commas.)"

    var correctedAddress = address.replace(Regex("\\s+,\\s*"), ", ").replace(Regex(",([^ ])"), ", $1")

    correctedAddress = correctedAddress.split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }

    if (!correctedAddress.endsWith(".")) correctedAddress += "."

    return "Corrected Address: $correctedAddress"
}

fun validateName(name: String): String {
    val trimmedName = name.trim()
    if (trimmedName.isEmpty()) return "Please type in a name!"

    val allowedCharacters = ('A'..'Z') + ('a'..'z') + setOf('.', ' ')
    if (trimmedName.any { it !in allowedCharacters }) {
        return "${trimmedName} doesn't look like a name"
    }

    var cleanedName = trimmedName.replace(".", " ")
    cleanedName = cleanedName.split("\\s+".toRegex()).filter { it.isNotEmpty() }.joinToString(" ")

    val letterOnlyName = cleanedName.replace(" ", "")
    if (letterOnlyName.length < 3) {
        return "${trimmedName} (This name must contain at least 3 letters.)"
    }

    val words = cleanedName.split(" ").map { word ->
        word.substring(0, 1).uppercase() + word.substring(1).lowercase()
    }

    val formattedName = words.joinToString(" ")

    val hasInitial = formattedName.any { it == ' ' }
    if (!hasInitial) {
        return "$formattedName (No initial provided)"
    }

    return "Formatted Name: $formattedName"
}

fun validatePhoneNumber(phoneNumber: String): String? {
    val cleanPhoneNumber = phoneNumber.filter { it.isDigit() }

    return if (cleanPhoneNumber.length == 10) {
        "+91 $cleanPhoneNumber"
    } else {
        "$cleanPhoneNumber must contain ten numbers"
    }
}

fun validateOTP(OTP: String): Pair<Boolean, String> {
    if (OTP.isEmpty()) {
        return Pair(false, "Type-In OTP")
    } else if (OTP.toIntOrNull() != null) {
        return if (OTP.length == 6) {
            Pair(true, OTP)
        } else {
            Pair(false, "Incomplete OTP")
        }
    } else {
        return Pair(false, "Invalid OTP")
    }
}
