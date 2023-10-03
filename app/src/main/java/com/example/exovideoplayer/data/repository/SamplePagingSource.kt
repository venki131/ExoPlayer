package com.example.exovideoplayer.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.exovideoplayer.data.remote.PagingListApi
import retrofit2.HttpException
import java.io.IOException

class SamplePagingSource(
    private val api: PagingListApi
): PagingSource<String, String>() {

    private var currentStart = 0 // Track the current starting position of the data

    override fun getRefreshKey(state: PagingState<String, String>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            // This loads starting from previous page, but since PagingConfig.initialLoadSize spans
            // multiple pages, the initial load will still load items centered around
            // anchorPosition. This also prevents needing to immediately launch prepend due to
            // prefetchDistance.
            state.closestPageToPosition(anchorPosition)?.prevKey
        }
    }

    val videoList = listOf("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4", "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3")

    override suspend fun load(params: LoadParams<String>): LoadResult<String, String> {
        return try {
            val data = api.getList(
                after = if (params is LoadParams.Append) params.key else null,
                before = if (params is LoadParams.Prepend) params.key else null,
                start = 0,
                limit = params.loadSize
            ).map {
                it.data
            }

            // Update the current starting position for the next load
            currentStart += params.loadSize

            //TODO change the prevKey, data and nextKey here accordingly
            LoadResult.Page(
                data = videoList,//data[0].dataList.map { it.url },
                prevKey = null,//if (data.data.before != null) data.data.before else null,
                nextKey = ""//if (data.data.after != null) data.data.after else null
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override val keyReuseSupported: Boolean
        get() = true

}