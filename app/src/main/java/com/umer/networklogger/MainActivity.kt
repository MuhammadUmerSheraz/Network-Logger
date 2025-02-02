package com.umer.networklogger

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    var progress_circular: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        progress_circular = findViewById<ProgressBar>(R.id.progress_bar)

        findViewById<Button>(R.id.btn_1).setOnClickListener {
            progress_circular?.isVisible = true
            responseCallBack(RetrofitInstance.apiService.getCall("https://dummyjson.com/products"))
        }
        findViewById<Button>(R.id.btn_2).setOnClickListener {
            progress_circular?.isVisible = true
            val postModel = PostModel("Title", "Body", 1)
            responseCallBack(
                RetrofitInstance.apiService.postCall(
                    "https://jsonplaceholder.typicode.com/posts",
                    postModel
                )
            )
        }
        findViewById<Button>(R.id.btn_3).setOnClickListener {
            progress_circular?.isVisible = true
            val postModel = PostModel("Title", "Body", 1,1)
            responseCallBack(
                RetrofitInstance.apiService.putCall(
                    "https://jsonplaceholder.typicode.com/posts/1",
                    postModel
                )
            )
        }
        findViewById<Button>(R.id.btn_4).setOnClickListener {
            progress_circular?.isVisible = true
            val postModel = PostModel("Title", "Body", 1,1)
            responseCallBack(
                RetrofitInstance.apiService.patchCall(
                    "https://jsonplaceholder.typicode.com/posts/1",
                    postModel
                )
            )
        }
        findViewById<Button>(R.id.btn_5).setOnClickListener {
            progress_circular?.isVisible = true
            responseCallBack(
                RetrofitInstance.apiService.deleteCall(
                    "https://jsonplaceholder.typicode.com/posts/1",
                )
            )
        }
    }

    private fun responseCallBack(result: Call<ResponseBody>) {
        result.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                progress_circular?.isVisible = false
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progress_circular?.isVisible = false

            }
        })
    }
}