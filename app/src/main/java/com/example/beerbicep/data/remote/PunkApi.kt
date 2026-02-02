package com.example.beerbicep.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PunkApi {

    @GET("beers")
    suspend fun getListOfBeers(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<BeerDto>

    @GET("beers/{id}")
    suspend fun getBeerById(
        @Path("id") id: Int
    ): BeerDto
}