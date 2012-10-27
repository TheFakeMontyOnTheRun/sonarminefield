package br.odb.sonarminefield;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class PlayGameActivity extends Activity {
	
	GameBoard gameBoard;
	GameSession session;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

}
