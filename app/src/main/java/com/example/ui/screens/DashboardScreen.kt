package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.CampusWellViewModel
import com.example.ui.components.MetricCard
import com.example.ui.components.WeeklyBalanceChart
import com.example.ui.navigation.Screen
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.AccentStudy
import com.example.ui.theme.AccentWater
import com.example.ui.theme.PrimaryContainerBlue
import com.example.ui.theme.TertiaryContainerGreen

@Composable
fun DashboardScreen(
    viewModel: CampusWellViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val allCheckIns by viewModel.allCheckIns.collectAsState()
    val waterGlasses by viewModel.waterGlasses.collectAsState()
    val sleepHours by viewModel.sleepHours.collectAsState()
    val studyHours by viewModel.studyHours.collectAsState()
    val selectedMood by viewModel.selectedMood.collectAsState()
    val aiInsight by viewModel.dashboardAiInsight.collectAsState()
    val latestStudentAdvice by viewModel.latestStudentAdvice.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Greeting Banner
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(TertiaryContainerGreen)
                    )
                    Text(
                        text = "DAILY OVERVIEW · TERM 2",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.8.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = PrimaryContainerBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Mon, 28 Apr 2025",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Good Morning, ${userProfile.name}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Small daily steps make a big difference in your academic journey.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Proactive AI Stress Guardian Alert Banner
        latestStudentAdvice?.let { advice ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
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
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AccentMood.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = AccentMood,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "Proactive AI Stress Guardian",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .clickable { viewModel.dismissStudentAdvice() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Dismiss", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        }
                    }

                    Text(
                        text = advice,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onNavigate(Screen.AiCounselor) },
                            modifier = Modifier.height(32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Talk with AI Counselor", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Daily Check-in Prompt Card (Hero Action Card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_checkin_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceContainerLowest,
                                MaterialTheme.colorScheme.surfaceContainerLow,
                                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                            )
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryContainerBlue)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DAILY HABIT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = TertiaryContainerGreen,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Private & encrypted",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TertiaryContainerGreen
                            )
                        }
                    }

                    Text(
                        text = "How are you today?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Take 45 seconds to pause and log how you are feeling between lectures.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Cognitive Indicator Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = PrimaryContainerBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Cognitive Load: Low · ${userProfile.streakDays}-Day Streak",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }

                    // Complete Daily Check-in CTA
                    Button(
                        onClick = { onNavigate(Screen.DailyCheckIn) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("cta_complete_checkin"),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Complete Daily Check-in",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 2x2 Metric Cards Grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sleep
                val sleepStr = "${sleepHours.toInt()}h ${((sleepHours - sleepHours.toInt()) * 60).toInt()}m"
                MetricCard(
                    title = "Sleep",
                    value = sleepStr,
                    subtitle = "Within target range",
                    icon = Icons.Default.Bedtime,
                    iconBgColor = AccentSleep.copy(alpha = 0.15f),
                    iconTintColor = AccentSleep,
                    progress = (sleepHours / 9f).coerceIn(0f, 1f),
                    progressColor = AccentSleep,
                    onClick = { onNavigate(Screen.Progress) },
                    modifier = Modifier.weight(1f)
                )

                // Water
                val waterLiters = (waterGlasses * 0.25f)
                val litersStr = if (waterGlasses % 4 == 0) "${waterLiters.toInt()} L" else "%.1f L".format(waterLiters)
                val waterPercent = ((waterGlasses / 8f) * 100).toInt()
                MetricCard(
                    title = "Water",
                    value = litersStr,
                    subtitle = "$waterGlasses of 8 glasses · $waterPercent%",
                    icon = Icons.Default.WaterDrop,
                    iconBgColor = AccentWater.copy(alpha = 0.15f),
                    iconTintColor = PrimaryContainerBlue,
                    progress = (waterGlasses / 8f).coerceIn(0f, 1f),
                    progressColor = PrimaryContainerBlue,
                    onClick = { onNavigate(Screen.Progress) },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Study Time
                val studyStr = "${studyHours.toInt()}h ${((studyHours - studyHours.toInt()) * 60).toInt()}m"
                MetricCard(
                    title = "Study Time",
                    value = studyStr,
                    subtitle = "Goal: 5h · 30m left",
                    icon = Icons.Default.Timer,
                    iconBgColor = AccentStudy.copy(alpha = 0.15f),
                    iconTintColor = AccentStudy,
                    progress = (studyHours / 5f).coerceIn(0f, 1f),
                    progressColor = AccentStudy,
                    onClick = { onNavigate(Screen.Progress) },
                    modifier = Modifier.weight(1f)
                )

                // Today's Mood
                MetricCard(
                    title = "Mood",
                    value = selectedMood,
                    subtitle = "9:15 AM · Stable",
                    icon = Icons.Default.SentimentSatisfied,
                    iconBgColor = AccentMood.copy(alpha = 0.15f),
                    iconTintColor = AccentMood,
                    progress = null,
                    progressColor = AccentMood,
                    onClick = { onNavigate(Screen.DailyCheckIn) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Serene Landscape Banner Card from Mockup
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { onNavigate(Screen.AiCounselor) },
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shadowElevation = 1.dp
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_landscape),
                        contentDescription = "Calm mountain reflection landscape",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.45f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Take care of your mind and body",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "A healthier you builds a brighter future",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // AI Insight Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainerBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = PrimaryContainerBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = aiInsight,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Weekly Study vs Rest Balance Chart
        WeeklyBalanceChart(records = allCheckIns)

        // Support & Campus Resources Quick Access Card
        Surface(
            modifier = Modifier.clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shadowElevation = 1.dp
        ) {
            Column {
                Text(
                    text = "Support & Campus Resources",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 6.dp)
                )

                QuickAccessRow(
                    title = "View Weekly Progress",
                    subtitle = "Analyze sleep & study trends",
                    icon = Icons.Default.Insights,
                    iconBgColor = MaterialTheme.colorScheme.surfaceContainer,
                    iconTintColor = PrimaryContainerBlue,
                    onClick = { onNavigate(Screen.Progress) }
                )

                QuickAccessRow(
                    title = "Campus Support",
                    subtitle = "Student wellness & counselling lines",
                    icon = Icons.Default.HealthAndSafety,
                    iconBgColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    iconTintColor = TertiaryContainerGreen,
                    onClick = { onNavigate(Screen.Support) }
                )

                QuickAccessRow(
                    title = "Quiet Spaces & Pods",
                    subtitle = "CoE Floor 4 quiet study availability",
                    icon = Icons.Default.MeetingRoom,
                    iconBgColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    iconTintColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = { onNavigate(Screen.Support) }
                )

                QuickAccessRow(
                    title = "AI Wellness Companion",
                    subtitle = "Multi-turn guidance & meditation art",
                    icon = Icons.Default.Psychology,
                    iconBgColor = AccentSleep.copy(alpha = 0.15f),
                    iconTintColor = AccentSleep,
                    onClick = { onNavigate(Screen.AiCounselor) }
                )

                // 24/7 Counselor Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneInTalk,
                                contentDescription = null,
                                tint = TertiaryContainerGreen,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Campus counselors on duty",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "24/7 ext 4100",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryContainerBlue
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuickAccessRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconTintColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )
    }
}
