package com.freedom.utils

object Validators {

    private val USERNAME_REGEX = Regex("^[a-zA-Z0-9_]{3,20}$")
    private val INVITE_CODE_REGEX = Regex("^[A-Z0-9]{6,10}$")
    private val PASSWORD_REGEX = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#\$%^&*]{8,}$")

    fun isValidUsername(username: String): Boolean {
        return username.matches(USERNAME_REGEX)
    }

    fun isValidInviteCode(code: String): Boolean {
        return code.matches(INVITE_CODE_REGEX)
    }

    fun isValidPassword(password: String): Boolean {
        return password.matches(PASSWORD_REGEX)
    }

    fun isValidMessage(message: String): Boolean {
        return message.trim().isNotEmpty() && message.length <= 2000
    }
}
