package com.meds.data.models

import kotlinx.serialization.Serializable

@Serializable
data class RestResponse<T>(
    val status : StatusResponse,
    val response : T
)

@Serializable
data class StatusResponse(
    val statusCode : Int,
    val message: String? = ""
)