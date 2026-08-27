package com.awaj.assistant.ui.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
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
import com.awaj.assistant.appfunctions.AwajAppFunctions
import com.awaj.assistant.nlu.AssistantMode
import com.awaj.assistant.overlay.FloatingMicService
import com.awaj.assistant.ui.MainViewModel
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
import com.awaj.assistant.voice.VoiceService

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val currentMode by viewModel.currentMode.collectAsState()
    val savedApiKey by viewModel.geminiApiKey.collectAsState()

    val isVoiceEnrolled by viewModel.isVoiceEnrolled.collectAsState()
    val voiceEnrollmentStep by viewModel.voiceEnrollmentStep.collectAsState()

    var apiKeyInput by remember(savedApiKey) { mutableStateOf(savedApiKey) }
    var isApiKeyVisible by remember { mutableStateOf(false) }
    var isFloatingMicEnabled by remember { mutableStateOf(false) }
    var isBackgroundServiceEnabled by remember { mutableStateOf(false) }
    var isAppFunctionsExposed by remember { mutableStateOf(true) }
    var isLockScreenAwakeningEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "সেটিংস ও নিরাপত্তা কেন্দ্র",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "ভয়েস সিকিউরিটি, লক-স্ক্রিন অ্যাক্টিভেশন, পেমেন্ট গার্ড ও সিস্টেম কন্ট্রোল",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 1: VOICE BIOMETRICS & PROFILE ENROLLMENT
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
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
                            Text("ভয়েস সিকিউরিটি ও প্রোফাইল", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
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
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (!isVoiceEnrolled) {
                    Text(
                        text = "এনরোলমেন্ট ধাপ: $voiceEnrollmentStep / ৩ (স্পষ্টভাবে ‘হেই আওয়াজ’ বলুন)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { voiceEnrollmentStep / 3f },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = BrandPrimary,
                        trackColor = Color(0xFF334155),
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val nextStep = viewModel.enrollVoiceSample()
                            if (nextStep >= 3) {
                                Toast.makeText(context, "ভয়েস প্রোফাইল সফলভাবে তৈরি হয়েছে!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "নমুনা $nextStep/৩ গৃহীত হয়েছে। আবার বলুন।", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Icon(Icons.Filled.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (voiceEnrollmentStep == 0) "ভয়েস নমুনা রেকর্ড শুরু করুন" else "পরবর্তী নমুনা রেকর্ড করুন ($voiceEnrollmentStep/৩)",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = {
                                viewModel.resetVoiceProfile()
                                Toast.makeText(context, "ভয়েস প্রোফাইল রিসেট করা হয়েছে। পুনরায় এনরোল করুন।", Toast.LENGTH_SHORT).show()
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

        // SECTION 2: LOCK SCREEN & BACKGROUND SERVICE
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = BorderStroke(1.dp, Color(0xFF334155)),
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
                            Text("ব্যাকগ্রাউন্ড ভয়েস সার্ভিস", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
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
                            Text("লক স্ক্রিনে স্ক্রিন অন ও জাগরণ", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
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
                            Text("ভাসমান মাইক বাবল", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
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

        // SECTION 3: PAYMENT & FINANCIAL SECURITY SAFEGUARD
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = BorderStroke(1.dp, BrandDanger.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccountBalance, contentDescription = null, tint = BrandDanger)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("পেমেন্ট ও ব্যাংকিং অ্যাপ সুরক্ষা গেট", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Google Pay (GPay), PhonePe, Paytm, BHIM, YONO SBI, HDFC, ICICI, বিকাশ, নগদ এবং সকল ব্যাংকিং অ্যাপের ক্ষেত্রে শুধুমাত্র সরাসরি অ্যাপ ওপেনিং কাজ করবে। কোনো প্রকার স্বয়ংক্রিয় টাকা পাঠানো, পিন বা ওটিপি প্রেস সম্পূর্ণ ব্লকড।",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION 4: ASSISTANT OPERATING MODES
        Text(
            text = "সহকারী অপারেটিং মোড",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
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

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 5: GEMINI LLM
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gemini LLM বুদ্ধিমত্তা (ঐচ্ছিক)",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            if (savedApiKey.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .background(BrandSuccess.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = BrandSuccess, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সংরক্ষিত", color = BrandSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "জটিল মিশ্র বাংলা কমান্ড নিখুঁতভাবে বুঝতে আপনার Gemini API Key যুক্ত করুন।",
            fontSize = 12.sp,
            color = TextSecondary
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (apiKeyInput.isNotBlank()) {
                        IconButton(onClick = { isApiKeyVisible = !isApiKeyVisible }) {
                            Icon(
                                imageVector = if (isApiKeyVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle Visibility",
                                tint = TextSecondary
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
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
                        if (keyToSave.isNotBlank()) "Gemini API Key সফলভাবে সংরক্ষিত হয়েছে!" else "API Key মুছে ফেলা হয়েছে!",
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

        Spacer(modifier = Modifier.height(32.dp))
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
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(
            1.5.dp,
            if (isSelected) accentColor else Color(0xFF334155)
        ),
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
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = TextSecondary,
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
                        tint = DarkBackground,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
