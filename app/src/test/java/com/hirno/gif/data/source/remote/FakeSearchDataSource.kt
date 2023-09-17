package com.hirno.gif.data.source.remote

import com.hirno.gif.data.Error
import com.hirno.gif.data.source.SearchDataSource
import com.hirno.gif.data.testArrayResponse
import com.hirno.gif.network.response.NetworkResponse
import kotlinx.coroutines.delay
import kotlin.time.Duration

object FakeSearchDataSource : SearchDataSource {
    var model = testArrayResponse
    var responseDelay: Duration? = null
    var errorMessage: String? = null

    fun reset() {
        model = testArrayResponse
        responseDelay = null
        errorMessage = null
    }

    override suspend fun searchGifs(term: String) = generateResponse()

    private suspend fun generateResponse() = errorMessage?.let { message ->
        val code = 400
        NetworkResponse.ApiError(
            body = Error(
                meta = Error.Meta(
                    status = code,
                    message = message,
                )
            ),
            code = code
        )
    } ?: run {
        responseDelay?.let { delay(it) }
        NetworkResponse.Success(model)
    }
}