package com.example.ui.drumkit

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.DrumInstrument
import com.example.audio.DrumSoundEngine
import com.example.audio.MetronomeController
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.InstrumentCrashColor
import com.example.ui.theme.InstrumentHiHatColor
import com.example.ui.theme.InstrumentKickColor
import com.example.ui.theme.InstrumentRideColor
import com.example.ui.theme.InstrumentSnareColor
import com.example.ui.theme.InstrumentTomFloorColor
import com.example.ui.theme.InstrumentTomHighColor
import com.example.ui.theme.TimingMissRed
import com.example.ui.theme.TimingPerfectGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class RecordedHit(
    val instrument: DrumInstrument,
    val timestampMs: Long
)

@Composable
fun DrumKitScreen(
    soundEngine: DrumSoundEngine,
    metronomeController: MetronomeController,
    modifier: Modifier = Modifier
) {
    val metronomeState by metronomeController.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    var isRecording by remember { mutableStateOf(false) }
    var isPlayingBack by remember { mutableStateOf(false) }
    val recordedHits = remember { mutableStateListOf<RecordedHit>() }
    var recordStartTime by remember { mutableStateOf(0L) }

    fun handlePadHit(instrument: DrumInstrument) {
        soundEngine.play(instrument)
        if (isRecording) {
            val offset = System.currentTimeMillis() - recordStartTime
            recordedHits.add(RecordedHit(instrument, offset))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header with Metronome Companion Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Acoustic Drum Kit",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Natural acoustic maple shells, coated heads & B20 bronze cymbals",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Quick Metronome Toggle
            Button(
                onClick = { metronomeController.toggle() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (metronomeState.isRunning) TimingMissRed else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_kit_metronome")
            ) {
                Icon(
                    imageVector = if (metronomeState.isRunning) Icons.Default.Pause else Icons.Default.Speed,
                    contentDescription = "Metronome",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (metronomeState.isRunning) "CLICK ON" else "${metronomeState.bpm} BPM",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Groove Recorder Toolbar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isRecording) "RECORDING..." else if (isPlayingBack) "PLAYING BACK..." else "GROOVE RECORDER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isRecording) TimingMissRed else if (isPlayingBack) DrumCyanSecondary else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${recordedHits.size} hits)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Record button
                    Button(
                        onClick = {
                            if (isRecording) {
                                isRecording = false
                            } else {
                                recordedHits.clear()
                                recordStartTime = System.currentTimeMillis()
                                isRecording = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) TimingMissRed else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_record_groove")
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                            contentDescription = "Record",
                            tint = if (isRecording) Color.White else TimingMissRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRecording) "Stop" else "Rec",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRecording) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Playback button
                    Button(
                        onClick = {
                            if (recordedHits.isEmpty() || isPlayingBack) return@Button
                            isPlayingBack = true
                            scope.launch {
                                for (i in recordedHits.indices) {
                                    val hit = recordedHits[i]
                                    val prevTime = if (i == 0) 0L else recordedHits[i - 1].timestampMs
                                    val delayMs = (hit.timestampMs - prevTime).coerceIn(0L, 2000L)
                                    delay(delayMs)
                                    soundEngine.play(hit.instrument)
                                }
                                isPlayingBack = false
                            }
                        },
                        enabled = recordedHits.isNotEmpty() && !isPlayingBack && !isRecording,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_playback_groove")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Play", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ================= DRUM KIT VISUAL PADS =================
        // Row 1: Cymbals (Crash, Closed Hi-Hat, Open Hi-Hat, Ride)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KitCymbalPad(
                name = "CRASH",
                color = InstrumentCrashColor,
                testTag = "kit_crash",
                modifier = Modifier.weight(1f),
                onClick = { handlePadHit(DrumInstrument.CRASH) }
            )
            KitCymbalPad(
                name = "HH CLOSED",
                color = InstrumentHiHatColor,
                testTag = "kit_hihat_closed",
                modifier = Modifier.weight(1f),
                onClick = { handlePadHit(DrumInstrument.HIHAT_CLOSED) }
            )
            KitCymbalPad(
                name = "HH OPEN",
                color = DrumAmberPrimary,
                testTag = "kit_hihat_open",
                modifier = Modifier.weight(1f),
                onClick = { handlePadHit(DrumInstrument.HIHAT_OPEN) }
            )
            KitCymbalPad(
                name = "RIDE",
                color = InstrumentRideColor,
                testTag = "kit_ride",
                modifier = Modifier.weight(1f),
                onClick = { handlePadHit(DrumInstrument.RIDE) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: High Tom & Floor Tom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KitDrumPad(
                name = "HIGH TOM",
                sub = "10\" Birch Tom (T1)",
                color = InstrumentTomHighColor,
                testTag = "kit_tom_high",
                modifier = Modifier.weight(1f),
                sizeHeight = 90.dp,
                onClick = { handlePadHit(DrumInstrument.TOM_HIGH) }
            )
            KitDrumPad(
                name = "FLOOR TOM",
                sub = "16\" Deep Floor Tom",
                color = InstrumentTomFloorColor,
                testTag = "kit_tom_floor",
                modifier = Modifier.weight(1f),
                sizeHeight = 90.dp,
                onClick = { handlePadHit(DrumInstrument.TOM_FLOOR) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 3: Snare Drum & Bass Drum (Hero Centerpieces)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KitDrumPad(
                name = "SNARE DRUM",
                sub = "14\" Coated Maple Snare",
                color = InstrumentSnareColor,
                testTag = "kit_snare",
                modifier = Modifier.weight(1f),
                sizeHeight = 110.dp,
                isHero = true,
                onClick = { handlePadHit(DrumInstrument.SNARE) }
            )
            KitDrumPad(
                name = "BASS DRUM",
                sub = "22\" Acoustic Kick",
                color = InstrumentKickColor,
                testTag = "kit_kick",
                modifier = Modifier.weight(1f),
                sizeHeight = 110.dp,
                isHero = true,
                onClick = { handlePadHit(DrumInstrument.KICK) }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
fun KitCymbalPad(
    name: String,
    color: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .height(72.dp)
            .scale(scale.value)
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(color.copy(alpha = 0.28f), color.copy(alpha = 0.12f))
                )
            )
            .border(2.dp, color.copy(alpha = 0.75f), RoundedCornerShape(14.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                scope.launch {
                    scale.animateTo(0.93f, tween(30, easing = FastOutSlowInEasing))
                    scale.animateTo(1f, tween(90, easing = FastOutSlowInEasing))
                }
                onClick()
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = name,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun KitDrumPad(
    name: String,
    sub: String,
    color: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    sizeHeight: androidx.compose.ui.unit.Dp = 95.dp,
    isHero: Boolean = false,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .height(sizeHeight)
            .scale(scale.value)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.radialGradient(
                    listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.12f))
                )
            )
            .border(
                width = if (isHero) 2.5.dp else 1.8.dp,
                color = color.copy(alpha = 0.9f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                scope.launch {
                    scale.animateTo(0.92f, tween(30, easing = FastOutSlowInEasing))
                    scale.animateTo(1f, tween(100, easing = FastOutSlowInEasing))
                }
                onClick()
            }
            .testTag(testTag)
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = if (isHero) 17.sp else 14.sp
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = color
            )
        }
    }
}
