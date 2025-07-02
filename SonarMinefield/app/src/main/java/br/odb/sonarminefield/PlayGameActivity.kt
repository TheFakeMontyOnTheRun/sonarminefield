package br.odb.sonarminefield

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import br.odb.sonarminefield.ui.theme.SonarMinefieldTheme


class PlayGameActivity : ComponentActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mines = intent.getIntExtra("mines", 10)
        Toast.makeText(this, "Playing with $mines", Toast.LENGTH_SHORT).show()

        enableEdgeToEdge()
        setContent {
            var gameSession by rememberSaveable {
                mutableStateOf(GameSession().apply {
                    placeRandomMines(mines)
                })
            }

            SonarMinefieldTheme {
                GameBoard(gameSession)
            }
        }
    }
}

@Composable
fun GameBoard(session: GameSession, modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context -> GameBoard(context, session) },
        modifier = Modifier.fillMaxSize(),
    )
}