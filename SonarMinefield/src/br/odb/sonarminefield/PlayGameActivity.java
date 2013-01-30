package br.odb.sonarminefield;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class PlayGameActivity extends Activity {
	
	GameBoard gameBoard;
	GameSession session;
	static Context instanceContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        instanceContext = this;
        //setContentView(R.layout.activity_play_game);
        int mines = 0;
        Intent intent = this.getIntent();
        mines = intent.getExtras().getInt( "mines" ) + 2;      
        
        session = new GameSession();
        session.placeRandomMines( mines );
        
        gameBoard = new GameBoard( this );
        //gameBoard = ( GameBoard )findViewById( R.id.gameBoard );
        setContentView( gameBoard );
        gameBoard.setSession( session );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_play_game, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch ( item.getItemId() ) {
			case R.id.menu_settings_flag:
				gameBoard.setToFlag();
			break;
			case R.id.menu_settings_poke:
				gameBoard.setToPoke();
			break;
			case R.id.menu_settings_quit_to_menu:
				finish();
			break;
			default:
				return false;
		}
		
		return true;
	}

	public static Context getCurrentContext() {

		return instanceContext;
	}   

}
