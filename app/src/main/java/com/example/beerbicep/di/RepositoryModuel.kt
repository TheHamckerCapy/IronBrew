package com.example.beerbicep.di

import com.example.beerbicep.data.RepositoryImpl
import com.example.beerbicep.domain.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsRepository(repositoryImpl: RepositoryImpl): Repository
}