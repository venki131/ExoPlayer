package com.example.exovideoplayer.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.exovideoplayer.data.remote.PagingListApi
import retrofit2.HttpException
import java.io.IOException

class SamplePagingSource(
    private val api: PagingListApi
): PagingSource<String, String>() {
    override fun getRefreshKey(state: PagingState<String, String>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, String> {
        return try {
            val data = api.getList(
                after = if (params is LoadParams.Append) params.key else null,
                before = if (params is LoadParams.Prepend) params.key else null,
                start = 0,
                limit = params.loadSize
            ).data

            LoadResult.Page(
                data = data.dataList.map { it.url },
                prevKey = data.before,
                nextKey = data.after
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}