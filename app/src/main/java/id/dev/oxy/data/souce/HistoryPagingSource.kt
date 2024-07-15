package id.dev.oxy.data.souce

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.dev.oxy.data.api.ApiService
import id.dev.oxy.data.local.DataStoreRepository
import id.dev.oxy.data.model.history.adapter.HistoryRow
import id.dev.oxy.util.Constant
import id.dev.oxy.util.mapToHistoryRow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class HistoryPagingSource @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreRepository: DataStoreRepository,
) : PagingSource<Int, HistoryRow>() {
    override fun getRefreshKey(state: PagingState<Int, HistoryRow>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HistoryRow> {
        val token = dataStoreRepository.readToken.first()

        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getHistorySell(
                Constant.HEADER_TOKEN + token,
                position.toString(),
                params.loadSize.toString()
            )
            LoadResult.Page(
                data = responseData.data.mapToHistoryRow(),
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.data.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}