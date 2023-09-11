package com.hirno.gif.data.source.repository

import com.hirno.gif.data.GenericResponse
import com.hirno.gif.model.SingleGifResponse

/**
 * Interface to the data layer.
 */
interface RandomRepository {
    suspend fun getRandomGif(): GenericResponse<SingleGifResponse>
}