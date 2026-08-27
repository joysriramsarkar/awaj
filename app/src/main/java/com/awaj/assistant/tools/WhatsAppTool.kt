package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.safety.PermissionGate
import java.net.URLEncoder

class WhatsAppTool : Tool {
    override val name: String = "send_whatsapp"
    override val descriptionBangla: String = "হোয়াটসঅ্যাপে মেসেজ পাঠায়"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val contactQuery = params["contact"]?.toString()?.trim() ?: ""
        val message = params["message"]?.toString()?.trim() ?: ""

        if (message.isBlank()) {
            return ToolResult.Failed("কী বার্তা পাঠাতে চান তা উল্লেখ করুন।")
        }

        var phoneNumber: String? = null
        if (contactQuery.matches(Regex("^[+0-9\\s-]+$"))) {
            phoneNumber = contactQuery.replace(" ", "")
        } else if (PermissionGate.hasContactsPermission(context)) {
            phoneNumber = resolveContactNumber(context, contactQuery)
        }

        return try {
            val encodedMsg = URLEncoder.encode(message, "UTF-8")
            val intent = if (!phoneNumber.isNullOrBlank()) {
                val cleanNumber = phoneNumber.replace("+", "").replace("-", "").replace(" ", "")
                Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=$encodedMsg")
                    `package` = "com.whatsapp"
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    `package` = "com.whatsapp"
                    putExtra(Intent.EXTRA_TEXT, message)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }

            context.startActivity(intent)
            val recipient = if (contactQuery.isNotBlank()) contactQuery else "হোয়াটসঅ্যাপ"
            ToolResult.Success("$recipient-কে মেসেজ পাঠানোর জন্য হোয়াটসঅ্যাপ খোলা হয়েছে।")
        } catch (e: Exception) {
            // Fallback generic share intent
            try {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(sendIntent, "মেসেজ পাঠান").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                ToolResult.Success("শেয়ার অপশন খোলা হয়েছে।")
            } catch (ex: Exception) {
                ToolResult.Failed("হোয়াটসঅ্যাপ খুলতে সমস্যা হয়েছে: ${ex.localizedMessage}")
            }
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
