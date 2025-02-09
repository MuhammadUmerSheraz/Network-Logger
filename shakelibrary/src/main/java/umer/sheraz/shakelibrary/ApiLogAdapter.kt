package umer.sheraz.shakelibrary

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import umer.sheraz.shakelibrary.NetworkLogger.currentLogs
import umer.sheraz.shakelibrary.NetworkLogger.getApiLogFromFile
import umer.sheraz.shakelibrary.NetworkLogger.isActivityDetailOpened
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
            holder.log.text = "${log.apiName} (${log.requestDuration?:""})"
            holder.tvTimestamp.text = formatDate(log.timestamp)
            if (log.apiIsSuccessful == true) {
                holder.logParent.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
                holder.log.setTextColor(ContextCompat.getColor(context, R.color.black))
                holder.tvTimestamp.setTextColor(ContextCompat.getColor(context, R.color.black))
                holder.tvMethod.setTextColor(ContextCompat.getColor(context, R.color.black))
            } else {
                holder.logParent.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
                holder.log.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.tvTimestamp.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.tvMethod.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            holder.logParent.setOnClickListener {
                getApiLogFromFile(log.id)?.let {
                    currentLogs = it
                    isActivityDetailOpened = true
                    val intent = Intent(context, RequestDetailActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    val options = ActivityOptions.makeCustomAnimation(
                        context,
                        R.anim.slide_in_bottom,
                        R.anim.fade_out
                    )
                    context.startActivity(intent, options.toBundle())
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


}

