package com.hirno.gif.data.source.remote

import com.hirno.gif.data.Error
import com.hirno.gif.data.source.RandomDataSource
import com.hirno.gif.data.testResponse
import com.hirno.gif.network.response.NetworkResponse

object FakeRandomDataSource : RandomDataSource {
    var model = testResponse
    var errorMessage: String? = null

    fun reset() {
        model = testResponse
        errorMessage = null
    }

    override suspend fun getRandomGif() = generateResponse()

    private fun generateResponse() = errorMessage?.let { message ->
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
    } ?: NetworkResponse.Success(model)
}