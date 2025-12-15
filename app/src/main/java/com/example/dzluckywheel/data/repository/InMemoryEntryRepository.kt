package com.example.dzluckywheel.data.repository

import com.example.dzluckywheel.data.model.Entry

class InMemoryEntryRepository : EntryRepository {

    private val entries = mutableListOf<Entry>()

    override fun getAllEntries(): List<Entry> = entries.toList()

    override fun addEntry(entry: Entry) {
        entries.add(entry)
    }

    override fun removeEntry(entry: Entry) {
        entries.removeIf { it.id == entry.id }
    }

    override fun updateEntry(entry: Entry) {
        val index = entries.indexOfFirst { it.id == entry.id }
        if (index != -1) {
            entries[index] = entry
        }
    }
}
