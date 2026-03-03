package com.app.fieldsync.auth

import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

class OtpService {
    private val otpStorage = ConcurrentHashMap<String, Pair<String, Long>>()

    fun generateOtp(phone: String): String {
        val otp = Random.nextInt(1000, 9999).toString()
        otpStorage[phone] = Pair(otp, System.currentTimeMillis())

        println("OTP for $phone is $otp")
        return otp
    }

    fun verifyOtp(phone: String, otp: String): Boolean {
        val (storedOtp, timestamp) = otpStorage[phone] ?: return false

        if (System.currentTimeMillis() - timestamp > 120000) {
            otpStorage.remove(phone)
            return false
        }

        val isValid = storedOtp == otp
        if (isValid) otpStorage.remove(phone)

        return isValid
    }
}