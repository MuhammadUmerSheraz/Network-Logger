package umer.sheraz.shakelibrary

data class ApiCallLog(
    var headers: String? = null,
    var contentType: String? = null,
    var method: String? = null,
    var apiName: String? = null,
    var apiResponse: String? = null,
    var apiParameters: String? = null,
    var apiIsSuccessful: Boolean? = null,
    var apiHttpCode: Int? = null,
    val timestamp: Long,

)
