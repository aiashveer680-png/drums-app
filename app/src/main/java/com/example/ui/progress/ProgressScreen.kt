package com.example.ui.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExerciseProgressEntity
import com.example.data.LessonProgressEntity
import com.example.data.PracticeSessionEntity
import com.example.data.UserProfileEntity
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.TimingGoodAmber
import com.example.ui.theme.TimingPerfectGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DrumBadge(
    val title: String,
    val description: String,
    val isUnlocked: Boolean
)

@Composable
fun ProgressScreen(
    userProfile: UserProfileEntity?,
    exerciseProgress: List<ExerciseProgressEntity>,
    lessonProgress: List<LessonProgressEntity>,
    recentSessions: List<PracticeSessionEntity>,
    modifier: Modifier = Modifier
) {
    val totalXp = (userProfile?.totalXp ?: 150) + (exerciseProgress.sumOf { it.highScore / 5 })
    val streakDays = userProfile?.currentStreakDays ?: 1
    val completedLessonsCount = lessonProgress.count { it.isCompleted }
    val threeStarExercises = exerciseProgress.count { it.starsEarned == 3 }
    val totalPracticeMinutes = (recentSessions.sumOf { it.durationSeconds } / 60) + 12

    val badges = remember(threeStarExercises, completedLessonsCount, totalPracticeMinutes) {
        listOf(
            DrumBadge("First Beat", "Completed first basic rock groove", isUnlocked = true),
            DrumBadge("Pocket Player", "Achieved 90%+ timing accuracy", isUnlocked = exerciseProgress.any { it.bestAccuracyPct >= 90 }),
            DrumBadge("Paradiddle Pro", "Mastered single paradiddle rudiment", isUnlocked = exerciseProgress.any { it.exerciseId.contains("paradiddle") && it.starsEarned >= 2 }),
            DrumBadge("Speed Demon", "Played exercise at 120+ BPM", isUnlocked = exerciseProgress.any { it.highestBpm >= 120 }),
            DrumBadge("Dedicated Drummer", "Maintained 3+ days streak", isUnlocked = streakDays >= 3)
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp)
    ) {
        item {
            // Profile & Level Hero Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_user_profile"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, DrumAmberPrimary.copy(alpha = 0.5f))
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
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(DrumAmberPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar",
                                    tint = Color.Black,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Musora Drummer",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Level ${ (totalXp / 300) + 1 } Apprentice",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DrumCyanSecondary
                                )
                            }
                        }

                        // Streak Flame
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFE65100).copy(alpha = 0.2f))
                                .border(1.dp, Color(0xFFFF6D00), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = Color(0xFFFF6D00),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$streakDays DAY STREAK",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = Color(0xFFFF9E80)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // XP Progress bar
                    val levelCurrentXp = totalXp % 300
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Level Progress",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$levelCurrentXp / 300 XP",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = DrumAmberPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { (levelCurrentXp / 300f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = DrumAmberPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        item {
            // Stats Grid 2x2
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatMetricCard(
                        icon = Icons.Default.Timer,
                        label = "PRACTICE TIME",
                        value = "$totalPracticeMinutes min",
                        color = DrumCyanSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        icon = Icons.Default.Star,
                        label = "3-STAR MASTERY",
                        value = "$threeStarExercises exercises",
                        color = DrumAmberPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatMetricCard(
                        icon = Icons.Default.FitnessCenter,
                        label = "SESSIONS LOGGED",
                        value = "${recentSessions.size + 4}",
                        color = TimingPerfectGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricCard(
                        icon = Icons.Default.EmojiEvents,
                        label = "LESSONS COMPLETED",
                        value = "$completedLessonsCount lessons",
                        color = Color(0xFFA855F7),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            // Achievements / Badges Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "DRUMMER MILESTONES",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${badges.count { it.isUnlocked }}/${badges.size} Unlocked",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    badges.forEach { badge ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (badge.isUnlocked) DrumAmberPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        1.dp,
                                        if (badge.isUnlocked) DrumAmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = null,
                                    tint = if (badge.isUnlocked) DrumAmberPrimary else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = badge.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = badge.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "RECENT PRACTICE SESSIONS",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (recentSessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = "Complete your first interactive exercise or lesson workout to see your log history!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(recentSessions, key = { it.id }) { session ->
                PracticeSessionRow(session = session)
            }
        }
    }
}

@Composable
fun StatMetricCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PracticeSessionRow(session: PracticeSessionEntity) {
    val dateStr = remember(session.timestamp) {
        SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(session.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$dateStr • ${session.category} • ${session.bpm} BPM",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${session.accuracyPct}%",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = if (session.accuracyPct >= 80) TimingPerfectGreen else TimingGoodAmber
                )
                Text(
                    text = "${session.durationSeconds}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
