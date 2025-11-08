package com.example.timecapsule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timecapsule.data.Message
import com.example.timecapsule.data.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class UiState(
    val list: List<Message> = emptyList(),
    val today: Message? = null
)

class CapsuleViewModel(private val repo: MessageRepository): ViewModel() {
    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    fun load() = viewModelScope.launch {
        val items = repo.all()
        _state.update { it.copy(list = items, today = items.randomOrNull()) }
    }

    fun add(title: String, text: String) = viewModelScope.launch {
        if (title.isBlank() || text.isBlank()) return@launch
        repo.add(title, text)
        load()
    }

    fun refreshRandom() = viewModelScope.launch {
        val items = _state.value.list
        if (items.isEmpty()) return@launch
        _state.update { it.copy(today = items.randomOrNullDifferent(it.today)) }
    }
}

private fun List<Message>.randomOrNull() =
    if (isEmpty()) null else this[Random.nextInt(size)]

private fun List<Message>.randomOrNullDifferent(current: Message?): Message? {
    if (isEmpty()) return null
    if (size == 1) return this[0]
    var next: Message
    do { next = this[Random.nextInt(size)] } while (next.id == current?.id)
    return next
}
