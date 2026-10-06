package com.example.ui.exercise

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.DrumInstrument
import com.example.model.Exercise
import com.example.model.ScheduledHit
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.InstrumentCrashColor
import com.example.ui.theme.InstrumentHiHatColor
import com.example.ui.theme.InstrumentKickColor
import com.example.ui.theme.InstrumentRideColor
import com.example.ui.theme.InstrumentSnareColor
import com.example.ui.theme.InstrumentTomFloorColor
import com.example.ui.theme.InstrumentTomHighColor
import com.example.ui.theme.TimingPerfectGreen
import kotlin.math.sin

/**
 * Real-time scrolling note visualizer (rhythm highway) paired with
 * a synchronized reactive audio waveform oscilloscope.
 */
@Composable
fun ScrollingNoteVisualizer(
    exercise: Exercise,
    currentBar: Int,
    currentStep: Int,
    bpm: Int,
    isPlaying: Boolean,
    livePeak: Float,
    modifier: Modifier = Modifier
) {
    // Total steps across all bars in this exercise loop
    val totalStepsInLoop = exercise.totalBars * exercise.stepsPerBar

    // Fractional progress animation for ultra-smooth 60fps scrolling
    val infiniteTransition = rememberInfiniteTransition(label = "waveOscillation")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    // Smooth hit flash target
    val targetPulse = remember { Animatable(0f) }
    LaunchedEffect(currentStep) {
        if (isPlaying) {
            targetPulse.snapTo(1f)
            targetPulse.animateTo(0f, tween(120))
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: Status, Current Beat, and Speed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) TimingPerfectGreen else DrumAmberPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "SCROLLING HIGHWAY • SYNCED" else "SCROLLING HIGHWAY (PAUSED)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = if (isPlaying) TimingPerfectGreen else DrumAmberPrimary
                    )
                }

                Text(
                    text = "${bpm} BPM • ${exercise.timeSignature.displayName}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Note-Scrolling Highway Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF090B10))
                    .border(1.dp, Color(0xFF1E2433), RoundedCornerShape(12.dp))
                    .testTag("scrolling_note_highway")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    // Strike zone (target line) X position
                    val strikeLineX = 72.dp.toPx()

                    // Lane configuration
                    // Lanes: 0: Cymbals, 1: Hi-Hat, 2: Toms, 3: Snare, 4: Kick
                    val numLanes = 5
                    val laneHeight = canvasHeight / numLanes

                    // 1. Draw lane horizontal guide tracks
                    for (i in 0 until numLanes) {
                        val y = i * laneHeight
                        drawLine(
                            color = Color(0xFF191F2E),
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = 1f
                        )
                    }

                    // 2. Draw measure bar vertical lines scrolling by
                    // Calculate pixels per step: we show roughly 1.5 bars ahead
                    val pixelsPerStep = (canvasWidth - strikeLineX) / 16f
                    val currentAbsoluteStep = (currentBar * exercise.stepsPerBar) + currentStep

                    for (bar in 0 until exercise.totalBars * 3) {
                        val barStep = bar * exercise.stepsPerBar
                        val deltaSteps = barStep - currentAbsoluteStep
                        // Loop wrapping logic
                        val wrappedDelta = ((deltaSteps % totalStepsInLoop) + totalStepsInLoop) % totalStepsInLoop
                        val barX = strikeLineX + (wrappedDelta * pixelsPerStep)

                        if (barX in 0f..canvasWidth) {
                            drawLine(
                                color = Color(0xFF2C374E),
                                start = Offset(barX, 0f),
                                end = Offset(barX, canvasHeight),
                                strokeWidth = 2f
                            )
                        }
                    }

                    // 3. Draw Strike Zone Beam (Neon Glow)
                    val glowAlpha = 0.4f + (targetPulse.value * 0.6f)
                    drawLine(
                        color = DrumCyanSecondary.copy(alpha = glowAlpha),
                        start = Offset(strikeLineX, 0f),
                        end = Offset(strikeLineX, canvasHeight),
                        strokeWidth = 3f + (targetPulse.value * 4f),
                        cap = StrokeCap.Round
                    )
                    // Strike zone pulse circle
                    drawCircle(
                        color = DrumCyanSecondary.copy(alpha = targetPulse.value * 0.7f),
                        radius = 18.dp.toPx() * (1f + targetPulse.value * 0.5f),
                        center = Offset(strikeLineX, canvasHeight / 2)
                    )

                    // 4. Draw Incoming Note Capsules
                    for (hit in exercise.hits) {
                        val hitAbsoluteStep = (hit.bar * exercise.stepsPerBar) + hit.step
                        var stepDiff = hitAbsoluteStep - currentAbsoluteStep
                        if (stepDiff < -2) {
                            stepDiff += totalStepsInLoop
                        }

                        val noteX = strikeLineX + (stepDiff * pixelsPerStep)

                        // Render only visible notes
                        if (noteX in -30f..(canvasWidth + 40f)) {
                            val laneIndex = when (hit.instrument) {
                                DrumInstrument.CRASH, DrumInstrument.RIDE -> 0
                                DrumInstrument.HIHAT_CLOSED, DrumInstrument.HIHAT_OPEN -> 1
                                DrumInstrument.TOM_HIGH, DrumInstrument.TOM_FLOOR -> 2
                                DrumInstrument.SNARE -> 3
                                DrumInstrument.KICK -> 4
                                else -> 1
                            }

                            val noteCenterY = (laneIndex * laneHeight) + (laneHeight / 2)
                            val noteColor = when (hit.instrument) {
                                DrumInstrument.KICK -> InstrumentKickColor
                                DrumInstrument.SNARE -> InstrumentSnareColor
                                DrumInstrument.HIHAT_CLOSED, DrumInstrument.HIHAT_OPEN -> InstrumentHiHatColor
                                DrumInstrument.TOM_HIGH -> InstrumentTomHighColor
                                DrumInstrument.TOM_FLOOR -> InstrumentTomFloorColor
                                DrumInstrument.CRASH -> InstrumentCrashColor
                                DrumInstrument.RIDE -> InstrumentRideColor
                                else -> DrumAmberPrimary
                            }

                            val noteRadius = if (hit.isAccented) 12.dp.toPx() else 9.dp.toPx()

                            // Outer accent ring if accented
                            if (hit.isAccented) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.85f),
                                    radius = noteRadius + 3.dp.toPx(),
                                    center = Offset(noteX, noteCenterY),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }

                            // Solid note body
                            drawCircle(
                                color = noteColor,
                                radius = noteRadius,
                                center = Offset(noteX, noteCenterY)
                            )

                            // Inner sticking / dot
                            drawCircle(
                                color = Color.White,
                                radius = noteRadius * 0.35f,
                                center = Offset(noteX, noteCenterY)
                            )
                        }
                    }
                }

                // Left-side lane labels overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 6.dp)
                        .width(62.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    val labels = listOf("CYMBAL", "HI-HAT", "TOMS", "SNARE", "KICK")
                    labels.forEach { lbl ->
                        Text(
                            text = lbl,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF6B7A99),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-time Audio Waveform Oscilloscope
            Text(
                text = "REAL-TIME RHYTHM WAVEFORM",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 9.sp),
                color = DrumCyanSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0C0E14))
                    .border(1.dp, Color(0xFF1E2433), RoundedCornerShape(10.dp))
                    .testTag("reactive_audio_waveform")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val centerY = h / 2f

                    // Base amplitude responds to live peak energy (e.g. hits)
                    val energy = if (isPlaying) (0.25f + livePeak * 0.75f) else 0.1f
                    val waveHeight = (h * 0.42f) * energy

                    val path = Path()
                    val pathGlow = Path()
                    val points = 48
                    val dx = w / points

                    for (i in 0..points) {
                        val x = i * dx
                        val normX = (i.toFloat() / points) * 12.566f // 4 * PI
                        // Combined harmonic waves with time phase
                        val yOffset = (sin(normX + wavePhase) * 0.65f + sin(normX * 2f - wavePhase * 1.5f) * 0.35f) * waveHeight
                        val y = centerY + yOffset

                        if (i == 0) {
                            path.moveTo(x, y)
                            pathGlow.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                            pathGlow.lineTo(x, y)
                        }
                    }

                    // Ambient glow line
                    drawPath(
                        path = pathGlow,
                        brush = Brush.horizontalGradient(listOf(DrumAmberPrimary, DrumCyanSecondary, TimingPerfectGreen)),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round),
                        alpha = 0.3f
                    )

                    // Sharp primary waveform
                    drawPath(
                        path = path,
                        brush = Brush.horizontalGradient(listOf(DrumAmberPrimary, DrumCyanSecondary, DrumAmberPrimary)),
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Center line zero-crossing indicator
                    drawLine(
                        color = Color(0xFF222B3D),
                        start = Offset(0f, centerY),
                        end = Offset(w, centerY),
                        strokeWidth = 1f
                    )
                }
            }
        }
    }
}
