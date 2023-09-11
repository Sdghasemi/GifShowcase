package com.hirno.gif.data.source.remote

import com.hirno.gif.data.source.RandomDataSource
import com.hirno.gif.network.ApiClient

/**
 * Random gif remote data source implementation
 */
object RandomRemoteDataSource : RandomDataSource {
    /**
     * Retrieves a random gif from server
     *
     * @return Server response of the network call
     */
    override suspend fun getRandomGif() = ApiClient.retrofit.getRandomGif()
}