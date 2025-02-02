package umer.sheraz.shakelibrary

data class TreeNode(
    val key: String,
    val value: Any?,
    var isExpanded: Boolean = false,
    val children: MutableList<TreeNode> = mutableListOf(),
    var level: Int = 0
)
