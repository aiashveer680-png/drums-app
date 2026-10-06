package com.example.ui.rhythm

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.MetronomeController
import com.example.audio.MetronomeSubdivision
import com.example.audio.PresetRhythm
import com.example.audio.PresetRhythmCatalog
import com.example.audio.StemMixer
import com.example.audio.TimeSignature
import com.example.data.DrumRepository
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.TimingMissRed
import com.example.ui.theme.TimingPerfectGreen
import kotlinx.coroutines.launch

@Composable
fun RhythmPracticeToolScreen(
    controller: MetronomeController,
    repository: DrumRepository? = null,
    modifier: Modifier = Modifier
) {
    val state by controller.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var selectedGenre by remember { mutableStateOf("All") }

    val genres = remember {
        listOf("All", "Rock / Pop", "Dance / Pop", "Funk / R&B", "Jazz", "Blues", "Latin / World", "Odd Time / Prog", "Rudiments")
    }

    val filteredRhythms = remember(selectedGenre) {
        if (selectedGenre == "All") {
            PresetRhythmCatalog.allRhythms
        } else {
            PresetRhythmCatalog.allRhythms.filter { it.genre.contains(selectedGenre, ignoreCase = true) }
        }
    }

    val tempoTerm = remember(state.bpm) {
        when {
            state.bpm < 60 -> "Largo"
            state.bpm < 76 -> "Adagio"
            state.bpm < 108 -> "Andante"
            state.bpm < 120 -> "Moderato"
            state.bpm < 168 -> "Allegro"
            state.bpm < 200 -> "Presto"
            else -> "Prestissimo"
        }
    }

    // Celebration dialog when countdown timer finishes
    if (state.countdownTimer.isCompleted) {
        PracticeTimerCompletedDialog(
            durationSeconds = state.countdownTimer.durationSeconds,
            rhythmName = state.selectedPresetRhythm?.name ?: "Metronome Practice",
            bpm = state.bpm,
            onSave = {
                scope.launch {
                    repository?.recordRhythmPracticeSession(
                        rhythmName = state.selectedPresetRhythm?.name ?: "Metronome Click Practice",
                        bpm = state.bpm,
                        durationSeconds = state.countdownTimer.durationSeconds
                    )
                }
                controller.dismissTimerCompletedDialog()
            },
            onDismiss = {
                controller.dismissTimerCompletedDialog()
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Tool Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Rhythm Practice Tool",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Groove Metronome • Multi-Meter • Practice Timer",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (state.isRunning) TimingPerfectGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        1.dp,
                        if (state.isRunning) TimingPerfectGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (state.isRunning) "ACTIVE" else "READY",
                    color = if (state.isRunning) TimingPerfectGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Real-time Audio Waveform & Beat Pulse Monitor
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "Waveform",
                            tint = DrumCyanSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.selectedPresetRhythm != null)
                                "PLAYING: ${state.selectedPresetRhythm?.name?.uppercase()}"
                            else
                                "NATURAL ACOUSTIC CLICK",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = DrumCyanSecondary
                        )
                    }

                    Text(
                        text = "BAR ${state.currentBar}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Real-time Waveform Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF090B10))
                        .border(1.dp, Color(0xFF1B2232), RoundedCornerShape(10.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val centerY = h / 2f

                        for (i in 1..3) {
                            val lineY = (h / 4f) * i
                            drawLine(
                                color = Color(0xFF141A28),
                                start = Offset(0f, lineY),
                                end = Offset(w, lineY),
                                strokeWidth = 1f
                            )
                        }

                        val path = Path()
                        val points = state.waveformPoints
                        val dx = w / (points.size - 1).coerceAtLeast(1)

                        points.forEachIndexed { i, amp ->
                            val x = i * dx
                            val waveHeight = if (state.isRunning) (amp * h * 0.42f) else (h * 0.05f)
                            val y = centerY + (if (i % 2 == 0) waveHeight else -waveHeight)
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }

                        drawPath(
                            path = path,
                            brush = Brush.horizontalGradient(listOf(DrumAmberPrimary, DrumCyanSecondary, TimingPerfectGreen)),
                            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        drawLine(
                            color = Color(0xFF1E283C),
                            start = Offset(0f, centerY),
                            end = Offset(w, centerY),
                            strokeWidth = 1f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Measure Beat LEDs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val numerator = state.timeSignature.numerator
                    for (beat in 1..numerator) {
                        val isCurrentBeat = state.isRunning && (state.currentBeat == beat)
                        val isAccent = (beat == 1)
                        val activeColor = if (isAccent) DrumAmberPrimary else DrumCyanSecondary

                        val ledColor by animateColorAsState(
                            targetValue = if (isCurrentBeat) activeColor else MaterialTheme.colorScheme.surfaceVariant,
                            animationSpec = tween(durationMillis = 70),
                            label = "beatLedRhythm"
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(if (isAccent) 34.dp else 28.dp)
                                    .clip(CircleShape)
                                    .background(ledColor)
                                    .border(
                                        width = if (isCurrentBeat) 2.dp else 1.dp,
                                        color = if (isCurrentBeat) Color.White else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = beat.toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = if (isAccent) 14.sp else 12.sp,
                                    color = if (isCurrentBeat) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero BPM & Playback Control Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tempoTerm.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary
                )

                // Large BPM value
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${state.bpm}",
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 62.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BPM",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Precision BPM Steppers (-10, -5, -1, +1, +5, +10)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RhythmStepButton("-10") { controller.adjustBpm(-10) }
                    RhythmStepButton("-5") { controller.adjustBpm(-5) }
                    RhythmStepButton("-1") { controller.adjustBpm(-1) }
                    RhythmStepButton("+1") { controller.adjustBpm(1) }
                    RhythmStepButton("+5") { controller.adjustBpm(5) }
                    RhythmStepButton("+10") { controller.adjustBpm(10) }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Half-Time & Double-Time Quick Modifiers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { controller.halfTime() },
                        modifier = Modifier.testTag("btn_half_time")
                    ) {
                        Text("½ Half-Time (${state.bpm / 2} BPM)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(
                        onClick = { controller.doubleTime() },
                        modifier = Modifier.testTag("btn_double_time")
                    ) {
                        Text("2× Double-Time (${state.bpm * 2} BPM)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // BPM Slider
                Slider(
                    value = state.bpm.toFloat(),
                    onValueChange = { controller.setBpm(it.toInt()) },
                    valueRange = 30f..260f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("rhythm_bpm_slider")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Tap Tempo & Master START / STOP Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { controller.tapTempo() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .testTag("btn_rhythm_tap_tempo"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = "Tap Tempo",
                            tint = DrumCyanSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TAP TEMPO", fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Button(
                        onClick = { controller.toggle() },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(56.dp)
                            .testTag("btn_rhythm_play_toggle"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isRunning) TimingMissRed else TimingPerfectGreen
                        )
                    ) {
                        Icon(
                            imageVector = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (state.isRunning) "Stop" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.isRunning) "STOP GROOVE" else "PLAY RHYTHM",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ================= COUNTDOWN TIMER SECTION =================
        val timer = state.countdownTimer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_countdown_timer"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
                width = if (timer.enabled) 1.5.dp else 1.dp,
                color = if (timer.enabled) DrumAmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Countdown Timer",
                            tint = if (timer.enabled) DrumAmberPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Practice Countdown Timer",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (timer.enabled)
                                    "Session duration: ${timer.formattedTotal} mins"
                                else
                                    "Set a specific duration for this session",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = timer.enabled,
                        onCheckedChange = { isChecked -> controller.setTimerEnabled(isChecked) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DrumAmberPrimary,
                            checkedTrackColor = DrumAmberPrimary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("switch_countdown_timer")
                    )
                }

                if (timer.enabled) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Prominent Live Countdown Timer HUD
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0F1420))
                            .border(1.dp, DrumAmberPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (state.isRunning) "TIME REMAINING" else "SESSION TARGET",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        fontSize = 10.sp
                                    ),
                                    color = if (state.isRunning) TimingPerfectGreen else DrumAmberPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = timer.formattedRemaining,
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 2.sp
                                )
                            }

                            // Circular Progress Ring
                            Box(contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    progress = { timer.progress },
                                    modifier = Modifier.size(54.dp),
                                    color = DrumAmberPrimary,
                                    trackColor = Color(0xFF222C3E),
                                    strokeWidth = 5.dp
                                )
                                Icon(
                                    imageVector = Icons.Default.HourglassBottom,
                                    contentDescription = null,
                                    tint = DrumAmberPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Preset Buttons: 1m, 3m, 5m, 10m, 15m, 20m, 30m
                    Text(
                        text = "QUICK DURATION PRESETS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 9.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val presets = listOf(
                        60 to "1m",
                        180 to "3m",
                        300 to "5m",
                        600 to "10m",
                        900 to "15m",
                        1200 to "20m",
                        1800 to "30m"
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(presets) { (sec, label) ->
                            val isSelected = (timer.durationSeconds == sec)
                            FilterChip(
                                selected = isSelected,
                                onClick = { controller.setTimerDuration(sec) },
                                label = { Text(label, fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DrumAmberPrimary,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.testTag("preset_timer_$label")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Stepper (-1m, +1m) and Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { controller.adjustTimerDuration(-60) },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_timer_minus_1m")
                            ) {
                                Text("-1 Min", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            OutlinedButton(
                                onClick = { controller.adjustTimerDuration(60) },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_timer_plus_1m")
                            ) {
                                Text("+1 Min", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        OutlinedButton(
                            onClick = { controller.resetTimer() },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_timer_reset")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Time Signature Selector (Supports 4/4, 3/4, 2/4, 5/4, 6/8, 7/8, 12/8)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TIME SIGNATURE / METER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Active: ${state.timeSignature.displayName}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TimeSignature.standardList) { ts ->
                        val isSelected = (state.timeSignature.displayName == ts.displayName)
                        FilterChip(
                            selected = isSelected,
                            onClick = { controller.setTimeSignature(ts) },
                            label = {
                                Text(
                                    text = ts.displayName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("ts_chip_${ts.numerator}_${ts.denominator}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pre-Set Drum Rhythms Library
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = "Presets",
                            tint = DrumAmberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PRE-SET DRUM RHYTHMS",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                            color = DrumAmberPrimary
                        )
                    }

                    if (state.selectedPresetRhythm != null) {
                        OutlinedButton(
                            onClick = { controller.selectPresetRhythm(null) },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Reset To Click", fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Genre Filter Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(genres) { g ->
                        FilterChip(
                            selected = selectedGenre == g,
                            onClick = { selectedGenre = g },
                            label = { Text(g, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredRhythms.forEach { rhythm ->
                        val isSelected = (state.selectedPresetRhythm?.id == rhythm.id)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { controller.selectPresetRhythm(rhythm) }
                                .testTag("rhythm_card_${rhythm.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.background
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) DrumAmberPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = rhythm.name,
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                            color = if (isSelected) DrumAmberPrimary else MaterialTheme.colorScheme.onSurface
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(DrumCyanSecondary.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = rhythm.timeSignature.displayName,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DrumCyanSecondary
                                            )
                                        }

                                        Text(
                                            text = "${rhythm.defaultBpm} BPM",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = rhythm.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { controller.selectPresetRhythm(rhythm) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) DrumAmberPrimary else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isSelected) "Active" else "Load",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stem Mixer (Kick, Snare, Cymbals, Click Isolation)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Mixer",
                        tint = DrumCyanSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DRUM STEM MIXER & ISOLATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = DrumCyanSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val mixer = state.stemMixer

                StemVolumeRow(
                    label = "Kick Drum",
                    volume = mixer.kickVolume,
                    color = Color(0xFFFF6D00),
                    onVolumeChange = { controller.setStemMixer(mixer.copy(kickVolume = it)) }
                )
                StemVolumeRow(
                    label = "Snare Drum",
                    volume = mixer.snareVolume,
                    color = Color(0xFF00E5FF),
                    onVolumeChange = { controller.setStemMixer(mixer.copy(snareVolume = it)) }
                )
                StemVolumeRow(
                    label = "Hi-Hat & Cymbals",
                    volume = mixer.cymbalsVolume,
                    color = Color(0xFFFFD600),
                    onVolumeChange = { controller.setStemMixer(mixer.copy(cymbalsVolume = it)) }
                )
                StemVolumeRow(
                    label = "Metronome Click",
                    volume = mixer.clickVolume,
                    color = DrumAmberPrimary,
                    onVolumeChange = { controller.setStemMixer(mixer.copy(clickVolume = it)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Speed Trainer Mode
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Speed Trainer",
                            tint = DrumAmberPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Auto Speed Trainer",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Build drumming endurance and tempo speed",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = state.speedTrainer.enabled,
                        onCheckedChange = { isChecked ->
                            controller.setSpeedTrainer(state.speedTrainer.copy(enabled = isChecked))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DrumAmberPrimary,
                            checkedTrackColor = DrumAmberPrimary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.testTag("switch_rhythm_speed_trainer")
                    )
                }

                if (state.speedTrainer.enabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Accelerates by +${state.speedTrainer.bpmIncrement} BPM every ${state.speedTrainer.barsInterval} bars up to ${state.speedTrainer.targetBpm} BPM",
                        style = MaterialTheme.typography.bodySmall,
                        color = DrumAmberPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun PracticeTimerCompletedDialog(
    durationSeconds: Int,
    rhythmName: String,
    bpm: Int,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val mins = durationSeconds / 60
    val secs = durationSeconds % 60
    val timeLabel = if (mins > 0) "${mins}m ${if (secs > 0) "${secs}s" else ""}" else "${secs}s"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = DrumAmberPrimary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "PRACTICE TARGET REACHED!",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = DrumAmberPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Awesome work! You locked into the groove and completed your planned practice duration.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Duration:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(timeLabel, fontWeight = FontWeight.Bold, color = TimingPerfectGreen)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Pattern:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(rhythmName, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tempo:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$bpm BPM", fontWeight = FontWeight.Bold, color = DrumCyanSecondary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("XP Earned:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("+50 XP", fontWeight = FontWeight.Black, color = DrumAmberPrimary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("btn_save_timer_session")
            ) {
                Text("Log to Practice Stats", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Done")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun RhythmStepButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(46.dp, 36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .testTag("btn_rhythm_step_$label"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StemVolumeRow(
    label: String,
    volume: Float,
    color: Color,
    onVolumeChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(110.dp)
        )

        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (volume < 0.05f) "MUTE" else "${(volume * 100).toInt()}%",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (volume < 0.05f) TimingMissRed else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(36.dp)
        )
    }
}
