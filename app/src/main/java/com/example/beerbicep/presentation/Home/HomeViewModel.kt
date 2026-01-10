package com.example.beerbicep.presentation.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.beerbicep.domain.BeerDomain
import com.example.beerbicep.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository
): ViewModel() {

    val beerPagingFlow = repository.getBeerPager().cachedIn(viewModelScope)

    fun onEvent(events: HomeEvents){
        when(events){
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

}