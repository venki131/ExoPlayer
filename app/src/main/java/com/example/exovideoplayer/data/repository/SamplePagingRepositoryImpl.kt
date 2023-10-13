package com.example.exovideoplayer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.exovideoplayer.data.remote.ListResponseDtoItem
import com.example.exovideoplayer.data.remote.PagingListApi
import com.example.exovideoplayer.data.remote.Posts
import com.example.exovideoplayer.domain.repository.SamplePagingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SamplePagingRepositoryImpl @Inject constructor(
    private val pagingListApi: PagingListApi
) :
    SamplePagingRepository {
    override fun listOfPagingData(pageSize: Int) = Pager(
        PagingConfig(pageSize)
    ) {
        SamplePagingSource(api = pagingListApi)
    }.flow

    override suspend fun getPosts(): List<Posts> {
        return pagingListApi.getPosts()
    }
}