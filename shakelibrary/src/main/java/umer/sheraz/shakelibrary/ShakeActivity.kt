package umer.sheraz.shakelibrary

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import umer.sheraz.shakelibrary.NetworkLogger.clearLogs
import umer.sheraz.shakelibrary.NetworkLogger.getRequestId
import umer.sheraz.shakelibrary.NetworkLogger.isActivityOpened

class ShakeActivity : AppCompatActivity() {
    lateinit var tv_no_items: TextView
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)
        tv_no_items = findViewById<TextView>(R.id.tv_no_items)

        val btn_clear = findViewById<Button>(R.id.btn_clear)
        recyclerView = findViewById(R.id.rv_api_log)
        recyclerView.layoutManager = LinearLayoutManager(this)
        btn_clear.setOnClickListener {
            clearLogs()
            displayApiLogs()
        }
        displayApiLogs()
        val closeIV = findViewById<ImageView>(R.id.closeIV)
        closeIV.setOnClickListener {
            finish()
        }
    }

    fun displayApiLogs() {

        val adapter = ApiLogAdapter(this, getRequestId())
        recyclerView.adapter = adapter
        tv_no_items.visibility =
            if (adapter.itemCount == 0) View.VISIBLE else View.GONE

    }

    override fun onResume() {
        super.onResume()
        isActivityOpened = true

    }

    override fun onStop() {
        super.onStop()
        isActivityOpened = false
    }
}