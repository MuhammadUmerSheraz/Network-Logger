package umer.sheraz.shakelibrary

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale


class ApiLogAdapter(
    private val context: Context,
    val apiCallLogs: ArrayList<ApiCallLog>
) : RecyclerView.Adapter<ApiLogAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_api_log, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        try {


            val log = apiCallLogs[position]
            holder.tvMethod.text = log.method
            holder.log.text = log.apiName
            holder.tvTimestamp.text = formatDate(log.timestamp)

            holder.logParent.setOnClickListener {
                val dialog = Dialog(context, R.style.AppMaterialPostLogin)
                dialog.setContentView(R.layout.dialog_log_detail)
                if (dialog.window != null) {
                }


                dialog.findViewById<LinearLayout>(R.id.ll_content).apply {
                    if (log.apiIsSuccessful == true) {
                        setBackgroundResource(R.drawable.box_dialog_background_success)
                    } else if (log.apiIsSuccessful == false) {
                        setBackgroundResource(R.drawable.box_dialog_background_fail)
                    }
                }
                dialog.show()

                val copyFullRequest = dialog.findViewById<TextView>(R.id.tv_cp_full_request)
                val copyApiName = dialog.findViewById<TextView>(R.id.tv_cp_api_name)
                val copyParameters = dialog.findViewById<TextView>(R.id.tv_cp_params)
                val copyResponse = dialog.findViewById<TextView>(R.id.tv_cp_response)
                val copyHeaders = dialog.findViewById<TextView>(R.id.tv_cp_api_header)
                val nonJsonResponse = dialog.findViewById<TextView>(R.id.non_json_response)
                val nonJsonParams = dialog.findViewById<TextView>(R.id.non_json_params)
                val apiName = dialog.findViewById<TextView>(R.id.tv_api_name)
                val apiParams = dialog.findViewById<RecyclerView>(R.id.rcv_api_params)
                val closeIV = dialog.findViewById<ImageView>(R.id.closeIV)
                val recyclerView = dialog.findViewById<RecyclerView>(R.id.body_rcv)
                val apiHeader = dialog.findViewById<RecyclerView>(R.id.rcv_api_header)
                val nonJsonHeader = dialog.findViewById<TextView>(R.id.non_json_header)




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

                apiName.text = log.apiName

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

                copyApiName.setOnClickListener {
                    val text = log.apiName ?: ""
                    copyClipBoard("Api Name", text)
                }

                copyParameters.setOnClickListener {
                    val text = log.apiParameters ?: ""
                    copyClipBoard("Api Parameters", text)
                }

                copyResponse.setOnClickListener {
                    val text = log.apiResponse ?: ""
                    copyClipBoard("Api Response", text)
                }

                closeIV.setOnClickListener {
                    dialog.dismiss()
                }
            }
        } catch (e: Exception) {
            print(e.message)
        }
    }

    private fun formatDate(timestamp: Long): String {
        var formattedDate = ""
        try {
            val date = Date(timestamp)

            // Create a SimpleDateFormat object with a readable format
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

            // Convert the Date object to a readable string
            formattedDate = dateFormat.format(date)
        } catch (error: Exception) {

        }

        return formattedDate

    }

    override fun getItemCount(): Int {
        return apiCallLogs.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var logParent: CardView = itemView.findViewById(R.id.cl_log)
        var log: TextView = itemView.findViewById(R.id.tv_api_log)
        var tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
        var tvMethod: TextView = itemView.findViewById(R.id.tv_api_method)
    }

    fun copyClipBoard(label: String, text: String) {
        val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipBoard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, "$label copied to Clipboard", Toast.LENGTH_SHORT).show()
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
        }catch (error: Exception){

        }

        return jsonObject
    }

}

