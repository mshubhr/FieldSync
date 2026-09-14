package com.app.fieldsync.views.navigation

import androidx.compose.runtime.Composable
import com.app.fieldsync.models.RamEntry
import com.app.fieldsync.reports.ReportRepository
import com.russhwolf.settings.Settings

@Composable
expect fun NavigationHost(
    initialScreen: Screen,
    reportRepository: ReportRepository,
    userName: String,
    historyEntries: List<RamEntry>,
    onLogout: () -> Unit,
    onReportSynced: (RamEntry) -> Unit,
    settings: Settings
)
