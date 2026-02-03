package com.example.beerbicep.domain

import androidx.paging.PagingData
import com.example.beerbicep.Resource_Class
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getBeerPager(): Flow<PagingData<BeerDomain>>

    fun getBeerById(id: Int): Flow<Resource_Class<BeerDomain>>

    fun getFavourBeer(): Flow<List<BeerDomain>>

    suspend fun toggleBeer(beerDomain: BeerDomain)

    fun searchBeerQuery(query: String): Flow<Resource_Class<List<BeerDomain>>>

}