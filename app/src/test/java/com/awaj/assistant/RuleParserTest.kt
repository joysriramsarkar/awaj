package com.awaj.assistant

import com.awaj.assistant.nlu.IntentNormalizer
import com.awaj.assistant.nlu.RiskLevel
import com.awaj.assistant.nlu.RuleParser
import com.awaj.assistant.safety.RiskClassifier
import com.awaj.assistant.safety.SensitiveAppBlocker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RuleParserTest {

    private lateinit var parser: RuleParser

    @Before
    fun setup() {
        parser = RuleParser()
    }

    @Test
    fun testIntentNormalizerDigits() {
        val banglaDigits = "সকাল ৮:৩০ এ অ্যালার্ম দাও"
        val converted = IntentNormalizer.convertBanglaDigitsToEnglish(banglaDigits)
        assertTrue(converted.contains("8:30"))
    }

    @Test
    fun testTimeExtraction() {
        val time1 = IntentNormalizer.extractTime("সকাল ৮টায় অ্যালার্ম")
        assertNotNull(time1)
        assertEquals(8, time1?.first)
        assertEquals(0, time1?.second)

        val time2 = IntentNormalizer.extractTime("রাত ৯:৩০ এ অ্যালার্ম")
        assertNotNull(time2)
        assertEquals(21, time2?.first)
        assertEquals(30, time2?.second)
    }

    @Test
    fun testTorchCommandParsing() {
        val reqOn = parser.parse("টর্চ জ্বালাও")
        assertNotNull(reqOn)
        assertEquals("toggle_torch", reqOn?.action)
        assertEquals("on", reqOn?.params?.get("state"))

        val reqOff = parser.parse("টর্চ বন্ধ করো")
        assertNotNull(reqOff)
        assertEquals("toggle_torch", reqOff?.action)
        assertEquals("off", reqOff?.params?.get("state"))
    }

    @Test
    fun testAppLaunchParsing() {
        val req = parser.parse("হোয়াটসঅ্যাপ খোলো")
        assertNotNull(req)
        assertEquals("open_app", req?.action)
        assertEquals("whatsapp", req?.params?.get("app_query"))

        val reqGPay = parser.parse("গুগল পে খোলো")
        assertNotNull(reqGPay)
        assertEquals("open_app", reqGPay?.action)
        assertEquals("gpay", reqGPay?.params?.get("app_query"))

        val reqSpotify = parser.parse("স্পটিফাই খোলো")
        assertNotNull(reqSpotify)
        assertEquals("open_app", reqSpotify?.action)
        assertEquals("spotify", reqSpotify?.params?.get("app_query"))
    }

    @Test
    fun testHotspotCommand() {
        val reqOn = parser.parse("হটস্পট চালু করো")
        assertNotNull(reqOn)
        assertEquals("toggle_hotspot", reqOn?.action)
        assertEquals("on", reqOn?.params?.get("state"))

        val reqOff = parser.parse("হটস্পট বন্ধ করো")
        assertNotNull(reqOff)
        assertEquals("toggle_hotspot", reqOff?.action)
        assertEquals("off", reqOff?.params?.get("state"))
    }

    @Test
    fun testConnectivityCommand() {
        val reqWifi = parser.parse("ওয়াইফাই সেটিংস")
        assertNotNull(reqWifi)
        assertEquals("control_connectivity", reqWifi?.action)
        assertEquals("wifi", reqWifi?.params?.get("target"))

        val reqBt = parser.parse("ব্লুটুথ চালু করো")
        assertNotNull(reqBt)
        assertEquals("control_connectivity", reqBt?.action)
        assertEquals("bluetooth", reqBt?.params?.get("target"))
    }

    @Test
    fun testMathCalculator() {
        val reqMult = parser.parse("১০ গুণ ২০")
        assertNotNull(reqMult)
        assertEquals("calculate_math", reqMult?.action)
        assertEquals(10.0, reqMult?.params?.get("num1"))
        assertEquals(20.0, reqMult?.params?.get("num2"))
        assertEquals("multiply", reqMult?.params?.get("operation"))

        val reqAdd = parser.parse("১০০ যোগ ৫০")
        assertNotNull(reqAdd)
        assertEquals("calculate_math", reqAdd?.action)
        assertEquals(100.0, reqAdd?.params?.get("num1"))
        assertEquals(50.0, reqAdd?.params?.get("num2"))
        assertEquals("add", reqAdd?.params?.get("operation"))
    }

    @Test
    fun testMusicCommand() {
        val reqYt = parser.parse("ইউটিউবে গান লাগাও")
        assertNotNull(reqYt)
        assertEquals("play_music_query", reqYt?.action)
        assertEquals("youtube", reqYt?.params?.get("platform"))

        val reqSpotify = parser.parse("স্পটিফাইতে গান চালাও")
        assertNotNull(reqSpotify)
        assertEquals("play_music_query", reqSpotify?.action)
        assertEquals("spotify", reqSpotify?.params?.get("platform"))
    }

    @Test
    fun testCameraCommand() {
        val req = parser.parse("সেলফি তোলো")
        assertNotNull(req)
        assertEquals("open_camera_mode", req?.action)
        assertEquals("selfie", req?.params?.get("mode"))
    }

    @Test
    fun testCallParsingHighRisk() {
        val req = parser.parse("মাকে কল করো")
        assertNotNull(req)
        assertEquals("make_call", req?.action)
        assertEquals(RiskLevel.HIGH, req?.risk)
        assertTrue(req?.confirmationRequired == true)
    }

    @Test
    fun testSmsParsingHighRisk() {
        val req = parser.parse("রাহুলকে মেসেজ পাঠাও: আমি পৌঁছে গেছি")
        assertNotNull(req)
        assertEquals("send_sms", req?.action)
        assertEquals(RiskLevel.HIGH, req?.risk)
        assertTrue(req?.confirmationRequired == true)
        assertEquals("আমি পৌঁছে গেছি", req?.params?.get("message"))
    }

    @Test
    fun testVolumeCommand() {
        val req = parser.parse("ভলিউম বাড়াও")
        assertNotNull(req)
        assertEquals("set_volume", req?.action)
        assertEquals("up", req?.params?.get("direction"))
    }

    @Test
    fun testAlarmCommand() {
        val req = parser.parse("কাল সকাল আটটায় অ্যালার্ম দাও")
        assertNotNull(req)
        assertEquals("set_alarm", req?.action)
        assertEquals(8, req?.params?.get("hour"))
        assertTrue(req?.params?.get("is_tomorrow") == true)
    }

    @Test
    fun testTimerCommand() {
        val req = parser.parse("৫ মিনিটের টাইমার দাও")
        assertNotNull(req)
        assertEquals("set_timer", req?.action)
        assertEquals(300, req?.params?.get("seconds"))
    }

    @Test
    fun testStopCommand() {
        val req = parser.parse("থামো")
        assertNotNull(req)
        assertEquals("stop_all", req?.action)
    }

    @Test
    fun testRoutineCommands() {
        val req = parser.parse("সুপ্রভাত")
        assertNotNull(req)
        assertEquals("run_routine", req?.action)
        assertEquals("morning_routine", req?.params?.get("routine_id"))
    }

    @Test
    fun testSensitiveAppBlocker() {
        assertTrue(SensitiveAppBlocker.isPackageBlocked("com.google.android.apps.nbu.paisa.user"))
        assertTrue(SensitiveAppBlocker.isPackageBlocked("com.phonepe.app"))
        assertTrue(SensitiveAppBlocker.isPackageBlocked("net.one97.paytm"))
        assertTrue(SensitiveAppBlocker.isPackageBlocked("in.org.npci.upiapp"))
        assertTrue(SensitiveAppBlocker.isPackageBlocked("com.bkash.customerapp"))
        assertTrue(SensitiveAppBlocker.isPackageBlocked("com.konasl.nagad"))

        assertTrue(SensitiveAppBlocker.containsFinancialTransferIntent("টাকা ট্রান্সফার করো"))
        assertTrue(SensitiveAppBlocker.containsFinancialTransferIntent("আমার ইউপিআই পিন ১২৩৪"))

        val req = parser.parse("বিকাশে টাকা পাঠাও")
        val risk = RiskClassifier.evaluateRisk(req!!)
        assertEquals(RiskLevel.BLOCKED, risk)
    }
}
