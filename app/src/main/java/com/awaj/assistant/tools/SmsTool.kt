package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.telephony.SmsManager
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.safety.PermissionGate

class SmsTool : Tool {
    override val name: String = "send_sms"
    override val descriptionBangla: String = "নির্দিষ্ট কন্টাক্ট বা নম্বরে SMS পাঠায়"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val contactQuery = params["contact"]?.toString()?.trim() ?: ""
        val message = params["message"]?.toString()?.trim() ?: ""

        if (contactQuery.isBlank()) {
            return ToolResult.Failed("কাকে মেসেজ পাঠাতে হবে তা পাওয়া যায়নি।")
        }
        if (message.isBlank()) {
            return ToolResult.Failed("কী বার্তা পাঠাতে চান তা উল্লেখ করুন।")
        }

        val isDigitsOnly = contactQuery.matches(Regex("^[+0-9\\s-]+$"))
        var phoneNumber: String? = if (isDigitsOnly) contactQuery.replace(" ", "") else null

        if (phoneNumber == null && PermissionGate.hasContactsPermission(context)) {
            phoneNumber = resolveContactNumber(context, contactQuery)
        }

        val targetNumber = phoneNumber ?: contactQuery

        return try {
            if (PermissionGate.hasSmsPermission(context) && phoneNumber != null) {
                val smsManager: SmsManager = context.getSystemService(SmsManager::class.java)
                smsManager.sendTextMessage(targetNumber, null, message, null, null)
                ToolResult.Success("$contactQuery-কে সফলভাবে SMS পাঠানো হয়েছে।")
            } else {
                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$targetNumber")
                    putExtra("sms_body", message)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(smsIntent)
                ToolResult.Success("$contactQuery এর মেসেজ কম্পোজার খোলা হয়েছে।")
            }
        } catch (e: Exception) {
            ToolResult.Failed("SMS পাঠাতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }

    private fun resolveContactNumber(context: Context, name: String): String? {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$name%")

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (index >= 0) {
                    return cursor.getString(index)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }
}
