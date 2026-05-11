package com.app.fieldsync.db

import app.cash.sqldelight.db.SqlDriver

class WasmDatabaseDriverFactory : DatabaseDriverFactory {

    override fun createDriver(): SqlDriver {
        throw UnsupportedOperationException("WASM driver not implemented yet")
    }
}