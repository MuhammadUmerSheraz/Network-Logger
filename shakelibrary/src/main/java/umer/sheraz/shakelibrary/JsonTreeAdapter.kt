package umer.sheraz.shakelibrary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

// TreeAdapter.kt
class TreeAdapter : RecyclerView.Adapter<TreeAdapter.ViewHolder>() {
    private var nodes = mutableListOf<TreeNode>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val expandCollapseIcon: ImageView = view.findViewById(R.id.expandCollapseIcon)
        val nodeText: TextView = view.findViewById(R.id.nodeText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tree_node, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val node = nodes[position]

        // Add indentation based on level
        holder.itemView.setPadding(node.level * 32, 0, 0, 0)

        // Set node text
        holder.nodeText.text = "${node.key}: ${node.value}"

        // Show/hide expand icon
        if (node.children.isEmpty()) {
            holder.expandCollapseIcon.visibility = View.INVISIBLE
        } else {
            holder.expandCollapseIcon.visibility = View.VISIBLE
            holder.expandCollapseIcon.setImageResource(
                if (node.isExpanded) R.drawable.ic_arrow_down
                else R.drawable.ic_arrow_right
            )
        }

        // Handle click events
        holder.expandCollapseIcon.setOnClickListener {
            toggleNode(position)
        }
    }

    override fun getItemCount() = nodes.size

    private fun toggleNode(position: Int) {
        val node = nodes[position]
        if (node.children.isEmpty()) return

        node.isExpanded = !node.isExpanded
        val flattenedNodes = if (node.isExpanded) {
            flattenNodes(node.children, node.level + 1)
        } else {
            emptyList()
        }

        if (node.isExpanded) {
            nodes.addAll(position + 1, flattenedNodes)
         //   notifyItemRangeInserted(position + 1, flattenedNodes.size)
            notifyDataSetChanged()
        } else {
            var count = 0
            var i = position + 1
            while (i < nodes.size && nodes[i].level > node.level) {
                count++
                i++
            }
            repeat(count) {
                nodes.removeAt(position + 1)
            }
           // notifyItemRangeRemoved(position + 1, count)
            notifyDataSetChanged()
        }
    }

    fun setData(jsonObject: JSONObject) {
        nodes.clear()
        nodes.addAll(parseJson("root", jsonObject))
        notifyDataSetChanged()
    }

    private fun parseJson(key: String, value: Any?, level: Int = 0): List<TreeNode> {
        val nodes = mutableListOf<TreeNode>()

        when (value) {
            is JSONObject -> {
                val node = TreeNode(key, "{...}", level = level)
                value.keys().forEach { childKey ->
                    node.children.addAll(parseJson(childKey, value.get(childKey), level + 1))
                }
                nodes.add(node)
            }
            is JSONArray -> {
                val node = TreeNode(key, "[...]", level = level)
                for (i in 0 until value.length()) {
                    node.children.addAll(parseJson("[$i]", value.get(i), level + 1))
                }
                nodes.add(node)
            }
            else -> {
                nodes.add(TreeNode(key, value?.toString(), level = level))
            }
        }
        return nodes
    }

    private fun flattenNodes(nodes: List<TreeNode>, level: Int): List<TreeNode> {
        val flattenedNodes = mutableListOf<TreeNode>()
        nodes.forEach { node ->
            node.level = level
            flattenedNodes.add(node)
            if (node.isExpanded) {
                flattenedNodes.addAll(flattenNodes(node.children, level + 1))
            }
        }
        return flattenedNodes
    }
}