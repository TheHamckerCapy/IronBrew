package com.example.beerbicep.presentation.Detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beerbicep.Resource
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val beerId: Int = savedStateHandle.get<Int>("beerId")!!

    private val _detailState = MutableStateFlow(DetailState())
    val detailState: StateFlow<DetailState> = _detailState.asStateFlow()

    init {
        fetchBearDetails()
    }

    private fun fetchBearDetails() {
        repository.getBeerById(beerId)
            .onEach {resource ->
                _detailState.value = _detailState.value.copy(
                    beerDetail = resource
                )
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(events: DetailEvents){
        when(events){
            DetailEvents.RetryDetail -> {
                fetchBearDetails()
            }
            DetailEvents.ToggleFav -> {
                val currentBeer = (_detailState.value.beerDetail as? Resource.Success)?.data
                if(currentBeer!=null){
                    viewModelScope.launch {
                        repository.toggleBeer(currentBeer)
                    }
                }

            }
        }
    }
}