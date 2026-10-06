package com.example.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.audio.MetronomeController
import com.example.data.DrumRepository
import com.example.model.Lesson
import com.example.model.Rudiment
import com.example.ui.bassletters.BassDrumLetterAudioPlayer
import com.example.ui.bassletters.BassDrumLetterScreen
import com.example.ui.drumkit.DrumKitScreen
import com.example.ui.exercise.ExerciseCatalogScreen
import com.example.ui.exercise.ExercisePracticeScreen
import com.example.ui.exercise.ExerciseViewModel
import com.example.ui.lessons.LessonDetailScreen
import com.example.ui.lessons.LessonsCatalogScreen
import com.example.ui.progress.ProgressScreen
import com.example.ui.rhythm.RhythmPracticeToolScreen
import com.example.ui.rudiments.RudimentAudioPlayer
import com.example.ui.rudiments.RudimentDetailScreen
import com.example.ui.rudiments.RudimentsCatalogScreen
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary
import com.example.ui.theme.TimingMissRed
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val testTag: String) {
    LESSONS("Lessons", Icons.Default.School, "tab_lessons"),
    EXERCISES("Exercises", Icons.Default.FitnessCenter, "tab_exercises"),
    RUDIMENTS("Rudiments", Icons.Default.MenuBook, "tab_rudiments"),
    BASS_LETTERS("Bass Letters", Icons.Default.GraphicEq, "tab_bass_letters"),
    RHYTHM_TOOL("Rhythm Tool", Icons.Default.Speed, "tab_rhythm_tool"),
    DRUM_KIT("Drum Kit", Icons.Default.Album, "tab_drumkit"),
    PROGRESS("Progress", Icons.Default.BarChart, "tab_progress")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppView(
    repository: DrumRepository,
    soundEngine: DrumSoundEngine,
    metronomeController: MetronomeController,
    exerciseViewModel: ExerciseViewModel,
    bassDrumPlayer: BassDrumLetterAudioPlayer,
    rudimentPlayer: RudimentAudioPlayer,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(MainTab.EXERCISES) }
    var activeExerciseId by remember { mutableStateOf<String?>(null) }
    var activeLesson by remember { mutableStateOf<Lesson?>(null) }
    var activeRudiment by remember { mutableStateOf<Rudiment?>(null) }

    val exerciseProgress by repository.allExerciseProgress.collectAsStateWithLifecycle(initialValue = emptyList())
    val lessonProgress by repository.allLessonProgress.collectAsStateWithLifecycle(initialValue = emptyList())
    val recentSessions by repository.recentSessions.collectAsStateWithLifecycle(initialValue = emptyList())
    val userProfile by repository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val metronomeState by metronomeController.state.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle back button for drawer and sub-screens
    BackHandler(enabled = drawerState.isOpen || activeExerciseId != null || activeLesson != null || activeRudiment != null) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (activeExerciseId != null) {
            exerciseViewModel.stopPlayback()
            activeExerciseId = null
        } else if (activeLesson != null) {
            activeLesson = null
        } else if (activeRudiment != null) {
            rudimentPlayer.stop()
            activeRudiment = null
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 760.dp

        val contentBlock: @Composable () -> Unit = {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    // Show top bar unless in full exercise practice mode
                    if (activeExerciseId == null && activeLesson == null && activeRudiment == null) {
                        TopAppBar(
                            navigationIcon = {
                                if (!isWideScreen) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF171D2B))
                                            .clickable { scope.launch { drawerState.open() } }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                            .testTag("btn_open_musora_menu")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Menu,
                                            contentDescription = "Open Musora Menu",
                                            tint = DrumAmberPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "MENU",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            letterSpacing = 1.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "MUSORA",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 2.sp
                                        ),
                                        color = DrumAmberPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "DRUMS",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Light,
                                            letterSpacing = 1.sp
                                        ),
                                        color = DrumCyanSecondary
                                    )
                                }
                            },
                            actions = {
                                // Streak Indicator
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFE65100).copy(alpha = 0.25f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalFireDepartment,
                                        contentDescription = "Streak",
                                        tint = Color(0xFFFF6D00),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${userProfile?.currentStreakDays ?: 3}d",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFFFF9E80)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Quick Metronome Click indicator in Top Bar
                                IconButton(
                                    onClick = { metronomeController.toggle() },
                                    modifier = Modifier.testTag("topbar_metronome_toggle")
                                ) {
                                    Icon(
                                        imageVector = if (metronomeState.isRunning) Icons.Default.Pause else Icons.Default.Speed,
                                        contentDescription = "Toggle Metronome",
                                        tint = if (metronomeState.isRunning) TimingMissRed else MaterialTheme.colorScheme.primary
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                },
                bottomBar = {
                    // Mobile navigation bar shown for top primary tabs when not in full practice
                    if (!isWideScreen && activeExerciseId == null && activeLesson == null && activeRudiment == null) {
                        val bottomNavTabs = listOf(
                            MainTab.LESSONS,
                            MainTab.EXERCISES,
                            MainTab.BASS_LETTERS,
                            MainTab.RHYTHM_TOOL,
                            MainTab.DRUM_KIT
                        )
                        NavigationBar(
                            modifier = Modifier.navigationBarsPadding(),
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            bottomNavTabs.forEach { tab ->
                                val isSelected = (selectedTab == tab)
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { selectedTab = tab },
                                    icon = {
                                        Icon(
                                            imageVector = tab.icon,
                                            contentDescription = tab.title
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = tab.title,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 10.5.sp
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    ),
                                    modifier = Modifier.testTag(tab.testTag)
                                )
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when {
                        activeExerciseId != null -> {
                            ExercisePracticeScreen(
                                viewModel = exerciseViewModel,
                                onBack = {
                                    exerciseViewModel.stopPlayback()
                                    activeExerciseId = null
                                }
                            )
                        }

                        activeLesson != null -> {
                            val lesson = activeLesson!!
                            val isCompleted = lessonProgress.any { it.lessonId == lesson.id && it.isCompleted }
                            LessonDetailScreen(
                                lesson = lesson,
                                isCompleted = isCompleted,
                                onBack = { activeLesson = null },
                                onLaunchExercise = { exerciseId ->
                                    exerciseViewModel.loadExercise(exerciseId)
                                    activeExerciseId = exerciseId
                                },
                                onMarkComplete = { lessonId ->
                                    scope.launch {
                                        repository.markLessonCompleted(lessonId)
                                    }
                                    activeLesson = null
                                }
                            )
                        }

                        else -> {
                            Crossfade(targetState = selectedTab, label = "TabSwitch") { tab ->
                                when (tab) {
                                    MainTab.LESSONS -> {
                                        LessonsCatalogScreen(
                                            lessons = repository.lessons,
                                            completedLessons = lessonProgress,
                                            onSelectLesson = { lesson -> activeLesson = lesson }
                                        )
                                    }

                                    MainTab.EXERCISES -> {
                                        ExerciseCatalogScreen(
                                            exercises = repository.exercises,
                                            savedProgress = exerciseProgress,
                                            onSelectExercise = { exId ->
                                                exerciseViewModel.loadExercise(exId)
                                                activeExerciseId = exId
                                            }
                                        )
                                    }

                                    MainTab.RUDIMENTS -> {
                                        if (activeRudiment != null) {
                                            RudimentDetailScreen(
                                                rudiment = activeRudiment!!,
                                                player = rudimentPlayer,
                                                onBack = {
                                                    rudimentPlayer.stop()
                                                    activeRudiment = null
                                                },
                                                onLaunchPracticeExercise = { exId ->
                                                    exerciseViewModel.loadExercise(exId)
                                                    activeExerciseId = exId
                                                }
                                            )
                                        } else {
                                            RudimentsCatalogScreen(
                                                onSelectRudiment = { rudiment ->
                                                    activeRudiment = rudiment
                                                }
                                            )
                                        }
                                    }

                                    MainTab.BASS_LETTERS -> {
                                        BassDrumLetterScreen(
                                            player = bassDrumPlayer
                                        )
                                    }

                                    MainTab.RHYTHM_TOOL -> {
                                        RhythmPracticeToolScreen(
                                            controller = metronomeController,
                                            repository = repository
                                        )
                                    }

                                    MainTab.DRUM_KIT -> {
                                        DrumKitScreen(
                                            soundEngine = soundEngine,
                                            metronomeController = metronomeController
                                        )
                                    }

                                    MainTab.PROGRESS -> {
                                        ProgressScreen(
                                            userProfile = userProfile,
                                            exerciseProgress = exerciseProgress,
                                            lessonProgress = lessonProgress,
                                            recentSessions = recentSessions
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isWideScreen) {
            // Tablet / Desktop layout: Permanent Musora side menu
            PermanentNavigationDrawer(
                drawerContent = {
                    MusoraSideDrawer(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            if (tab != MainTab.RUDIMENTS) {
                                rudimentPlayer.stop()
                                activeRudiment = null
                            }
                            selectedTab = tab
                        },
                        userProfile = userProfile,
                        metronomeState = metronomeState,
                        metronomeController = metronomeController,
                        onToggleMetronome = { metronomeController.toggle() },
                        onCloseDrawer = {}
                    )
                }
            ) {
                contentBlock()
            }
        } else {
            // Handheld mobile: Modal Musora side drawer with edge swipe and menu button
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    MusoraSideDrawer(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            if (tab != MainTab.RUDIMENTS) {
                                rudimentPlayer.stop()
                                activeRudiment = null
                            }
                            selectedTab = tab
                            scope.launch { drawerState.close() }
                        },
                        userProfile = userProfile,
                        metronomeState = metronomeState,
                        metronomeController = metronomeController,
                        onToggleMetronome = { metronomeController.toggle() },
                        onCloseDrawer = { scope.launch { drawerState.close() } }
                    )
                }
            ) {
                contentBlock()
            }
        }
    }
}
