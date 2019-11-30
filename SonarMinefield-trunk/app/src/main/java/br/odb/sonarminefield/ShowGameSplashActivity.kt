package br.odb.sonarminefield

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_show_game_splash.*
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class ShowGameSplashActivity : Activity(), View.OnClickListener, OnSeekBarChangeListener {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_game_splash)
        skMines.setOnSeekBarChangeListener(this)
        btnNext.setOnClickListener(this)
        restoreData()
        updateMinesText()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_show_game_splash, menu)
        return true
    }

    private fun destroySavedState() {
        deleteFile("state")
    }

    override fun onClick(arg0: View) {
        destroySavedState()
        val intent = Intent(this, PlayGameActivity::class.java)
        val mines = skMines!!.progress
        val bundle = Bundle()
        bundle.putInt("mines", mines)
        intent.putExtras(bundle)
        this.startActivityForResult(intent, 1)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        skMines!!.progress = 25
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val result = data.getStringExtra("result")
                val intent = Intent(this, ShowGameOutcomeActivity::class.java)
                val bundle = Bundle()
                bundle.putString("result", result)
                intent.putExtras(bundle)
                this.startActivity(intent)
            }
        }
        if (resultCode == RESULT_CANCELED) { //Write your code on no result return
        }
    }

    override fun onProgressChanged(arg0: SeekBar, arg1: Int, arg2: Boolean) {
        updateMinesText()
    }

    private fun updateMinesText() {
        val mines = skMines!!.progress
        tvMinesAmount!!.text = "Playing with " + (mines + 2) + " mines"
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) { // TODO Auto-generated method stub
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) { // TODO Auto-generated method stub
    }

    override fun onDestroy() {
        saveData()
        super.onDestroy()
    }

    private fun saveData() {
        val FILENAME = "data.dat"
        val mines: Int
        val fos: FileOutputStream
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE)
            mines = skMines!!.progress
            fos.write(mines)
            fos.close()
        } catch (e: FileNotFoundException) { // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) { // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    private fun restoreData() {
        val FILENAME = "data.dat"
        val fis: FileInputStream
        val mines: Int
        try {
            fis = openFileInput(FILENAME)
            mines = fis.read()
            skMines!!.progress = mines
            fis.close()
        } catch (e: FileNotFoundException) { // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) { // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }
}