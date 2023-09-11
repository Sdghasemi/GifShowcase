package com.hirno.gif.data.source.remote

import com.hirno.gif.data.source.SearchDataSource
import com.hirno.gif.network.ApiClient

/**
 * Search gifs remote data source implementation
 */
object SearchRemoteDataSource : SearchDataSource {
    /**
     * Retrieves gif search results from server
     *
     * @return Server response of the network call
     */
    override suspend fun searchGifs(term: String) = ApiClient.retrofit.searchGifs(term)
}