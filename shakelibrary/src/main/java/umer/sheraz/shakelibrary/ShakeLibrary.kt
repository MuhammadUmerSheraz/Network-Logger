package umer.sheraz.shakelibrary
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import java.lang.ref.WeakReference

object ShakeLibrary : Application.ActivityLifecycleCallbacks {
    private var shakeDetector: ShakeDetector? = null
    private var activityCount = 0
    var isActivityOpened = false
    val apiCallLogs = mutableListOf<ApiCallLog>()

    fun initialize(context: Context) {
        val app = context.applicationContext as Application
        app.registerActivityLifecycleCallbacks(this)

        shakeDetector = ShakeDetector(WeakReference(context)) {
            openShakeActivity(context)
        }
    }

    private fun openShakeActivity(context: Context) {
        if (!isActivityOpened) {
            isActivityOpened = true
            val intent = Intent(context, ShakeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
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

    fun parseJsonToTree(jsonString: String): List<TreeNode> {
        val jsonElement = JsonParser.parseString(jsonString)
        return parseJsonElement(jsonElement, "")
    }

    private fun parseJsonElement(element: JsonElement, key: String): List<TreeNode> {
        val nodes = mutableListOf<TreeNode>()
        when {
            element.isJsonObject -> {
                val obj = element.asJsonObject
                val node = TreeNode(key, null, isExpanded = false)
                obj.entrySet().forEach { entry ->
                    node.children.addAll(parseJsonElement(entry.value, entry.key))
                }
                nodes.add(node)
            }
            element.isJsonArray -> {
                val array = element.asJsonArray
                val node = TreeNode(key, "[Array]", isExpanded = false)
                array.forEachIndexed { index, jsonElement ->
                    node.children.addAll(parseJsonElement(jsonElement, "[$index]"))
                }
                nodes.add(node)
            }
            else -> nodes.add(TreeNode(key, element.asString))
        }
        return nodes
    }

}