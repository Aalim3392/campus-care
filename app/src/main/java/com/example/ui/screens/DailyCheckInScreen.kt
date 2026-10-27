package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CampusWellViewModel
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.AccentStressed
import com.example.ui.theme.AccentStudy
import com.example.ui.theme.AccentWater
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryContainerBlue
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.TertiaryContainerGreen

data class MoodOption(val name: String, val icon: ImageVector, val color: Color)

@Composable
fun DailyCheckInScreen(
    viewModel: CampusWellViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMood by viewModel.selectedMood.collectAsState()
    val sleepHours by viewModel.sleepHours.collectAsState()
    val waterGlasses by viewModel.waterGlasses.collectAsState()
    val studyHours by viewModel.studyHours.collectAsState()
    val stressLevel by viewModel.stressLevel.collectAsState()

    val scrollState = rememberScrollState()

    var sleepMenuExpanded by remember { mutableStateOf(false) }
    var studyMenuExpanded by remember { mutableStateOf(false) }

    val moodOptions = listOf(
        MoodOption("Great", Icons.Default.SentimentVerySatisfied, TertiaryContainerGreen),
        MoodOption("Good", Icons.Default.SentimentSatisfied, PrimaryBlue),
        MoodOption("Okay", Icons.Default.SentimentNeutral, MaterialTheme.colorScheme.onSurfaceVariant),
        MoodOption("Low", Icons.Default.SentimentDissatisfied, MaterialTheme.colorScheme.secondary),
        MoodOption("Stressed", Icons.Default.MoodBad, AccentStressed)
    )

    val sleepOptions = listOf(4.5f, 5.5f, 6.5f, 7.0f, 7.5f, 8.0f, 9.0f)
    val studyOptions = listOf(0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f)

    val stressLabels = mapOf(
        1 to "Level 1: Minimal Tension",
        2 to "Level 2: Mild / Balanced",
        3 to "Level 3: Moderate Stress",
        4 to "Level 4: Elevated Strain",
        5 to "Level 5: High / Acute Fatigue"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Page Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SELF-CARE METRIC ENTRY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Daily Check-in",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Takes less than a minute. Only you can see your entries.",
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
                        text = "Mon, 28 Apr 2025",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Card 1: Emotional State Assessment
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
                    Column {
                        Text(
                            text = "How are you feeling today?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Select the option that best reflects your current state.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Baseline Metric",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // 5 Mood Selector Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    moodOptions.forEach { option ->
                        val isSelected = selectedMood.equals(option.name, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(82.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) SecondaryContainer.copy(alpha = 0.35f)
                                    else MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.7f)
                                )
                                .then(
                                    if (isSelected) Modifier.border(
                                        2.dp,
                                        PrimaryContainerBlue,
                                        RoundedCornerShape(12.dp)
                                    )
                                    else Modifier
                                )
                                .clickable { viewModel.selectedMood.value = option.name }
                                .padding(4.dp)
                                .testTag("mood_${option.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryContainerBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = option.name,
                                    tint = if (isSelected) PrimaryContainerBlue else option.color,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = option.name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) PrimaryContainerBlue else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Card 2: Physiological & Academic Parameters
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "Physiological & Academic Parameters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Quantify habits across key restorative and cognitive dimensions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 1. Sleep Duration Dropdown
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = null,
                                tint = AccentSleep,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Sleep",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Target: 7-9h",
                            fontSize = 11.sp,
                            color = PrimaryContainerBlue,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .clickable { sleepMenuExpanded = true }
                                .padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val sleepLabel = when (sleepHours) {
                                7.0f -> "7 hours (Optimal baseline)"
                                8.0f -> "8 hours"
                                9.0f -> "9+ hours"
                                else -> "$sleepHours hours"
                            }
                            Text(text = sleepLabel, style = MaterialTheme.typography.bodyMedium)
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = sleepMenuExpanded,
                            onDismissRequest = { sleepMenuExpanded = false }
                        ) {
                            sleepOptions.forEach { hours ->
                                DropdownMenuItem(
                                    text = { Text("$hours hours") },
                                    onClick = {
                                        viewModel.sleepHours.value = hours
                                        sleepMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 2. Water Intake Stepper
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = PrimaryContainerBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Water Intake",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        val liters = (waterGlasses * 0.25f)
                        Text(
                            text = "%.1f L total".format(liters),
                            fontSize = 11.sp,
                            color = TertiaryContainerGreen,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TertiaryContainerGreen.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(horizontal = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.adjustWater(-1) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                                .testTag("btn_water_decrement")
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease water", modifier = Modifier.size(18.dp))
                        }

                        Text(
                            text = "$waterGlasses glasses (%.1f L)".format(waterGlasses * 0.25f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        IconButton(
                            onClick = { viewModel.adjustWater(1) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                                .testTag("btn_water_increment")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase water", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // 3. Study Time Dropdown
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = AccentStudy,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Study Time",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Focused outside lectures",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .clickable { studyMenuExpanded = true }
                                .padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val studyLabel = when (studyHours) {
                                4.0f -> "4 hours (Recommended split)"
                                0.0f -> "0 hours (Rest day)"
                                6.0f -> "6+ hours (Heavy cognitive load)"
                                else -> "$studyHours hours"
                            }
                            Text(text = studyLabel, style = MaterialTheme.typography.bodyMedium)
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = studyMenuExpanded,
                            onDismissRequest = { studyMenuExpanded = false }
                        ) {
                            studyOptions.forEach { hours ->
                                DropdownMenuItem(
                                    text = { Text("$hours hours") },
                                    onClick = {
                                        viewModel.studyHours.value = hours
                                        studyMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 4. Stress Level Slider
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Stress Level",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = stressLabels[stressLevel] ?: "Level $stressLevel",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PrimaryContainerBlue,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(SecondaryContainer.copy(alpha = 0.35f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Slider(
                            value = stressLevel.toFloat(),
                            onValueChange = { viewModel.stressLevel.value = it.toInt() },
                            valueRange = 1f..5f,
                            steps = 3,
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryContainerBlue,
                                activeTrackColor = PrimaryContainerBlue
                            ),
                            modifier = Modifier.testTag("stress_slider")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Low (1)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Mild (2)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Moderate (3)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Elevated (4)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "High (5)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Weekly Consistency Alert Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
            shadowElevation = 0.dp
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Insights,
                    contentDescription = null,
                    tint = PrimaryContainerBlue,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Weekly consistency checkpoint",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "4/7 days",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryContainerBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Logging daily reveals workload triggers before high-stakes examination periods. You've recorded 4 check-ins this week.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 7-day indicator line
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val recordedDays = listOf(true, true, true, true, false, false, false)
                        val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")
                        recordedDays.forEachIndexed { idx, done ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = dayNames[idx],
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (done) PrimaryContainerBlue else MaterialTheme.colorScheme.secondaryContainer)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Save Check-in Action Button
        Button(
            onClick = { viewModel.saveCheckIn() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_save_checkin"),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Save Check-in",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Confidentiality Notice
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Your responses are private to you. They are never shared with academic staff or faculty.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
