package com.app.fieldsync.reports

import com.app.fieldsync.db.FieldSyncDatabase
import com.app.fieldsync.db.ReportEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalReportDataSource(database: FieldSyncDatabase) {
    private val queries = database.fieldSyncDatabaseQueries

    suspend fun insertReport(category: String, note: String, imageBase64: String, timestamp: Long, isSynced: Boolean): Long = withContext(Dispatchers.Default) {
        queries.insertReport(category, note, imageBase64, timestamp, if (isSynced) 1L else 0L)
        queries.lastInsertId().executeAsOne()
    }

    suspend fun getAllReports(): List<ReportEntity> = withContext(Dispatchers.Default) {
        queries.getAllReports().executeAsList()
    }

    suspend fun getUnsyncedReports(): List<ReportEntity> = withContext(Dispatchers.Default) {
        queries.getUnsyncedReports().executeAsList()
    }

    suspend fun markAsSynced(id: Long) = withContext(Dispatchers.Default) {
        queries.markAsSynced(id)
    }
}
