package br.odb.sonarminefield;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShowGameOutcomeActivity extends Activity implements OnClickListener {

	Button btnBack;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_game_outcome);
        
        TextView tvOutcome;
        tvOutcome = (TextView) findViewById( R.id.tvOutcome );
        tvOutcome.setText( 
        		getIntent().getExtras().getString( "result" ).equals( "victory" ) ? 
        				"Congratulations, you cleared the field! Try a harder level!":
        				"You activated a mine. Please try again!"
        		);
        
        btnBack = (Button) findViewById( R.id.btnBack );
        btnBack.setOnClickListener( this );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_show_game_outcome, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}

	public void onClick(View v) {
		finish();		
	}
}
