package com.example.beerbicep.domain

import androidx.paging.PagingData
import com.example.beerbicep.Resource
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getBeerPager(): Flow<PagingData<BeerDomain>>

    fun getBeerById(id: Int): Flow<Resource<BeerDomain>>

     fun getFavourBeer(): Flow<List<BeerDomain>>

    suspend fun toggleBeer(beerDomain: BeerDomain)

     fun searchBeerQuery(query: String): Flow<Resource<List<BeerDomain>>>

}