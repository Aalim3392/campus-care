package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import com.example.ui.CampusWellViewModel
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryContainerBlue
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.SecondaryContainer
import com.example.ui.theme.TertiaryContainerGreen
import com.example.ui.theme.TertiaryFixed

data class SupportDetailItem(
    val title: String,
    val badge: String,
    val desc: String,
    val meta: String,
    val icon: ImageVector,
    val iconBgColor: Color,
    val iconTintColor: Color,
    val fullDetails: String,
    val contactInfo: String
)

@Composable
fun SupportScreen(
    viewModel: CampusWellViewModel,
    modifier: Modifier = Modifier
) {
    val supportTopic by viewModel.supportTopic.collectAsState()
    val supportMessage by viewModel.supportMessage.collectAsState()
    val isSubmitted by viewModel.supportSubmitted.collectAsState()

    val scrollState = rememberScrollState()
    var topicDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDetailItem by remember { mutableStateOf<SupportDetailItem?>(null) }

    val topics = listOf(
        "Study Pressure & Deadlines",
        "Stress & Anxiety",
        "Sleep Difficulties",
        "Exam Fatigue",
        "Other Concerns"
    )

    val supportCards = listOf(
        SupportDetailItem(
            title = "Counselling Services",
            badge = "Academic Guidance",
            desc = "Confidential one-on-one sessions and academic wellness guidance for students.",
            meta = "Available Mon-Fri · 09:00-17:00",
            icon = Icons.Default.Psychology,
            iconBgColor = PrimaryFixed,
            iconTintColor = PrimaryBlue,
            fullDetails = "Our licensed clinical staff provides confidential 50-minute personal consultations covering cognitive load, exam stress, imposter syndrome, and study-life alignment.",
            contactInfo = "Location: Student Care Sanctuary, Wing B (Room 304)\nPhone: (020) 7946-0120\nEmail: counselling@campuswell.edu"
        ),
        SupportDetailItem(
            title = "Wellness Resources",
            badge = "Self-Paced",
            desc = "Self-guided stress management workshops, mindfulness toolkits, and sleep guides.",
            meta = "12 Modular Toolkits",
            icon = Icons.Default.Spa,
            iconBgColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            iconTintColor = TertiaryContainerGreen,
            fullDetails = "Self-paced multimedia modules tailored specifically to STEM and engineering curriculums. Access box breathing audio guides, pomodoro pacing templates, and sleep hygiene checklists.",
            contactInfo = "Portal: well.campus.edu/toolkits\nAccess: Student ID Single Sign-On"
        ),
        SupportDetailItem(
            title = "Campus Services",
            badge = "Campus Network",
            desc = "Student affairs, study habit support centres, and peer mentoring networks.",
            meta = "Main Campus & Virtual Hub",
            icon = Icons.Default.Groups,
            iconBgColor = SecondaryContainer,
            iconTintColor = MaterialTheme.colorScheme.secondary,
            fullDetails = "Connect with high-year peer tutors, academic schedule advisors, and disability accommodation coordinators who ensure exams and coursework pacing suit your health needs.",
            contactInfo = "Hours: Mon-Sat 08:30-19:00\nHelpdesk: Hall 2, Student Union Hub"
        ),
        SupportDetailItem(
            title = "Emergency Contacts",
            badge = "Urgent 24/7 Access",
            desc = "24/7 campus safety dispatch and certified student mental health crisis helpline.",
            meta = "Direct Dispatch Lines",
            icon = Icons.Default.Emergency,
            iconBgColor = ErrorContainer,
            iconTintColor = ErrorRed,
            fullDetails = "Emergency crisis counseling and on-campus medical response. Certified responders are available at all times. All calls are treated with the highest medical confidentiality.",
            contactInfo = "Urgent Hotline: Call ext 4100 (Internal) or 1-800-273-TALK\nCampus Security Dispatch: (020) 7946-0999"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "DEPT OF COMPUTER ENGINEERING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        letterSpacing = 0.6.sp
                    )
                    Text(text = "•", color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        text = "CARE DESK",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Support",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Find help and resources when you need them.",
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
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = TertiaryContainerGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Confidential Resources",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 4 Category Support Cards
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            supportCards.forEach { card ->
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
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(card.iconBgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = card.icon,
                                    contentDescription = null,
                                    tint = card.iconTintColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(card.iconBgColor.copy(alpha = 0.5f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = card.badge,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = card.iconTintColor
                                )
                            }
                        }

                        Text(
                            text = card.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = card.desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = card.meta,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )

                            OutlinedButton(
                                onClick = { selectedDetailItem = card },
                                modifier = Modifier.height(34.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("View Details", fontSize = 12.sp, color = PrimaryContainerBlue)
                            }
                        }
                    }
                }
            }
        }

        // Request Support Form
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surfaceContainerLowest,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = null,
                        tint = PrimaryContainerBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Wellness Coordinator Desk",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryContainerBlue
                    )
                }

                Text(
                    text = "Request Support",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Need guidance? Reach out to the student wellness coordinator. You are in control.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Topic Selector
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Topic of Concern",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .clickable { topicDropdownExpanded = true }
                                .padding(horizontal = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = supportTopic, style = MaterialTheme.typography.bodyMedium)
                            Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null)
                        }

                        DropdownMenu(
                            expanded = topicDropdownExpanded,
                            onDismissRequest = { topicDropdownExpanded = false }
                        ) {
                            topics.forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t) },
                                    onClick = {
                                        viewModel.supportTopic.value = t
                                        topicDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Optional Message Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Optional Message",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = supportMessage,
                        onValueChange = { viewModel.supportMessage.value = it },
                        placeholder = { Text("Briefly share how we can assist you (optional)...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("support_message_input"),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                // Submit Button
                Button(
                    onClick = { viewModel.submitSupportRequest() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_submit_support"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Request Support", fontWeight = FontWeight.SemiBold)
                }

                // Submission confirmation banner
                AnimatedVisibility(visible = isSubmitted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = TertiaryContainerGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Request submitted privately",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "A designated wellness coordinator will review your request. Communication will arrive solely via your secure student portal within 1 business day.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                TextButton(
                                    onClick = { viewModel.resetSupportForm() },
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Submit another request", fontSize = 11.sp, color = PrimaryContainerBlue)
                                }
                            }
                        }
                    }
                }

                // Privacy lock footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = TertiaryContainerGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Your request is only shared if you choose to submit it. Faculty and course tutors cannot view support tickets.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Detail Dialog when clicking "View Details"
    selectedDetailItem?.let { item ->
        Dialog(onDismissRequest = { selectedDetailItem = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(item.iconBgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = item.icon, contentDescription = null, tint = item.iconTintColor, modifier = Modifier.size(22.dp))
                        }
                        Column {
                            Text(text = item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = item.badge, fontSize = 11.sp, color = item.iconTintColor)
                        }
                    }

                    Text(text = item.fullDetails, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = item.contactInfo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { selectedDetailItem = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryContainerBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Close Details")
                    }
                }
            }
        }
    }
}
