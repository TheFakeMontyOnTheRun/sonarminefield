package br.odb.sonarminefield

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import br.odb.sonarminefield.ui.theme.SonarMinefieldTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            var minesCount by rememberSaveable { mutableStateOf(10) }
            SonarMinefieldTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameSplash(
                        mines = minesCount,
                        modifier = Modifier.padding(innerPadding),
                        onSliderValueChange = { minesCount = it.toInt() },
                        playAction = {
                            val intent = Intent(this, PlayGameActivity::class.java)
                            intent.putExtra("mines", minesCount)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GameSplash(
    mines: Int,
    modifier: Modifier = Modifier,
    onSliderValueChange: (Float) -> Unit = {},
    playAction: () -> Unit = {}
) {
    Column(modifier = modifier) {

        Slider(
            value = mines.toFloat(),
            onValueChange = onSliderValueChange,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 20,
            valueRange = 5f..20f,

            )
        Text(
            text = "Play with $mines",
            modifier = modifier
        )
        Button(onClick = playAction) {
            Text("Play")

        }
    }
}