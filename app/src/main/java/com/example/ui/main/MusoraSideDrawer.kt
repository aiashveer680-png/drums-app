package com.example.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.MetronomeController
import com.example.audio.MetronomeState
import com.example.data.UserProfileEntity
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.TimingMissRed
import com.example.ui.theme.TimingPerfectGreen

/**
 * Side Navigation Menu styled after the official Musora / Drumeo mobile & tablet app.
 */
@Composable
fun MusoraSideDrawer(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    userProfile: UserProfileEntity?,
    metronomeState: MetronomeState,
    metronomeController: MetronomeController? = null,
    onToggleMetronome: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalXp = userProfile?.totalXp ?: 340
    val level = (totalXp / 300) + 1
    val streakDays = userProfile?.currentStreakDays ?: 3

    ModalDrawerSheet(
        modifier = modifier
            .width(325.dp)
            .fillMaxHeight(),
        drawerContainerColor = Color(0xFF0C0F17),
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            // ================= 1. MUSORA BRAND HEADER =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(DrumAmberPrimary, Color(0xFFFF5722))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MUSORA",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                ),
                                color = DrumAmberPrimary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "DRUMS",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 1.sp
                                ),
                                color = DrumCyanSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "THE OFFICIAL DRUM LEARNING METHOD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.5.sp
                        ),
                        color = Color(0xFF94A3B8)
                    )
                }

                IconButton(
                    onClick = onCloseDrawer,
                    modifier = Modifier.testTag("btn_close_drawer")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Menu",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= 2. MUSORA STUDENT PASSPORT CARD =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141824)),
                border = BorderStroke(1.dp, DrumAmberPrimary.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(DrumAmberPrimary, Color(0xFFFF6D00))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar",
                                    tint = Color.Black,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Musora Student",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "Level $level • Intermediate Drummer",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = DrumCyanSecondary
                                )
                            }
                        }

                        // Musora All-Access Gold Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DrumAmberPrimary.copy(alpha = 0.2f))
                                .border(1.dp, DrumAmberPrimary, RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = DrumAmberPrimary,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "ALL-ACCESS",
                                    color = DrumAmberPrimary,
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Daily Streak & Practice XP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = Color(0xFFFF6D00),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$streakDays Day Practice Streak",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFFFF9E80)
                            )
                        }

                        Text(
                            text = "$totalXp XP",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = DrumAmberPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { ((totalXp % 300) / 300f).coerceIn(0.08f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = DrumAmberPrimary,
                        trackColor = Color(0xFF22293A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ================= 3. SECTION: THE DRUM METHOD =================
            Text(
                text = "THE DRUM METHOD",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    fontSize = 10.sp
                ),
                color = Color(0xFF64748B),
                modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
            )

            MusoraMenuItem(
                title = "The Drum Method",
                subtitle = "10 Levels of Guided Video Lessons",
                icon = Icons.Default.School,
                badge = "METHOD",
                badgeColor = DrumAmberPrimary,
                selected = selectedTab == MainTab.LESSONS,
                testTag = "drawer_item_lessons",
                onClick = { onTabSelected(MainTab.LESSONS) }
            )

            MusoraMenuItem(
                title = "Interactive Exercises",
                subtitle = "Play-Along with Live Timing Feedback",
                icon = Icons.Default.FitnessCenter,
                badge = "WORKOUT",
                badgeColor = DrumCyanSecondary,
                selected = selectedTab == MainTab.EXERCISES,
                testTag = "drawer_item_exercises",
                onClick = { onTabSelected(MainTab.EXERCISES) }
            )

            MusoraMenuItem(
                title = "Drum Rudiments",
                subtitle = "PAS 40 Rudiments • Rolls & Diddles",
                icon = Icons.Default.MenuBook,
                badge = "FOUNDATIONS",
                badgeColor = DrumAmberPrimary,
                selected = selectedTab == MainTab.RUDIMENTS,
                testTag = "drawer_item_rudiments",
                onClick = { onTabSelected(MainTab.RUDIMENTS) }
            )

            MusoraMenuItem(
                title = "Bass Drum Letters",
                subtitle = "The Language of Drumming (A - P)",
                icon = Icons.Default.GraphicEq,
                badge = "BENNY GREB",
                badgeColor = Color(0xFF4FB3C6),
                selected = selectedTab == MainTab.BASS_LETTERS,
                testTag = "drawer_item_bass_letters",
                onClick = { onTabSelected(MainTab.BASS_LETTERS) }
            )

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFF1E2433), thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // ================= 4. SECTION: PRACTICE TOOLS =================
            Text(
                text = "PRACTICE TOOLS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    fontSize = 10.sp
                ),
                color = Color(0xFF64748B),
                modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
            )

            MusoraMenuItem(
                title = "Rhythm Practice Tool",
                subtitle = "Groove Metronome & Time Signatures",
                icon = Icons.Default.Speed,
                badge = "PRO METRONOME",
                badgeColor = TimingPerfectGreen,
                selected = selectedTab == MainTab.RHYTHM_TOOL,
                testTag = "drawer_item_rhythm_tool",
                onClick = { onTabSelected(MainTab.RHYTHM_TOOL) }
            )

            // ================= 5. QUICK COUNTDOWN TIMER LAUNCHER =================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111520)),
                border = BorderStroke(1.dp, Color(0xFF222B3D))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = DrumAmberPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Practice Countdown",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }

                        val timerConfig = metronomeState.countdownTimer
                        if (timerConfig.enabled) {
                            Text(
                                text = timerConfig.formattedRemaining,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = TimingPerfectGreen
                            )
                        } else {
                            Text(
                                text = "Quick Launch",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        QuickTimerChip("5m", 300) { sec ->
                            metronomeController?.setTimerEnabled(true)
                            metronomeController?.setTimerDuration(sec)
                            onTabSelected(MainTab.RHYTHM_TOOL)
                        }
                        QuickTimerChip("10m", 600) { sec ->
                            metronomeController?.setTimerEnabled(true)
                            metronomeController?.setTimerDuration(sec)
                            onTabSelected(MainTab.RHYTHM_TOOL)
                        }
                        QuickTimerChip("15m", 900) { sec ->
                            metronomeController?.setTimerEnabled(true)
                            metronomeController?.setTimerDuration(sec)
                            onTabSelected(MainTab.RHYTHM_TOOL)
                        }
                        QuickTimerChip("20m", 1200) { sec ->
                            metronomeController?.setTimerEnabled(true)
                            metronomeController?.setTimerDuration(sec)
                            onTabSelected(MainTab.RHYTHM_TOOL)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFF1E2433), thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // ================= 6. SECTION: STUDIO & JOURNAL =================
            Text(
                text = "STUDIO & PLAY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                    fontSize = 10.sp
                ),
                color = Color(0xFF64748B),
                modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
            )

            MusoraMenuItem(
                title = "Acoustic Drum Kit",
                subtitle = "Playable Maple/Bronze Drum Studio",
                icon = Icons.Default.Album,
                badge = "NATURAL SOUND",
                badgeColor = Color(0xFFFF9800),
                selected = selectedTab == MainTab.DRUM_KIT,
                testTag = "drawer_item_drumkit",
                onClick = { onTabSelected(MainTab.DRUM_KIT) }
            )

            MusoraMenuItem(
                title = "My Progress & Stats",
                subtitle = "Practice Journal & Accuracy History",
                icon = Icons.Default.BarChart,
                badge = "ANALYTICS",
                badgeColor = DrumCyanSecondary,
                selected = selectedTab == MainTab.PROGRESS,
                testTag = "drawer_item_progress",
                onClick = { onTabSelected(MainTab.PROGRESS) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ================= 7. QUICK METRONOME WIDGET =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141926)),
                border = BorderStroke(1.dp, Color(0xFF222B3D))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Metronome",
                            tint = if (metronomeState.isRunning) TimingPerfectGreen else DrumAmberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Quick Metronome",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${metronomeState.bpm} BPM • ${metronomeState.timeSignature.displayName}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF202738))
                                .clickable { metronomeController?.adjustBpm(-5) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-5", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF202738))
                                .clickable { metronomeController?.adjustBpm(5) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+5", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = onToggleMetronome,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (metronomeState.isRunning) TimingMissRed else TimingPerfectGreen)
                        ) {
                            Icon(
                                imageVector = if (metronomeState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Toggle",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ================= 8. SOUND ENGINE STATUS =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F1420))
                    .border(1.dp, Color(0xFF1D2638), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = TimingPerfectGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "NATURAL ACOUSTIC ENGINE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                        color = Color.White
                    )
                    Text(
                        text = "Maple Shells & B20 Bronze • Real-Time PCM",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ================= 9. MUSORA COACH SPOTLIGHT =================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF111520))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = "Quote",
                    tint = DrumAmberPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "\"The foot must become as articulate as your hands. Remove physical blind spots with the 16 letters.\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.5.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 14.sp
                        ),
                        color = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "— Benny Greb (The Language of Drumming)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        color = Color(0xFF4FB3C6)
                    )
                }
            }
        }
    }
}

@Composable
private fun MusoraMenuItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    badgeColor: Color,
    selected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFF1D2333) else Color.Transparent)
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = if (selected) DrumAmberPrimary.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(26.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(DrumAmberPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (selected) DrumAmberPrimary.copy(alpha = 0.2f) else Color(0xFF191F2D)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (selected) DrumAmberPrimary else Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = if (selected) DrumAmberPrimary else Color.White
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .border(0.5.dp, badgeColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun QuickTimerChip(
    label: String,
    seconds: Int,
    onSelect: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E2638))
            .clickable { onSelect(seconds) }
            .padding(horizontal = 9.dp, vertical = 5.dp)
            .testTag("drawer_quick_timer_$label"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
