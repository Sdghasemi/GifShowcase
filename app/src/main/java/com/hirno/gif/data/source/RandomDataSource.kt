package com.hirno.gif.data.source

import com.hirno.gif.data.GenericResponse
import com.hirno.gif.model.SingleGifResponse

/**
 * Main entry point for retrieving a random gif data.
 */
interface RandomDataSource {
    suspend fun getRandomGif(): GenericResponse<SingleGifResponse>
}