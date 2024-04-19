package com.example.roomdbdemo

import android.util.Patterns

object InputValidator {

    //private val EMAIL_REGEX = Regex("[a-zA-Z0-9._%+-]{1,256}+@[a-z0-9.-]{1,256}+\\.[a-z]{2,3}")
    private val PHONE_NUMBER_REGEX = Regex("^\\d+\$") // accept only digits 0~9
    fun validateInput(
        firstName: String?,
        lastName: String?,
        userName: String?,
        phoneNum: String?,
        email: String?
    ): Pair<Boolean, String?> {
        return when {
            isValidName(firstName) -> Pair(false, "First name should be at least 2 characters")
            isValidName(lastName) -> Pair(false, "Last name should be at least 2 characters")
            isValidName(userName) -> Pair(false, "Username should be at least 2 characters")
            isValidPhoneNum(phoneNum) -> Pair(false, "Invalid phone number")
            isValidEmail(email) -> Pair(false, "Invalid email address")
            else -> Pair(true, null)
        }
    }

    private fun isValidName(s: String?): Boolean {
        return (s.isNullOrEmpty() || s.length < 2)
    }

    private fun isValidPhoneNum(n: String?): Boolean {
        return (n.isNullOrEmpty() || !PHONE_NUMBER_REGEX.matches(n))
    }

    private fun isValidEmail(e: String?): Boolean {
        return (e.isNullOrEmpty() || !Patterns.EMAIL_ADDRESS.matcher(e).matches())
    }
}