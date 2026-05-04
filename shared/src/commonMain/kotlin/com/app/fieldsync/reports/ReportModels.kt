package com.app.fieldsync.reports

import kotlinx.serialization.Serializable

@Serializable
data class FieldReportRequest(
    val category: String,
    val note: String,
    val imageBase64: String
)

@Serializable
data class FieldReportResponse(
    val id: String,
    val message: String,
    val timestamp: Long
)
