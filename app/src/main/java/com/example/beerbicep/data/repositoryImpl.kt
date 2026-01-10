package com.example.beerbicep.data

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.example.beerbicep.Resource
import com.example.beerbicep.data.local.BeerDb
import com.example.beerbicep.data.local.BeerEntity
import com.example.beerbicep.data.mapper.toBeerDomain
import com.example.beerbicep.data.mapper.toBeerEntity
import com.example.beerbicep.data.remote.PunkApi
import com.example.beerbicep.domain.BeerDomain
import com.example.beerbicep.domain.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "RepositoryImpl" // Tag for logging


@Singleton
class RepositoryImpl @Inject constructor(
    private val api: PunkApi,
    private val db: BeerDb,
    private val pager: Pager<Int,BeerEntity>
): Repository {

    override fun getBeerPager(): Flow<PagingData<BeerDomain>> {
        Log.d(TAG, "getBeerPager: Creating beer pager flow.")
        return pager.flow
            .map { pagingData->
                Log.v(TAG, "getBeerPager: Mapping PagingData<BeerEntity> to PagingData<BeerDomain>.")
                pagingData.map { beerEntity->
                    beerEntity.toBeerDomain()
                }

            }
    }

    override fun getBeerById(id: Int): Flow<Resource<BeerDomain>> {
       return flow {
           Log.d(TAG, "getBeerById: Starting fetch for beer with id: $id")
           emit(Resource.Loading(true))

           val checkDbFirst = db.beerDao.getBeerById(id = id).first()
           if(checkDbFirst!=null){
               Log.i(TAG, "getBeerById: Found beer id $id in local database. Emitting success.")
               emit(Resource.Success(checkDbFirst.toBeerDomain()))
           }
           try {
               Log.d(TAG, "getBeerById: Fetching beer id $id from remote API.")
                val apiCall = api.getBeerById(id = id)
               if(apiCall!=null){
                   Log.i(TAG, "getBeerById: Successfully fetched beer id $id from API.")
                   val newEntity = apiCall.toBeerEntity()
                   val finalEntity = newEntity.copy(
                       isFavourite = checkDbFirst?.isFavourite ?: false
                   )
                   Log.d(TAG, "getBeerById: Inserting/updating beer id $id into database.")
                   db.beerDao.insertAllBeers(listOf(finalEntity))
                   db.beerDao.getBeerById(id).collect {
                       Log.d(TAG, "getBeerById: Emitting updated beer id $id from database.")
                       if(it != null) emit(Resource.Success(it.toBeerDomain()))
                   }
               }else{
                   if(checkDbFirst==null){
                       Log.w(TAG, "getBeerById: Beer id $id not found in API or database.")
                       emit(Resource.Error("Beer Not Found"))
                   }
               }
           }catch (e: HttpException) {
               Log.e(TAG, "getBeerById: HTTP error fetching beer id $id: ${e.code()}", e)
               emit(Resource.Error("An error occurred: ${e.message()}"))
           } catch (e: IOException) {
               Log.e(TAG, "getBeerById: Network error fetching beer id $id", e)
               emit(Resource.Error("Couldn't reach the server. Check your internet connection."))
           } catch (e: Exception) {
               Log.e(TAG, "getBeerById: Unknown error fetching beer id $id", e)
               emit(Resource.Error("An unknown error occurred: ${e.message}"))
           }
       }
    }

    override  fun getFavourBeer(): Flow<List<BeerDomain>> {
        Log.d(TAG, "getFavourBeer: Fetching favorite beers from database.")
       return db.beerDao.getFavoriteBeers().map {list->
           Log.i(TAG, "getFavourBeer: Found ${list.size} favorite beers. Mapping to domain model.")
           list.map { it.toBeerDomain() }
       }
    }

    override suspend fun toggleBeer(beerDomain: BeerDomain) {
        val beerToUpdate = beerDomain.copy(isFavorite = !beerDomain.isFavorite)
        db.beerDao.insertAllBeers(listOf(beerToUpdate.toBeerEntity()))
    }


}