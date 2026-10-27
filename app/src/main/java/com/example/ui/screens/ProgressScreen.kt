package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CampusWellViewModel
import com.example.ui.components.WeeklyTrendsLineChart
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.AccentStudy
import com.example.ui.theme.AccentWater
import com.example.ui.theme.PrimaryContainerBlue
import com.example.ui.theme.TertiaryContainerGreen

@Composable
fun ProgressScreen(
    viewModel: CampusWellViewModel,
    modifier: Modifier = Modifier
) {
    val allCheckIns by viewModel.allCheckIns.collectAsState()
    val waterGlasses by viewModel.waterGlasses.collectAsState()
    val sleepHours by viewModel.sleepHours.collectAsState()
    val studyHours by viewModel.studyHours.collectAsState()
    val selectedMood by viewModel.selectedMood.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Weekly Summary",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Your habits and restorative pacing at a glance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = PrimaryContainerBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "21 Apr – 27 Apr 2025",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 4 Summary Metrics Chips Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryStatCard(
                title = "Average Sleep",
                value = "7h 12m",
                delta = "↑ 12%",
                icon = Icons.Default.Bedtime,
                iconColor = AccentSleep,
                modifier = Modifier.weight(1f)
            )
            SummaryStatCard(
                title = "Water Intake",
                value = "1.6 L/day",
                delta = "↑ 8%",
                icon = Icons.Default.WaterDrop,
                iconColor = AccentWater,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SummaryStatCard(
                title = "Study Time",
                value = "4h 48m/day",
                delta = "↑ 15%",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                iconColor = AccentStudy,
                modifier = Modifier.weight(1f)
            )
            SummaryStatCard(
                title = "Mood",
                value = "Good",
                delta = "↑ 10%",
                icon = Icons.Default.SentimentSatisfied,
                iconColor = AccentMood,
                modifier = Modifier.weight(1f)
            )
        }

        // Multi-line Trends Graph
        WeeklyTrendsLineChart(records = allCheckIns)

        // Specialized Module 1: Sleep Tracker Detail
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentSleep.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Bedtime, contentDescription = null, tint = AccentSleep, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(text = "Sleep Quality & Rest", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Better sleep. Better focus. Better you.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // 5-star rating display
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(4) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                        }
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.surfaceContainerHigh, modifier = Modifier.size(16.dp))
                    }
                }

                // Sleep Tips Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "Tips for restorative sleep:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TertiaryContainerGreen, modifier = Modifier.size(14.dp))
                            Text(text = "Maintain consistent bedtime within 30 min window", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TertiaryContainerGreen, modifier = Modifier.size(14.dp))
                            Text(text = "Avoid high-contrast coding screens 45 min before sleep", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Specialized Module 2: Water Intake with Quick Add
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentWater.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = PrimaryContainerBlue, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(text = "Hydration Quick Log", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Stay hydrated. Stay energised.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Text(
                        text = "$waterGlasses / 8 Glasses",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryContainerBlue
                    )
                }

                // Quick Add Stepper Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickAddButton(label = "+ 250 ml", onClick = { viewModel.quickAddWater(1) }, modifier = Modifier.weight(1f))
                    QuickAddButton(label = "+ 500 ml", onClick = { viewModel.quickAddWater(2) }, modifier = Modifier.weight(1f))
                    QuickAddButton(label = "+ 1 Liter", onClick = { viewModel.quickAddWater(4) }, modifier = Modifier.weight(1f))
                }
            }
        }

        // Specialized Module 3: Study Tracker Goals
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentStudy.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = AccentStudy, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text(text = "Study Tracker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Track your study time and stay consistent.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Text(
                        text = "Goal: 5 hours",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TertiaryContainerGreen,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TertiaryContainerGreen.copy(alpha = 0.12f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                val currentStudyHours = studyHours
                val percent = ((currentStudyHours / 5f) * 100).toInt()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (currentStudyHours / 5f).coerceIn(0f, 1f))
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp))
                            .background(AccentStudy)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "${currentStudyHours.toInt()}h 30m logged today", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "$percent% completed", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = AccentStudy)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SummaryStatCard(
    title: String,
    value: String,
    delta: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(14.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
            }
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = delta, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TertiaryContainerGreen)
        }
    }
}

@Composable
private fun QuickAddButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .clickable { onClick() }
            .testTag("quick_add_${label.replace(" ", "_")}"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = PrimaryContainerBlue, modifier = Modifier.size(14.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = PrimaryContainerBlue)
        }
    }
}
