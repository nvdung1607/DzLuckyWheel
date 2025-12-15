package com.example.dzluckywheel.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dzluckywheel.data.model.Entry
import com.example.dzluckywheel.data.repository.EntryRepository

class WheelViewModel(
    private val repository: EntryRepository
) : ViewModel() {

    private val _entries = MutableLiveData<List<Entry>>()
    val entries: LiveData<List<Entry>> = _entries

    fun loadEntries() { /* gọi repository */ }
    fun shuffleEntries() { /* gọi usecase */ }
    fun sortEntries() { /* gọi usecase */ }
    fun excludeEntry(entry: Entry) { /* gọi usecase */ }
}
