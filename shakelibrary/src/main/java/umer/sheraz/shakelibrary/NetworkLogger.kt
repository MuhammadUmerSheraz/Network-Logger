package umer.sheraz.shakelibrary

import android.app.Activity
import android.app.ActivityOptions
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.lang.ref.WeakReference

object NetworkLogger : Application.ActivityLifecycleCallbacks {
    private var shakeDetector: ShakeDetector? = null
    private var activityCount = 0
    var isActivityOpened = false
    var isActivityDetailOpened = false
    lateinit var currentLogs: ApiCallLog
    val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    lateinit var logsDirectory: File
    lateinit var sharedPreferences: SharedPreferences


    fun clearLogs() {
        sharedPreferences.edit().putStringSet("uniqueRequestIds", null).apply()
    }

    fun saveApiLogToFile(requestId: String, apiCallLog: ApiCallLog) {
        val apiLogJson = gson.toJson(apiCallLog)
        val logFile = File(logsDirectory, "$requestId.json")
        logFile.writeText(apiLogJson)
    }
    fun getApiLogFromFile(requestId: String): ApiCallLog? {
        // Construct the file path using the requestId
        val logFile = File(logsDirectory, "$requestId.json")

        // Check if the file exists
        if (logFile.exists()) {
            val gson = Gson()

            // Read the content of the file
            val apiLogJson = logFile.readText()

            // Deserialize the JSON content back to ApiCallLog
            return gson.fromJson(apiLogJson, ApiCallLog::class.java)
        }

        // Return null if the file does not exist
        return null
    }
    fun getRequestId(): ArrayList<ApiCallLog> {

        val list = ArrayList<ApiCallLog>()
        val uniqueRequestIds =
            sharedPreferences.getStringSet("uniqueRequestIds", mutableSetOf()) ?: mutableSetOf()
        uniqueRequestIds.forEach {
            list.add(gson.fromJson(it, ApiCallLog::class.java))
        }
        list.sortByDescending {
            it.timestamp
        }
        return list
    }
    // Save the unique request ID to SharedPreferences
    fun saveRequestId(requestId: String) {
        // Get the current set of request IDs
        val uniqueRequestIds =
            sharedPreferences.getStringSet("uniqueRequestIds", mutableSetOf()) ?: mutableSetOf()

        // Create a copy of the set and add the new requestId
        val updatedRequestIds = uniqueRequestIds.toMutableSet() // Creating a new mutable set

        // Add the new request ID
        updatedRequestIds.add(requestId)

        // Save the updated set back to SharedPreferences
        sharedPreferences.edit().putStringSet("uniqueRequestIds", updatedRequestIds).apply()
    }



    fun initialize(context: Context) {
        val app = context.applicationContext as Application
        app.registerActivityLifecycleCallbacks(this)
        shakeDetector = ShakeDetector(WeakReference(context)) {
            navigateLogsActivity(context)
        }
        logsDirectory = File(context.filesDir, "api_logs")
        sharedPreferences =
            context.getSharedPreferences("ApiLogs", Context.MODE_PRIVATE)
        if (!logsDirectory.exists()) {
            logsDirectory.mkdir()
        }

    }

    fun navigateLogsActivity(context: Context) {
        if (!isActivityOpened && !isActivityDetailOpened) {
            isActivityOpened = true
            val intent = Intent(context, ShakeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            val options = ActivityOptions.makeCustomAnimation(
                context,
                R.anim.slide_in_bottom,
                R.anim.fade_out
            )
            context.startActivity(intent, options.toBundle())

        }

    }


    override fun onActivityStarted(activity: Activity) {
        activityCount++
        if (activityCount == 1) {
            shakeDetector?.startListening() // Start when app is in foreground
        }
    }

    override fun onActivityStopped(activity: Activity) {
        activityCount--
        if (activityCount == 0) {
            shakeDetector?.stopListening() // Stop when app goes to background
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

}