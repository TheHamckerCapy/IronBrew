package com.example.beerbicep.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.beerbicep.data.local.BeerDb
import com.example.beerbicep.data.local.BeerEntity
import com.example.beerbicep.data.mapper.toBeerEntity

import retrofit2.HttpException
import java.io.IOException

import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class BeerRemoteMediator @Inject constructor(
    private val punkApi: PunkApi,
    private val beerDb: BeerDb
): RemoteMediator<Int,BeerEntity>() {

    private val dao = beerDb.beerDao
    private val remoteDao = beerDb.remoteKeyDao

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BeerEntity>
    ): MediatorResult {
        return try {
            val loadKey = when(loadType){
                LoadType.REFRESH ->1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem==null){
                        1
                    }else{
                        (lastItem.id/state.config.pageSize)+1
                    }
                }
            }

            val beer = punkApi.getListOfBeers(
                page = loadKey,
                perPage = state.config.pageSize
            )
            beerDb.withTransaction {
                if(loadType==LoadType.REFRESH){
                    dao.clearAll()

                }
                val beerEntitites = beer.map { it.toBeerEntity() }
                dao.insertAllBeers(beerEntitites)
            }
            MediatorResult.Success(
                endOfPaginationReached = beer.isEmpty()
            )

        }catch (e: IOException){
            MediatorResult.Error(e)
        }catch (e:HttpException){
            MediatorResult.Error(e)
        }
    }

}