package com.example.dzluckywheel.domain.usecase

import com.example.dzluckywheel.data.model.Entry

class SortEntriesUseCase {
    operator fun invoke(entries: List<Entry>): List<Entry> {
        return entries.sortedBy { it.value }
    }
}
