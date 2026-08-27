package com.awaj.assistant.accessibility

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AssistAccessibilityService : AccessibilityService() {

    companion object {
        private var instance: AssistAccessibilityService? = null

        fun getInstance(): AssistAccessibilityService? = instance

        private val _isServiceEnabled = MutableStateFlow(false)
        val isServiceEnabled: StateFlow<Boolean> = _isServiceEnabled.asStateFlow()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isServiceEnabled.value = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Active window changed or content refreshed
    }

    override fun onInterrupt() {
        // Service interrupted
    }

    override fun onUnbind(intent: Intent?): Boolean {
        instance = null
        _isServiceEnabled.value = false
        return super.onUnbind(intent)
    }

    fun getCurrentScreenElements(): List<UiElement> {
        val root = rootInActiveWindow ?: return emptyList()
        return ScreenTreeReader.dumpWindowHierarchy(root)
    }

    fun clickOnText(text: String, onComplete: (() -> Unit)? = null): Boolean {
        val elements = getCurrentScreenElements()
        val target = NodeFinder.findElementByText(elements, text) ?: return false

        // Try direct accessibility action first
        if (target.nodeRef != null && target.nodeRef.isClickable) {
            target.nodeRef.performAction(android.view.accessibility.AccessibilityNodeInfo.ACTION_CLICK)
            onComplete?.invoke()
            return true
        }

        // Fallback to gesture dispatch at center coordinates
        GestureDispatcher.clickElementBounds(this, target.bounds, onComplete)
        return true
    }
}
