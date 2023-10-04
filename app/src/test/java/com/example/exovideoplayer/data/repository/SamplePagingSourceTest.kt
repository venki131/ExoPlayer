package com.example.exovideoplayer.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams.Refresh
import com.example.exovideoplayer.data.remote.ListResponseDto
import com.example.exovideoplayer.data.remote.ListResponseDtoItem
import com.example.exovideoplayer.data.remote.ListingData
import com.example.exovideoplayer.data.remote.PagingListApi
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SamplePagingSourceTest {

    @Mock
    private lateinit var api: PagingListApi
    private lateinit var fakeResponse: List<String>
    private lateinit var pagingSource: SamplePagingSource

    @Before
    fun setup() {
        pagingSource = SamplePagingSource(api)
        fakeResponse = listOf(
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3"
        )
    }

    @Test
    fun `load data success returns pagedList`() = runTest {
        // Arrange
        val params = Refresh(
            key = null,
            loadSize = 10,
            placeholdersEnabled = false
        )
        val responseList = listOf(
            ListResponseDtoItem(
                1,
                1,
                title = "accusamus beatae ad facilis cum similique qui sunt",
                url = "https://via.placeholder.com/600/92c952",
                thumbnailUrl = "https://via.placeholder.com/150/92c952"
            ),
            ListResponseDtoItem(
                1,
                2,
                title = "accusamus beatae ad facilis cum similique qui sunt",
                url = "https://via.placeholder.com/600/92c952",
                thumbnailUrl = "https://via.placeholder.com/150/92c952"
            )
        )

        val listingData = ListingData(responseList, "", "", "0", "10")

        val responseData = listOf(ListResponseDto(listingData))

        whenever(api.getList(after = null, before = null, start = 0, limit = params.loadSize))
            .thenReturn(responseData)

        val paramss: PagingSource.LoadParams<String> = Refresh(
            key = null,
            loadSize = 10,
            placeholdersEnabled = false
        )

        // Act
        val result = pagingSource.load(paramss)

        // Assert
        assert(result is PagingSource.LoadResult.Page)
        val pageResult = result as PagingSource.LoadResult.Page
        assert(pageResult.data == fakeResponse)
    }

    @Test
    fun `load data failure throws IO exception`() = runTest {
        // Arrange
        val params: PagingSource.LoadParams<String> = Refresh(
            key = null,
            loadSize = 10,
            placeholdersEnabled = false
        )

        val exception = IOException("Simulated IO Exception")

        whenever(api.getList(after = null, before = null, start = 0, limit = params.loadSize))
            .thenAnswer { throw exception }

        // Act
        val result = pagingSource.load(params)

        // Assert
        assert(result is PagingSource.LoadResult.Error)
        val errorResult = result as PagingSource.LoadResult.Error
        assert(errorResult.throwable == exception)
    }

    @Test
    fun `load data failure throws http exception`() = runTest {
        // Arrange
        val params: PagingSource.LoadParams<String> = Refresh(
            key = null,
            loadSize = 10,
            placeholdersEnabled = false
        )

        val httpException = HttpException(Response.error<Any>(400, mock()))

        whenever(api.getList(after = null, before = null, start = 0, limit = params.loadSize))
            .thenAnswer { throw httpException }

        // Act
        val result = pagingSource.load(params)

        // Assert
        assert(result is PagingSource.LoadResult.Error)
        val errorResult = result as PagingSource.LoadResult.Error
        assert(errorResult.throwable == httpException)
    }
}