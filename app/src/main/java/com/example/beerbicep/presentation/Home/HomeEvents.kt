package com.example.beerbicep.presentation.Home

import com.example.beerbicep.domain.BeerDomain

sealed class HomeEvents {
    data class ToggleFav(val beerDomain: BeerDomain) : HomeEvents()
    data class OnBeerClick(val id: Int) : HomeEvents()
    data object Refresh : HomeEvents()
}