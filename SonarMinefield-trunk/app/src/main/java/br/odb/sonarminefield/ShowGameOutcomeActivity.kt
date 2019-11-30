package br.odb.sonarminefield

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_show_game_outcome.*

class ShowGameOutcomeActivity : Activity(), View.OnClickListener {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_game_outcome)
        val tvOutcome: TextView
        tvOutcome = findViewById(R.id.tvOutcome)
        tvOutcome.text = if (intent.extras!!.getString("result") == "victory") "Congratulations, you cleared the field! Try a harder level!" else "You activated a mine. Please try again!"
        btnBack.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_show_game_outcome, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    override fun onClick(v: View) {
        finish()
    }
}