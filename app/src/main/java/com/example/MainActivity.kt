package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.audio.DrumSoundEngine
import com.example.audio.MetronomeController
import com.example.data.DrumDatabase
import com.example.data.DrumRepository
import com.example.ui.bassletters.BassDrumLetterAudioPlayer
import com.example.ui.exercise.ExerciseViewModel
import com.example.ui.main.MainAppView
import com.example.ui.rudiments.RudimentAudioPlayer
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var soundEngine: DrumSoundEngine
    private lateinit var metronomeController: MetronomeController
    private lateinit var repository: DrumRepository
    private lateinit var exerciseViewModel: ExerciseViewModel
    private lateinit var bassDrumPlayer: BassDrumLetterAudioPlayer
    private lateinit var rudimentPlayer: RudimentAudioPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = DrumDatabase.getDatabase(applicationContext)
        repository = DrumRepository(database.drumDao())
        soundEngine = DrumSoundEngine(applicationContext)
        metronomeController = MetronomeController(soundEngine, lifecycleScope)
        exerciseViewModel = ExerciseViewModel(repository, soundEngine, applicationContext)
        bassDrumPlayer = BassDrumLetterAudioPlayer(soundEngine, lifecycleScope)
        rudimentPlayer = RudimentAudioPlayer(soundEngine, lifecycleScope)

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppView(
                        repository = repository,
                        soundEngine = soundEngine,
                        metronomeController = metronomeController,
                        exerciseViewModel = exerciseViewModel,
                        bassDrumPlayer = bassDrumPlayer,
                        rudimentPlayer = rudimentPlayer
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        metronomeController.stop()
        exerciseViewModel.stopPlayback()
        bassDrumPlayer.stop()
        rudimentPlayer.stop()
        soundEngine.release()
    }
}
