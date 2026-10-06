package com.example.ui.exercise

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.DrumInstrument
import com.example.model.Exercise
import com.example.model.ScheduledHit
import com.example.ui.theme.InstrumentCrashColor
import com.example.ui.theme.InstrumentHiHatColor
import com.example.ui.theme.InstrumentKickColor
import com.example.ui.theme.InstrumentRideColor
import com.example.ui.theme.InstrumentSnareColor
import com.example.ui.theme.InstrumentTomFloorColor
import com.example.ui.theme.InstrumentTomHighColor
import com.example.ui.theme.TimingPerfectGreen

@Composable
fun DrumBeatVisualizer(
    exercise: Exercise,
    currentBar: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    // Collect instruments present in this exercise
    val activeInstruments = listOf(
        DrumInstrument.CRASH,
        DrumInstrument.RIDE,
        DrumInstrument.HIHAT_OPEN,
        DrumInstrument.HIHAT_CLOSED,
        DrumInstrument.TOM_HIGH,
        DrumInstrument.SNARE,
        DrumInstrument.TOM_FLOOR,
        DrumInstrument.KICK
    ).filter { inst -> exercise.hits.any { it.instrument == inst } }

    val stepsPerBar = exercise.stepsPerBar
    val stepsPerQuarter = stepsPerBar / exercise.timeSignature.numerator

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: Measure & Count Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MEASURE ${currentBar + 1} OF ${exercise.totalBars}",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                )

                // Current Beat Count
                val currentBeatNumber = (currentStep / stepsPerQuarter) + 1
                val subLabels = if (stepsPerQuarter == 4) listOf("e", "&", "a") else listOf("&")
                val subIndex = currentStep % stepsPerQuarter
                val subText = if (subIndex == 0) "" else subLabels.getOrNull(subIndex - 1) ?: ""

                Text(
                    text = "BEAT $currentBeatNumber $subText",
                    color = TimingPerfectGreen,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Count ruler numbers (1 e & a  2 e & a ...)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Label spacing box
                Box(modifier = Modifier.width(42.dp)) {
                    Text(
                        text = "COUNT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (step in 0 until stepsPerBar) {
                        val isQuarter = (step % stepsPerQuarter == 0)
                        val beatNum = (step / stepsPerQuarter) + 1
                        val isCurrentStep = (step == currentStep)

                        val label = when {
                            isQuarter -> beatNum.toString()
                            stepsPerQuarter == 4 -> when (step % 4) {
                                1 -> "e"
                                2 -> "&"
                                3 -> "a"
                                else -> ""
                            }
                            else -> "&"
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = if (isQuarter) 11.sp else 9.sp,
                                fontWeight = if (isCurrentStep || isQuarter) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isCurrentStep -> TimingPerfectGreen
                                    isQuarter -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                },
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Instrument Notation Lanes
            activeInstruments.forEach { instrument ->
                InstrumentLaneRow(
                    instrument = instrument,
                    exercise = exercise,
                    currentBar = currentBar,
                    currentStep = currentStep
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun InstrumentLaneRow(
    instrument: DrumInstrument,
    exercise: Exercise,
    currentBar: Int,
    currentStep: Int
) {
    val instrumentColor = when (instrument) {
        DrumInstrument.KICK -> InstrumentKickColor
        DrumInstrument.SNARE -> InstrumentSnareColor
        DrumInstrument.HIHAT_CLOSED, DrumInstrument.HIHAT_OPEN -> InstrumentHiHatColor
        DrumInstrument.TOM_HIGH -> InstrumentTomHighColor
        DrumInstrument.TOM_FLOOR -> InstrumentTomFloorColor
        DrumInstrument.CRASH -> InstrumentCrashColor
        DrumInstrument.RIDE -> InstrumentRideColor
        else -> MaterialTheme.colorScheme.primary
    }

    val stepsPerBar = exercise.stepsPerBar
    val stepsPerQuarter = stepsPerBar / exercise.timeSignature.numerator

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                shape = RoundedCornerShape(6.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Instrument badge
        Box(
            modifier = Modifier
                .width(42.dp)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = instrument.shortcut,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                color = instrumentColor
            )
        }

        // Steps Track
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (step in 0 until stepsPerBar) {
                val hit: ScheduledHit? = exercise.hits.firstOrNull {
                    it.bar == currentBar && it.step == step && it.instrument == instrument
                }

                val isCurrentStep = (step == currentStep)
                val isQuarter = (step % stepsPerQuarter == 0)

                val cellBgColor by animateColorAsState(
                    targetValue = when {
                        isCurrentStep && hit != null -> instrumentColor
                        isCurrentStep -> MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        hit != null -> instrumentColor.copy(alpha = 0.45f)
                        isQuarter -> MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
                        else -> Color.Transparent
                    },
                    animationSpec = tween(durationMillis = 60),
                    label = "cellColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(26.dp)
                        .padding(horizontal = 1.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(cellBgColor)
                        .then(
                            if (isCurrentStep) {
                                Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (hit != null) {
                        // Display note circle and sticking info
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (hit.isAccented) 10.dp else 7.dp)
                                    .clip(CircleShape)
                                    .background(if (isCurrentStep) Color.White else instrumentColor)
                            )
                            if (hit.sticking != null) {
                                Text(
                                    text = hit.sticking.label,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isCurrentStep) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
