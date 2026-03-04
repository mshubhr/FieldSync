package com.app.fieldsync.auth

import com.app.fieldsync.SERVER_PORT
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AuthRepository {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val baseUrl = "http://10.0.2.2:$SERVER_PORT"

    suspend fun sendOtp(phone: String): Result<String> {
        return try {
            val response = client.post("$baseUrl/send-otp") {
                contentType(ContentType.Application.Json)
                setBody(SendOtpRequest(phone))
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success("OTP sent")
            } else {
                Result.failure(Exception("Failed to send OTP: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(
        phone: String,
        otp: String,
        name: String,
        deviceId: String,
        deviceName: String,
        platform: String
    ): Result<AuthResponse> {
        return try {
            val response = client.post("$baseUrl/verify-otp") {
                contentType(ContentType.Application.Json)
                setBody(VerifyOtpRequest(phone, otp, name, deviceId, deviceName, platform))
            }
            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Verification failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
