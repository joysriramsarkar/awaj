package com.awaj.assistant.safety

import java.util.Locale

/**
 * SensitiveAppBlocker strictly guards financial, banking, UPI, and authentication applications
 * and credentials from unauthorized automated gestures, screen scraping, and autonomous manipulation.
 */
object SensitiveAppBlocker {

    private val blockedPackagePrefixes = listOf(
        // Indian UPI & Payment Apps
        "com.google.android.apps.nbu.paisa.user", // Google Pay (GPay)
        "com.phonepe.app",                        // PhonePe
        "net.one97.paytm",                        // Paytm
        "in.org.npci.upiapp",                     // BHIM UPI
        "com.dreamplug.androidapp",               // CRED
        "com.mobikwik_new",                       // MobiKwik
        "com.freecharge.android",                 // Freecharge
        "com.sbi.lotusintouch",                   // YONO SBI
        "com.csam.icici.bank.imobile",            // iMobile ICICI
        "com.snapwork.hdfc",                      // HDFC Bank
        "com.axis.mobile",                        // Axis Mobile
        "com.msf.kbank.mobile",                   // Kotak 811
        "com.pnb.one",                            // PNB ONE
        "com.canarabank.mobility",                // Canara ai1
        "com.bankofbaroda.mconnect",              // BOB World
        "com.indusind.indusmobile",               // IndusMobile
        "com.fedmobile",                          // FedMobile
        "com.kreditbee.android",                  // KreditBee

        // Bangladeshi Mobile Financial & Banking Apps
        "com.bkash",
        "com.bKash",
        "com.konasl.nagad",
        "com.dbbl.mwallet",
        "com.upay.customer",
        "com.ibbl.cellfin",
        "com.citybank.citytouch",
        "com.thecitybank.citytouch",
        "com.bracbank.astha",
        "com.ebl.skybanking",
        "com.islamibankbd.ibbl",
        "com.mtb.mtbsmartbanking",
        "com.midlandbankbd.midlandonline",

        // Global Crypto & Wallets
        "com.google.android.apps.walletnfcrel",
        "com.paypal.android.p2pmobile",
        "com.binance.dev"
    )

    private val financialTransferKeywords = listOf(
        "টাকা পাঠাও", "টাকা পাঠাও", "টাকা ট্রান্সফার", "টাকা সেন্ড", "সেন্ড মানি", "send money",
        "পেমেন্ট করো", "পেমেন্ট কর", "make payment", "pay money", "transfer money",
        "ইউপিআই পিন", "upi pin", "পিন কোড", "pin code",
        "ওটিপি", "otp", "cvv", "সিভিভি", "পাসওয়ার্ড", "password",
        "কার্ড নম্বর", "card number", "atm pin", "এটিএম পিন"
    )

    fun isPackageBlocked(packageName: String): Boolean {
        val lowerPkg = packageName.lowercase(Locale.getDefault())
        return blockedPackagePrefixes.any { lowerPkg.startsWith(it.lowercase(Locale.getDefault())) }
    }

    /**
     * Checks if user is requesting automated money transfer or credential input.
     * Note: Pure manual app opening ("গুগল পে খোলো") is allowed via Intent for the human user,
     * but any financial transaction or screen automation is strictly blocked.
     */
    fun containsFinancialTransferIntent(text: String): Boolean {
        val lower = text.lowercase(Locale.getDefault())
        return financialTransferKeywords.any { lower.contains(it) }
    }

    fun containsSensitiveKeywords(text: String): Boolean {
        return containsFinancialTransferIntent(text)
    }
}
