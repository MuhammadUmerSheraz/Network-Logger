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
import java.util.ArrayList


class ApiLogAdapter(private val context: Context,
                    val apiCallLogs :ArrayList<ApiCallLog>) : RecyclerView.Adapter<ApiLogAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_api_log, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {



        val log = apiCallLogs[position]
        holder.log.text = "${log.method} - ${log.apiName}"

        holder.logParent.setOnClickListener {
            val dialog = Dialog(context, R.style.AppMaterialPostLogin)
            dialog.setContentView(R.layout.dialog_log_detail)
            if (dialog.window != null) {
            }


            dialog.findViewById<LinearLayout>(R.id.ll_content).apply {
                if(log.apiIsSuccessful == true) {
                    setBackgroundResource(R.drawable.box_dialog_background_success)
                } else if(log.apiIsSuccessful == false) {
                    setBackgroundResource(R.drawable.box_dialog_background_fail)
                }
            }

            try {
                dialog.show()
            } catch (e: Exception) {
            }
            val copyFullRequest = dialog.findViewById<TextView>(R.id.tv_cp_full_request)
            val copyApiName = dialog.findViewById<TextView>(R.id.tv_cp_api_name)
            val copyParameters = dialog.findViewById<TextView>(R.id.tv_cp_params)
            val copyResponse = dialog.findViewById<TextView>(R.id.tv_cp_response)
            val apiName = dialog.findViewById<TextView>(R.id.tv_api_name)
            val apiParams = dialog.findViewById<RecyclerView>(R.id.rcv_api_params)
            val closeIV = dialog.findViewById<ImageView>(R.id.closeIV)
            val recyclerView = dialog.findViewById<RecyclerView>(R.id.body_rcv)


            val adapterParam = TreeAdapter()
            apiParams.adapter = adapterParam



            val adapter = TreeAdapter()
            recyclerView.adapter = adapter





            apiName.text = log.apiName

            if (!log.apiParameters.isNullOrBlank()) {
                val jsonObject = JSONObject(log.apiParameters?:"")
                adapterParam.setData(jsonObject)
                copyParameters.isVisible=true
            } else {
                copyParameters.isVisible=false
            }

            if (!log.apiResponse.isNullOrBlank()) {
                val jsonObject = JSONObject(log.apiResponse?:"")
                adapter.setData(jsonObject)
                copyResponse.isVisible=true
            } else {
                copyResponse.isVisible=false
            }

            copyFullRequest.setOnClickListener {
                val text = "Api Name:\n${log.apiName}\n\n${if (copyParameters.isVisible) "Api Parameters:\n${log.apiParameters}\n\n" else ""}Api Response:\n${log.apiResponse}"
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
    }

    override fun getItemCount(): Int {
        return apiCallLogs.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var logParent: CardView = itemView.findViewById(R.id.cl_log)
        var log: TextView = itemView.findViewById(R.id.tv_api_log)
    }

    fun copyClipBoard(label: String, text: String) {
        val clipBoard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipBoard.setPrimaryClip(ClipData.newPlainText(label, text))
        Toast.makeText(context, "$label copied to Clipboard", Toast.LENGTH_SHORT).show()
    }


}

