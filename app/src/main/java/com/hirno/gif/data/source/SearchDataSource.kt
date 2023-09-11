package com.hirno.gif.data.source

import com.hirno.gif.data.GenericResponse
import com.hirno.gif.model.GifArrayResponse

/**
 * Main entry point for searching gifs with the provided search term.
 */
interface SearchDataSource {
    suspend fun searchGifs(term: String): GenericResponse<GifArrayResponse>
}