package com.app.fieldsync.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

class JvmDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        val databaseFile = File("FieldSyncDatabase.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        if (!databaseFile.exists() || databaseFile.length() == 0L) {
            FieldSyncDatabase.Schema.create(driver)
        }
        return driver
    }
}