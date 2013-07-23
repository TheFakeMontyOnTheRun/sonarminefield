/**
 * 
 */
package br.odb.sonarminefield;

import android.app.Activity;
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
import br.odb.droidlib.Vector2;

/**
 * @author monty
 * 
 */
public class GameBoard extends View implements OnTouchListener {
	
	static int count;

	private Vector2 cameraPosition;
	private Vector2 lastTouchPosition;
	

	int smaller;

	GameSession gameSession;
	Bitmap[] palette;
	PlayGameActivity manager;	

	private MinefieldOperations playerAction = MinefieldOperations.POKE;

	private long pressTime;

	private long releaseTime;

	public GameBoard(Context appContext, AttributeSet attrs, int defStyle) {
		super(appContext, attrs, defStyle);

		init(appContext);
	}

	public GameBoard(Context appContext) {
		super(appContext);
		init(appContext);
	}

	public GameBoard(Context appContext, AttributeSet set) {
		super(appContext, set);
		init(appContext);
	}

	private class FinishGameRunnable implements Runnable {

		public void run() {
			Intent intent = manager.getIntent();
			intent.putExtra("result", gameSession.isVictory() ? "victory"
					: "failure");
			manager.setResult(Activity.RESULT_OK, intent);
			manager.finish();
		}
	}

	private void init(Context appContext) {
		count = 0;
		manager = (PlayGameActivity) appContext;
		palette = new Bitmap[13];
		palette[0] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.blanksvg);
		palette[1] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.n1svg);
		palette[2] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.n2svg);
		palette[3] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.n3svg);
		palette[4] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.n4svg);
		palette[5] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.n5svg);
		palette[6] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.n6svg);
		palette[7] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.n7svg);
		palette[8] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.n8svg);
		palette[9] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.minesvg);
		palette[10] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.coveredsvg);
		palette[11] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.minespokedvg);
		palette[12] = BitmapFactory.decodeResource(appContext.getResources(),
				R.drawable.flagged);

		cameraPosition = new Vector2();
		lastTouchPosition = new Vector2();

		setToPoke();
		this.setOnTouchListener(this);
	}

	public void setSession(GameSession session) {
		gameSession = session;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.graphics.drawable.Drawable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw(Canvas canvas) {

		Rect rectSrc = new Rect();
		Rect rectDst = new Rect();

		int newWidth = getWidth() / gameSession.getWidth();
		int newHeight = getHeight() / gameSession.getHeight();

		if (newWidth <= newHeight)
			smaller = newHeight;
		else
			smaller = newWidth;

		int pos;
		Bitmap bitmap;
		Paint paint = new Paint();

		if (gameSession != null) {
			for (int x = 0; x < gameSession.getWidth(); ++x) {
				for (int y = 0; y < gameSession.getHeight(); ++y) {

					pos = gameSession.getPos(x, y);

					if (gameSession.isCoveredAt(x, y))
						if (gameSession.isFlaggedAt(x, y))
							bitmap = palette[GameSession.POSITION_FLAGGED];
						else
							bitmap = palette[GameSession.POSITION_COVERED];
					else {
						bitmap = palette[pos];
					}
					rectDst.top = (int) (-cameraPosition.y + (y * smaller));
					rectDst.left = (int) (-cameraPosition.x + (x * smaller));
					rectDst.bottom = (int) (-cameraPosition.y + ((y + 1) * smaller));
					rectDst.right = (int) (-cameraPosition.x + ((x + 1) * smaller));

					rectSrc.top = 0;
					rectSrc.left = 0;
					rectSrc.right = bitmap.getWidth();
					rectSrc.bottom = bitmap.getHeight();

					canvas.drawBitmap(bitmap, rectSrc, rectDst, paint);
				}
			}
		} else {
			paint.setColor(Color.MAGENTA);
			canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
		}
	}

	public void revealAll() {
		if (gameSession != null) {
			for (int x = 0; x < gameSession.getWidth(); ++x) {
				for (int y = 0; y < gameSession.getHeight(); ++y) {
					gameSession.uncoverAt(x, y);
				}
			}
		}

	}
	
	public boolean onTouch(View v, MotionEvent event) {

		Vector2 touch = new Vector2();
		int downX;
		int downY;
		float diffX;
		float diffY;
		int newWidth = getWidth() / gameSession.getWidth();
		int newHeight = getHeight() / gameSession.getHeight();
		int smaller;

		touch.x = cameraPosition.x + ((event.getX()));
		touch.y = cameraPosition.y + ((event.getY()));

		if (newWidth <= newHeight)
			smaller = newHeight;
		else
			smaller = newWidth;

		downX = (int) ((touch.x / smaller));
		downY = (int) ((touch.y / smaller));

		switch (playerAction) {

		case POKE:
			
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				pressTime = System.currentTimeMillis();
			} 
			
			if (event.getAction() == MotionEvent.ACTION_UP) {
				releaseTime = System.currentTimeMillis();

				if ( ( releaseTime - pressTime ) > 1000 ) {
					gameSession.flag(downX, downY);
					invalidate();
					return true;			
				} else {
					gameSession.poke(downX, downY);
				}
				
				pressTime = -1;
				releaseTime = -1;
			}		
			break;

		case FLAG:

			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				gameSession.flag(downX, downY);
			}
			break;

		case MOVE:

			if (event.getAction() == MotionEvent.ACTION_MOVE) {

				cameraPosition.x += (lastTouchPosition.x - event.getX());
				cameraPosition.y += (lastTouchPosition.y - event.getY());
			}
			break;

		default:

		}

		lastTouchPosition.x = (int) event.getX();
		lastTouchPosition.y = (int) event.getY();

		if (gameSession.isFinished()) {
			revealAll();
			this.postDelayed(new FinishGameRunnable(), 5000);
		}

		if (cameraPosition.x < -getWidth() + smaller)
			cameraPosition.x = -getWidth() + smaller;

		if (cameraPosition.y < -getHeight() + smaller)
			cameraPosition.y = -getHeight() + smaller;

		if (cameraPosition.x > (this.getWidth() - smaller))
			cameraPosition.x = getWidth() - smaller;

		if (cameraPosition.y > (this.getHeight() - smaller))
			cameraPosition.y = getHeight() - smaller;

		this.invalidate();
		return true;

	}

	public void setToFlag() {

		playerAction = MinefieldOperations.FLAG;
	}

	public void setToPoke() {

		playerAction = MinefieldOperations.POKE;
	}

	public void setToMove() {

		playerAction = MinefieldOperations.MOVE;
	}
}
