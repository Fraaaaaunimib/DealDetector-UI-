package com.example.navi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModelProvider

class SharedViewModel(private val preferencesManager: PreferencesManager) : ViewModel() {
    private val _requireIdentification = MutableStateFlow(preferencesManager.requireIdentification)
    val requireIdentification: StateFlow<Boolean> = _requireIdentification

    fun setRequireIdentification(value: Boolean) {
        viewModelScope.launch {
            preferencesManager.requireIdentification = value
            _requireIdentification.value = value
        }
    }
}

class SharedViewModelFactory(private val preferencesManager: PreferencesManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}