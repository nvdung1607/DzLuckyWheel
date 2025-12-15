package com.example.dzluckywheel.data.repository

import com.example.dzluckywheel.data.model.Entry

interface EntryRepository {
    suspend fun getAllEntries(): List<Entry>
    suspend fun addEntry(entry: Entry)
    suspend fun removeEntry(entry: Entry)
    suspend fun updateEntry(entry: Entry)
}
