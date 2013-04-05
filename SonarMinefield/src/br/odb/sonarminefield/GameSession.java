/**
 * 
 */
package br.odb.sonarminefield;

import java.util.Random;

/**
 * @author monty
 *
 */
public class GameSession {

	public static final int WIDTH = 15;
	public static final int HEIGHT = 10;
	
	public static final int POSITION_MINE = 9;
	public static final int POSITION_COVERED = 10;
	public static final int POSITION_MINE_POKED = 11;
	public static final int POSITION_FLAGGED = 12;
	public static final int POSITION_BLANK = 0;
	
	public static final int GAMESTATE_NOTSTARTED = 0;
	public static final int GAMESTATE_STARTED = 1;
	public static final int GAMESTATE_FININSHED = 2;
	public static final int GAMESTATE_GAMEOVER = 3;
	
	private int[][] map;
	private boolean[][] covered;
	private boolean[][] flagged;
	private int remainingTilesToClear;
	private int mines;
	
	private int gameState;
	
	public GameSession() {
		map = new int[ WIDTH ][];
		covered = new boolean[ WIDTH ][];
		flagged = new boolean[ WIDTH ][];
		
		for ( int c = 0; c < WIDTH; ++c ) {
			
			map[ c ] = new int[ HEIGHT ];
			covered[ c ] = new boolean[ HEIGHT ];
			flagged[ c ] = new boolean[ HEIGHT ];
			
			for ( int d = 0; d < HEIGHT; ++d ) {
				covered[ c ][ d ] = true;
				flagged[ c ][ d ] = false;
				map[ c ][ d ] = POSITION_BLANK;
			}
		}
		remainingTilesToClear = WIDTH * HEIGHT;
	}
	
	public void placeRandomMines( int n ) {
		
		int x;
		int y;
		Random random = new Random();
		mines = n;
		while ( n > 0) {
			
			x = random.nextInt( WIDTH - 2 ) + 1;
			y = random.nextInt( HEIGHT - 2 ) + 1;
			
			if ( map[ x ][ y ] != POSITION_MINE ) {

				--n;			
				map[ x ][ y ] = POSITION_MINE;				
			}
		}
		
		
		placeNumbersOnBoard();
	}
	 
	private void placeNumbersOnBoard() {
		for ( int x = 1; x < getWidth() - 1; ++x ) {
			for ( int y = 1; y < getHeight() - 1; ++y ) {
				if ( map[ x ][ y ] == POSITION_MINE ) {
					
					if ( map[ x - 1 ][ y - 1 ] != POSITION_MINE)
						++map[ x - 1 ][ y - 1 ];
					
					if ( map[ x ][ y - 1 ] != POSITION_MINE)						
						++map[ x ][ y - 1];
					
					if ( map[ x + 1 ][ y - 1 ] != POSITION_MINE)
						++map[ x + 1 ][ y - 1 ];
					
					if ( map[ x - 1 ][ y ] != POSITION_MINE)					
						++map[ x - 1][ y ];
					
					if ( map[ x + 1 ][ y ] != POSITION_MINE)					
						++map[ x + 1 ][ y ];
					
					if ( map[ x - 1 ][ y + 1 ] != POSITION_MINE)					
						++map[ x - 1 ][ y + 1 ];
					
					if ( map[ x ][ y + 1 ] != POSITION_MINE)					
						++map[ x ][ y + 1 ];
					
					if ( map[ x + 1 ][ y + 1 ] != POSITION_MINE)					
						++map[ x + 1 ][ y + 1 ];					
				}
			}
		}		
	}

	public boolean isCoveredAt( int x, int y ) {

		if ( x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT )
			return true;

		return covered[ x ][ y ];
	}
	
	public int getWidth() {

		return WIDTH;
	}

	public int getHeight() {

		return HEIGHT;
	}

	public int getPos(int x, int y) {

		if ( x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT )
			return POSITION_BLANK;

		return map[ x ][ y ];
	}

	public void poke(int x, int y) {
		
		if ( x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT )
			return;
		
		if ( flagged[ x][ y ] )	{
			
			flagged[ x ][ y ] = false;
			return;
		}
		
		
		switch ( map[ x ][ y ] ) {
			case POSITION_MINE:
				
				 map[ x ][ y ] = this.POSITION_MINE_POKED;
				gameState = this.GAMESTATE_GAMEOVER;
				return;

			case POSITION_BLANK:					
					floodUncover( x, y );
					
					if ( covered[ x ][ y ] && !flagged[ x ][ y ] )
						this.remainingTilesToClear--;
					
					covered[ x ][ y ] = false;					
			default:
					
					if ( covered[ x ][ y ]  )
						this.remainingTilesToClear--;
					
					covered[ x ][ y ] = false;
		}
		
		if ( this.remainingTilesToClear == mines )
			gameState = gameState = this.GAMESTATE_FININSHED;
		

	}


	private void floodUncover(int x, int y) {
		if ( covered[ x ][ y ] && !flagged[ x ][ y ] ) {
			
			if ( map[ x ][ y ] == POSITION_BLANK ) {
				
				if ( covered[ x ][ y ] )
					this.remainingTilesToClear--;
				
				covered[ x ][ y ] = false;
				
				if ( x > 0)
					floodUncover( x - 1, y );
				
				if ( ( x + 1 ) < WIDTH )
					floodUncover( x + 1, y );
				
				if ( y > 0 )
					floodUncover( x, y -1 );
				
				if ( ( y + 1 ) < HEIGHT )
					floodUncover( x, y + 1);
				
				if ( x > 0 && y > 0 )
					floodUncover( x - 1, y - 1 );
				
				if ( ( x + 1 ) < WIDTH && ( y + 1 ) < HEIGHT )
					floodUncover( x + 1, y + 1 );
				
				if ( y > 0 && ( x + 1 ) < WIDTH )
					floodUncover( x + 1, y -1 );
				
				if ( ( y + 1 ) < HEIGHT && x > 0 )
					floodUncover( x - 1, y + 1);				
				
				
			} if ( map[ x ][ y ] != POSITION_MINE ) {
				
				if ( covered[ x ][ y ] )
					this.remainingTilesToClear--;
				
				covered[ x ][ y ] = false;
			}
		}		
	}

	public boolean isFinished() {
		
		return gameState == this.GAMESTATE_GAMEOVER || isVictory();
	}

	public boolean isVictory() {
		
		return gameState == this.GAMESTATE_FININSHED;
	}

	public void uncoverAt(int x, int y) {
		
		if ( isValid( x, y ) ) {
			covered[ x ][ y ] = false;
			flagged[ x ][ y ] = false;
		}
	}

	public void flag(int x, int y) {
		
		if ( isValid( x, y ) )		
			flagged[ x ][ y ] = !flagged[ x ][ y ];		
	}

	public boolean isFlaggedAt(int x, int y) {

		if ( isValid( x, y ) )
			return flagged[ x ][ y ];
		else
			return false;
	}

	private boolean isValid(int x, int y) {
	
		if ( x < 0 )
			return false;
		
		if ( y < 0 )
			return false;

		if ( x >= WIDTH )
			return false;

		if ( y >= HEIGHT )
			return false;
		
		return true;
	}
}
