package com.example.dzluckywheel.data.model

enum class EntryType {
    TEXT,
    IMAGE
}

data class Entry(
    val id: Int,
    val type: EntryType,
    val value: String,      // text hoặc URI ảnh
    var excluded: Boolean = false
)
