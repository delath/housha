package dev.delath.housha.ui.subscribed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.delath.housha.data.AnimeRepository
import dev.delath.housha.model.Anime
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscribedViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    val subscribedAnime: StateFlow<List<Anime>> = repository.getSubscribed()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun unsubscribe(anime: Anime) {
        viewModelScope.launch {
            repository.unsubscribe(anime)
        }
    }
}