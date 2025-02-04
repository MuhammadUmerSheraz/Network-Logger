package umer.sheraz.shakelibrary
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import umer.sheraz.shakelibrary.ShakeLibrary.apiCallLogs
import java.nio.charset.StandardCharsets


class ApiLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val timestamp = System.currentTimeMillis()

        // Extract API name from URL path
        val headersJson = convertHeadersToJson(request.headers)

        val method = request.method
        val apiName = request.url.toUrl().toString()
        val contentType = request.body?.contentType()?.subtype

        // Capture request body
        var apiParameters: String? = null
        request.body?.let { body ->
            val buffer = Buffer()
            body.contentType()
            body.writeTo(buffer)
            apiParameters = buffer.readString(StandardCharsets.UTF_8)

        }

        // Proceed with the request
        val response = chain.proceed(request)



        // Capture response body
        val responseBodyString: String? = response.body?.let { body ->
            val source = body.source()
            source.request(Long.MAX_VALUE) // Buffer the response
            val buffer = source.buffer
            val rawJson = buffer.clone().readString(StandardCharsets.UTF_8)
            beautifyJson(rawJson)

        }

        // Determine API success
        val apiIsSuccessful = response.isSuccessful

        // Log API call
        apiCallLogs.add(
            ApiCallLog(
                headers = headersJson,
                contentType = contentType,
                method = method,
                apiName = apiName,
                apiResponse = responseBodyString,
                apiParameters = apiParameters,
                apiIsSuccessful = apiIsSuccessful,
                apiHttpCode = response.code,
                timestamp = timestamp
            )
        )

        return response
    }
    // Function to convert headers to JSON
    fun convertHeadersToJson(headers: okhttp3.Headers): String {
        try {
            val jsonObject = JsonObject()
            for (i in 0 until headers.size) {
                val name = headers.name(i)
                val value = headers.value(i)
                jsonObject.addProperty(name, value)
            }
            return jsonObject.toString()
        } catch (_: Exception) {
            return headers.toString()
        }
    }
    // Helper function to format form data
    fun formatFormData(formData: String): String {
        return formData.split("&").joinToString("\n") {
            val pair = it.split("=")
            "${pair[0]}: ${pair.getOrElse(1) { "" }}"
        }
    }


    fun beautifyJson(jsonString: String): String {
        try {
            val jsonElement = JsonParser.parseString(jsonString)
            val gson = GsonBuilder().setPrettyPrinting().create()
            return gson.toJson(jsonElement)
        }catch (_: Exception){
            return jsonString
        }

    }
}

