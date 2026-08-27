package com.awaj.assistant.tools

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.safety.PermissionGate

class CallTool : Tool {
    override val name: String = "make_call"
    override val descriptionBangla: String = "নির্দিষ্ট কন্টাক্ট বা নম্বরে কল করে"

    @SuppressLint("MissingPermission")
    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val contactQuery = params["contact"]?.toString()?.trim() ?: ""

        if (contactQuery.isBlank()) {
            return ToolResult.Failed("কাকে কল করতে হবে তা উল্লেখ করুন।")
        }

        // Check if query is directly a phone number
        val isDigitsOnly = contactQuery.matches(Regex("^[+0-9\\s-]+$"))
        var phoneNumber: String? = if (isDigitsOnly) contactQuery.replace(" ", "") else null

        // If not a number, search in Contacts
        if (phoneNumber == null && PermissionGate.hasContactsPermission(context)) {
            phoneNumber = resolveContactNumber(context, contactQuery)
        }

        if (phoneNumber.isNullOrBlank()) {
            // If contact not found, open dialer with query
            try {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$contactQuery")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(dialIntent)
                return ToolResult.Success("$contactQuery এর জন্য ডায়ালার খোলা হয়েছে।")
            } catch (e: Exception) {
                return ToolResult.Failed("$contactQuery এর নম্বর খুঁজে পাওয়া যায়নি।")
            }
        }

        // Initiate call
        return try {
            if (PermissionGate.hasCallPermission(context)) {
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(callIntent)
                ToolResult.Success("$contactQuery-কে কল করা হচ্ছে ($phoneNumber)।")
            } else {
                val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(dialIntent)
                ToolResult.Success("কল পারমিশন না থাকায় $contactQuery এর ডায়ালার খোলা হয়েছে।")
            }
        } catch (e: Exception) {
            ToolResult.Failed("কল শুরু করতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }

    private fun resolveContactNumber(context: Context, name: String): String? {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$name%")

        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (numberIndex >= 0) {
                    return cursor.getString(numberIndex)
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
