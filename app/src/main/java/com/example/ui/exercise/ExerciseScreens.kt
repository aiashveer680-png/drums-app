package com.example.ui.exercise

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ExerciseProgressEntity
import com.example.model.DifficultyLevel
import com.example.model.Exercise
import com.example.model.ExerciseCategory
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.TimingGoodAmber
import com.example.ui.theme.TimingMissRed
import com.example.ui.theme.TimingPerfectGreen

@Composable
fun ExerciseCatalogScreen(
    exercises: List<Exercise>,
    savedProgress: List<ExerciseProgressEntity>,
    onSelectExercise: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(ExerciseCategory.BASIC_RHYTHM) }

    val filtered = remember(selectedCategory, exercises) {
        exercises.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title and description
        Text(
            text = "Interactive Drum Workouts",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Real-time timing feedback, visual beats, and sticking workouts.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExerciseCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = {
                        Text(
                            text = category.displayName,
                            fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("chip_${category.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List of Exercise Cards
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filtered, key = { it.id }) { exercise ->
                val progress = savedProgress.firstOrNull { it.exerciseId == exercise.id }
                ExerciseCard(
                    exercise = exercise,
                    progress = progress,
                    onClick = { onSelectExercise(exercise.id) }
                )
            }
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    progress: ExerciseProgressEntity?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diffColor = Color(exercise.difficulty.colorHex)
    val stars = progress?.starsEarned ?: 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("exercise_card_${exercise.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category & Difficulty Chip
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(diffColor.copy(alpha = 0.2f))
                            .border(1.dp, diffColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = exercise.difficulty.displayName.uppercase(),
                            color = diffColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "${exercise.defaultBpm} BPM",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                // Stars display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = if (i <= stars) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star $i",
                            tint = if (i <= stars) DrumAmberPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = exercise.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (progress != null && progress.bestAccuracyPct > 0) {
                    Text(
                        text = "Best: ${progress.bestAccuracyPct}% accuracy (${progress.highestBpm} BPM)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TimingPerfectGreen
                    )
                } else {
                    Text(
                        text = "Not practiced yet",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_practice_${exercise.id}")
                ) {
                    Text(
                        text = "Practice",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ExercisePracticeScreen(
    viewModel: ExerciseViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val exercise = state.currentExercise

    if (exercise == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading Exercise...", color = Color.White)
        }
        return
    }

    val diffColor = Color(exercise.difficulty.colorHex)

    // Completion Dialog
    if (state.isSessionCompleted) {
        SessionCompletedDialog(
            stats = state.stats,
            bpm = state.bpm,
            onPlayAgain = {
                viewModel.dismissCompletionDialog()
                viewModel.startPlayback()
            },
            onDone = {
                viewModel.dismissCompletionDialog()
                onBack()
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    viewModel.stopPlayback()
                    onBack()
                },
                modifier = Modifier.testTag("btn_back_exercise")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${exercise.category.displayName} • ${exercise.difficulty.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = diffColor
                )
            }

            // Loop indicator
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Loop ${state.loopsCompleted}/${state.targetLoops}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Live Performance Stats Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ACCURACY",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${state.stats.accuracyPercentage}%",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = if (state.stats.accuracyPercentage >= 80) TimingPerfectGreen else TimingGoodAmber
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "COMBO",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${state.stats.currentCombo}x",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = DrumCyanSecondary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SCORE",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${state.stats.score}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = DrumAmberPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        var visualizerMode by remember { mutableStateOf("highway") } // "highway", "grid", "split"

        // Visualizer Mode Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = visualizerMode == "highway",
                onClick = { visualizerMode = "highway" },
                label = { Text("Note Highway & Waveform", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("chip_mode_highway")
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = visualizerMode == "grid",
                onClick = { visualizerMode = "grid" },
                label = { Text("Notation Grid", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("chip_mode_grid")
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = visualizerMode == "split",
                onClick = { visualizerMode = "split" },
                label = { Text("Dual View", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("chip_mode_split")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Visualizer Content
        if (visualizerMode == "highway" || visualizerMode == "split") {
            ScrollingNoteVisualizer(
                exercise = exercise,
                currentBar = state.currentBar,
                currentStep = state.currentStep,
                bpm = state.bpm,
                isPlaying = state.isPlaying,
                livePeak = state.liveWaveformPeak
            )
            if (visualizerMode == "split") {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (visualizerMode == "grid" || visualizerMode == "split") {
            DrumBeatVisualizer(
                exercise = exercise,
                currentBar = state.currentBar,
                currentStep = state.currentStep
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tempo & Sound Toggles Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Tempo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${state.bpm} BPM",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Stepper buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { viewModel.setBpm(state.bpm - 5) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Text("-5", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        IconButton(
                            onClick = { viewModel.setBpm(state.bpm - 1) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Remove, "Minus 1", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { viewModel.setBpm(state.bpm + 1) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Add, "Plus 1", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = { viewModel.setBpm(state.bpm + 5) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Text("+5", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Slider(
                    value = state.bpm.toFloat(),
                    onValueChange = { viewModel.setBpm(it.toInt()) },
                    valueRange = 40f..200f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Backing drum sound toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.toggleAudioBacking() }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Drum Backing",
                            tint = if (state.audioBackingEnabled) DrumCyanSecondary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.audioBackingEnabled) "Backing: ON" else "Backing: MUTE",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (state.audioBackingEnabled) DrumCyanSecondary else MaterialTheme.colorScheme.outline
                        )
                    }

                    // Metronome click toggle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.toggleMetronomeClick() }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Metronome",
                            tint = if (state.metronomeClickEnabled) DrumAmberPrimary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (state.metronomeClickEnabled) "Click: ON" else "Click: MUTE",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (state.metronomeClickEnabled) DrumAmberPrimary else MaterialTheme.colorScheme.outline
                        )
                    }

                    // Big Play/Pause Toggle
                    Button(
                        onClick = { viewModel.togglePlayPause() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.isPlaying) TimingMissRed else TimingPerfectGreen
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_toggle_exercise_play")
                    ) {
                        Icon(
                            imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (state.isPlaying) "Pause" else "Play",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (state.isPlaying) "PAUSE" else "START",
                            color = Color.Black,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sticking Guide & Coach Advice
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Coach Tip",
                        tint = DrumAmberPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Musora Coach Sticking Guide",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = DrumAmberPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = exercise.stickingAdvice,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pro Tip: ${exercise.coachTip}",
                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Interactive Drum Touch Pads
        ExerciseDrumPads(
            onHit = { inst -> viewModel.onPadHit(inst) },
            latestFeedback = state.latestFeedback
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SessionCompletedDialog(
    stats: com.example.model.ExerciseSessionStats,
    bpm: Int,
    onPlayAgain: () -> Unit,
    onDone: () -> Unit
) {
    val stars = stats.starsEarned
    val xpEarned = (stats.score / 5) + (stars * 50) + 20

    AlertDialog(
        onDismissRequest = onDone,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "WORKOUT COMPLETE!",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = DrumAmberPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = if (i <= stars) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star $i",
                            tint = if (i <= stars) DrumAmberPrimary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Overall Accuracy:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${stats.accuracyPercentage}%", fontWeight = FontWeight.Bold, color = TimingPerfectGreen)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Tempo Practiced:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$bpm BPM", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Max Combo:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${stats.maxCombo}x", fontWeight = FontWeight.Bold, color = DrumCyanSecondary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Hits Breakdown:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "P: ${stats.perfectHits} | G: ${stats.goodHits} | M: ${stats.missedHits}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("XP Earned:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("+$xpEarned XP", fontWeight = FontWeight.Black, color = DrumAmberPrimary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onPlayAgain,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Play Again", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDone) {
                Text("Finish", color = MaterialTheme.colorScheme.onSurface)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}
