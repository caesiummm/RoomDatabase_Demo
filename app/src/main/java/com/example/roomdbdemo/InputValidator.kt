package com.example.roomdbdemo

import android.util.Patterns

object InputValidator {

    //private val EMAIL_REGEX = Regex("[a-zA-Z0-9._%+-]{1,256}+@[a-z0-9.-]{1,256}+\\.[a-z]{2,3}")
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

            phoneNum.isNullOrEmpty() -> Pair(false, "Phone number should not be empty")

            email.isNullOrEmpty() -> Pair(false, "Email should not be empty")
            !Patterns.EMAIL_ADDRESS.matcher(email.toString()).matches() -> Pair(false, "Invalid email")
            else -> Pair(true, null)
        }
    }

    private fun isValidName(s: String?): Boolean {
        return (s.isNullOrEmpty() || s.trim().length < 2)
    }
}