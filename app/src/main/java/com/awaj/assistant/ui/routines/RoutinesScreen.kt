package com.awaj.assistant.ui.routines

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.awaj.assistant.routines.Routine
import com.awaj.assistant.ui.MainViewModel
import com.awaj.assistant.ui.theme.BrandPrimary
import com.awaj.assistant.ui.theme.BrandSecondary
import com.awaj.assistant.ui.theme.TextMuted

@Composable
fun RoutinesScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val predefinedRoutines = listOf(
        Routine(
            id = "morning_routine",
            nameBangla = "সুপ্রভাত রুটিন",
            triggerPhraseBangla = "সুপ্রভাত / শুভ সকাল",
            description = "আবহাওয়া এবং ব্যাটারি স্ট্যাটাস জানাবে",
            actions = emptyList()
        ),
        Routine(
            id = "night_routine",
            nameBangla = "শুভ রাত্রি রুটিন",
            triggerPhraseBangla = "শুভ রাত্রি / ঘুমের মোড",
            description = "টর্চ বন্ধ করবে এবং সকাল ৭টায় অ্যালার্ম সেট করবে",
            actions = emptyList()
        )
    )

    val textColor = MaterialTheme.colorScheme.onBackground
    val cardBg = MaterialTheme.colorScheme.surfaceVariant
    val border = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "দৈনন্দিন রুটিন",
            style = MaterialTheme.typography.headlineMedium,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "একটি মাত্র ভয়েস কমান্ডে একাধিক কাজ একসাথে সম্পন্ন করুন",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(predefinedRoutines, key = { it.id }) { routine ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, border)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(BrandPrimary.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Schedule,
                                        contentDescription = null,
                                        tint = BrandPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = routine.nameBangla,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = textColor,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "ট্রিগার: “${routine.triggerPhraseBangla}”",
                                        fontSize = 12.sp,
                                        color = BrandSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = routine.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.runRoutine(routine.id) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "চালাও",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("এখনই চালান", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
