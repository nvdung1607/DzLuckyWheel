package com.example.dzluckywheel.domain.usecase

import com.example.dzluckywheel.data.model.Entry
import com.example.dzluckywheel.data.repository.EntryRepository

class ExcludeEntryUseCase(private val repository: EntryRepository) {
    operator fun invoke(entry: Entry) {
        val updated = entry.copy(excluded = true)
        repository.updateEntry(updated)
    }
}
