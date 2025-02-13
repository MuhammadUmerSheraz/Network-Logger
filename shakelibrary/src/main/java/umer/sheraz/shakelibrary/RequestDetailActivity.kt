package umer.sheraz.shakelibrary

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import umer.sheraz.shakelibrary.NetworkLogger.currentLogs
import umer.sheraz.shakelibrary.NetworkLogger.isActivityDetailOpened

class RequestDetailActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.dialog_log_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail_screen)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val copyCurl = findViewById<Button>(R.id.copy_curl)
        val copyFullRequest = findViewById<Button>(R.id.tv_cp_full_request)
        val copyApiName = findViewById<TextView>(R.id.tv_cp_api_name)
        val copyParameters = findViewById<TextView>(R.id.tv_cp_params)
        val copyResponse = findViewById<TextView>(R.id.tv_cp_response)
        val copyHeaders = findViewById<TextView>(R.id.tv_cp_api_header)
        val nonJsonResponse = findViewById<TextView>(R.id.non_json_response)
        val nonJsonParams = findViewById<TextView>(R.id.non_json_params)
        val apiName = findViewById<TextView>(R.id.tv_api_name)
        val apiMethod = findViewById<TextView>(R.id.api_method)
        val apiParams = findViewById<RecyclerView>(R.id.rcv_api_params)
        val closeIV = findViewById<ImageView>(R.id.closeIV)
        val recyclerView = findViewById<RecyclerView>(R.id.body_rcv)
        val requestCode = findViewById<TextView>(R.id.requestCode)
        val apiHeader = findViewById<RecyclerView>(R.id.rcv_api_header)
        val nonJsonHeader = findViewById<TextView>(R.id.non_json_header)

        val log = currentLogs
        requestCode.text = "Response Code: ${log.apiHttpCode}"

        if (!log.headers.isNullOrBlank()) {
            try {
                apiHeader.adapter = TreeAdapter().apply {
                    val jsonObject = JSONObject(log.headers ?: "")
                    setData(jsonObject)
                }
            } catch (_: Exception) {
                nonJsonHeader.isVisible = true
                nonJsonHeader.text = log.headers
            }
            copyHeaders.isVisible = true
        } else {
            copyHeaders.isVisible = false
        }

        apiName.text ="${log.apiName} (${log.requestDuration?:""})"
        apiMethod.text= log.method
        if (!log.apiParameters.isNullOrBlank()) {
            val contentType = log.contentType
            // Check if it's JSON (model class) or form data
            if (contentType?.contains("json") == true) {
                // Handle JSON body (model class)
                try {
                    apiParams.adapter = TreeAdapter().apply {
                        val jsonObject = JSONObject(log.apiParameters ?: "")
                        setData(jsonObject)
                    }

                } catch (_: Exception) {
                    nonJsonParams.isVisible = true
                    nonJsonParams.text = log.apiParameters
                }

            } else if (contentType?.contains("x-www") == true) {
                // Handle form data
                val apiParameters = formatFormData(log.apiParameters ?: "")
                try {
                    apiParams.adapter = TreeAdapter().apply {
                        setData(apiParameters)
                    }
                } catch (_: Exception) {
                    nonJsonParams.isVisible = true
                    nonJsonParams.text = log.apiParameters
                }

            }


        } else {
            copyParameters.isVisible = false
        }

        if (!log.apiResponse.isNullOrBlank()) {
            try {
                recyclerView.adapter = TreeAdapter().apply {
                    val jsonObject = JSONObject(log.apiResponse ?: "")
                    setData(jsonObject)
                }
            } catch (_: Exception) {
                nonJsonResponse.isVisible = true
                nonJsonResponse.text = log.apiResponse
            }
            copyResponse.isVisible = true
        } else {
            copyResponse.isVisible = false
        }

        copyFullRequest.setOnClickListener {
            val text =
                "Api Name:\n${log.apiName}\n\n${if (copyParameters.isVisible) "Api Parameters:\n${log.apiParameters}\n\n" else ""}Api Response:\n${log.apiResponse}"
            copyClipBoard("Full Api Request", text)
        }

        copyCurl.setOnClickListener {
            copyClipBoard("Full CURL Request", log.curlRequest ?: "")
        }

        copyApiName.setOnClickListener {
            val text = log.apiName ?: ""
            copyClipBoard("Api Name", text)
        }

        copyParameters.setOnClickListener {
            val text = log.apiParameters ?: ""
            copyClipBoard("Api Parameters", text)
        }
        copyHeaders.setOnClickListener {
            val text = log.headers ?: ""
            copyClipBoard("Api Header", text)
        }

        copyResponse.setOnClickListener {
            val text = log.apiResponse ?: ""
            copyClipBoard("Api Response", text)
        }

        closeIV.setOnClickListener {
            finish()
        }
    }

    fun copyClipBoard(label: String, text: String) {
        val clipBoard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipBoard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(this, "$label copied to Clipboard", Toast.LENGTH_SHORT).show()
    }

    // Helper function to format form data
    fun formatFormData(formData: String): JSONObject {
        val jsonObject = JSONObject()

        try {
            formData.split("&").joinToString("\n") {
                val pair = it.split("=")
                jsonObject.put(pair[0], pair.getOrElse(1) { "" })
                "${pair[0]}: ${pair.getOrElse(1) { "" }}"
            }
        } catch (error: Exception) {

        }

        return jsonObject
    }

    override fun onResume() {
        super.onResume()
        isActivityDetailOpened = true

    }

    override fun onStop() {
        super.onStop()
        isActivityDetailOpened = false
    }
}