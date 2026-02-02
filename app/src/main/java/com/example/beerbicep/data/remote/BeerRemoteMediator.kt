package com.example.beerbicep.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.beerbicep.data.local.BeerDb
import com.example.beerbicep.data.local.BeerEntity
import com.example.beerbicep.data.local.RemoteKey
import com.example.beerbicep.data.mapper.toBeerEntity

import retrofit2.HttpException
import java.io.IOException

import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class BeerRemoteMediator @Inject constructor(
    private val punkApi: PunkApi,
    private val beerDb: BeerDb
) : RemoteMediator<Int, BeerEntity>() {

    private val dao = beerDb.beerDao
    private val remoteDao = beerDb.remoteKeyDao

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BeerEntity>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: 1
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    // If remoteKeys is null, that means the refresh result is not in the database yet.
                    val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    // If remoteKeys is null, that means the refresh result is not in the database yet.
                    // We can return Success with endOfPaginationReached = false because Paging
                    // will call REFRESH if it's empty.
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val beer = punkApi.getListOfBeers(
                page = loadKey,
                perPage = state.config.pageSize
            )
            val endOfPaginationReached = beer.isEmpty()
            beerDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteDao.clearRemoteKeys()
                    dao.clearAll()

                }
                val prevKey = if (loadKey == 1) null else loadKey - 1
                val nextKey = if (endOfPaginationReached) null else loadKey + 1

                // Create keys for EACH item
                val keys = beer.map {
                    RemoteKey(
                        beerId = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                val beerEntitites = beer.map { it.toBeerEntity() }
                remoteDao.insertAll(keys)
                dao.insertAllBeers(beerEntitites)
            }
            MediatorResult.Success(
                endOfPaginationReached = endOfPaginationReached
            )

        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, BeerEntity>): RemoteKey? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { beer ->
                // Get the remote keys of the last item retrieved
                remoteDao.getRemoteKeyByBeerId(beer.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, BeerEntity>): RemoteKey? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { beer ->
                // Get the remote keys of the first items retrieved
                remoteDao.getRemoteKeyByBeerId(beer.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, BeerEntity>
    ): RemoteKey? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { beerId ->
                remoteDao.getRemoteKeyByBeerId(beerId)
            }
        }
    }

}