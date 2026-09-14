package com.app.fieldsync.views.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.app.fieldsync.db.DatabaseDriverFactory
import com.app.fieldsync.db.FieldSyncDatabase
import com.app.fieldsync.reports.LocalReportDataSource
import com.app.fieldsync.reports.ReportRepository
import com.app.fieldsync.models.RamEntry
import androidx.compose.runtime.LaunchedEffect
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.russhwolf.settings.Settings

@Composable
@Preview
fun App(databaseDriverFactory: DatabaseDriverFactory? = null) {
    val settings = remember { Settings() }
    val hasSeenSplash = remember { settings.getBoolean("has_seen_splash", false) }
    val isLoggedIn = remember { settings.getBoolean("is_logged_in", false) }
    var userName by remember { mutableStateOf(settings.getString("user_name", "User")) }
    var historyEntries by remember { mutableStateOf(emptyList<RamEntry>()) }

    val reportRepository = remember {
        val driver = databaseDriverFactory?.createDriver()
        val localDataSource = driver?.let { LocalReportDataSource(FieldSyncDatabase(it)) }
        ReportRepository(localDataSource)
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            val localReports = reportRepository.getLocalReports()
            historyEntries = localReports.map { report ->
                RamEntry(
                    sizeKb = ((report.imageBase64.length * 0.75) / 1024).toInt(),
                    date = kotlin.time.Instant.fromEpochMilliseconds(report.timestamp)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                )
            }
        }
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE3F2FD),
            secondary = Color(0xFF00BCD4)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            val initialScreen = remember {
                when {
                    !hasSeenSplash -> Screen.Splash
                    isLoggedIn -> Screen.Main
                    else -> Screen.SignIn
                }
            }

            NavigationHost(
                initialScreen = initialScreen,
                reportRepository = reportRepository,
                userName = userName,
                historyEntries = historyEntries,
                onLogout = {
                    settings.putBoolean("is_logged_in", false)
                },
                onReportSynced = { newEntry ->
                    historyEntries = historyEntries + newEntry
                },
                settings = settings
            )
        }
    }
}
