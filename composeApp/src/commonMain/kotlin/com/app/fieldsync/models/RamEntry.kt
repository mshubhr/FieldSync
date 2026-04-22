package com.app.fieldsync.models

import kotlinx.datetime.LocalDate

data class RamEntry(
    val sizeKb: Int,
    val date: LocalDate
)