package com.awaj.assistant.accessibility

import android.graphics.Rect
import android.view.accessibility.AccessibilityNodeInfo

data class UiElement(
    val id: Int,
    val text: String,
    val contentDescription: String,
    val viewIdResourceName: String,
    val className: String,
    val isClickable: Boolean,
    val isEditable: Boolean,
    val isScrollable: Boolean,
    val bounds: Rect,
    val nodeRef: AccessibilityNodeInfo? = null
)

object ScreenTreeReader {

    fun dumpWindowHierarchy(rootNode: AccessibilityNodeInfo?): List<UiElement> {
        if (rootNode == null) return emptyList()

        val elements = mutableListOf<UiElement>()
        var counter = 1

        fun traverse(node: AccessibilityNodeInfo?) {
            if (node == null) return

            val text = node.text?.toString() ?: ""
            val desc = node.contentDescription?.toString() ?: ""
            val viewId = node.viewIdResourceName ?: ""
            val className = node.className?.toString() ?: ""
            val clickable = node.isClickable
            val editable = node.isEditable
            val scrollable = node.isScrollable

            val rect = Rect()
            node.getBoundsInScreen(rect)

            // Only capture elements with meaningful info or interactivity
            if (text.isNotBlank() || desc.isNotBlank() || clickable || editable || scrollable) {
                elements.add(
                    UiElement(
                        id = counter++,
                        text = text,
                        contentDescription = desc,
                        viewIdResourceName = viewId,
                        className = className,
                        isClickable = clickable,
                        isEditable = editable,
                        isScrollable = scrollable,
                        bounds = rect,
                        nodeRef = node
                    )
                )
            }

            for (i in 0 until node.childCount) {
                traverse(node.getChild(i))
            }
        }

        traverse(rootNode)
        return elements
    }
}
