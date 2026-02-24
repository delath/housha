package dev.delath.housha.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.delath.housha.data.AnimeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    repository: AnimeRepository
) : ViewModel() {

    val subscribedCount: StateFlow<Int> = repository.getSubscribedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}