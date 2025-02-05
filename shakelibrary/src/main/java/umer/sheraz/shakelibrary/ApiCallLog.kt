package umer.sheraz.shakelibrary

import java.util.UUID

data class ApiCallLog(
    var headers: String? = null,
    var contentType: String? = null,
    var method: String? = null,
    var apiName: String? = null,
    var apiResponse: String? = null,
    var apiParameters: String? = null,
    var apiIsSuccessful: Boolean? = null,
    var apiHttpCode: Int? = null,
    val curlRequest: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val id: String = UUID.randomUUID().toString()

)
