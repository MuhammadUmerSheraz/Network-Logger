package umer.sheraz.shakelibrary

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import umer.sheraz.shakelibrary.ShakeLibrary.clearLogs
import umer.sheraz.shakelibrary.ShakeLibrary.getRequestId
import umer.sheraz.shakelibrary.ShakeLibrary.isActivityOpened

class ShakeActivity : AppCompatActivity() {
    lateinit var tv_no_items: TextView
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_shake)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tv_no_items = findViewById<TextView>(R.id.tv_no_items)

        val btn_clear = findViewById<MaterialButton>(R.id.btn_clear)
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