package com.umer.networklogger

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface ApiService {

    // Define an API endpoint (GET request)
    @GET()
    fun getCall(@Url url: String): Call<ResponseBody>


    @POST()
    fun postCall2(@Url url: String): Call<ResponseBody>


    @POST()
    fun postCall(@Url url: String, @Body postModel: PostModel): Call<ResponseBody>

    @PUT()
    fun putCall(@Url url: String, @Body postModel: PostModel): Call<ResponseBody>


    @PATCH()
    fun patchCall(@Url url: String, @Body postModel: PostModel): Call<ResponseBody>

    @DELETE()
    fun deleteCall(@Url url: String): Call<ResponseBody>
}
