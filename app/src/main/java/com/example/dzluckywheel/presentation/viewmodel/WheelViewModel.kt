package com.example.dzluckywheel.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dzluckywheel.data.model.Entry

class WheelViewModel : ViewModel() {

    private val _entries = MutableLiveData<List<Entry>>(emptyList())
    val entries: LiveData<List<Entry>> = _entries

    fun addEntry(entry: Entry) {
        _entries.value = _entries.value?.plus(entry)
    }

    fun removeEntry(entry: Entry) {
        _entries.value = _entries.value?.filter { it.id != entry.id }
    }

    fun shuffleEntries() {
        _entries.value = _entries.value?.shuffled()
    }

    fun sortEntries() {
        _entries.value = _entries.value?.sortedBy { it.value }
    }

    // 👉 Hàm này cần thêm để MainActivity gọi được
    fun setEntries(newEntries: List<Entry>) {
        _entries.value = newEntries
    }
}
