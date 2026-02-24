package dev.delath.housha.ui.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.delath.housha.data.AnimeRepository
import dev.delath.housha.model.Anime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState {
    object Loading : UiState()
    data class Success(val animes: List<Anime>) : UiState()
    data class Error(val message: String) : UiState()
}

@HiltViewModel
class AllViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _allAnimes = MutableStateFlow<List<Anime>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(true)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<UiState> = combine(
        _allAnimes, _searchQuery, _isLoading, _error
    ) { animes, query, loading, error ->
        when {
            loading -> UiState.Loading
            error != null -> UiState.Error(error)
            else -> UiState.Success(
                if (query.isBlank()) animes
                else animes.filter { it.title.contains(query, ignoreCase = true) }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)

    val subscribedIds: StateFlow<Set<Int>> = repository.getSubscribed()
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        loadCurrentSeason()
    }

    fun loadCurrentSeason() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _allAnimes.value = repository.fetchCurrentSeason()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load anime"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSubscribe(anime: Anime, isCurrentlySubscribed: Boolean) {
        viewModelScope.launch {
            if (isCurrentlySubscribed) repository.unsubscribe(anime)
            else repository.subscribe(anime)
        }
    }
}
