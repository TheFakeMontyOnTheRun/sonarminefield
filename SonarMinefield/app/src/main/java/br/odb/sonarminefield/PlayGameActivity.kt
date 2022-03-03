package br.odb.sonarminefield

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import java.io.FileNotFoundException
import java.io.IOException

class PlayGameActivity : Activity() {
    var gameBoard: GameBoard? = null
    var session: GameSession? = null
    var mines = 0
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentContext = this
        //setContentView(R.layout.activity_play_game);
        val intent = this.intent
        mines = intent.extras!!.getInt("mines") + 2
        startNewGame()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveState()
        super.onSaveInstanceState(outState)
    }

    fun saveState() {
        try {
            val fos = openFileOutput("state", Context.MODE_PRIVATE)
            session!!.saveState(fos)
            fos.close()
        } catch (e: FileNotFoundException) { // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) { // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    fun startNewGame() {
        session = GameSession()
        session!!.placeRandomMines(mines)
        session!!.clearBorders()
        gameBoard = GameBoard(this)
        //gameBoard = ( GameBoard )findViewById( R.id.gameBoard );
        setContentView(gameBoard)
        gameBoard!!.setSession(session)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        startNewGame()
        tryReload()
        super.onRestoreInstanceState(savedInstanceState)
    }

    fun tryReload() {
        try {
            val fis = openFileInput("state")
            session!!.loadState(fis)
            fis.close()
        } catch (e: FileNotFoundException) {
            startNewGame()
            e.printStackTrace()
        } catch (e: IOException) { // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_play_game, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings_flag -> gameBoard!!.setToFlag()
            R.id.menu_settings_poke -> gameBoard!!.setToPoke()
            R.id.menu_settings_move -> gameBoard!!.setToMove()
            R.id.menu_settings_quit_to_menu -> finish()
            else -> return false
        }
        return true
    }

    companion object {
        var currentContext: Context? = null
    }
}