package com.hirno.gif.data.source.repository

import com.hirno.gif.data.source.RandomDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Concrete implementation to load random gif from remote data source.
 */
class RandomRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val remoteDataSource: RandomDataSource,
) : RandomRepository {
    override suspend fun getRandomGif() = withContext(ioDispatcher) {
        remoteDataSource.getRandomGif()
    }
}