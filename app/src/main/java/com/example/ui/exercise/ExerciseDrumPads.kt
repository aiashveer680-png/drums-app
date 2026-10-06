package com.example.ui.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.DrumInstrument
import com.example.model.HitFeedback
import com.example.model.TimingRating
import com.example.ui.theme.InstrumentCrashColor
import com.example.ui.theme.InstrumentHiHatColor
import com.example.ui.theme.InstrumentKickColor
import com.example.ui.theme.InstrumentSnareColor
import com.example.ui.theme.InstrumentTomFloorColor
import com.example.ui.theme.InstrumentTomHighColor
import com.example.ui.theme.TimingEarlyBlue
import com.example.ui.theme.TimingGoodAmber
import com.example.ui.theme.TimingLatePurple
import com.example.ui.theme.TimingMissRed
import com.example.ui.theme.TimingPerfectGreen
import kotlinx.coroutines.launch

@Composable
fun ExerciseDrumPads(
    onHit: (DrumInstrument) -> Unit,
    latestFeedback: HitFeedback?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Feedback HUD Display
        Box(
            modifier = Modifier
                .height(44.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            this@Column.AnimatedVisibility(
                visible = latestFeedback != null,
                enter = scaleIn(initialScale = 0.7f) + fadeIn(),
                exit = scaleOut(targetScale = 1.2f) + fadeOut()
            ) {
                if (latestFeedback != null) {
                    val (textColor, textBg) = when (latestFeedback.rating) {
                        TimingRating.PERFECT -> TimingPerfectGreen to TimingPerfectGreen.copy(alpha = 0.2f)
                        TimingRating.GOOD -> TimingGoodAmber to TimingGoodAmber.copy(alpha = 0.2f)
                        TimingRating.EARLY -> TimingEarlyBlue to TimingEarlyBlue.copy(alpha = 0.2f)
                        TimingRating.LATE -> TimingLatePurple to TimingLatePurple.copy(alpha = 0.2f)
                        TimingRating.MISS -> TimingMissRed to TimingMissRed.copy(alpha = 0.2f)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(textBg)
                            .border(1.5.dp, textColor, RoundedCornerShape(20.dp))
                            .padding(horizontal = 18.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = latestFeedback.rating.label,
                            color = textColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Top Row: Crash, High Tom, Floor Tom
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DrumPadButton(
                title = "CRASH",
                subTitle = "18\" Bronze",
                color = InstrumentCrashColor,
                testTag = "pad_crash",
                modifier = Modifier.weight(1f),
                onClick = { onHit(DrumInstrument.CRASH) }
            )
            DrumPadButton(
                title = "HIGH TOM",
                subTitle = "10\" Birch",
                color = InstrumentTomHighColor,
                testTag = "pad_tom_high",
                modifier = Modifier.weight(1f),
                onClick = { onHit(DrumInstrument.TOM_HIGH) }
            )
            DrumPadButton(
                title = "FLOOR TOM",
                subTitle = "16\" Deep Tom",
                color = InstrumentTomFloorColor,
                testTag = "pad_tom_floor",
                modifier = Modifier.weight(1f),
                onClick = { onHit(DrumInstrument.TOM_FLOOR) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Row: Hi-Hat, Snare, Bass Drum (Kick)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DrumPadButton(
                title = "HI-HAT",
                subTitle = "14\" Bronze",
                color = InstrumentHiHatColor,
                testTag = "pad_hihat",
                modifier = Modifier.weight(1f),
                onClick = { onHit(DrumInstrument.HIHAT_CLOSED) }
            )
            DrumPadButton(
                title = "SNARE",
                subTitle = "14\" Coated",
                color = InstrumentSnareColor,
                testTag = "pad_snare",
                modifier = Modifier.weight(1.2f),
                isHero = true,
                onClick = { onHit(DrumInstrument.SNARE) }
            )
            DrumPadButton(
                title = "KICK",
                subTitle = "22\" Acoustic",
                color = InstrumentKickColor,
                testTag = "pad_kick",
                modifier = Modifier.weight(1.2f),
                isHero = true,
                onClick = { onHit(DrumInstrument.KICK) }
            )
        }
    }
}

@Composable
fun DrumPadButton(
    title: String,
    subTitle: String,
    color: Color,
    testTag: String,
    modifier: Modifier = Modifier,
    isHero: Boolean = false,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .height(if (isHero) 88.dp else 76.dp)
            .scale(scale.value)
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.18f))
            .border(2.dp, color.copy(alpha = 0.85f), RoundedCornerShape(14.dp))
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
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = if (isHero) 15.sp else 13.sp
                ),
                color = Color.White
            )
            Text(
                text = subTitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = color
            )
        }
    }
}
