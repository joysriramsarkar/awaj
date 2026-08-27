package com.awaj.assistant.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect

object GestureDispatcher {

    fun click(service: AccessibilityService, x: Float, y: Float, onComplete: (() -> Unit)? = null) {
        val clickPath = Path().apply {
            moveTo(x, y)
        }
        val stroke = GestureDescription.StrokeDescription(clickPath, 0, 80)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    onComplete?.invoke()
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    // Gesture cancelled
                }
            },
            null
        )
    }

    fun clickElement(service: AccessibilityService, element: UiElement, onComplete: (() -> Unit)? = null): Boolean {
        // Dynamic Safety Gate: Never click password or financial credential nodes
        if (element.isPassword || element.isSensitive) {
            return false
        }

        val centerX = element.bounds.centerX().toFloat()
        val centerY = element.bounds.centerY().toFloat()
        click(service, centerX, centerY, onComplete)
        return true
    }

    fun clickElementBounds(service: AccessibilityService, bounds: Rect, onComplete: (() -> Unit)? = null) {
        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()
        click(service, centerX, centerY, onComplete)
    }

    fun scroll(service: AccessibilityService, startY: Float, endY: Float, onComplete: (() -> Unit)? = null) {
        val screenWidth = 540f // approx mid screen
        val scrollPath = Path().apply {
            moveTo(screenWidth, startY)
            lineTo(screenWidth, endY)
        }
        val stroke = GestureDescription.StrokeDescription(scrollPath, 0, 300)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    onComplete?.invoke()
                }
            },
            null
        )
    }
}
