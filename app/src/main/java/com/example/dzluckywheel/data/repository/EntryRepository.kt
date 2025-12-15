package com.example.dzluckywheel.data.repository

import com.example.dzluckywheel.data.model.Entry

interface EntryRepository {
    fun getAllEntries(): List<Entry>
    fun addEntry(entry: Entry)
    fun removeEntry(entry: Entry)
    fun updateEntry(entry: Entry)
}
