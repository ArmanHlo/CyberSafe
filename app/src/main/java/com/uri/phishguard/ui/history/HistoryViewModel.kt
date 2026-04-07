package com.uri.phishguard.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uri.phishguard.data.local.ScanResult
import com.uri.phishguard.data.repository.DatabaseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: DatabaseRepository) : ViewModel() {

    val scanHistory: StateFlow<List<ScanResult>> = repository.allScans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteScan(scan: ScanResult) {
        viewModelScope.launch {
            repository.deleteScan(scan)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
