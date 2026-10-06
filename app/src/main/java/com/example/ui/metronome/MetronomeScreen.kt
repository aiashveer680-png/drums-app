package com.example.ui.metronome

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.MetronomeController
import com.example.audio.MetronomeSubdivision
import com.example.audio.SpeedTrainerConfig
import com.example.audio.TimeSignature
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.TimingMissRed
import com.example.ui.theme.TimingPerfectGreen

@Composable
fun MetronomeScreen(
    controller: MetronomeController,
    modifier: Modifier = Modifier
) {
    val state by controller.state.collectAsStateWithLifecycle()

    val tempoName = remember(state.bpm) {
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pro Metronome",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "High precision click with speed trainer & subdivisions",
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
                    text = if (state.isRunning) "PLAYING" else "IDLE",
                    color = if (state.isRunning) TimingPerfectGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Beat LED Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BAR ${state.currentBar} • BEAT ${state.currentBeat} OF ${state.timeSignature.numerator}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Beat LEDs
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
                            animationSpec = tween(durationMillis = 80),
                            label = "beatLed"
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(if (isAccent) 38.dp else 30.dp)
                                    .clip(CircleShape)
                                    .background(ledColor)
                                    .border(
                                        width = if (isCurrentBeat) 2.dp else 1.dp,
                                        color = if (isCurrentBeat) Color.White else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = beat.toString(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = if (isAccent) 16.sp else 13.sp,
                                    color = if (isCurrentBeat) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isAccent) "ACCENT" else "",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = DrumAmberPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big BPM Hero Display & Steppers
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = tempoName.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Massive BPM Number
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${state.bpm}",
                        fontSize = 68.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 72.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BPM",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // BPM Steppers Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BpmStepButton(label = "-5", onClick = { controller.adjustBpm(-5) })
                    BpmStepButton(label = "-1", onClick = { controller.adjustBpm(-1) })
                    BpmStepButton(label = "+1", onClick = { controller.adjustBpm(1) })
                    BpmStepButton(label = "+5", onClick = { controller.adjustBpm(5) })
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Smooth BPM Slider
                Slider(
                    value = state.bpm.toFloat(),
                    onValueChange = { controller.setBpm(it.toInt()) },
                    valueRange = 30f..260f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("bpm_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tap Tempo & Big Play/Stop Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Tap Tempo Button
            Button(
                onClick = { controller.tapTempo() },
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .testTag("btn_tap_tempo"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Tap Tempo",
                        tint = DrumCyanSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "TAP TEMPO",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tap 4 times",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Play / Stop Hero Button
            Button(
                onClick = { controller.toggle() },
                modifier = Modifier
                    .weight(1.2f)
                    .height(64.dp)
                    .testTag("btn_metronome_toggle"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.isRunning) TimingMissRed else TimingPerfectGreen
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (state.isRunning) "Stop" else "Start",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isRunning) "STOP" else "START CLICK",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time Signature Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "TIME SIGNATURE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimeSignature.standardList.forEach { ts ->
                        val isSelected = (state.timeSignature.displayName == ts.displayName)
                        FilterChip(
                            selected = isSelected,
                            onClick = { controller.setTimeSignature(ts) },
                            label = {
                                Text(
                                    text = ts.displayName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("chip_ts_${ts.numerator}_${ts.denominator}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Subdivisions Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "SUBDIVISION / RHYTHM GRID",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetronomeSubdivision.entries.forEach { sub ->
                        val isSelected = (state.subdivision == sub)
                        FilterChip(
                            selected = isSelected,
                            onClick = { controller.setSubdivision(sub) },
                            label = {
                                Text(
                                    text = "${sub.symbol} ${sub.displayName.split(" ")[0]}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.testTag("chip_sub_${sub.name.lowercase()}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                                text = "Speed Trainer",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Automatically increase BPM every few bars",
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
                        modifier = Modifier.testTag("switch_speed_trainer")
                    )
                }

                if (state.speedTrainer.enabled) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+${state.speedTrainer.bpmIncrement} BPM every ${state.speedTrainer.barsInterval} bars (Target: ${state.speedTrainer.targetBpm})",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = DrumAmberPrimary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val inc = if (state.speedTrainer.bpmIncrement == 2) 5 else 2
                                    controller.setSpeedTrainer(state.speedTrainer.copy(bpmIncrement = inc))
                                },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("+${state.speedTrainer.bpmIncrement} BPM", fontSize = 11.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    val interval = if (state.speedTrainer.barsInterval == 4) 8 else 4
                                    controller.setSpeedTrainer(state.speedTrainer.copy(barsInterval = interval))
                                },
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("${state.speedTrainer.barsInterval} Bars", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Volume Slider
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Volume",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = state.volume,
                    onValueChange = { controller.setVolume(it) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun BpmStepButton(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp, 40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("btn_step_$label"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
