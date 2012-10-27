package br.odb.sonarminefield;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ShowGameSplashActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {

	Button btnNext;
	SeekBar skMines;
	TextView tvMinesAmount;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_game_splash);
        
        tvMinesAmount = (TextView) findViewById( R.id.tvMinesAmount );
    	skMines = (SeekBar) findViewById( R.id.skMines );
    	skMines.setOnSeekBarChangeListener( this );
        btnNext = (Button) findViewById( R.id.btnNext );
        btnNext.setOnClickListener( this );        
        tryRestoringContext( savedInstanceState );
        updateMinesText();
    }
    
    public void tryRestoringContext( Bundle savedInstanceState ) {
    	//tenta recuperar as configurações da ultima partida.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_show_game_splash, menu);
        return true;
    }

	public void onClick(View arg0) {
		Intent intent = new Intent( this, PlayGameActivity.class );
		int mines = skMines.getProgress();
		Bundle bundle = new Bundle();
		bundle.putInt( "mines", mines );
		intent.putExtras( bundle );
		this.startActivityForResult( intent, 1 );		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

		     if(resultCode == RESULT_OK) {
		    	 
				String result = data.getStringExtra("result");
				Intent intent = new Intent( this, ShowGameOutcomeActivity.class );
				
				Bundle bundle = new Bundle();
				bundle.putString( "result", result );
				intent.putExtras( bundle );				
				
				this.startActivity( intent );
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
		tvMinesAmount.setText( "Playing with " + ( mines + 2 ) + " mines" );
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
}
