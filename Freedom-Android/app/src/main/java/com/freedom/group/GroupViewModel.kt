package com.freedom.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GroupViewModel : ViewModel() {
    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups

    fun createGroup(name: String, currentUser: String) {
        viewModelScope.launch {
            val newGroup = Group(id = name.lowercase(), name = name, members = listOf(currentUser))
            _groups.value = _groups.value + newGroup
        }
    }
}
