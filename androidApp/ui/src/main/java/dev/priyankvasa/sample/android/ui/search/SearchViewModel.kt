package dev.priyankvasa.sample.android.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.priyankvasa.sample.android.ui.core.model.TaskState
import dev.priyankvasa.sample.android.ui.util.launchSafe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _searchResults = MutableStateFlow<List<String>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _searchState = MutableStateFlow<TaskState>(TaskState.Idle)
    val searchState = _searchState.asStateFlow()

    fun onQueryChange(query: String) {
        viewModelScope.launchSafe {
            _query.emit(query)

            if (query.isBlank()) {
                submitQuery()
            }
        }
    }

    fun submitQuery() {
        viewModelScope.launchSafe {
            val query = _query.value

            if (query.isBlank()) {
                _searchState.value = TaskState.Idle
            } else {
                _searchState.value = TaskState.Running
                _searchState.value = TaskState.Idle
                _searchResults.value = buildList {
                    add(Random.nextBytes(10).toString())
                }
            }
        }
    }
}
