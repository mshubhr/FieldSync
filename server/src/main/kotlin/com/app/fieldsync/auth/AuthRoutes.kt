package com.app.fieldsync.auth

import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoutes() {

    val users = mutableListOf<User>()
    val otpService = OtpService()

    post("/send-otp") {
        val request = call.receive<SendOtpRequest>()
        otpService.generateOtp(request.phone)
        call.respond(mapOf("message" to "OTP sent"))
    }

    post("/verify-otp") {
        val request = call.receive<VerifyOtpRequest>()

        if (!(otpService.verifyOtp(request.phone, request.otp))) {
            call.respond(mapOf("error" to "Invalid or expired OTP"))
            return@post
        }

        var user = users.find { it.phone == request.phone }

        if (user == null) {
            user = User(
                id = users.size + 1, phone = request.phone, name = request.name
            )
            users.add(user)
        }

        user.devices.add(
            Device(
                deviceId = request.deviceId,
                deviceName = request.deviceName,
                platform = request.platform
            )
        )

        call.respond(AuthResponse(JwtService().generateToken(user.phone)))
    }
}