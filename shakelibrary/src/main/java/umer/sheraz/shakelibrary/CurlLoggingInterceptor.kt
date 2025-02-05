package umer.sheraz.shakelibrary

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import umer.sheraz.shakelibrary.ShakeLibrary.saveApiLogToFile
import umer.sheraz.shakelibrary.ShakeLibrary.saveRequestId
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.UUID


class ApiLoggingInterceptor(context: Context) : Interceptor {


    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("ApiLogs", Context.MODE_PRIVATE)


    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()

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
        // Build cURL command
        val curlRequest = buildCurlCommand(request)

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

        val apiCallLog = ApiCallLog(
            headers = headersJson,
            contentType = contentType,
            method = method,
            apiName = apiName,
            apiResponse = responseBodyString,
            apiParameters = apiParameters,
            apiIsSuccessful = apiIsSuccessful,
            apiHttpCode = response.code,
            curlRequest = curlRequest
        )

        // Save the unique request ID to SharedPreferences
        saveRequestId(
            Gson().toJson(
                ApiCallLog(
                    method = apiCallLog.method,
                    apiName = apiCallLog.apiName,
                    apiIsSuccessful = apiCallLog.apiIsSuccessful,
                    id = apiCallLog.id,
                )
            )
        )

        // Save the detailed API log to a file
        saveApiLogToFile(apiCallLog.id, apiCallLog)
        return response
    }






    // Save the detailed log to a file


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

    fun formatFormData(formData: String): String {
        return formData.split("&").joinToString("&") {
            val pair = it.split("=")
            // Ensure that each key and value is properly URL-encoded
            val key = pair[0]
            val value = pair.getOrElse(1) { "" }
            "$key=$value"
        }
    }


    fun beautifyJson(jsonString: String): String {
        try {
            val jsonElement = JsonParser.parseString(jsonString)
            val gson = GsonBuilder().setPrettyPrinting().create()
            return gson.toJson(jsonElement)
        } catch (_: Exception) {
            return jsonString
        }

    }

    private fun buildCurlCommand(request: Request): String {
        val url = request.url.toUrl().toString()
        val method = request.method
        val headers = request.headers
        val body = request.body
        val curlCommand = StringBuilder("curl -X $method \"$url\"")

        // Add headers to the cURL request
        for (i in 0 until headers.size) {
            val name = headers.name(i)
            val value = headers.value(i)
            curlCommand.append(" -H \"$name: $value\"")
        }

        // Add body data if available
        body?.let {
            val contentType = it.contentType()

            // Handle specific content types
            when (contentType?.subtype) {
                "x-www-form-urlencoded" -> {
                    // Handle form data (application/x-www-form-urlencoded)
                    val buffer = Buffer()
                    it.writeTo(buffer)
                    val bodyContent = buffer.readString(StandardCharsets.UTF_8)
                    if (bodyContent.isNotBlank()) {
                        val formattedBody = formatFormData(bodyContent)
                        curlCommand.append(" -d \"$formattedBody\"")
                    }
                }

                "json" -> {
                    // Handle JSON (application/json)
                    val buffer = Buffer()
                    it.writeTo(buffer)
                    val bodyContent = buffer.readString(StandardCharsets.UTF_8)
                    if (bodyContent.isNotBlank()) {
                        curlCommand.append(" -d \"$bodyContent\"")
                    }
                }

                "multipart" -> {
                    // Handle Multipart (multipart/form-data)
                    // Multipart data is more complex, and we'll need to extract the content boundaries and format it correctly.
                    val boundary = contentType.parameter("boundary")
                    if (boundary != null) {
                        val buffer = Buffer()
                        it.writeTo(buffer)
                        val bodyContent = buffer.readString(StandardCharsets.UTF_8)
                        if (bodyContent.isNotBlank()) {
                            curlCommand.append(" -d \"--$boundary\\n$bodyContent\"")
                        }
                    }
                }

                else -> {
                    // For other content types, treat as raw data (this could be plain text, XML, etc.)
                    val buffer = Buffer()
                    it.writeTo(buffer)
                    val bodyContent = buffer.readString(StandardCharsets.UTF_8)
                    if (bodyContent.isNotBlank()) {
                        curlCommand.append(" -d \"$bodyContent\"")
                    }
                }
            }
        }

        return curlCommand.toString()
    }

}

