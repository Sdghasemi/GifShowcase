package com.hirno.gif.data.source.repository

import com.hirno.gif.data.source.SearchDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation to search gifs from remote data source.
 */
class SearchRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val remoteDataSource: SearchDataSource,
) : SearchRepository {
    override suspend fun searchGifs(term: String) = withContext(ioDispatcher) {
        remoteDataSource.searchGifs(term)
    }
}