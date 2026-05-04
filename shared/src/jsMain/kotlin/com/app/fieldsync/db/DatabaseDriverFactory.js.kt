package com.app.fieldsync.db

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        throw Exception("Not implemented for JS")
    }
}
