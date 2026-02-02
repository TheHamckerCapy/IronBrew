package com.example.beerbicep.presentation.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.beerbicep.Resource
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    val beerPagingFlow = repository.getBeerPager().cachedIn(viewModelScope)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

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
        }
    }


    fun onToggleFavorite(beer: BeerDomain) {
        viewModelScope.launch {
            repository.toggleBeer(beer)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResult: StateFlow<Resource<List<BeerDomain>>> = _searchQuery
        .debounce(300L)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                flowOf(Resource.Success(emptyList()))
            } else {
                repository.searchBeerQuery(query)
            }

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Success(emptyList())
        )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}


