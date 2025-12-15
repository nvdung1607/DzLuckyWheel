package com.example.dzluckywheel.domain.usecase

import com.example.dzluckywheel.data.model.Entry

class ShuffleEntriesUseCase {
    operator fun invoke(entries: List<Entry>): List<Entry> {
        return entries.shuffled()
    }
}
