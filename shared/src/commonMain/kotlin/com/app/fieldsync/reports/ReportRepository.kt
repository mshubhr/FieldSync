package com.app.fieldsync.reports

import com.app.fieldsync.SERVER_PORT
import com.app.fieldsync.getPlatform
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.time.Clock

class ReportRepository(private val localDataSource: LocalReportDataSource? = null) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    private val baseUrl: String
        get() {
            return if (getPlatform().name.contains("Android")) {
                "http://10.0.2.2:$SERVER_PORT"
            } else {
                "http://localhost:$SERVER_PORT"
            }
        }

    suspend fun syncReport(category: String, note: String, imageBase64: String): Result<FieldReportResponse> {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        val localId = localDataSource?.insertReport(category, note, imageBase64, timestamp, false)

        return try {
            val response = client.post("$baseUrl/sync-report") {
                contentType(ContentType.Application.Json)
                setBody(FieldReportRequest(category, note, imageBase64))
            }
            if (response.status == HttpStatusCode.OK) {
                val reportResponse = response.body<FieldReportResponse>()
                localId?.let { localDataSource



                    .markAsSynced(it) }
                Result.success(reportResponse)
            } else {
                val error = try { response.body<String>() } catch (_: Exception) { "Sync failed: ${response.status}" }
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLocalReports() = localDataSource?.getAllReports() ?: emptyList()
}
