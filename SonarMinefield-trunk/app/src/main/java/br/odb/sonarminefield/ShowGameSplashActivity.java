package br.odb.sonarminefield;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShowGameSplashActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {

	Button btnNext;
	SeekBar skMines;
	TextView tvMinesAmount;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_game_splash);

		tvMinesAmount = findViewById(R.id.tvMinesAmount);
		skMines = findViewById(R.id.skMines);
		skMines.setOnSeekBarChangeListener(this);
		btnNext = findViewById(R.id.btnNext);
		btnNext.setOnClickListener(this);
		this.restoreData();
		updateMinesText();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_show_game_splash, menu);
		return true;
	}

	private void destroySavedState() {

		deleteFile("state");
	}

	public void onClick(View arg0) {
		destroySavedState();
		Intent intent = new Intent(this, PlayGameActivity.class);
		int mines = skMines.getProgress();
		Bundle bundle = new Bundle();
		bundle.putInt("mines", mines);
		intent.putExtras(bundle);
		this.startActivityForResult(intent, 1);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		skMines.setProgress(25);
		return true;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {

				String result = data.getStringExtra("result");
				Intent intent = new Intent(this, ShowGameOutcomeActivity.class);

				Bundle bundle = new Bundle();
				bundle.putString("result", result);
				intent.putExtras(bundle);

				this.startActivity(intent);
			}
		}

		if (resultCode == RESULT_CANCELED) {

			//Write your code on no result return

		}
	}

	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		updateMinesText();

	}

	private void updateMinesText() {

		int mines = skMines.getProgress();
		tvMinesAmount.setText("Playing with " + (mines + 2) + " mines");
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}


	@Override
	protected void onDestroy() {

		this.saveData();

		super.onDestroy();
	}

	private void saveData() {

		String FILENAME = "data.dat";

		int mines;
		FileOutputStream fos;


		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			mines = skMines.getProgress();
			fos.write(mines);


			fos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void restoreData() {
		String FILENAME = "data.dat";
		FileInputStream fis;
		int mines;

		try {
			fis = openFileInput(FILENAME);

			mines = fis.read();
			skMines.setProgress(mines);
			fis.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
