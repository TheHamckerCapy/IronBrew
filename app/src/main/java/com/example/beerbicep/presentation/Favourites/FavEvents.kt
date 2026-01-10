package com.example.beerbicep.presentation.Favourites

import com.example.beerbicep.domain.BeerDomain

sealed class FavEvents {
    data class OnBeerClick(val id: Int): FavEvents()
    data class ToggleFav(val beerDomain: BeerDomain): FavEvents()

}