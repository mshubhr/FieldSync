package com.app.fieldsync.auth

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val phone: String,
    val name: String,
    val devices: MutableList<Device> = mutableListOf()
)

@Serializable
data class Device(
    val deviceId: String, val deviceName: String, val platform: String
)

@Serializable
data class SendOtpRequest(
    val phone: String
)

@Serializable
data class VerifyOtpRequest(
    val phone: String,
    val otp: String,
    val name: String,
    val deviceId: String,
    val deviceName: String,
    val platform: String
)

@Serializable
data class AuthResponse(
    val token: String
)