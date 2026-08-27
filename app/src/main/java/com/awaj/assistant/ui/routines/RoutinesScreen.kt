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
import com.awaj.assistant.ui.theme.DarkBackground
import com.awaj.assistant.ui.theme.DarkSurfaceCard
import com.awaj.assistant.ui.theme.TextMuted
import com.awaj.assistant.ui.theme.TextPrimary
import com.awaj.assistant.ui.theme.TextSecondary

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
            description = "মিডিয়া পজ করবে, ব্রাইটনেস কমাবে এবং অ্যালার্ম সেট করবে",
            actions = emptyList()
        ),
        Routine(
            id = "focus_routine",
            nameBangla = "পড়ার / মনোযোগ মোড",
            triggerPhraseBangla = "পড়ার মোড",
            description = "ভলিউম মিউট করবে এবং গান বন্ধ করবে",
            actions = emptyList()
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ভয়েস রুটিনসমূহ",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "এক কমান্ডে একাধিক কাজের সমন্বিত অটোমেশন",
            fontSize = 12.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(predefinedRoutines) { routine ->
                RoutineCard(
                    routine = routine,
                    onRun = { viewModel.runRoutine(routine.id) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun RoutineCard(
    routine: Routine,
    onRun: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = BorderStroke(1.dp, Color(0xFF334155)),
        modifier = Modifier.fillMaxWidth()
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
                            .background(BrandPrimary.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
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
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ট্রিগার: \"${routine.triggerPhraseBangla}\"",
                            fontSize = 12.sp,
                            color = BrandSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Button(
                    onClick = onRun,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "চালান",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "চালান", color = Color.White, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = routine.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
