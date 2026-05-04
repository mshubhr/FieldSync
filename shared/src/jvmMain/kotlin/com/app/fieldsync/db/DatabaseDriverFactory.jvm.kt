package com.app.fieldsync.db

import app.cash.sqldelight.db.SqlDriver

class JvmDatabaseDriverFactory : DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        throw Exception("Not implemented for JVM")
    }
}
