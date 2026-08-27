package com.awaj.assistant

import android.graphics.Rect
import com.awaj.assistant.accessibility.UiElement
import com.awaj.assistant.appfunctions.AwajAppFunctions
import com.awaj.assistant.nlu.ActionRequest
import com.awaj.assistant.nlu.RiskLevel
import com.awaj.assistant.safety.RiskClassifier
import com.awaj.assistant.safety.SensitiveAppBlocker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class SafetyAndSecurityTest {

    @Test
    fun testSensitiveUiNodeMaskingAndDetection() {
        val passwordNode = UiElement(
            id = 1,
            text = "••••••",
            contentDescription = "Password Field",
            viewIdResourceName = "com.bank.app:id/pin_entry",
            className = "android.widget.EditText",
            isClickable = true,
            isEditable = true,
            isScrollable = false,
            isPassword = true,
            isSensitive = true,
            bounds = Rect(100, 200, 300, 250)
        )

        // Ensure password node is marked sensitive
        assertTrue(passwordNode.isPassword)
        assertTrue(passwordNode.isSensitive)
        assertEquals("••••••", passwordNode.text)
    }

    @Test
    fun testAppFunctionsExposesOnlyLowRiskTools() {
        val exposed = AwajAppFunctions.exposedFunctions
        assertEquals(6, exposed.size)

        val functionNames = exposed.map { it.functionName }.toSet()
        assertTrue(functionNames.contains("awaj_open_app"))
        assertTrue(functionNames.contains("awaj_toggle_torch"))
        assertTrue(functionNames.contains("awaj_set_alarm"))
        assertTrue(functionNames.contains("awaj_set_timer"))
        assertTrue(functionNames.contains("awaj_set_volume"))
        assertTrue(functionNames.contains("awaj_get_device_info"))

        // High-risk tools MUST NEVER be exposed to AppFunctions
        assertFalse(functionNames.contains("awaj_make_call"))
        assertFalse(functionNames.contains("awaj_send_sms"))
        assertFalse(functionNames.contains("awaj_send_whatsapp"))
        assertFalse(functionNames.contains("awaj_transfer_money"))
    }

    @Test
    fun testFinancialIntentsAreStrictlyBlocked() {
        val blockedQueries = listOf(
            "বিকাশে ৫০০ টাকা পাঠাও",
            "আমার ইউপিআই পিন দিয়ে পেমেন্ট করো",
            "সেন্ড মানি করো",
            "নগদ থেকে টাকা পাঠাও",
            "গুগল পে দিয়ে টাকা পাঠাও"
        )

        for (query in blockedQueries) {
            assertTrue(
                "Query '$query' should be detected as financial transfer intent",
                SensitiveAppBlocker.containsFinancialTransferIntent(query)
            )

            val request = ActionRequest(
                action = "open_app",
                params = mapOf("app_query" to "bKash"),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = query,
                summaryBangla = ""
            )

            val evaluatedRisk = RiskClassifier.evaluateRisk(request)
            assertEquals("Financial intent '$query' must evaluate to BLOCKED risk", RiskLevel.BLOCKED, evaluatedRisk)
        }
    }

    @Test
    fun testAcousticCosineSimilarityMath() {
        fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
            var dot = 0f
            var norm1 = 0f
            var norm2 = 0f
            for (i in v1.indices) {
                dot += v1[i] * v2[i]
                norm1 += v1[i] * v1[i]
                norm2 += v2[i] * v2[i]
            }
            val denom = sqrt(norm1 * norm2)
            return if (denom > 0f) (dot / denom).coerceIn(-1f, 1f) else 0f
        }

        val vecA = floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f)
        val vecB = floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f)
        val vecOpposite = floatArrayOf(-0.5f, -0.5f, -0.5f, -0.5f)
        val vecOrthogonal = floatArrayOf(0.5f, -0.5f, 0.5f, -0.5f)

        // Identical vectors have cosine similarity = 1.0
        assertEquals(1.0f, cosineSimilarity(vecA, vecB), 0.001f)

        // Opposite vectors have cosine similarity = -1.0
        assertEquals(-1.0f, cosineSimilarity(vecA, vecOpposite), 0.001f)

        // Orthogonal vectors have cosine similarity = 0.0
        assertEquals(0.0f, cosineSimilarity(vecA, vecOrthogonal), 0.001f)
    }

    @Test
    fun testSensitiveAppBlockerCoverage() {
        val knownFintechApps = listOf(
            "com.google.android.apps.nbu.paisa.user",
            "com.phonepe.app",
            "net.one97.paytm",
            "in.org.npci.upiapp",
            "com.bkash.customerapp",
            "com.konasl.nagad",
            "com.dbbl.nexus.pay",
            "com.upay.customer",
            "com.sbi.upi",
            "com.csam.icici.bank.imobile",
            "com.hdfcbank.androidquickpay"
        )

        for (pkg in knownFintechApps) {
            assertTrue("Package '$pkg' must be blocked", SensitiveAppBlocker.isPackageBlocked(pkg))
        }

        // Test dynamic registration
        val newFintech = "com.futurebank.smartpay"
        assertFalse(SensitiveAppBlocker.isPackageBlocked(newFintech))
        SensitiveAppBlocker.registerCustomBlockedPackages(listOf(newFintech))
        assertTrue(SensitiveAppBlocker.isPackageBlocked(newFintech))
    }

    @Test
    fun testUndoRegistryExecution() {
        var torchState = "on"
        com.awaj.assistant.tools.UndoRegistry.recordUndoableAction("টর্চ বন্ধ করা") {
            torchState = "off"
            com.awaj.assistant.nlu.ToolResult.Success("টর্চ বন্ধ করা হয়েছে।")
        }

        val undoResult = com.awaj.assistant.tools.UndoRegistry.executeUndo()
        assertTrue(undoResult is com.awaj.assistant.nlu.ToolResult.Success)
        assertEquals("off", torchState)

        // Second undo when empty should fail safely
        val secondUndo = com.awaj.assistant.tools.UndoRegistry.executeUndo()
        assertTrue(secondUndo is com.awaj.assistant.nlu.ToolResult.Failed)
    }
}
