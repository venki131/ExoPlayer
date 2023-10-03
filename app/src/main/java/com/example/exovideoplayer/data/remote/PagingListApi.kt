package com.example.exovideoplayer.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PagingListApi {

    @GET("/photos")
    suspend fun getList(
        @Query("after") after: String? = null,
        @Query("before") before: String? = null,
        @Query("_start") start: Int,
        @Query("_limit") limit: Int? = 10,
    ): List<ListResponseDto>
}