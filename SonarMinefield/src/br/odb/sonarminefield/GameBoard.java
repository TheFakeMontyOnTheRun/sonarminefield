/**
 * 
 */
package br.odb.sonarminefield;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author monty
 *
 */
public class GameBoard extends View implements OnTouchListener {

	GameSession gameSession;
	Bitmap[] palette;
	PlayGameActivity manager;
	
	public GameBoard(Context appContext, AttributeSet attrs, int defStyle) {
		super(appContext, attrs, defStyle);

		init( appContext );
	}
	
	public GameBoard( Context appContext ) {
		super( appContext );
		init( appContext );
	}
	
	public GameBoard( Context appContext, AttributeSet set ) {
		super( appContext, set );
		init( appContext );		
	}
	
	private class FinishGameRunnable implements Runnable {

		public void run() {
	    	Intent intent = manager.getIntent();
	    	intent.putExtra("result", gameSession.isVictory() ? "victory" : "failure" );
	    	manager.setResult( manager.RESULT_OK, intent);
	    	manager.finish();			
		}	
	}
	
	
		
	private void init( Context appContext ) {
		
		manager = (PlayGameActivity) appContext;
		palette = new Bitmap[ 12 ];
		palette[ 0 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.blanksvg );
		palette[ 1 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.n1svg );
		palette[ 2 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.n2svg );
		palette[ 3 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.n3svg );
		palette[ 4 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.n4svg );
		palette[ 5 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.n5svg );
		palette[ 6 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.n6svg );
		palette[ 7 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.n7svg );
		palette[ 8 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.n7svg );
		palette[ 9 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.minesvg );
		palette[ 10 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.coveredsvg );
		palette[ 11 ] = BitmapFactory.decodeResource( appContext.getResources(), R.drawable.minespokedvg );
		
		this.setOnTouchListener( this );
	}
	
	public void setSession( GameSession session ) {
		gameSession = session;
	}
	
	
	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas ) {
		int newWidth = getWidth() / gameSession.getWidth();
		int newHeight = getHeight() / gameSession.getHeight();
		int smaller;
		Rect rectSrc = new Rect();
		Rect rectDst = new Rect();
		
		if ( newWidth >= newHeight )
			smaller = newHeight;
		else
			smaller = newWidth;
		
		int pos;
		Bitmap bitmap;
		Paint paint = new Paint();
		
		if ( gameSession != null ) {
			for ( int x = 0; x < gameSession.getWidth(); ++x ) {
				for ( int y = 0; y < gameSession.getHeight(); ++y ) {
					
					pos = gameSession.getPos( x, y );
					
					if ( gameSession.isCoveredAt( x, y ) )
						bitmap = palette[ GameSession.POSITION_COVERED ];
					//	paint.setColor( Color.BLUE );
					else {
						bitmap = palette[ pos ];
					}
					rectDst.top = y * smaller;
					rectDst.left = x * smaller;
					rectDst.bottom = ( y + 1 ) * smaller;
					rectDst.right = ( x + 1 ) * smaller;
					
					rectSrc.top = 0;
					rectSrc.left = 0;
					rectSrc.right = bitmap.getWidth();
					rectSrc.bottom = bitmap.getHeight();
					
					canvas.drawBitmap(bitmap, rectSrc, rectDst, paint);
					
					//canvas.drawBitmap( bitmap, x * smaller, y * smaller, paint );
					//canvas.drawRect( x * 50, y * 50 , ( x + 1 ) * 50, ( y + 1 ) * 50, paint );
				}
			}
		} else {
			paint.setColor( Color.MAGENTA );
			canvas.drawRect( 0, 0, getWidth(), getHeight(), paint );
		}
	}


	public void revealAll() {
		if ( gameSession != null ) {
			for ( int x = 0; x < gameSession.getWidth(); ++x ) {
				for ( int y = 0; y < gameSession.getHeight(); ++y ) {					
					gameSession.uncoverAt( x, y );					
				}
			}
		}					

	}

	public boolean onTouch(View v, MotionEvent event) {
		int pos;
		int x;
		int y;
		Bitmap bitmap;
		int newWidth = getWidth() / gameSession.getWidth();
		int newHeight = getHeight() / gameSession.getHeight();
		int smaller;
		
		if ( newWidth >= newHeight )
			smaller = newHeight;
		else
			smaller = newWidth;		
				
		bitmap = palette[ 0 ];
	
		x = (int) (event.getX() / smaller);
		y = (int) (event.getY() / smaller);
		
		if ( gameSession != null ) {
			gameSession.poke( x, y );
		}
		
		this.invalidate();
		
		if ( gameSession.isFinished() ) {
			revealAll();
			this.postDelayed( new FinishGameRunnable(), 5000 );
		}
		
		return true;
	}	
}
