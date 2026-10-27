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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CheckInRecord
import com.example.ui.theme.AccentMood
import com.example.ui.theme.AccentSleep
import com.example.ui.theme.AccentStudy
import com.example.ui.theme.AccentWater

@Composable
fun WeeklyTrendsLineChart(
    records: List<CheckInRecord>,
    modifier: Modifier = Modifier
) {
    // 7 days data points normalized (0f to 10f scale)
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val sleepPoints = listOf(7.5f, 7.0f, 8.2f, 6.0f, 6.8f, 9.0f, 7.5f)
    val waterPoints = listOf(6.0f, 6.5f, 7.0f, 8.0f, 6.0f, 5.0f, 7.0f)
    val studyPoints = listOf(4.5f, 5.2f, 3.5f, 6.0f, 4.8f, 2.5f, 1.5f)
    val moodPoints = listOf(8.0f, 8.0f, 6.0f, 7.5f, 7.0f, 9.0f, 8.5f)

    Surface(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Weekly Trends",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Synchronized behavioral metrics across the 7-day cycle",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Legend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LegendItem(label = "Sleep", color = AccentSleep)
                    LegendItem(label = "Water", color = AccentWater)
                    LegendItem(label = "Study", color = AccentStudy)
                    LegendItem(label = "Mood", color = AccentMood)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas drawing multi-line graph
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val width = size.width
                    val height = size.height
                    val paddingBottom = 20.dp.toPx()
                    val graphHeight = height - paddingBottom
                    val numPoints = 7
                    val stepX = width / (numPoints - 1)

                    // Draw subtle grid lines
                    val gridSteps = 4
                    for (i in 0..gridSteps) {
                        val y = graphHeight * (i.toFloat() / gridSteps)
                        drawLine(
                            color = Color(0xFFE2E8F0),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1f
                        )
                    }

                    fun drawMetricLine(values: List<Float>, lineColor: Color) {
                        val path = Path()
                        val points = mutableListOf<Offset>()

                        for (i in values.indices) {
                            val x = i * stepX
                            val normalized = (values[i] / 10f).coerceIn(0f, 1f)
                            val y = graphHeight - (normalized * graphHeight)
                            val pt = Offset(x, y)
                            points.add(pt)
                            if (i == 0) {
                                path.moveTo(pt.x, pt.y)
                            } else {
                                val prev = points[i - 1]
                                val cx = (prev.x + pt.x) / 2
                                path.cubicTo(cx, prev.y, cx, pt.y, pt.x, pt.y)
                            }
                        }

                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(
                                width = 2.5.dp.toPx(),
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )

                        // Draw point dots
                        points.forEach { pt ->
                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = lineColor,
                                radius = 3.dp.toPx(),
                                center = pt
                            )
                        }
                    }

                    drawMetricLine(sleepPoints, AccentSleep)
                    drawMetricLine(waterPoints, AccentWater)
                    drawMetricLine(studyPoints, AccentStudy)
                    drawMetricLine(moodPoints, AccentMood)
                }
            }

            // Day labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 11.sp,
                        fontWeight = if (day == "Mon") FontWeight.Bold else FontWeight.Normal,
                        color = if (day == "Mon") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
