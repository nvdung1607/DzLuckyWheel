package com.example.dzluckywheel.data.model

data class Entry(
    val id: Int = 0,
    val type: EntryType,
    val value: String,
    val excluded: Boolean = false
)

enum class EntryType { TEXT, IMAGE }
