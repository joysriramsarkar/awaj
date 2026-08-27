package com.awaj.assistant.ui.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awaj.assistant.nlu.AssistantMode
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.stt.SpeechState
import com.awaj.assistant.ui.MainViewModel
import com.awaj.assistant.ui.components.ActionCard
import com.awaj.assistant.ui.components.ConfirmationDialog
import com.awaj.assistant.ui.components.GlowingMicOrb
import com.awaj.assistant.ui.components.SuggestionChips
import com.awaj.assistant.ui.theme.BrandDanger
import com.awaj.assistant.ui.theme.BrandPrimary
import com.awaj.assistant.ui.theme.BrandSecondary
import com.awaj.assistant.ui.theme.BrandSuccess
import com.awaj.assistant.ui.theme.BrandWarning
import com.awaj.assistant.ui.theme.TextMuted

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val speechState by viewModel.speechState.collectAsState()
    val lastResult by viewModel.lastResult.collectAsState()
    val lastQuery by viewModel.lastQuery.collectAsState()
    val lastAction by viewModel.lastAction.collectAsState()
    val lastRisk by viewModel.lastRisk.collectAsState()
    val pendingConfirmation by viewModel.pendingConfirmation.collectAsState()
    val currentMode by viewModel.currentMode.collectAsState()
    val agentReasoning by viewModel.agentLiveReasoning.collectAsState()
    val isAgentRunning by viewModel.isAgentRunning.collectAsState()

    val textColor = MaterialTheme.colorScheme.onBackground

    // Two-way Confirmation Modal
    if (pendingConfirmation != null) {
        ConfirmationDialog(
            request = pendingConfirmation!!,
            onConfirm = { viewModel.confirmPendingAction() },
            onDismiss = { viewModel.cancelPendingAction() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "আওয়াজ (Awaj)",
                    style = MaterialTheme.typography.headlineMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "বাংলা এআই সহকারী ও অটোমেশন",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            // Mode Badge
            val (modeTitle, modeColor) = when (currentMode) {
                AssistantMode.SAFE_MODE -> Pair("Safe Mode", BrandSuccess)
                AssistantMode.ACCESSIBILITY_MODE -> Pair("Access Mode", BrandSecondary)
                AssistantMode.LAB_MODE -> Pair("Lab Mode", BrandWarning)
            }

            Box(
                modifier = Modifier
                    .background(modeColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (currentMode == AssistantMode.SAFE_MODE) Icons.Filled.Security else Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = modeColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = modeTitle,
                        color = modeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Live Agent Reasoning Banner (Lab Mode)
        if (isAgentRunning && agentReasoning.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, BrandWarning.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Psychology,
                                contentDescription = null,
                                tint = BrandWarning,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ল্যাব এজেন্ট লাইভ থট ও প্রসেসিং",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandWarning
                            )
                        }

                        Button(
                            onClick = { viewModel.emergencyStopAgent() },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandDanger),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Filled.Stop, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("থামুন", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = agentReasoning,
                        fontSize = 12.sp,
                        color = textColor,
                        lineHeight = 17.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Status text
        val statusText = when (speechState) {
            is SpeechState.Listening -> "শুনছি... কথা বলুন"
            is SpeechState.Processing -> "কমান্ড বা প্রশ্ন প্রক্রিয়াধীন..."
            is SpeechState.Speaking -> "উত্তর দেওয়া হচ্ছে..."
            is SpeechState.Recognized -> (speechState as SpeechState.Recognized).text
            is SpeechState.Error -> (speechState as SpeechState.Error).messageBangla
            is SpeechState.Idle -> "মাইকে ট্যাপ করে যেকোনো প্রশ্ন বা আদেশ করুন"
        }

        val statusColor = when (speechState) {
            is SpeechState.Listening -> BrandSecondary
            is SpeechState.Processing -> BrandPrimary
            is SpeechState.Speaking -> BrandSuccess
            is SpeechState.Error -> BrandWarning
            else -> TextMuted
        }

        Text(
            text = statusText,
            style = MaterialTheme.typography.titleMedium,
            color = statusColor,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Animated Glowing Mic Orb
        GlowingMicOrb(
            state = speechState,
            onClick = { viewModel.toggleListening() }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Last action or result card
        if (lastQuery.isNotBlank()) {
            val resultSummary = when (val res = lastResult) {
                is ToolResult.Success -> res.messageBangla
                is ToolResult.Failed -> res.reasonBangla
                is ToolResult.Blocked -> res.reasonBangla
                is ToolResult.NeedsConfirmation -> res.summaryBangla
                is ToolResult.ClarificationNeeded -> res.questionBangla
                else -> "প্রক্রিয়াধীন রয়েছে..."
            }

            val isSuccess = lastResult is ToolResult.Success || lastResult is ToolResult.NeedsConfirmation

            ActionCard(
                rawQuery = lastQuery,
                parsedAction = lastAction.ifBlank { "AI ASSISTANT" },
                riskLevel = lastRisk,
                resultSummary = resultSummary,
                isSuccess = isSuccess,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Suggestion Chips
        SuggestionChips(
            onSelectSuggestion = { command ->
                viewModel.processVoiceCommand(command)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(36.dp))
    }
}
