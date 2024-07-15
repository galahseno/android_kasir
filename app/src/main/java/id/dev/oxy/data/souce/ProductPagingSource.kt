package id.dev.oxy.data.souce

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.dev.oxy.data.api.ApiService
import id.dev.oxy.data.local.DataStoreRepository
import id.dev.oxy.data.model.sales.product.Data
import id.dev.oxy.util.Constant.HEADER_TOKEN
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ProductPagingSource @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreRepository: DataStoreRepository,
    private val name: String,
    private val categoryId: String
) :
    PagingSource<Int, Data>() {

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
        delay(100)
        val token = dataStoreRepository.readToken.first()
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getProduct(
                HEADER_TOKEN + token,
                position.toString(),
                params.loadSize.toString(),
                name,
                sku = name,
                categoryId
            )
            LoadResult.Page(
                data = responseData.data,
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