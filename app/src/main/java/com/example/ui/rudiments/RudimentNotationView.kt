package com.example.ui.rudiments

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Rudiment
import com.example.model.RudimentNote
import com.example.model.StickingHand
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.TimingPerfectGreen

/**
 * Visual Drum Sheet Music Notation component for Drum Rudiments.
 *
 * Renders:
 * - Percussion Clef (neutral clef ||)
 * - 5-line standard drum music staff
 * - Time signature
 * - Snare note heads, stems, beams, and flags
 * - Accent marks (>) above accented strokes
 * - Grace notes with slurs for Flams and Drags
 * - Colored Sticking letters: 'R' (Amber) and 'L' (Cyan)
 * - Counting rhythm syllables (1 e & a)
 * - Live animated cursor tracking active note during playback
 */
@Composable
fun RudimentNotationView(
    rudiment: Rudiment,
    activeNoteIndex: Int,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Calculate dynamic canvas width based on note count
    val noteSpacing = 52.dp
    val startMargin = 70.dp
    val endMargin = 40.dp
    val totalWidth = startMargin + (rudiment.notes.size * noteSpacing.value).dp + endMargin
    val minCanvasWidth = totalWidth.coerceAtLeast(340.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0A0D14))
            .border(1.dp, Color(0xFF1E2638), RoundedCornerShape(16.dp))
            .testTag("rudiment_notation_container")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
        ) {
            Canvas(
                modifier = Modifier
                    .width(minCanvasWidth)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp)
                    .testTag("rudiment_notation_canvas")
            ) {
                val canvasW = size.width
                val canvasH = size.height

                // Staff lines configuration (5 standard staff lines)
                val staffLineSpacing = 11.dp.toPx()
                val staffCenterY = canvasH * 0.44f
                val topStaffY = staffCenterY - 2 * staffLineSpacing
                val bottomStaffY = staffCenterY + 2 * staffLineSpacing

                // 1. Draw 5 horizontal staff lines
                for (i in -2..2) {
                    val lineY = staffCenterY + i * staffLineSpacing
                    drawLine(
                        color = Color(0xFF2A344A),
                        start = Offset(16.dp.toPx(), lineY),
                        end = Offset(canvasW - 16.dp.toPx(), lineY),
                        strokeWidth = 1.2.dp.toPx()
                    )
                }

                // 2. Draw Start Barline & Neutral Percussion Clef
                val startX = 24.dp.toPx()
                // Barline
                drawLine(
                    color = Color(0xFF64748B),
                    start = Offset(startX, topStaffY),
                    end = Offset(startX, bottomStaffY),
                    strokeWidth = 2.dp.toPx()
                )

                // Neutral Percussion Clef (Two solid vertical parallel blocks)
                val clefX = startX + 12.dp.toPx()
                val clefHeight = staffLineSpacing * 2.2f
                val clefTop = staffCenterY - clefHeight / 2f
                drawRect(
                    color = Color(0xFF94A3B8),
                    topLeft = Offset(clefX, clefTop),
                    size = Size(3.5.dp.toPx(), clefHeight)
                )
                drawRect(
                    color = Color(0xFF94A3B8),
                    topLeft = Offset(clefX + 7.dp.toPx(), clefTop),
                    size = Size(3.5.dp.toPx(), clefHeight)
                )

                // 3. Time Signature Text (native canvas draw)
                val paintTimeSig = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 14.dp.toPx()
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                val tsX = clefX + 22.dp.toPx()
                val (numerator, denominator) = when (rudiment.timeSignatureString) {
                    "6/8" -> "6" to "8"
                    "3/4" -> "3" to "4"
                    "2/4" -> "2" to "4"
                    else -> "4" to "4"
                }
                drawContext.canvas.nativeCanvas.drawText(
                    numerator,
                    tsX,
                    staffCenterY - 2.dp.toPx(),
                    paintTimeSig
                )
                drawContext.canvas.nativeCanvas.drawText(
                    denominator,
                    tsX,
                    staffCenterY + staffLineSpacing + 2.dp.toPx(),
                    paintTimeSig
                )

                // 4. Render Rudiment Notes
                val noteStartX = startMargin.toPx()
                val stepDx = noteSpacing.toPx()

                // Snare drum sits on 3rd space (between lines -1 and 0 from center)
                val snareHeadY = staffCenterY - (staffLineSpacing * 0.5f)
                val stemHeight = 32.dp.toPx()
                val headRadiusX = 5.5.dp.toPx()
                val headRadiusY = 4.2.dp.toPx()

                rudiment.notes.forEachIndexed { index, note ->
                    val noteX = noteStartX + (index * stepDx)
                    val isActive = isPlaying && (activeNoteIndex == index)

                    // Draw Live Playback Highlight Bar behind active note
                    if (isActive) {
                        drawRoundRect(
                            color = DrumAmberPrimary.copy(alpha = 0.22f),
                            topLeft = Offset(noteX - 16.dp.toPx(), 4.dp.toPx()),
                            size = Size(32.dp.toPx(), canvasH - 8.dp.toPx()),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx(), 10.dp.toPx())
                        )
                        // Glowing top dot
                        drawCircle(
                            color = DrumAmberPrimary,
                            radius = 3.5.dp.toPx(),
                            center = Offset(noteX, 8.dp.toPx())
                        )
                    }

                    // Accented marker '>' above note stem
                    if (note.isAccented) {
                        val accentY = snareHeadY - stemHeight - 12.dp.toPx()
                        val accentPath = Path().apply {
                            moveTo(noteX - 6.dp.toPx(), accentY - 4.dp.toPx())
                            lineTo(noteX + 4.dp.toPx(), accentY)
                            lineTo(noteX - 6.dp.toPx(), accentY + 4.dp.toPx())
                        }
                        drawPath(
                            path = accentPath,
                            color = if (isActive) DrumAmberPrimary else Color(0xFFFF9800),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    if (note.isGraceNote) {
                        // Small grace note for Flams/Drags
                        val graceHeadX = noteX - 12.dp.toPx()
                        val graceHeadY = snareHeadY + 2.dp.toPx()

                        drawOval(
                            color = if (isActive) Color.White else Color(0xFFCBD5E1),
                            topLeft = Offset(graceHeadX - 3.5.dp.toPx(), graceHeadY - 2.8.dp.toPx()),
                            size = Size(7.dp.toPx(), 5.6.dp.toPx())
                        )
                        // Grace note stem
                        drawLine(
                            color = if (isActive) Color.White else Color(0xFFCBD5E1),
                            start = Offset(graceHeadX + 3.5.dp.toPx(), graceHeadY),
                            end = Offset(graceHeadX + 3.5.dp.toPx(), graceHeadY - 18.dp.toPx()),
                            strokeWidth = 1.2.dp.toPx()
                        )
                        // Grace note slash (accidental grace indicator)
                        drawLine(
                            color = if (isActive) Color.White else Color(0xFFCBD5E1),
                            start = Offset(graceHeadX, graceHeadY - 12.dp.toPx()),
                            end = Offset(graceHeadX + 6.dp.toPx(), graceHeadY - 16.dp.toPx()),
                            strokeWidth = 1.dp.toPx()
                        )
                        // Slur curve to primary note
                        val slurPath = Path().apply {
                            moveTo(graceHeadX, graceHeadY + 5.dp.toPx())
                            quadraticTo(
                                (graceHeadX + noteX) / 2f,
                                snareHeadY + 10.dp.toPx(),
                                noteX - 2.dp.toPx(),
                                snareHeadY + 5.dp.toPx()
                            )
                        }
                        drawPath(
                            path = slurPath,
                            color = Color(0xFF94A3B8),
                            style = Stroke(width = 1.dp.toPx())
                        )
                    } else {
                        // Standard Snare Note Head (rotated oval)
                        val noteColor = when {
                            isActive -> DrumAmberPrimary
                            note.isAccented -> Color.White
                            else -> Color(0xFFE2E8F0)
                        }

                        drawOval(
                            color = noteColor,
                            topLeft = Offset(noteX - headRadiusX, snareHeadY - headRadiusY),
                            size = Size(headRadiusX * 2, headRadiusY * 2)
                        )

                        // Upward Stem
                        val stemTopY = snareHeadY - stemHeight
                        drawLine(
                            color = noteColor,
                            start = Offset(noteX + headRadiusX - 0.5f, snareHeadY),
                            end = Offset(noteX + headRadiusX - 0.5f, stemTopY),
                            strokeWidth = 1.5.dp.toPx()
                        )

                        // 16th Note Double Flag or Beam marker
                        val flagX = noteX + headRadiusX - 0.5f
                        drawLine(
                            color = noteColor,
                            start = Offset(flagX, stemTopY),
                            end = Offset(flagX + 8.dp.toPx(), stemTopY + 4.dp.toPx()),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        if (note.durationFraction <= 0.25f) {
                            drawLine(
                                color = noteColor,
                                start = Offset(flagX, stemTopY + 5.dp.toPx()),
                                end = Offset(flagX + 8.dp.toPx(), stemTopY + 9.dp.toPx()),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    // 5. Sticking Letter Text below staff ('R' in Amber, 'L' in Cyan)
                    val stickingColor = when (note.sticking) {
                        StickingHand.RIGHT -> if (isActive) Color(0xFFFFD54F) else DrumAmberPrimary
                        StickingHand.LEFT -> if (isActive) Color(0xFF80DEEA) else DrumCyanSecondary
                        else -> Color.White
                    }

                    val paintSticking = android.graphics.Paint().apply {
                        color = stickingColor.hashCode()
                        textSize = if (note.isGraceNote) 11.dp.toPx() else 14.dp.toPx()
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                        textAlign = android.graphics.Paint.Align.CENTER
                    }

                    val stickingLabel = if (note.isGraceNote) note.sticking.label.lowercase() else note.sticking.label
                    val stickingY = bottomStaffY + 22.dp.toPx()
                    drawContext.canvas.nativeCanvas.drawText(
                        stickingLabel,
                        if (note.isGraceNote) noteX - 8.dp.toPx() else noteX,
                        stickingY,
                        paintSticking
                    )

                    // 6. Syllable / Count text below sticking (e.g. "1", "e", "&", "a")
                    if (!note.isGraceNote && note.syllable.isNotEmpty()) {
                        val paintSyllable = android.graphics.Paint().apply {
                            color = if (isActive) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#94A3B8")
                            textSize = 10.5.dp.toPx()
                            typeface = android.graphics.Typeface.DEFAULT
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            note.syllable,
                            noteX,
                            stickingY + 16.dp.toPx(),
                            paintSyllable
                        )
                    }
                }

                // Final Double Barline at the end
                val endX = noteStartX + (rudiment.notes.size * stepDx) + 12.dp.toPx()
                drawLine(
                    color = Color(0xFF64748B),
                    start = Offset(endX, topStaffY),
                    end = Offset(endX, bottomStaffY),
                    strokeWidth = 1.5.dp.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(endX + 5.dp.toPx(), topStaffY),
                    end = Offset(endX + 5.dp.toPx(), bottomStaffY),
                    strokeWidth = 3.dp.toPx()
                )
            }
        }
    }
}
