package com.hirno.gif.network

import com.hirno.gif.data.GenericResponse
import com.hirno.gif.model.GifArrayResponse
import com.hirno.gif.model.SingleGifResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * API interface of the app endpoints
 */
interface ApiInterface {
    @GET("random")
    suspend fun getRandomGif(): GenericResponse<SingleGifResponse>

    @GET("search")
    suspend fun searchGifs(@Query("q") term: String): GenericResponse<GifArrayResponse>

    @GET("{id}")
    suspend fun getGif(@Path("id") id: String): GenericResponse<SingleGifResponse>
}