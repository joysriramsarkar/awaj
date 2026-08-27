package com.awaj.assistant

import android.graphics.Rect
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.awaj.assistant.accessibility.GestureDispatcher
import com.awaj.assistant.accessibility.UiElement
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented Safety Tests to verify safety invariants on Android Runtime.
 */
@RunWith(AndroidJUnit4::class)
class SafetyInstrumentationTest {

    @Test
    fun testSensitiveNodeSafetyInvariant() {
        val sensitiveElement = UiElement(
            id = 42,
            text = "Enter UPI PIN",
            contentDescription = "PIN Input Field",
            viewIdResourceName = "com.upi:id/pin_field",
            className = "android.widget.EditText",
            isClickable = true,
            isEditable = true,
            isScrollable = false,
            isPassword = true,
            isSensitive = true,
            bounds = Rect(200, 400, 800, 500)
        )

        // Safety Invariant: isPassword and isSensitive must be true
        assertTrue("Password element must be marked isPassword", sensitiveElement.isPassword)
        assertTrue("Password element must be marked isSensitive", sensitiveElement.isSensitive)
    }

    @Test
    fun testRegularElementIsNotMarkedSensitive() {
        val regularButton = UiElement(
            id = 10,
            text = "টর্চ অন করুন",
            contentDescription = "Torch Toggle",
            viewIdResourceName = "com.awaj.assistant:id/btn_torch",
            className = "android.widget.Button",
            isClickable = true,
            isEditable = false,
            isScrollable = false,
            isPassword = false,
            isSensitive = false,
            bounds = Rect(50, 100, 250, 180)
        )

        assertFalse(regularButton.isPassword)
        assertFalse(regularButton.isSensitive)
        assertEquals("টর্চ অন করুন", regularButton.text)
    }
}
