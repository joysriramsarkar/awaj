package com.awaj.assistant.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.awaj.assistant.stt.SpeechState
import com.awaj.assistant.ui.theme.BrandDanger
import com.awaj.assistant.ui.theme.BrandPrimary
import com.awaj.assistant.ui.theme.BrandSecondary
import com.awaj.assistant.ui.theme.GlowPurple
import com.awaj.assistant.ui.theme.GlowTeal
import com.awaj.assistant.ui.theme.TextPrimary

@Composable
fun GlowingMicOrb(
    state: SpeechState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_glow")
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val isListening = state is SpeechState.Listening
    val isProcessing = state is SpeechState.Processing
    val isSpeaking = state is SpeechState.Speaking

    val accessibilityDesc = when {
        isListening -> "কথা বলুন, সহকারী এখন শুনছে। থামাতে ট্যাপ করুন।"
        isProcessing -> "আপনার নির্দেশ প্রসেস করা হচ্ছে। অনুগ্রহ করে অপেক্ষা করুন।"
        isSpeaking -> "সহকারী উত্তর দিচ্ছে। থামানোর জন্য ট্যাপ করুন।"
        else -> "ভয়েস সহকারী চালু করতে ট্যাপ করুন।"
    }

    val outerGlowColor = when {
        isListening -> GlowTeal.copy(alpha = 0.35f)
        isProcessing -> GlowPurple.copy(alpha = 0.35f)
        isSpeaking -> BrandSecondary.copy(alpha = 0.35f)
        else -> BrandPrimary.copy(alpha = 0.15f)
    }

    val gradientBrush = when {
        isListening -> Brush.radialGradient(
            colors = listOf(BrandSecondary, GlowTeal, BrandPrimary)
        )
        isProcessing -> Brush.radialGradient(
            colors = listOf(GlowPurple, BrandPrimary, Color(0xFF312E81))
        )
        isSpeaking -> Brush.radialGradient(
            colors = listOf(BrandSecondary, BrandPrimary)
        )
        else -> Brush.radialGradient(
            colors = listOf(BrandPrimary, Color(0xFF4338CA), Color(0xFF1E1B4B))
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(160.dp)
            .semantics(mergeDescendants = true) {
                role = Role.Button
                contentDescription = accessibilityDesc
            }
    ) {
        // Outer pulsing ring
        if (isListening || isProcessing || isSpeaking) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(outerGlowColor)
            )
        }

        // Middle ring
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    if (isListening) GlowTeal.copy(alpha = 0.4f) else BrandPrimary.copy(alpha = 0.25f)
                )
        )

        // Center button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(gradientBrush)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        ) {
            Icon(
                imageVector = when {
                    isListening -> Icons.Filled.Mic
                    isProcessing -> Icons.Filled.MicNone
                    isSpeaking -> Icons.Filled.Stop
                    else -> Icons.Filled.Mic
                },
                contentDescription = null, // Handled by parent container semantics
                tint = TextPrimary,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
