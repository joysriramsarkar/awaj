package com.awaj.assistant.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awaj.assistant.ui.theme.BrandSecondary

data class SuggestionItem(
    val title: String,
    val command: String,
    val icon: ImageVector
)

@Composable
fun SuggestionChips(
    onSelectSuggestion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestions = listOf(
        SuggestionItem("এআই প্রশ্ন করুন", "চাঁদ পৃথিবী থেকে কত দূরে?", Icons.Filled.AutoAwesome),
        SuggestionItem("গান চালাও", "ইউটিউবে রবীন্দ্রসঙ্গীত লাগাও", Icons.Filled.MusicNote),
        SuggestionItem("অঙ্ক করো", "১০ গুণ ২০ কত", Icons.Filled.Calculate),
        SuggestionItem("হটস্পট অন করো", "হটস্পট চালু করো", Icons.Filled.Wifi),
        SuggestionItem("হোয়াটসঅ্যাপ খোলো", "হোয়াটসঅ্যাপ খোলো", Icons.Filled.Chat),
        SuggestionItem("টর্চ জ্বালাও", "টর্চ জ্বালাও", Icons.Filled.FlashlightOn),
        SuggestionItem("মাকে কল দাও", "মাকে কল করো", Icons.Filled.Call),
        SuggestionItem("অ্যালার্ম দাও", "কাল সকাল আটটায় অ্যালার্ম দাও", Icons.Filled.Alarm),
        SuggestionItem("আবহাওয়া কেমন?", "আজকের আবহাওয়া কেমন", Icons.Filled.WbSunny)
    )

    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface
    val border = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (item in suggestions) {
            Card(
                onClick = { onSelectSuggestion(item.command) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, border)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = BrandSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.title,
                        color = textColor,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
