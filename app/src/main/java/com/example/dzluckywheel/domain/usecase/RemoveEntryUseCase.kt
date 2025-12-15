package com.example.dzluckywheel.domain.usecase

import com.example.dzluckywheel.data.model.Entry
import com.example.dzluckywheel.data.repository.EntryRepository

class RemoveEntryUseCase(private val repository: EntryRepository) {
    operator fun invoke(entry: Entry) {
        repository.removeEntry(entry)
    }
}
