package com.app.fieldsync.reports

import kotlinx.serialization.Serializable

@Serializable
enum class SyncNetworkMode(val displayName: String, val description: String) {
    ONLINE("Online", "Normal network operation"), OFFLINE(
        "Simulate Offline", "Network disconnected (All syncs saved locally)"
    ),
    SLOW_NETWORK(
        "Simulate Slow Network", "High latency network delay (2s delay per sync)"
    ),
    FORCE_FAILURE(
        "Simulate Failure", "Server error (HTTP 500 Internal Error)"
    ),
    FORCE_CONFLICT("Simulate Conflict", "Data conflict detected and resolved")
}

data class SyncLogEntry(
    val timestamp: String, val message: String, val level: LogLevel = LogLevel.INFO
) {
    enum class LogLevel { INFO, SUCCESS, WARNING, ERROR }
}

data class SyncBatchResult(
    val processedCount: Int, val successCount: Int, val failureCount: Int, val conflictCount: Int
)