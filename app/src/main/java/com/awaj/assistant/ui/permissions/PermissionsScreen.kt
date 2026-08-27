package com.awaj.assistant.ui.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.awaj.assistant.safety.PermissionGate
import com.awaj.assistant.ui.theme.BrandPrimary
import com.awaj.assistant.ui.theme.BrandSecondary
import com.awaj.assistant.ui.theme.BrandSuccess
import com.awaj.assistant.ui.theme.BrandWarning
import com.awaj.assistant.ui.theme.TextMuted

data class PermissionItemData(
    val titleBangla: String,
    val description: String,
    val permissionKey: String?,
    val icon: ImageVector,
    val isSpecialSettings: Boolean = false,
    val settingsAction: String? = null,
    val prominentDisclosure: String? = null
)

@Composable
fun PermissionsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshTrigger by remember { mutableIntStateOf(0) }

    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val border = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    // Re-check permissions whenever user resumes from Settings screen
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        refreshTrigger++
    }

    val permissionsList = listOf(
        PermissionItemData(
            titleBangla = "মাইক্রোফোন (বাধ্যতামূলক)",
            description = "আপনার বাংলা ভয়েস কমান্ড শোনার জন্য প্রয়োজন।",
            permissionKey = Manifest.permission.RECORD_AUDIO,
            icon = Icons.Filled.Mic
        ),
        PermissionItemData(
            titleBangla = "কন্টাক্ট তালিকা",
            description = "নাম দিয়ে কন্টাক্ট নম্বর খুঁজে পেতে প্রয়োজন।",
            permissionKey = Manifest.permission.READ_CONTACTS,
            icon = Icons.Filled.Contacts
        ),
        PermissionItemData(
            titleBangla = "ফোন কল",
            description = "ভয়েস কমান্ডের মাধ্যমে সরাসরি কল দেওয়ার জন্য।",
            permissionKey = Manifest.permission.CALL_PHONE,
            icon = Icons.Filled.Call
        ),
        PermissionItemData(
            titleBangla = "SMS বার্তা",
            description = "নিশ্চিতকরণের পর সরাসরি এসএমএস পাঠানোর জন্য।",
            permissionKey = Manifest.permission.SEND_SMS,
            icon = Icons.Filled.Sms
        ),
        PermissionItemData(
            titleBangla = "ব্যাটারি অপ্টিমাইজেশন ছাড় (Keep-Alive)",
            description = "লক-স্ক্রিনে ব্যাকগ্রাউন্ডে সবসময় 'হেই আওয়াজ' শোনার জন্য স্যামসাং/শাওমি/অপ্পো ডিভাইসে ব্যাটারি সেভার ছাড় দিন।",
            permissionKey = null,
            isSpecialSettings = true,
            settingsAction = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            icon = Icons.Filled.BatterySaver
        ),
        PermissionItemData(
            titleBangla = "নোটিফিকেশন এক্সেস (Notification Access)",
            description = "আপনার অনুরোধে সাম্প্রতিক নোটিফিকেশন পড়ে শোনানোর জন্য এই বিশেষ সিস্টেম পারমিশন প্রয়োজন।",
            permissionKey = null,
            isSpecialSettings = true,
            settingsAction = Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS,
            icon = Icons.Filled.Notifications,
            prominentDisclosure = "বিশেষ প্রকাশ (Prominent Disclosure): নোটিফিকেশন তথ্য সম্পূর্ণ লোকাল মেমোরিতে সাময়িকভাবে সংরক্ষিত হয় এবং শুধুমাত্র আপনার ভয়েস আদেশে পড়ে শোনানো হয়। কোনো নোটিফিকেশন ক্লাউডে পাঠানো হয় না।"
        ),
        PermissionItemData(
            titleBangla = "অ্যাক্সেসিবিলিটি সার্ভিস (Accessibility)",
            description = "সহায়ক মোড ও ল্যাব মোডে স্ক্রিনের বাটন ও উপাদান পড়ে সহকারী কমান্ড চালাতে ব্যবহৃত হয়।",
            permissionKey = null,
            isSpecialSettings = true,
            settingsAction = Settings.ACTION_ACCESSIBILITY_SETTINGS,
            icon = Icons.Filled.Accessibility,
            prominentDisclosure = "বিশেষ প্রকাশ (Prominent Disclosure): অ্যাক্সেসিবিলিটি দিয়ে কোনো ব্যাংক, পেমেন্ট, পাসওয়ার্ড বা ওটিপি স্ক্রিনে কাজ করা হয় না। শুধুমাত্র অনুমোদিত সাধারণ অ্যাপ কমান্ড সম্পাদন করা হয়।"
        ),
        PermissionItemData(
            titleBangla = "ভাসমান মাইক ওভারলে",
            description = "অন্য যেকোনো অ্যাপের ওপর ভাসমান মাইক বাটন দেখানোর জন্য।",
            permissionKey = null,
            isSpecialSettings = true,
            settingsAction = Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            icon = Icons.Filled.Layers
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "পারমিশন সেন্টার ও প্রাইভেসি",
                    style = MaterialTheme.typography.headlineMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "স্বচ্ছতা, নিরাপত্তা ও নিয়ন্ত্রণের জন্য প্রতিটি পারমিশনের বিবরণ",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            IconButton(
                onClick = { refreshTrigger++ },
                modifier = Modifier
                    .background(BrandPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "রিফ্রেশ করুন",
                    tint = BrandPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // APM & Restricted Settings Advisory Banner
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, BrandWarning.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = BrandWarning,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Android Restricted Settings সংক্রান্ত তথ্য",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Android 13+ ভার্সনে সাইডলোডকৃত অ্যাপে অ্যাক্সেসিবিলিটি বন্ধ থাকলে: সেটিংস -> অ্যাপস -> Awaj -> উপরে ৩-ডট মেন্যু -> 'Allow restricted settings' অন করুন।",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(permissionsList.size) { index ->
                val item = permissionsList[index]
                val isGranted = checkPermissionStatus(context, item, refreshTrigger)

                PermissionCard(
                    item = item,
                    isGranted = isGranted,
                    onRequest = {
                        if (item.isSpecialSettings && item.settingsAction != null) {
                            if (item.settingsAction == Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
                                PermissionGate.requestIgnoreBatteryOptimization(context)
                            } else {
                                val intent = Intent(item.settingsAction)
                                if (item.settingsAction == Settings.ACTION_MANAGE_OVERLAY_PERMISSION) {
                                    intent.data = Uri.parse("package:${context.packageName}")
                                }
                                context.startActivity(intent)
                            }
                        } else if (item.permissionKey != null) {
                            permissionLauncher.launch(arrayOf(item.permissionKey))
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PermissionCard(
    item: PermissionItemData,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface
    val border = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(
            1.dp,
            if (isGranted) BrandSuccess.copy(alpha = 0.3f) else border
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (isGranted) BrandSuccess.copy(alpha = 0.15f) else BrandPrimary.copy(alpha = 0.15f),
                                RoundedCornerShape(10.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = if (isGranted) BrandSuccess else BrandPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = item.titleBangla,
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isGranted) "অনুমতি দেওয়া হয়েছে ✓" else "অনুমতি দেওয়া হয়নি",
                            fontSize = 11.sp,
                            color = if (isGranted) BrandSuccess else TextMuted
                        )
                    }
                }

                if (!isGranted) {
                    Button(
                        onClick = onRequest,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("অনুমতি দিন", fontSize = 11.sp, color = Color.White)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .background(BrandSuccess.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = BrandSuccess,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("সক্রিয়", color = BrandSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                fontSize = 12.sp,
                color = TextMuted
            )

            // Prominent Disclosure
            if (item.prominentDisclosure != null && !isGranted) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .background(BrandSecondary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = item.prominentDisclosure,
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.85f),
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

private fun checkPermissionStatus(context: Context, item: PermissionItemData, trigger: Int): Boolean {
    if (item.isSpecialSettings) {
        return when (item.settingsAction) {
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS -> PermissionGate.isIgnoringBatteryOptimizations(context)
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION -> Settings.canDrawOverlays(context)
            Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS -> PermissionGate.isNotificationListenerGranted(context)
            Settings.ACTION_ACCESSIBILITY_SETTINGS -> PermissionGate.isAccessibilityServiceEnabled(context)
            else -> false
        }
    }
    return if (item.permissionKey != null) {
        ContextCompat.checkSelfPermission(context, item.permissionKey) == PackageManager.PERMISSION_GRANTED
    } else {
        false
    }
}
