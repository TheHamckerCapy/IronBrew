package com.example.beerbicep.presentation.Detail

import com.example.beerbicep.Resource
import com.example.beerbicep.domain.BeerDomain

data class DetailState(
    val beerDetail: Resource<BeerDomain> = Resource.Loading(),
)
