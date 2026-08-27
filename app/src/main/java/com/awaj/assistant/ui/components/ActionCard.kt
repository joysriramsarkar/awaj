package com.awaj.assistant.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.awaj.assistant.nlu.RiskLevel
import com.awaj.assistant.ui.theme.BrandDanger
import com.awaj.assistant.ui.theme.BrandPrimary
import com.awaj.assistant.ui.theme.BrandSecondary
import com.awaj.assistant.ui.theme.BrandSuccess
import com.awaj.assistant.ui.theme.BrandWarning
import com.awaj.assistant.ui.theme.DarkSurfaceCard
import com.awaj.assistant.ui.theme.DarkSurfaceGlass
import com.awaj.assistant.ui.theme.TextMuted
import com.awaj.assistant.ui.theme.TextPrimary
import com.awaj.assistant.ui.theme.TextSecondary

@Composable
fun ActionCard(
    rawQuery: String,
    parsedAction: String,
    riskLevel: RiskLevel,
    resultSummary: String,
    isSuccess: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceGlass),
        border = BorderStroke(1.dp, Color(0xFF2D3748))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with Action badge & Risk Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Action tag
                Box(
                    modifier = Modifier
                        .background(BrandPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = parsedAction.replace("_", " ").uppercase(),
                        color = BrandPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Risk tag
                val (riskText, riskColor) = when (riskLevel) {
                    RiskLevel.LOW -> Pair("SAFE", BrandSuccess)
                    RiskLevel.MEDIUM -> Pair("MODERATE", BrandWarning)
                    RiskLevel.HIGH -> Pair("CONFIRMATION", BrandDanger)
                    RiskLevel.BLOCKED -> Pair("BLOCKED", BrandDanger)
                }

                Box(
                    modifier = Modifier
                        .background(riskColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (riskLevel == RiskLevel.LOW) Icons.Filled.Security else Icons.Filled.Warning,
                            contentDescription = null,
                            tint = riskColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = riskText,
                            color = riskColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User input query
            Text(
                text = "“$rawQuery”",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Result summary
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error,
                    contentDescription = null,
                    tint = if (isSuccess) BrandSuccess else BrandDanger,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = resultSummary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSuccess) TextSecondary else BrandDanger.copy(alpha = 0.9f)
                )
            }
        }
    }
}
