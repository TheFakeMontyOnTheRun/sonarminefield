package br.odb.sonarminefield

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class PlayGameActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        setContentView(R.layout.activity_game_layout)
    }
}