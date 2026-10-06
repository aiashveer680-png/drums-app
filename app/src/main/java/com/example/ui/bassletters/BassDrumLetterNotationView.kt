package com.example.ui.bassletters

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.BassDrumLetter
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary

/**
 * Visual Drum Staff Notation matching Page 18 of The Language of Drumming (Benny Greb system).
 *
 * Displays:
 * - 5 standard staff lines with measure repeat barlines
 * - Hi-hat eighth-note 'x' noteheads
 * - Snare drum on beats 2 and 4
 * - Bass Drum (Kick) in glowing teal/cyan for the selected letter
 * - "chid" vocalization cue on downbeats
 * - Syllable labels ("1", "e", "&", "a") for each of the 4 beats
 * - Active beat background highlight & moving playback cursor
 */
@Composable
fun BassDrumLetterNotationView(
    letter: BassDrumLetter,
    currentStep: Int, // 0..15 (-1 when stopped)
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val canvasWidth = 740.dp
    val canvasHeight = 210.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(canvasHeight)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0F151B))
            .border(1.dp, Color(0xFF24333E), RoundedCornerShape(18.dp))
            .testTag("bass_drum_letter_notation_container")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
        ) {
            Canvas(
                modifier = Modifier
                    .width(canvasWidth)
                    .fillMaxHeight()
                    .testTag("bass_drum_letter_notation_canvas")
            ) {
                val cw = size.width
                val ch = size.height

                // Layout constants matching the reference
                val base = 110.dp.toPx()
                val lineSpacing = 10.dp.toPx()
                val topBeamY = 58.dp.toPx()
                val yHat = 99.dp.toPx()
                val ySnare = 125.dp.toPx()
                val yKick = 145.dp.toPx()

                val beatWidth = 148.dp.toPx()
                val x0 = 98.dp.toPx()

                val currentBeat = if (currentStep >= 0) currentStep / 4 else -1

                // 1. Draw 5 horizontal staff lines
                for (k in 0..4) {
                    val lineY = base + k * lineSpacing
                    drawLine(
                        color = Color(0xFF5C6D79),
                        start = Offset(28.dp.toPx(), lineY),
                        end = Offset(712.dp.toPx(), lineY),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // 2. Percussion Clef & Start Barline
                drawRect(color = Color.White, topLeft = Offset(44.dp.toPx(), 120.dp.toPx()), size = Size(5.dp.toPx(), 20.dp.toPx()))
                drawRect(color = Color.White, topLeft = Offset(54.dp.toPx(), 120.dp.toPx()), size = Size(5.dp.toPx(), 20.dp.toPx()))
                drawRect(color = Color.White, topLeft = Offset(72.dp.toPx(), 110.dp.toPx()), size = Size(4.dp.toPx(), 40.dp.toPx()))
                drawLine(color = Color.White, start = Offset(80.dp.toPx(), 110.dp.toPx()), end = Offset(80.dp.toPx(), 150.dp.toPx()), strokeWidth = 1.2.dp.toPx())
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(86.dp.toPx(), 122.dp.toPx()))
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(86.dp.toPx(), 138.dp.toPx()))

                // 3. End Repeat Barline
                drawLine(color = Color.White, start = Offset(698.dp.toPx(), 110.dp.toPx()), end = Offset(698.dp.toPx(), 150.dp.toPx()), strokeWidth = 1.2.dp.toPx())
                drawRect(color = Color.White, topLeft = Offset(702.dp.toPx(), 110.dp.toPx()), size = Size(4.dp.toPx(), 40.dp.toPx()))
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(691.dp.toPx(), 122.dp.toPx()))
                drawCircle(color = Color.White, radius = 2.dp.toPx(), center = Offset(691.dp.toPx(), 138.dp.toPx()))

                val names = listOf("1", "e", "&", "a")
                val activeCursorX = mutableListOf<Float>()

                // 4. Draw 4 Beats
                for (b in 0..3) {
                    val ox = x0 + b * beatWidth
                    val isBeatActive = (currentBeat == b)

                    // Beat background highlight
                    if (isBeatActive) {
                        drawRoundRect(
                            color = Color(0xFF1E2B33),
                            topLeft = Offset(ox - 6.dp.toPx(), 12.dp.toPx()),
                            size = Size(beatWidth - 6.dp.toPx(), 186.dp.toPx()),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx(), 10.dp.toPx())
                        )
                    }

                    // "chid" vocalization cue above beat
                    val isChidActive = isBeatActive && (currentStep % 4 == 0)
                    val paintChid = android.graphics.Paint().apply {
                        color = if (isChidActive) android.graphics.Color.parseColor("#4FB3C6") else android.graphics.Color.parseColor("#96A6B1")
                        textSize = 15.dp.toPx()
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    val chidX = ox + 16.dp.toPx() + 3.dp.toPx()
                    drawContext.canvas.nativeCanvas.drawText("chid", chidX, 38.dp.toPx(), paintChid)

                    val xs = mutableListOf<Float>()
                    val presentSlots = mutableListOf<Int>()
                    val hasHiHat = BooleanArray(4)
                    val hasSnare = BooleanArray(4)
                    val hasKick = BooleanArray(4)

                    for (i in 0..3) {
                        val posX = ox + 16.dp.toPx() + i * 34.dp.toPx()
                        xs.add(posX)

                        val h = (i == 0 || i == 2)
                        val s = (i == 0 && (b == 1 || b == 3))
                        val k = if (letter.letter == "A") {
                            (i == 0 && (b == 0 || b == 2))
                        } else {
                            letter.hasKickOnSlot(i)
                        }

                        hasHiHat[i] = h
                        hasSnare[i] = s
                        hasKick[i] = k

                        if (h || s || k) presentSlots.add(i)
                    }

                    // Draw connecting horizontal top beams (16th notes)
                    if (presentSlots.size > 1) {
                        val firstX = xs[presentSlots.first()] + 5.6.dp.toPx()
                        val lastX = xs[presentSlots.last()] + 5.6.dp.toPx()
                        drawLine(
                            color = Color.White,
                            start = Offset(firstX, topBeamY + 2.dp.toPx()),
                            end = Offset(lastX, topBeamY + 2.dp.toPx()),
                            strokeWidth = 4.5.dp.toPx()
                        )
                    }

                    // Secondary beam (for 16ths)
                    for (i in 0..2) {
                        val f1 = presentSlots.contains(i) && (i == 3 || presentSlots.contains(i + 1))
                        val f2 = presentSlots.contains(i + 1) && (i + 1 == 3 || presentSlots.contains(i + 2))
                        if (f1 && f2) {
                            drawLine(
                                color = Color.White,
                                start = Offset(xs[i] + 5.6.dp.toPx(), topBeamY + 11.dp.toPx()),
                                end = Offset(xs[i + 1] + 5.6.dp.toPx(), topBeamY + 11.dp.toPx()),
                                strokeWidth = 4.5.dp.toPx()
                            )
                        }
                    }

                    // Render Notes on this Beat
                    for (i in 0..3) {
                        val x = xs[i]
                        val globalStep = b * 4 + i
                        val isSlotActive = isPlaying && (currentStep == globalStep)
                        if (isSlotActive) {
                            activeCursorX.add(x + 3.dp.toPx())
                        }

                        var lowestY: Float? = null

                        // 1. Hi-hat 'x' notehead
                        if (hasHiHat[i]) {
                            val hatColor = if (isSlotActive) DrumAmberPrimary else Color.White
                            drawLine(
                                color = hatColor,
                                start = Offset(x - 5.dp.toPx(), yHat - 5.dp.toPx()),
                                end = Offset(x + 5.dp.toPx(), yHat + 5.dp.toPx()),
                                strokeWidth = 2.2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = hatColor,
                                start = Offset(x + 5.dp.toPx(), yHat - 5.dp.toPx()),
                                end = Offset(x - 5.dp.toPx(), yHat + 5.dp.toPx()),
                                strokeWidth = 2.2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                            lowestY = yHat
                        }

                        // 2. Snare notehead
                        if (hasSnare[i]) {
                            val snColor = if (isSlotActive) DrumAmberPrimary else Color.White
                            drawOval(
                                color = snColor,
                                topLeft = Offset(x - 6.4.dp.toPx(), ySnare - 4.7.dp.toPx()),
                                size = Size(12.8.dp.toPx(), 9.4.dp.toPx())
                            )
                            lowestY = ySnare
                        }

                        // 3. Kick / Bass Drum notehead (Teal / Cyan pulse)
                        if (hasKick[i]) {
                            val kickColor = if (isSlotActive) DrumAmberPrimary else Color(0xFF4FB3C6)
                            drawOval(
                                color = kickColor,
                                topLeft = Offset(x - 6.4.dp.toPx(), yKick - 4.7.dp.toPx()),
                                size = Size(12.8.dp.toPx(), 9.4.dp.toPx())
                            )
                            lowestY = yKick
                        }

                        // Stem connecting noteheads upward to the top beam
                        if (lowestY != null) {
                            val stemColor = if (isSlotActive) DrumAmberPrimary else Color.White
                            val stemBottom = lowestY - (if (lowestY == yHat) 0f else 2.dp.toPx())
                            drawLine(
                                color = stemColor,
                                start = Offset(x + 5.6.dp.toPx(), stemBottom),
                                end = Offset(x + 5.6.dp.toPx(), topBeamY),
                                strokeWidth = 2.dp.toPx()
                            )
                        }

                        // Syllable text at the bottom ("1", "e", "&", "a")
                        val isKickNote = hasKick[i]
                        val paintLabel = android.graphics.Paint().apply {
                            color = when {
                                isSlotActive -> android.graphics.Color.parseColor("#F0B84A")
                                isKickNote -> android.graphics.Color.parseColor("#4FB3C6")
                                else -> android.graphics.Color.parseColor("#96A6B1")
                            }
                            textSize = 14.5.dp.toPx()
                            typeface = if (isKickNote || isSlotActive) android.graphics.Typeface.DEFAULT_BOLD else android.graphics.Typeface.DEFAULT
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawContext.canvas.nativeCanvas.drawText(names[i], x + 3.dp.toPx(), 186.dp.toPx(), paintLabel)
                    }
                }

                // 5. Draw live vertical playback cursor line
                if (activeCursorX.isNotEmpty()) {
                    val curX = activeCursorX.first()
                    drawLine(
                        color = Color(0xFF4FB3C6),
                        start = Offset(curX, 88.dp.toPx()),
                        end = Offset(curX, 168.dp.toPx()),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }
    }
}
