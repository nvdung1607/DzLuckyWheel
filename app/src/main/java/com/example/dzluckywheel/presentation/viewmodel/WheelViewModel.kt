package com.example.dzluckywheel.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dzluckywheel.data.model.Entry
import com.example.dzluckywheel.data.model.EntryType

class WheelViewModel : ViewModel() {

    private val _entries = MutableLiveData<List<Entry>>(emptyList())
    val entries: LiveData<List<Entry>> = _entries

    fun addEntry(entry: Entry) {
        val current = _entries.value ?: emptyList()
        _entries.value = current + entry
    }

    fun removeEntry(entry: Entry) {
        val current = _entries.value ?: emptyList()
        _entries.value = current.filter { it.id != entry.id }
    }

    fun shuffleEntries() {
        val current = _entries.value ?: emptyList()
        _entries.value = current.shuffled()
    }

    fun sortEntries() {
        val current = _entries.value ?: emptyList()
        _entries.value = current.sortedBy { it.value }
    }
}
