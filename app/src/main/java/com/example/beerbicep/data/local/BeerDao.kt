package com.example.beerbicep.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface BeerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBeers(beer: List<BeerEntity>)

    @Query("DELETE FROM beers")
    suspend fun clearAll()

    @Query("SELECT * FROM beers")
     fun pagingSource(): PagingSource<Int, BeerEntity>

    @Query("SELECT * FROM beers WHERE isFavourite = 1")
     fun getFavoriteBeers(): Flow<List<BeerEntity>>

    @Query("SELECT * FROM beers WHERE id = :id")
     fun getBeerById(id: Int): Flow<BeerEntity?>
}