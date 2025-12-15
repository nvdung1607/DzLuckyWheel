package com.example.dzluckywheel.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.dzluckywheel.data.model.Entry
import com.example.dzluckywheel.data.model.EntryType
import com.example.dzluckywheel.data.repository.InMemoryEntryRepository
import com.example.dzluckywheel.domain.usecase.*

class WheelViewModel : ViewModel() {

    private val repository = InMemoryEntryRepository()

    private val addEntryUseCase = AddEntryUseCase(repository)
    private val removeEntryUseCase = RemoveEntryUseCase(repository)
    private val shuffleEntriesUseCase = ShuffleEntriesUseCase()
    private val sortEntriesUseCase = SortEntriesUseCase()
    private val excludeEntryUseCase = ExcludeEntryUseCase(repository)

    fun testLogic() {
        addEntryUseCase(Entry(1, EntryType.TEXT, "Alice"))
        addEntryUseCase(Entry(2, EntryType.TEXT, "Bob"))
        addEntryUseCase(Entry(3, EntryType.TEXT, "Charlie"))

        Log.d("WheelViewModel", "All entries: ${repository.getAllEntries()}")

        val shuffled = shuffleEntriesUseCase(repository.getAllEntries())
        Log.d("WheelViewModel", "Shuffled: $shuffled")

        val sorted = sortEntriesUseCase(repository.getAllEntries())
        Log.d("WheelViewModel", "Sorted: $sorted")

        excludeEntryUseCase(Entry(2, EntryType.TEXT, "Bob"))
        Log.d("WheelViewModel", "After exclude: ${repository.getAllEntries()}")
    }
}
