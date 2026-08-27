package com.awaj.assistant.ui.settings

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awaj.assistant.nlu.AssistantMode
import com.awaj.assistant.overlay.FloatingMicService
import com.awaj.assistant.ui.MainViewModel
import com.awaj.assistant.ui.theme.AppThemeMode
import com.awaj.assistant.ui.theme.BrandDanger
import com.awaj.assistant.ui.theme.BrandPrimary
import com.awaj.assistant.ui.theme.BrandSecondary
import com.awaj.assistant.ui.theme.BrandSuccess
import com.awaj.assistant.ui.theme.BrandWarning
import com.awaj.assistant.ui.theme.TextMuted
import com.awaj.assistant.voice.VoiceService

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val currentMode by viewModel.currentMode.collectAsState()
    val currentThemeMode by viewModel.themeMode.collectAsState()
    val savedApiKey by viewModel.geminiApiKey.collectAsState()

    val isVoiceEnrolled by viewModel.isVoiceEnrolled.collectAsState()
    val voiceEnrollmentStep by viewModel.voiceEnrollmentStep.collectAsState()
    val isRecordingVoiceSample by viewModel.isRecordingVoiceSample.collectAsState()
    val voiceEnrollmentProgress by viewModel.voiceEnrollmentProgress.collectAsState()

    var apiKeyInput by remember(savedApiKey) { mutableStateOf(savedApiKey) }
    var isApiKeyVisible by remember { mutableStateOf(false) }
    var isFloatingMicEnabled by remember { mutableStateOf(false) }
    var isBackgroundServiceEnabled by remember { mutableStateOf(false) }
    var isLockScreenAwakeningEnabled by remember { mutableStateOf(true) }

    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onBackground
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "সেটিংস ও কনফিগারেশন",
            style = MaterialTheme.typography.headlineMedium,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "থিম, এআই প্রশ্নোত্তর, ভয়েস সিকিউরিটি ও সিস্টেম নিয়ন্ত্রণ",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 1: THEME SELECTION (DARK / LIGHT / SYSTEM)
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(BrandPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SettingsBrightness,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("অ্যাপের থিম (Theme Mode)", style = MaterialTheme.typography.titleMedium, color = textColor, fontWeight = FontWeight.SemiBold)
                        Text("ডার্ক, লাইট অথবা আপনার ফোনের সিস্টেম থিম বেছে নিন", fontSize = 11.sp, color = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOptionButton(
                        title = "ডার্ক",
                        icon = Icons.Filled.DarkMode,
                        isSelected = currentThemeMode == AppThemeMode.DARK,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setThemeMode(AppThemeMode.DARK) }
                    )
                    ThemeOptionButton(
                        title = "লাইট",
                        icon = Icons.Filled.LightMode,
                        isSelected = currentThemeMode == AppThemeMode.LIGHT,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setThemeMode(AppThemeMode.LIGHT) }
                    )
                    ThemeOptionButton(
                        title = "সিস্টেম",
                        icon = Icons.Filled.SettingsBrightness,
                        isSelected = currentThemeMode == AppThemeMode.SYSTEM,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.setThemeMode(AppThemeMode.SYSTEM) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 2: GEMINI AI KNOWLEDGE & Q/A INTELLIGENCE
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, if (savedApiKey.isNotBlank()) BrandSuccess.copy(alpha = 0.4f) else BrandPrimary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(BrandPrimary.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Gemini AI প্রশ্নোত্তর ও জ্ঞান", style = MaterialTheme.typography.titleMedium, color = textColor, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (savedApiKey.isNotBlank()) "এআই সক্রিয় ✓ (যেকোনো প্রশ্নের উত্তর দেবে)" else "এআই বুদ্ধিমত্তা যোগ করতে কী যুক্ত করুন",
                                fontSize = 11.sp,
                                color = if (savedApiKey.isNotBlank()) BrandSuccess else TextMuted
                            )
                        }
                    }

                    if (savedApiKey.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .background(BrandSuccess.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("সক্রিয়", color = BrandSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "সাধারণ জ্ঞান, বিজ্ঞান, সাহিত্য, ইতিহাস, পরামর্শ বা যেকোনো প্রশ্নের তাৎক্ষণিক উত্তর বাংলায় শুনতে আপনার Gemini API Key যুক্ত করুন।",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Gemini API Key") },
                    placeholder = { Text("AIzaSy...") },
                    visualTransformation = if (isApiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    leadingIcon = {
                        Icon(Icons.Filled.Key, contentDescription = null, tint = BrandPrimary)
                    },
                    trailingIcon = {
                        if (apiKeyInput.isNotBlank()) {
                            IconButton(onClick = { isApiKeyVisible = !isApiKeyVisible }) {
                                Icon(
                                    imageVector = if (isApiKeyVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Toggle Visibility",
                                    tint = TextMuted
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = borderColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val keyToSave = apiKeyInput.trim()
                            viewModel.setGeminiApiKey(keyToSave)
                            keyboardController?.hide()
                            Toast.makeText(
                                context,
                                if (keyToSave.isNotBlank()) "Gemini API Key সংরক্ষিত হয়েছে! এখন যেকোনো প্রশ্ন করুন।" else "API Key মুছে ফেলা হয়েছে!",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("API Key সংরক্ষণ করুন", color = Color.White, fontSize = 12.sp)
                    }

                    if (savedApiKey.isNotBlank() || apiKeyInput.isNotBlank()) {
                        OutlinedButton(
                            onClick = {
                                apiKeyInput = ""
                                viewModel.setGeminiApiKey("")
                                keyboardController?.hide()
                                Toast.makeText(context, "API Key মুছে ফেলা হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, BrandDanger.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandDanger)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "মুছে ফেলুন", tint = BrandDanger, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("মুছুন", color = BrandDanger, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 3: VOICE BIOMETRICS & PROFILE ENROLLMENT
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, if (isVoiceEnrolled) BrandSuccess.copy(alpha = 0.4f) else BrandPrimary.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isVoiceEnrolled) BrandSuccess.copy(alpha = 0.15f) else BrandPrimary.copy(alpha = 0.15f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Fingerprint,
                                contentDescription = null,
                                tint = if (isVoiceEnrolled) BrandSuccess else BrandPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("ভয়েস সিকিউরিটি ও প্রোফাইল", style = MaterialTheme.typography.titleMedium, color = textColor, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (isVoiceEnrolled) "কণ্ঠস্বর সংরক্ষিত ও লক-স্ক্রিনে সুরক্ষিত" else "আপনার কণ্ঠস্বর দিয়ে এনরোল করুন",
                                fontSize = 11.sp,
                                color = if (isVoiceEnrolled) BrandSuccess else TextMuted
                            )
                        }
                    }

                    if (isVoiceEnrolled) {
                        Box(
                            modifier = Modifier
                                .background(BrandSuccess.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("এনরোলড", color = BrandSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "লক থাকা অবস্থায় কেবল আপনার কণ্ঠ চিনতে পারলে তবেই ‘হেই আওয়াজ’ সাড়া দেবে। অন্য কারো কণ্ঠস্বরে কমান্ড গৃহীত হবে না।",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (!isVoiceEnrolled) {
                    val statusText = if (isRecordingVoiceSample) {
                        "🔴 রেকর্ড হচ্ছে... স্পষ্ট কণ্ঠে বলুন 'হেই আওয়াজ'"
                    } else {
                        "এনরোলমেন্ট ধাপ: $voiceEnrollmentStep / ৩ (স্পষ্টভাবে ‘হেই আওয়াজ’ বলুন)"
                    }

                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRecordingVoiceSample) BrandDanger else BrandPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { if (isRecordingVoiceSample) voiceEnrollmentProgress else (voiceEnrollmentStep / 3f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = if (isRecordingVoiceSample) BrandDanger else BrandPrimary,
                        trackColor = borderColor,
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (!isRecordingVoiceSample) {
                                viewModel.recordRealVoiceSample(
                                    onSuccess = { step ->
                                        if (step >= 3) {
                                            Toast.makeText(context, "ভয়েস প্রোফাইল সফলভাবে তৈরি হয়েছে!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "নমুনা $step/৩ সেভ হয়েছে! পরবর্তী নমুনা রেকর্ড করুন।", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    onError = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        },
                        enabled = !isRecordingVoiceSample,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecordingVoiceSample) BrandDanger else BrandPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                isRecordingVoiceSample -> "শুনছি... কথা বলুন..."
                                voiceEnrollmentStep == 0 -> "ভয়েস নমুনা রেকর্ড শুরু করুন"
                                else -> "পরবর্তী নমুনা রেকর্ড করুন ($voiceEnrollmentStep/৩)"
                            },
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                viewModel.resetVoiceProfile()
                                Toast.makeText(context, "ভয়েস প্রোফাইল রিসেট করা হয়েছে। পুনরায় এনরোল করুন।", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, BrandWarning.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandWarning)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, tint = BrandWarning, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("পুনরায় এনরোল করুন", fontSize = 11.sp, color = BrandWarning)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 4: LOCK SCREEN & BACKGROUND SERVICE
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Background Service Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.RecordVoiceOver, contentDescription = null, tint = BrandPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("ব্যাকগ্রাউন্ড ভয়েস সার্ভিস", style = MaterialTheme.typography.titleSmall, color = textColor)
                            Text("‘হেই আওয়াজ’ বললে ব্যাকগ্রাউন্ডে সক্রিয় হবে", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Switch(
                        checked = isBackgroundServiceEnabled,
                        onCheckedChange = { enabled ->
                            isBackgroundServiceEnabled = enabled
                            if (enabled) {
                                VoiceService.start(context)
                                Toast.makeText(context, "ওয়েক-ওয়ার্ড সার্ভিস চালু করা হয়েছে", Toast.LENGTH_SHORT).show()
                            } else {
                                VoiceService.stop(context)
                                Toast.makeText(context, "ওয়েক-ওয়ার্ড সার্ভিস বন্ধ করা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Lock Screen Awakening Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = BrandSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("লক স্ক্রিনে স্ক্রিন অন ও জাগরণ", style = MaterialTheme.typography.titleSmall, color = textColor)
                            Text("লক থাকলেও আপনার কণ্ঠে আলো জ্বলে উঠবে", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Switch(
                        checked = isLockScreenAwakeningEnabled,
                        onCheckedChange = { isLockScreenAwakeningEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandSecondary)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Floating Mic Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Layers, contentDescription = null, tint = BrandSuccess)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("ভাসমান মাইক বাবল", style = MaterialTheme.typography.titleSmall, color = textColor)
                            Text("অন্যান্য অ্যাপের ওপর ড্র্যাগেবল মাইক", fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    Switch(
                        checked = isFloatingMicEnabled,
                        onCheckedChange = { enabled ->
                            isFloatingMicEnabled = enabled
                            if (enabled) {
                                FloatingMicService.start(context)
                                Toast.makeText(context, "ভাসমান মাইক চালু করা হয়েছে", Toast.LENGTH_SHORT).show()
                            } else {
                                FloatingMicService.stop(context)
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = BrandSuccess)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 5: PAYMENT SAFEGUARD
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, BrandDanger.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = BrandDanger)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("পেমেন্ট ও ব্যাংকিং অ্যাপ সুরক্ষা গেট", style = MaterialTheme.typography.titleMedium, color = textColor, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Google Pay, PhonePe, Paytm, BHIM, YONO SBI, HDFC, ICICI, বিকাশ, নগদ এবং সব ব্যাংকিং অ্যাপে শুধুমাত্র সরাসরি অ্যাপ ওপেনিং কাজ করবে। কোনো স্বয়ংক্রিয় টাকা পাঠানো, পিন বা ওটিপি প্রেস সম্পূর্ণ ব্লকড।",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 6: ASSISTANT MODES
        Text(
            text = "সহকারী অপারেটিং মোড",
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        ModeOptionCard(
            title = "Safe Mode (নিরাপদ মোড)",
            description = "প্লে-স্টোর সেফ। সাধারণ Intent, সেটিংস, অ্যালার্ম ও নিশ্চিতকরণসহ কল/মেসেজ।",
            isSelected = currentMode == AssistantMode.SAFE_MODE,
            icon = Icons.Filled.Security,
            accentColor = BrandSuccess,
            onClick = { viewModel.setAssistantMode(AssistantMode.SAFE_MODE) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ModeOptionCard(
            title = "Accessibility Mode (সহায়ক মোড)",
            description = "দৃষ্টি/শারীরিক প্রতিবন্ধীদের জন্য স্ক্রিন পড়ে শোনানো ও ভয়েস নেভিগেশন।",
            isSelected = currentMode == AssistantMode.ACCESSIBILITY_MODE,
            icon = Icons.Filled.Accessibility,
            accentColor = BrandSecondary,
            onClick = { viewModel.setAssistantMode(AssistantMode.ACCESSIBILITY_MODE) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        ModeOptionCard(
            title = "Lab Agent Mode (ল্যাব মোড)",
            description = "অ্যাডভান্সড স্ক্রিন এজেন্ট ও ReAct লুপ। সতর্কতা: Android APM বা Restricted Settings থাকলে স্বয়ংক্রিয়ভাবে স্থগিত হবে।",
            isSelected = currentMode == AssistantMode.LAB_MODE,
            icon = Icons.Filled.Science,
            accentColor = BrandWarning,
            onClick = { viewModel.setAssistantMode(AssistantMode.LAB_MODE) }
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ThemeOptionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bg = if (isSelected) BrandPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
    val border = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    val contentColor = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        border = BorderStroke(1.5.dp, border),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = contentColor, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = contentColor)
        }
    }
}

@Composable
fun ModeOptionCard(
    title: String,
    description: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onBackground
    val border = if (isSelected) accentColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.5.dp, border),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .background(accentColor, RoundedCornerShape(50))
                        .padding(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
