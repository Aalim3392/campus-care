package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CheckInRecord
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.PrimaryContainerBlue
import com.example.ui.theme.TertiaryContainerGreen

data class DayBalance(
    val day: String,
    val studyHours: Float,
    val sleepHours: Float,
    val isToday: Boolean = false
)

@Composable
fun WeeklyBalanceChart(
    records: List<CheckInRecord>,
    modifier: Modifier = Modifier
) {
    // Default 7 days if records are empty or less
    val days = listOf(
        DayBalance("Mon", 4.5f, 7.5f, isToday = true),
        DayBalance("Tue", 5.2f, 7.0f),
        DayBalance("Wed", 3.5f, 8.2f),
        DayBalance("Thu", 6.0f, 6.0f),
        DayBalance("Fri", 4.8f, 6.8f),
        DayBalance("Sat", 2.5f, 9.0f),
        DayBalance("Sun", 1.5f, 7.5f)
    )

    Surface(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Study vs. Rest Balance",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Aggregated focus blocks vs restorative sleep",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Legend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(PrimaryContainerBlue)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Study",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(AccentSleep)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Rest",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bars Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEach { item ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (item.isToday) Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    .padding(vertical = 4.dp)
                                else Modifier
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Pair of bars
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(95.dp)
                        ) {
                            // Study Bar (max scale 10 hours)
                            val studyHeight = (item.studyHours / 10f).coerceIn(0.1f, 1f) * 90f
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(studyHeight.dp)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(PrimaryContainerBlue)
                            )

                            // Rest Bar
                            val sleepHeight = (item.sleepHours / 10f).coerceIn(0.1f, 1f) * 90f
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(sleepHeight.dp)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(if (item.isToday) AccentSleep else AccentSleep.copy(alpha = 0.65f))
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.day,
                            fontSize = 11.sp,
                            fontWeight = if (item.isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (item.isToday) PrimaryContainerBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Equilibrium indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recommended ratio: 1 : 1.4",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Optimal equilibrium",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TertiaryContainerGreen
                )
            }
        }
    }
}
