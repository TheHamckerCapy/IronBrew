package com.example.beerbicep.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

//data base operations for beer related data
@Dao
interface BeerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBeers(beer: List<BeerEntity>)

    @Query("DELETE FROM beers")
    suspend fun clearAll()

    /*
    Returns a PagingSource<Int, BeerEntity>. This is a crucial component for the Paging 3 library,
    providing the data source for a paginated list of beers directly from the database.

    PagingSource: A class from the Paging 3 library that defines how to load snapshots of data into a PagingData stream.
    It's the foundation of creating a paginated list.
     */

    @Query("SELECT * FROM beers")
    fun pagingSource(): PagingSource<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE isFavourite = 1")
    fun getFavoriteBeers(): Flow<List<BeerEntity>>//returns flow for reactive ui

    @Query("SELECT * FROM beers WHERE id = :id")
    fun getBeerById(id: Int): Flow<BeerEntity?>

    @Query(
        """
             SELECT * FROM beers 
             WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%'
             
         """
    )//searches in name column of beer table for the query/search string and it is not case sensitive
    suspend fun searchBeerQuery(query: String): List<BeerEntity>
}