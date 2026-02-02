package com.example.beerbicep.di

import android.app.Application
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.example.beerbicep.Constant
import com.example.beerbicep.data.local.BeerDb
import com.example.beerbicep.data.local.BeerEntity
import com.example.beerbicep.data.remote.BeerRemoteMediator
import com.example.beerbicep.data.remote.PunkApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesBeerApi(): PunkApi {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpCliend = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .client(okHttpCliend)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun providesBeerDatabase(app: Application): BeerDb {
        return Room.databaseBuilder(
            context = app,
            BeerDb::class.java,
            "beers.db"
        ).build()

    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun providesPager(beerDb: BeerDb, punkApi: PunkApi): Pager<Int, BeerEntity> {
        return Pager(
            config = PagingConfig(pageSize = 30),
            remoteMediator = BeerRemoteMediator(
                beerDb = beerDb,
                punkApi = punkApi
            ),
            pagingSourceFactory = {
                beerDb.beerDao.pagingSource()
            }
        )
    }
}