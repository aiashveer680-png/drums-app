package com.example.ui.rudiments

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Rudiment
import com.example.model.StickingHand
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.TimingMissRed
import com.example.ui.theme.TimingPerfectGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RudimentDetailScreen(
    rudiment: Rudiment,
    player: RudimentAudioPlayer,
    onBack: () -> Unit,
    onLaunchPracticeExercise: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsStateWithLifecycle()

    LaunchedEffect(rudiment.id) {
        player.loadRudiment(rudiment)
    }

    DisposableEffect(Unit) {
        onDispose {
            player.stop()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            player.stop()
                            onBack()
                        },
                        modifier = Modifier.testTag("btn_rudiment_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                title = {
                    Column {
                        Text(
                            text = rudiment.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${rudiment.category.displayName} • ${rudiment.timeSignatureString}",
                            style = MaterialTheme.typography.bodySmall,
                            color = DrumAmberPrimary
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DrumAmberPrimary.copy(alpha = 0.2f))
                            .border(1.dp, DrumAmberPrimary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = rudiment.difficulty.displayName.uppercase(),
                            color = DrumAmberPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // ================= 1. VISUAL NOTATION SHEET =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = DrumCyanSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "STANDARD DRUM NOTATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = DrumCyanSecondary
                    )
                }

                if (state.isPlaying) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TimingPerfectGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PLAYING AUDIO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TimingPerfectGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sheet Music Visualizer
            RudimentNotationView(
                rudiment = rudiment,
                activeNoteIndex = state.currentNoteIndex,
                isPlaying = state.isPlaying
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Sticking sequence overview chips
            StickingSequenceBar(stickingSequence = rudiment.stickingSequence)

            Spacer(modifier = Modifier.height(16.dp))

            // ================= 2. AUDIO PLAYBACK & TEMPO CONTROLS =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // BPM readout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PRACTICE TEMPO",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${state.bpm}",
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "BPM",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DrumAmberPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }

                        // Loop count badge
                        if (state.isPlaying) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF141A28))
                                    .border(1.dp, Color(0xFF222C3E), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Loop #${state.currentLoopCount + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DrumCyanSecondary
                                )
                            }
                        }
                    }

                    // Stepper buttons (-10, -5, -1, +1, +5, +10)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StepButton("-10") { player.adjustBpm(-10) }
                        StepButton("-5") { player.adjustBpm(-5) }
                        StepButton("-1") { player.adjustBpm(-1) }
                        StepButton("+1") { player.adjustBpm(1) }
                        StepButton("+5") { player.adjustBpm(5) }
                        StepButton("+10") { player.adjustBpm(10) }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Slider(
                        value = state.bpm.toFloat(),
                        onValueChange = { player.setBpm(it.toInt()) },
                        valueRange = 40f..220f,
                        colors = SliderDefaults.colors(
                            thumbColor = DrumAmberPrimary,
                            activeTrackColor = DrumAmberPrimary
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("rudiment_bpm_slider")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary Toggles: Metronome Click & Loop Mode
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = state.metronomeClickEnabled,
                            onClick = { player.toggleMetronomeClick() },
                            leadingIcon = {
                                Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            label = { Text("Metronome Click", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DrumAmberPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = DrumAmberPrimary
                            ),
                            modifier = Modifier.testTag("chip_metronome_click")
                        )

                        FilterChip(
                            selected = state.loopModeEnabled,
                            onClick = { player.toggleLoopMode() },
                            leadingIcon = {
                                Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            label = { Text("Continuous Loop", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DrumCyanSecondary.copy(alpha = 0.2f),
                                selectedLabelColor = DrumCyanSecondary
                            ),
                            modifier = Modifier.testTag("chip_loop_mode")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Master Play/Stop Button
                    Button(
                        onClick = { player.togglePlay() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("btn_rudiment_play_toggle"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isPlaying) TimingMissRed else TimingPerfectGreen
                        )
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (state.isPlaying) "Stop" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (state.isPlaying) "STOP PLAYBACK" else "LISTEN & PLAY ALONG",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ================= 3. SPEED TRAINER & PRACTICE TIMER =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Speed Trainer Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = DrumAmberPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Speed Trainer Ramp",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Increases +2 BPM every 4 loops",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = state.speedTrainerEnabled,
                            onCheckedChange = { player.toggleSpeedTrainer() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DrumAmberPrimary,
                                checkedTrackColor = DrumAmberPrimary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("switch_speed_trainer")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF222938), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    // 2-Minute Speed Drill Timer Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = DrumCyanSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "2-Min Daily Pad Routine",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = if (state.timerEnabled) "Remaining: ${state.formattedTimer}" else "Timed speed endurance drill",
                                    fontSize = 11.sp,
                                    color = if (state.timerEnabled) TimingPerfectGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = state.timerEnabled,
                            onCheckedChange = { isChecked -> player.setTimerEnabled(isChecked, 120) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DrumCyanSecondary,
                                checkedTrackColor = DrumCyanSecondary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("switch_pad_timer")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ================= 4. TECHNICAL BREAKDOWN & GRIP GUIDE =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = DrumAmberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TECHNICAL BREAKDOWN & TECHNIQUE",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                            color = DrumAmberPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = rudiment.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF131826))
                            .border(1.dp, Color(0xFF222B3E), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "COACH'S MECHANICS TIP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DrumCyanSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = rudiment.technicalBreakdown,
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ================= 5. MUSICAL APPLICATION & FAMOUS SONGS =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = null,
                            tint = DrumCyanSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MUSICAL APPLICATION AROUND THE KIT",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                            color = DrumCyanSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = rudiment.musicalApplication,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF121622))
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = DrumAmberPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Iconic Recording Application:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = DrumAmberPrimary
                            )
                            Text(
                                text = rudiment.famousExample,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ================= 6. TEMPO TARGETS & MASTERY =================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "TEMPO MASTERY TARGETS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TempoTargetPill("Bronze", "${rudiment.tempoTarget.bronzeBpm} BPM", Color(0xFFCD7F32), state.bpm >= rudiment.tempoTarget.bronzeBpm)
                        TempoTargetPill("Silver", "${rudiment.tempoTarget.silverBpm} BPM", Color(0xFFC0C0C0), state.bpm >= rudiment.tempoTarget.silverBpm)
                        TempoTargetPill("Gold", "${rudiment.tempoTarget.goldBpm} BPM", DrumAmberPrimary, state.bpm >= rudiment.tempoTarget.goldBpm)
                        TempoTargetPill("Platinum", "${rudiment.tempoTarget.platinumBpm} BPM", Color(0xFFE5E4E2), state.bpm >= rudiment.tempoTarget.platinumBpm)
                    }
                }
            }

            // Launch Practice Exercise if applicable
            if (rudiment.relatedExerciseId != null && onLaunchPracticeExercise != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        player.stop()
                        onLaunchPracticeExercise(rudiment.relatedExerciseId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_launch_rudiment_interactive"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232D42))
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = DrumAmberPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TEST ACCURACY IN INTERACTIVE WORKOUT",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun StickingSequenceBar(stickingSequence: String) {
    val items = stickingSequence.split("\\s+".toRegex()).filter { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF101420))
            .border(1.dp, Color(0xFF1E283C), RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "STICKING PATTERN",
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = Color(0xFF94A3B8)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { hand ->
                val isRight = hand.contains("R", ignoreCase = true)
                val isLeft = hand.contains("L", ignoreCase = true)
                val badgeColor = when {
                    isRight && !isLeft -> DrumAmberPrimary
                    isLeft && !isRight -> DrumCyanSecondary
                    else -> Color.White
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = hand,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = badgeColor
                    )
                }
            }
        }
    }
}

@Composable
private fun StepButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1B2232))
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 6.dp)
            .testTag("rudiment_step_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun TempoTargetPill(label: String, bpmText: String, color: Color, isAchieved: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isAchieved) color.copy(alpha = 0.25f) else Color(0xFF181F2E))
                .border(
                    width = if (isAchieved) 2.dp else 1.dp,
                    color = if (isAchieved) color else Color(0xFF28344A),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isAchieved) {
                Icon(Icons.Default.Check, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            } else {
                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = bpmText, fontSize = 9.sp, color = Color(0xFF94A3B8))
    }
}
