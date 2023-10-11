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

    val videoList = listOf(
        "https://storage.googleapis.com/stockgro-feed-assets-qa/post_media/428de1c3-03d2-46a5-a5b8-2bab2eca9850.mp4",
        "https://storage.googleapis.com/stockgro-feed-assets-qa/post_media/5b007f34-5ecb-4d0f-8972-fa643ad528b5.mp4",
        "https://storage.googleapis.com/stockgro-feed-assets-qa/post_media/07ef8030-d627-47bc-a09d-b0a34514c3f8.mp4",
        "https://storage.googleapis.com/stockgro-feed-assets-qa/post_media/b5ad16e9-01d6-4a29-9b6f-cb68069f4d85.mp4",
        "https://storage.googleapis.com/stockgro-feed-assets-qa/post_media/8f55abf0-ffd5-42d3-8253-64a28470ac9d.mp4"
    )

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