package com.example.beerbicep.presentation.Favourites

import androidx.activity.result.launch
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beerbicep.domain.BeerDomain
import com.example.beerbicep.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _favBeerState = MutableStateFlow(FavStates())
    val favBeerState : StateFlow<FavStates> = _favBeerState.asStateFlow()

    init {
        // Observe the repository's flow of favorite beers when the ViewModel is created.
        observeFavoriteBeers()
    }


    fun onEvent(events: FavEvents){
        when(events){
            is FavEvents.OnBeerClick ->{

            }
            is FavEvents.ToggleFav -> {
                OnToggleFavorite(events.beerDomain)
            }
        }
    }

    private  fun observeFavoriteBeers() {
        // .onEach will trigger every time the underlying database query emits a new list.
        repository.getFavourBeer()
            .onEach { beers ->
                // Update the internal state with the new list from the repository.
                _favBeerState.value= _favBeerState.value.copy(
                    favBeersList = beers,
                    isEmpty = beers.isEmpty()
                )
            }
            .launchIn(viewModelScope) // launchIn is a concise way to collect a flow in a scope.
    }

    fun OnToggleFavorite(beer: BeerDomain) {
        viewModelScope.launch {
            repository.toggleBeer(beer)
        }
    }
}