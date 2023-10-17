package com.example.exovideoplayer.domain.repository

import androidx.paging.PagingData
import com.example.exovideoplayer.domain.data.Posts
import kotlinx.coroutines.flow.Flow

interface SamplePagingRepository {
    fun listOfPagingData(pageSize: Int): Flow<PagingData<String>>

    suspend fun getPosts() : List<Posts>
}