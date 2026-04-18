package com.englishwords.ui.screens.result

import androidx.lifecycle.ViewModel
import com.englishwords.domain.model.SessionResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionResultViewModel : ViewModel() {
    
    private val _result = MutableStateFlow<SessionResult?>(null)
    val result: StateFlow<SessionResult?> = _result.asStateFlow()
    
    fun setResult(result: SessionResult) {
        _result.value = result
    }
}
