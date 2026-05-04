package com.app.fieldsync.reports

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import java.util.UUID

fun Route.reportRoutes() {
    val reports = mutableListOf<FieldReportRequest>()

    post("/sync-report") {
        val request = call.receive<FieldReportRequest>()
        reports.add(request)
        
        println("Report received: ${request.category} - ${request.note}. Image size: ${request.imageBase64.length} chars")
        
        call.respond(
            FieldReportResponse(
                id = UUID.randomUUID().toString(),
                message = "Report synced successfully",
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
