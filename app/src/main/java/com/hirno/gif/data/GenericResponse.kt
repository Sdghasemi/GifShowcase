package com.hirno.gif.data

import com.hirno.gif.network.response.NetworkResponse

/**
 * A typealias used for using generic [Error] as the ErrorModel type of [NetworkResponse]
 */
typealias GenericResponse<S> = NetworkResponse<S, Error>