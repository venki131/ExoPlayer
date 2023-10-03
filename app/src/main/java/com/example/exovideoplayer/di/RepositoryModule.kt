package com.example.exovideoplayer.di

import com.example.exovideoplayer.data.repository.SamplePagingRepositoryImpl
import com.example.exovideoplayer.domain.repository.SamplePagingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindListRepository(
        pagingRepo: SamplePagingRepositoryImpl
    ): SamplePagingRepository
}