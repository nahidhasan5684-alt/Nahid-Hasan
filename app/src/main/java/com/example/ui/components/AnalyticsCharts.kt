package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TranscriptEntity
import com.example.ui.theme.*

@Composable
fun ProductivityRadialGauge(
    productivityRate: Int,
    attendanceRate: Int,
    modifier: Modifier = Modifier
) {
    val animatedProductivity by animateFloatAsState(
        targetValue = productivityRate / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "productivity"
    )

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Real-Time Productivity & Health",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Academic tasks completed vs deadlines & live attendance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 14.dp.toPx()
                    val diameter = size.minDimension - strokeWidth
                    val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)

                    // Background track
                    drawArc(
                        color = Slate200,
                        startAngle = 135f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Active progress
                    drawArc(
                        color = PrimaryBlue,
                        startAngle = 135f,
                        sweepAngle = 270f * animatedProductivity,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$productivityRate%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = PrimaryBlue
                    )
                    Text(
                        text = "Task Pace",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricPill(label = "Attendance", value = "$attendanceRate%", color = SuccessGreen)
                MetricPill(label = "Study Pace", value = "Optimal", color = SecondaryTeal)
                MetricPill(label = "Submissions", value = "On Time", color = TertiaryAmber)
            }
        }
    }
}

@Composable
fun MetricPill(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = color.copy(alpha = 0.12f),
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GradeDistributionChart(
    transcripts: List<TranscriptEntity>,
    modifier: Modifier = Modifier
) {
    val aPlusCount = transcripts.count { it.letterGrade == "A+" }
    val aCount = transcripts.count { it.letterGrade == "A" }
    val aMinusCount = transcripts.count { it.letterGrade == "A-" }
    val bCount = transcripts.count { it.letterGrade == "B" || it.letterGrade == "C" }
    val total = maxOf(transcripts.size, 1)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Academic Grade Distribution",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Course letter grade breakdown across active semester",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            GradeBarItem(grade = "A+ (GPA 5.00)", count = aPlusCount, total = total, color = PrimaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            GradeBarItem(grade = "A  (GPA 4.00)", count = aCount, total = total, color = SecondaryTeal)
            Spacer(modifier = Modifier.height(8.dp))
            GradeBarItem(grade = "A- (GPA 3.50)", count = aMinusCount, total = total, color = TertiaryAmber)
            Spacer(modifier = Modifier.height(8.dp))
            GradeBarItem(grade = "B/C (Passing)", count = bCount, total = total, color = Slate700)
        }
    }
}

@Composable
fun GradeBarItem(grade: String, count: Int, total: Int, color: Color) {
    val fraction = count.toFloat() / total.toFloat()
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(grade, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text("$count courses (${(fraction * 100).toInt()}%)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = Slate200
        )
    }
}
