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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.awaj.assistant.safety.PermissionGate
import com.awaj.assistant.ui.theme.BrandDanger
import com.awaj.assistant.ui.theme.BrandPrimary
import com.awaj.assistant.ui.theme.BrandSecondary
import com.awaj.assistant.ui.theme.BrandSuccess
import com.awaj.assistant.ui.theme.BrandWarning
import com.awaj.assistant.ui.theme.DarkBackground
import com.awaj.assistant.ui.theme.DarkSurfaceCard
import com.awaj.assistant.ui.theme.TextMuted
import com.awaj.assistant.ui.theme.TextPrimary
import com.awaj.assistant.ui.theme.TextSecondary

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
            .background(DarkBackground)
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
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "স্বচ্ছতা, নিরাপত্তা ও নিয়ন্ত্রণের জন্য প্রতিটি পারমিশনের বিবরণ",
                    fontSize = 12.sp,
                    color = TextSecondary
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
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = BorderStroke(1.dp, BrandWarning.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = null,
                    tint = BrandWarning,
                    modifier = Modifier.size(20.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Android APM ও Restricted Settings সতর্কতা",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandWarning
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "আপনার ডিভাইসে Advanced Protection Mode (APM) চালু থাকলে বা সাইডলোড অ্যাপের 'Restricted Settings' কার্যকর থাকলে সিস্টেম অ্যাক্সেসিবিলিটি পারমিশন স্বয়ংক্রিয়ভাবে ব্লক করতে পারে। সেক্ষেত্রে Safe Mode নির্ভরযোগ্যভাবে কাজ করবে।",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(permissionsList.size) { index ->
                val item = permissionsList[index]
                // evaluate based on current refreshTrigger
                val isGranted = remember(refreshTrigger, index) {
                    if (item.permissionKey != null) {
                        ContextCompat.checkSelfPermission(context, item.permissionKey) == PackageManager.PERMISSION_GRANTED
                    } else if (item.settingsAction == Settings.ACTION_MANAGE_OVERLAY_PERMISSION) {
                        Settings.canDrawOverlays(context)
                    } else if (item.settingsAction == Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS) {
                        PermissionGate.isNotificationListenerGranted(context)
                    } else if (item.settingsAction == Settings.ACTION_ACCESSIBILITY_SETTINGS) {
                        PermissionGate.isAccessibilityServiceEnabled(context)
                    } else {
                        false
                    }
                }

                PermissionRowCard(
                    item = item,
                    isGranted = isGranted,
                    onRequest = {
                        if (item.permissionKey != null) {
                            permissionLauncher.launch(arrayOf(item.permissionKey))
                        } else if (item.settingsAction != null) {
                            try {
                                val intent = Intent(item.settingsAction).apply {
                                    if (item.settingsAction == Settings.ACTION_MANAGE_OVERLAY_PERMISSION) {
                                        data = Uri.parse("package:${context.packageName}")
                                    }
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            }
                        }
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun PermissionRowCard(
    item: PermissionItemData,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, if (isGranted) BrandSuccess.copy(alpha = 0.3f) else Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (isGranted) BrandSuccess.copy(alpha = 0.15f) else BrandPrimary.copy(alpha = 0.15f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(10.dp)
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = if (isGranted) BrandSuccess else BrandPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.titleBangla,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = item.description,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                if (isGranted) {
                    Box(
                        modifier = Modifier
                            .background(BrandSuccess.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = BrandSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "অনুমোদিত", color = BrandSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = onRequest,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text(text = "অনুমতি দিন", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            // Prominent Disclosure section if present
            if (item.prominentDisclosure != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = BrandSecondary,
                            modifier = Modifier.size(14.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.prominentDisclosure,
                            fontSize = 10.sp,
                            color = TextMuted,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
