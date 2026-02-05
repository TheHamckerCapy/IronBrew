package com.example.beerbicep.presentation.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.beerbicep.AdditionalComponents.TtsManager
import com.example.beerbicep.Resource_Class
import com.example.beerbicep.domain.BeerDomain
import com.example.beerbicep.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val ttsManager: TtsManager
) : ViewModel() {

    val beerPagingFlow = repository.getBeerPager().cachedIn(viewModelScope)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()
    init {

        observeTtsSettings()
    }

    private fun observeTtsSettings() {
        ttsManager.setting
            .onEach { settings ->
                _state.update { it.copy(ttsSettings = settings) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(events: HomeEvents) {
        when (events) {
            is HomeEvents.OnBeerClick -> {
            }

            HomeEvents.Refresh -> {

            }

            is HomeEvents.ToggleFav -> {
                viewModelScope.launch {
                    repository.toggleBeer(beerDomain = events.beerDomain)
                }
            }

            is HomeEvents.OnPitchChange ->ttsManager.updatePitch(events.value)
            is HomeEvents.OnRateChange -> ttsManager.updateRate(events.value)
        }
    }


    fun onToggleFavorite(beer: BeerDomain) {
        viewModelScope.launch {
            repository.toggleBeer(beer)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResult: StateFlow<Resource_Class<List<BeerDomain>>> = _searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(Resource_Class.Success(emptyList()))
            } else {
                repository.searchBeerQuery(query)
            }

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource_Class.Success(emptyList())
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}


