package br.odb.sonarminefield;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PlayGameActivity extends Activity {

	static Context instanceContext;
	GameBoard gameBoard;
	GameSession session;
	int mines = 0;

	public static Context getCurrentContext() {

		return instanceContext;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instanceContext = this;
		//setContentView(R.layout.activity_play_game);
		Intent intent = this.getIntent();
		mines = intent.getExtras().getInt("mines") + 2;
		startNewGame();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		saveState();
		super.onSaveInstanceState(outState);
	}

	public void saveState() {
		try {
			FileOutputStream fos = openFileOutput("state", Context.MODE_PRIVATE);
			session.saveState(fos);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void startNewGame() {

		session = new GameSession();
		session.placeRandomMines(mines);

		gameBoard = new GameBoard(this);
		//gameBoard = ( GameBoard )findViewById( R.id.gameBoard );
		setContentView(gameBoard);
		gameBoard.setSession(session);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		startNewGame();
		tryReload();
		super.onRestoreInstanceState(savedInstanceState);
	}

	public void tryReload() {
		try {
			FileInputStream fis = openFileInput("state");
			session.loadState(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			startNewGame();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_play_game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_settings_flag:
				gameBoard.setToFlag();
				break;
			case R.id.menu_settings_poke:
				gameBoard.setToPoke();
				break;
			case R.id.menu_settings_move:
				gameBoard.setToMove();
				break;
			case R.id.menu_settings_quit_to_menu:
				finish();
				break;
			default:
				return false;
		}

		return true;
	}

}
