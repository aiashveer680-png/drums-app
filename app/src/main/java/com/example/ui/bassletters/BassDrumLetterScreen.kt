package com.example.ui.bassletters

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.DrumSoundEngine
import com.example.data.BassDrumLetterCatalog
import com.example.model.BassDrumLetter
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.TimingMissRed
import com.example.ui.theme.TimingPerfectGreen

@Composable
fun BassDrumLetterScreen(
    player: BassDrumLetterAudioPlayer,
    modifier: Modifier = Modifier
) {
    val state by player.state.collectAsStateWithLifecycle()
    val letters = remember { BassDrumLetterCatalog.allLetters }

    DisposableEffect(Unit) {
        onDispose {
            player.stop()
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
        Spacer(modifier = Modifier.height(10.dp))

        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Bass Drum Letters",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Page 18 • The Language of Drumming System",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4FB3C6)
                )
            }

            // Quick Status Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF141F28))
                    .border(1.dp, Color(0xFF233644), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Letter ${state.currentLetter.letter}",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = Color(0xFF4FB3C6)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Your hands keep the groove (hi-hat eighths, snare on 2 and 4) while your foot plays one letter on every beat. Mute the kick to play it yourself!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ================= 1. 16-LETTER SELECTOR BAR (A - P) =================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            letters.forEach { letterItem ->
                val isSelected = (state.currentLetter.letter == letterItem.letter)
                LetterPickerCard(
                    letter = letterItem,
                    isSelected = isSelected,
                    onSelect = { player.selectLetter(letterItem) }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ================= 2. ACTIVE LETTER CAPTION & NOTATION =================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Caption
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Letter ${state.currentLetter.letter}",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "•  ${state.currentLetter.activeCountDescription}",
                                fontSize = 13.sp,
                                color = Color(0xFF4FB3C6),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Hi-hat (x) on top, snare in the middle, kick (teal) at the bottom. Say \"chid\" on each beat.",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (state.isPlaying) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(TimingPerfectGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "BAR ${state.currentBar}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TimingPerfectGreen
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Visual Staff Notation
                BassDrumLetterNotationView(
                    letter = state.currentLetter,
                    currentStep = state.currentStep,
                    isPlaying = state.isPlaying
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ================= 3. PLAYBACK CONTROLS & TEMPO =================
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
                // Master Play Button and Tempo Readout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { player.togglePlay() },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .testTag("btn_bass_drum_play"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isPlaying) TimingMissRed else Color(0xFF4FB3C6)
                        )
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (state.isPlaying) "Stop" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.isPlaying) "STOP" else "PLAY GROOVE",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = Color.Black
                        )
                    }

                    // Stepper: -5, BPM, +5
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StepCircleBtn("-5") { player.adjustBpm(-5) }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.bpm}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "BPM",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4FB3C6)
                            )
                        }

                        StepCircleBtn("+5") { player.adjustBpm(5) }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // BPM Slider
                Slider(
                    value = state.bpm.toFloat(),
                    onValueChange = { player.setBpm(it.toInt()) },
                    valueRange = 40f..150f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF4FB3C6),
                        activeTrackColor = Color(0xFF4FB3C6)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("slider_bass_drum_bpm")
                )

                Text(
                    text = "Tempo is locked to the audio clock, so beats stay evenly spaced and never drift.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ================= 4. INDIVIDUAL VOICE MUTES =================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = null,
                        tint = Color(0xFF4FB3C6),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "VOICE MUTES (ISOLATION)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = Color(0xFF4FB3C6)
                    )
                }
                Text(
                    text = "Mute the kick to play it yourself with your foot along with the groove!",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VoiceMuteCheckbox("Hi-hat", state.voiceConfig.playHiHat) { player.toggleHiHat() }
                    VoiceMuteCheckbox("Snare", state.voiceConfig.playSnare) { player.toggleSnare() }
                    VoiceMuteCheckbox("Kick (Letter)", state.voiceConfig.playKick, isHighlight = true) { player.toggleKick() }
                    VoiceMuteCheckbox("Click", state.voiceConfig.playClick) { player.toggleClick() }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ================= 5. AUTO-ADVANCE SETTING =================
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
                        Checkbox(
                            checked = state.autoAdvance.enabled,
                            onCheckedChange = { player.setAutoAdvance(it, state.autoAdvance.barsPerLetter) },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4FB3C6))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Auto-advance to next letter every:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(1, 2, 4, 8).forEach { bars ->
                            val isSelected = (state.autoAdvance.barsPerLetter == bars)
                            FilterChip(
                                selected = isSelected,
                                onClick = { player.setAutoAdvance(state.autoAdvance.enabled, bars) },
                                label = { Text("${bars}b", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF4FB3C6),
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ================= 6. BENNY GREB MASTERCLASS PRACTICE GUIDE =================
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = DrumAmberPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "HOW TO PRACTICE (BENNY GREB SYSTEM)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black, letterSpacing = 1.sp),
                        color = DrumAmberPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Loop the groove with the kick muted until your hands feel 100% automatic, then bring your foot in.\n" +
                            "• Start at 60–70 BPM, say \"chid\" on each beat, and increase by 5 BPM when the letter feels easy.\n" +
                            "• As printed on page 18, Letter A has no kick under the snare on beats 2 and 4; every other letter does.\n" +
                            "• Check: Can you play Letter B (on the 'e') without your right hand flamming or dragging?",
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF141A24))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Coach Tip: \"The foot must become as articulate as your hands. By isolating each 16th-note permutation, you remove physical blind spots forever.\" — Benny Greb",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color(0xFFCBD5E1)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun LetterPickerCard(
    letter: BassDrumLetter,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.onBackground else Color(0xFF172028))
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onBackground else Color(0xFF2B3842),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(vertical = 8.dp)
            .testTag("btn_letter_${letter.letter}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = letter.letter,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            // 4 Dots
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                for (i in 0..3) {
                    val isFilled = letter.hasKickOnSlot(i)
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    if (isFilled) MaterialTheme.colorScheme.background else Color.Transparent
                                } else {
                                    if (isFilled) Color(0xFF4FB3C6) else Color.Transparent
                                }
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.background else (if (isFilled) Color(0xFF4FB3C6) else Color(0xFF586873)),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun StepCircleBtn(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF172028))
            .border(1.dp, Color(0xFF2B3842), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun VoiceMuteCheckbox(
    label: String,
    checked: Boolean,
    isHighlight: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = if (isHighlight) Color(0xFF4FB3C6) else DrumAmberPrimary
            )
        )
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) Color(0xFF4FB3C6) else MaterialTheme.colorScheme.onSurface
        )
    }
}
