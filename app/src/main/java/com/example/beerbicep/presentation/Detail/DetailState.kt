package com.example.beerbicep.presentation.Detail

import com.example.beerbicep.Resource_Class
import com.example.beerbicep.domain.BeerDomain

data class DetailState(
    val beerDetail: Resource_Class<BeerDomain> = Resource_Class.Loading(),
)
