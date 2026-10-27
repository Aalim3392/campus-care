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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.model.DepartmentStudent
import com.example.model.StressStatus
import com.example.model.UserRole
import com.example.ui.CampusWellViewModel
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.AccentStressed
import com.example.ui.theme.AccentStudy
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.NavyDark
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryContainerBlue
import com.example.ui.theme.TertiaryContainerGreen

@Composable
fun FacultyDashboardScreen(
    viewModel: CampusWellViewModel,
    onNavigateToCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val students by viewModel.departmentStudents.collectAsState()
    val filter by viewModel.departmentFilter.collectAsState()
    val activeAlerts by viewModel.activeAlerts.collectAsState()

    val scrollState = rememberScrollState()

    val filteredStudents = when (filter) {
        "Div A" -> students.filter { it.division == "Div A" }
        "Div B" -> students.filter { it.division == "Div B" }
        "Attention Needed" -> students.filter { it.stressStatus != StressStatus.NORMAL }
        else -> students
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Faculty & HOD Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(PrimaryContainerBlue)
                    )
                    Text(
                        text = if (userProfile.role == UserRole.HOD) "HOD EXECUTIVE OVERSIGHT" else "FACULTY CARE PORTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        letterSpacing = 0.8.sp
                    )
                }
                Text(
                    text = "Department Wellbeing Monitor",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Computer Engineering Dept · K.K. Wagh Institute",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onNavigateToCheckIn,
                modifier = Modifier.height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Self Check-in", fontSize = 12.sp)
            }
        }

        // Cohort Stats Cards (64 students tracked)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CohortStatCard(
                label = "Total Students",
                value = "64",
                subtitle = "Div A & Div B",
                accentColor = PrimaryContainerBlue,
                modifier = Modifier.weight(1f)
            )
            CohortStatCard(
                label = "Healthy Equilibrium",
                value = "78%",
                subtitle = "50 students",
                accentColor = TertiaryContainerGreen,
                modifier = Modifier.weight(1f)
            )
            CohortStatCard(
                label = "Attention Needed",
                value = "22%",
                subtitle = "14 flagged by AI",
                accentColor = AccentStressed,
                modifier = Modifier.weight(1f)
            )
        }

        // AI Department Stress Guardian Alert Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AccentStressed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = AccentStressed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "AI Stress Early-Intervention Alert",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Automated proactive care monitoring active",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PrimaryContainerBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Gemini Diagnostic: 2 students in Div A logged critical acute stress (Level 5) with acute sleep deficits (<4.5h). The AI has issued proactive calming guidance and notified your desk to offer pacing consultations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // Filter Chips Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Default.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
            listOf("All", "Div A", "Div B", "Attention Needed").forEach { tab ->
                val isSelected = filter == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) PrimaryContainerBlue
                            else MaterialTheme.colorScheme.surfaceContainerLowest
                        )
                        .clickable { viewModel.departmentFilter.value = tab }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("filter_$tab")
                ) {
                    Text(
                        text = tab,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Students Roster
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            filteredStudents.forEach { student ->
                StudentRosterCard(
                    student = student,
                    onIntervention = { viewModel.sendFacultyIntervention(student.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun CohortStatCard(
    label: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(14.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = accentColor)
            Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun StudentRosterCard(
    student: DepartmentStudent,
    onIntervention: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NavyDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.name.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Column {
                        Text(text = student.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = "${student.rollNo} · ${student.division}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Status Badge
                val (badgeText, badgeBg, badgeColor) = when (student.stressStatus) {
                    StressStatus.CRITICAL_FACULTY_ESCALATION -> Triple("High Stress Alert", ErrorContainer, ErrorRed)
                    StressStatus.WATCHLIST_WARNING -> Triple("Watchlist", AccentMood.copy(alpha = 0.2f), AccentMood)
                    StressStatus.NORMAL -> Triple("Healthy", TertiaryContainerGreen.copy(alpha = 0.15f), TertiaryContainerGreen)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = badgeText, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                }
            }

            // Metrics row: Mood, Sleep, Water, Stress
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Mood: ${student.lastMood}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "Sleep: ${student.sleepHours}h", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "Water: ${student.waterGlasses} gl", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = "Stress: Level ${student.stressLevel}/5",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (student.stressLevel >= 4) AccentStressed else PrimaryBlue
                )
            }

            // AI Advice / Diagnostic Strip
            Text(
                text = "AI Assessment: ${student.aiAdvice}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
            )

            // Faculty action if in watchlist or critical
            if (student.stressStatus != StressStatus.NORMAL) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (student.facultyNotified) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TertiaryContainerGreen, modifier = Modifier.size(14.dp))
                            Text("Consultation Scheduled", fontSize = 11.sp, color = TertiaryContainerGreen, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Button(
                            onClick = onIntervention,
                            modifier = Modifier.height(32.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Send Pacing Outreach", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
