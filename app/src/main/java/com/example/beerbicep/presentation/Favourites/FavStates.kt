package com.example.beerbicep.presentation.Favourites

import com.example.beerbicep.domain.BeerDomain

data class FavStates (
    val favBeersList : List<BeerDomain> = emptyList(),
    val isEmpty: Boolean = true
)