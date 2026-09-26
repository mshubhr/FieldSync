package com.app.fieldsync.reports

import com.app.fieldsync.BuildConfig
import com.app.fieldsync.db.ReportEntity
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

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

    var networkMode: SyncNetworkMode = SyncNetworkMode.ONLINE

    private val baseUrl: String
        get() = BuildConfig.baseUrl

    suspend fun syncReport(
        category: String, note: String, imageBase64: String
    ): Result<FieldReportResponse> {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val localId = localDataSource?.insertReport(category, note, imageBase64, timestamp, false)

        return processSyncForReport(localId, category, note, imageBase64, timestamp)
    }

    private suspend fun processSyncForReport(
        localId: Long?, category: String, note: String, imageBase64: String, timestamp: Long
    ): Result<FieldReportResponse> {
        when (networkMode) {
            SyncNetworkMode.OFFLINE -> {
                return Result.failure(Exception("Network Offline (Simulated). Saved locally."))
            }

            SyncNetworkMode.SLOW_NETWORK -> {
                delay(2000.milliseconds)
            }

            SyncNetworkMode.FORCE_FAILURE -> {
                return Result.failure(Exception("HTTP 500: Server Error (Simulated Failure)"))
            }

            SyncNetworkMode.FORCE_CONFLICT -> {
                localId?.let { localDataSource?.markAsSynced(it) }
                return Result.success(
                    FieldReportResponse(
                        id = localId?.toString() ?: "conflict-${timestamp}",
                        message = "Conflict resolved: merged client & server states",
                        timestamp = timestamp
                    )
                )
            }

            SyncNetworkMode.ONLINE -> {}
        }

        return try {
            val response = client.post("$baseUrl/sync-report") {
                contentType(ContentType.Application.Json)
                setBody(FieldReportRequest(category, note, imageBase64))
            }
            if (response.status == HttpStatusCode.OK) {
                val reportResponse = response.body<FieldReportResponse>()
                localId?.let { localDataSource?.markAsSynced(it) }
                Result.success(reportResponse)
            } else {
                val error = try {
                    response.body<String>()
                } catch (_: Exception) {
                    "Sync failed: ${response.status}"
                }
                Result.failure(Exception(error))
            }
        } catch (_: Exception) {
            localId?.let { localDataSource?.markAsSynced(it) }
            Result.success(
                FieldReportResponse(
                    id = localId?.toString() ?: "offline-synced-${timestamp}",
                    message = "Synced locally (Offline fallback)",
                    timestamp = timestamp
                )
            )
        }
    }

    suspend fun createTestRecords(count: Int): List<Long> {
        val insertedIds = mutableListOf<Long>()
        val sampleCategories = listOf(
            "Site Audit", "Equipment Check", "Safety Inspection", "Soil Sample", "Structural Check"
        )
        val timestamp = Clock.System.now().toEpochMilliseconds()

        for (i in 1..count) {
            val cat = sampleCategories[(i - 1) % sampleCategories.size]
            val note = "Simulated offline field report #$i ($cat)"
            val sampleImage =
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
            val id =
                localDataSource?.insertReport(cat, note, sampleImage, timestamp + i * 100, false)
            if (id != null) insertedIds.add(id)
        }
        return insertedIds
    }

    suspend fun syncAllPendingReports(
        onLog: (SyncLogEntry) -> Unit = {}
    ): SyncBatchResult {
        val pendingReports = localDataSource?.getUnsyncedReports() ?: emptyList()
        if (pendingReports.isEmpty()) {
            onLog(
                SyncLogEntry(
                    nowTime(), "No pending operations to sync.", SyncLogEntry.LogLevel.INFO
                )
            )
            return SyncBatchResult(0, 0, 0, 0)
        }

        onLog(
            SyncLogEntry(
                nowTime(),
                "Starting batch sync for ${pendingReports.size} pending reports...",
                SyncLogEntry.LogLevel.INFO
            )
        )

        var successCount = 0
        var failureCount = 0
        var conflictCount = 0

        for (report in pendingReports) {
            onLog(
                SyncLogEntry(
                    nowTime(),
                    "Syncing Report #${report.id} [${report.category}]...",
                    SyncLogEntry.LogLevel.INFO
                )
            )

            val result = processSyncForReport(
                localId = report.id,
                category = report.category,
                note = report.note,
                imageBase64 = report.imageBase64,
                timestamp = report.timestamp
            )

            if (result.isSuccess) {
                if (networkMode == SyncNetworkMode.FORCE_CONFLICT) {
                    conflictCount++
                    onLog(
                        SyncLogEntry(
                            nowTime(),
                            "Report #${report.id} CONFLICT RESOLVED & SYNCED",
                            SyncLogEntry.LogLevel.WARNING
                        )
                    )
                } else {
                    successCount++
                    localDataSource?.markAsSynced(report.id)
                    onLog(
                        SyncLogEntry(
                            nowTime(),
                            "Report #${report.id} SYNC SUCCESS",
                            SyncLogEntry.LogLevel.SUCCESS
                        )
                    )
                }
            } else {
                failureCount++
                val errMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                onLog(
                    SyncLogEntry(
                        nowTime(),
                        "Report #${report.id} SYNC FAILED: $errMsg",
                        SyncLogEntry.LogLevel.ERROR
                    )
                )
            }
        }

        onLog(
            SyncLogEntry(
                nowTime(),
                "Sync Batch Complete: $successCount Succeeded, $failureCount Failed, $conflictCount Conflicts",
                if (failureCount == 0) SyncLogEntry.LogLevel.SUCCESS else SyncLogEntry.LogLevel.WARNING
            )
        )

        return SyncBatchResult(
            processedCount = pendingReports.size,
            successCount = successCount,
            failureCount = failureCount,
            conflictCount = conflictCount
        )
    }

    suspend fun getPendingCount(): Int {
        return localDataSource?.getUnsyncedReports()?.size ?: 0
    }

    suspend fun getSyncedCount(): Int {
        return localDataSource?.getAllReports()?.count { it.isSynced == 1L } ?: 0
    }

    suspend fun getAllLocalReports(): List<ReportEntity> {
        return localDataSource?.getAllReports() ?: emptyList()
    }

    suspend fun getLocalReports() = getAllLocalReports()

    private fun nowTime(): String {
        val localDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val h = localDateTime.hour.toString().padStart(2, '0')
        val m = localDateTime.minute.toString().padStart(2, '0')
        val s = localDateTime.second.toString().padStart(2, '0')
        return "$h:$m:$s"
    }
}
