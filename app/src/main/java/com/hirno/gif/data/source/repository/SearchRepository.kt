package com.hirno.gif.data.source.repository

import com.hirno.gif.data.GenericResponse
import com.hirno.gif.model.GifArrayResponse

/**
 * Interface to the data layer.
 */
interface SearchRepository {
    suspend fun searchGifs(term: String): GenericResponse<GifArrayResponse>
}