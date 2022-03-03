/**
 *
 */
package br.odb.sonarminefield;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author monty
 *
 */
public class GameSession {

    public static final int WIDTH = 10;
    public static final int HEIGHT = 15;

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
        map = new int[HEIGHT][];
        covered = new boolean[HEIGHT][];
        flagged = new boolean[HEIGHT][];

        for (int c = 0; c < HEIGHT; ++c) {

            map[c] = new int[WIDTH];
            covered[c] = new boolean[WIDTH];
            flagged[c] = new boolean[WIDTH];

            for (int d = 0; d < WIDTH; ++d) {
                covered[c][d] = true;
                flagged[c][d] = false;
                map[c][d] = POSITION_BLANK;
            }
        }
        remainingTilesToClear = WIDTH * HEIGHT;
    }

    public void placeRandomMines(int n) {

        int x;
        int y;
        Random random = new Random();
        mines = n;
        while (n > 0) {

            x = random.nextInt(WIDTH - 2) + 1;
            y = random.nextInt(HEIGHT - 2) + 1;

            if (map[y][x] != POSITION_MINE) {

                --n;
                map[y][x] = POSITION_MINE;
            }
        }


        placeNumbersOnBoard();
    }

    private void placeNumbersOnBoard() {
        for (int y = 1; y < getHeight() - 1; ++y) {
            for (int x = 1; x < getWidth() - 1; ++x) {
                if (map[y][x] == POSITION_MINE) {

                    if (map[y - 1][x - 1] != POSITION_MINE)
                        ++map[y - 1][x - 1];

                    if (map[y][x - 1] != POSITION_MINE)
                        ++map[y][x - 1];

                    if (map[y + 1][x - 1] != POSITION_MINE)
                        ++map[y + 1][x - 1];

                    if (map[y - 1][x] != POSITION_MINE)
                        ++map[y - 1][x];

                    if (map[y + 1][x] != POSITION_MINE)
                        ++map[y + 1][x];

                    if (map[y - 1][x + 1] != POSITION_MINE)
                        ++map[y - 1][x + 1];

                    if (map[y][x + 1] != POSITION_MINE)
                        ++map[y][x + 1];

                    if (map[y + 1][x + 1] != POSITION_MINE)
                        ++map[y + 1][x + 1];
                }
            }
        }
    }

    public boolean isCoveredAt(int x, int y) {

        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
            return true;

        return covered[y][x];
    }

    public int getWidth() {

        return WIDTH;
    }

    public int getHeight() {

        return HEIGHT;
    }

    public int getPos(int x, int y) {

        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
            return POSITION_BLANK;

        return map[y][x];
    }

    public void poke(int x, int y) {

        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT)
            return;

        if (flagged[x][y]) {

            flagged[y][x] = false;
            return;
        }


        switch (map[y][x]) {
            case POSITION_MINE:

                map[y][x] = GameSession.POSITION_MINE_POKED;
                gameState = GameSession.GAMESTATE_GAMEOVER;
                return;

            case POSITION_BLANK:
                floodUncover(x, y);

                if (covered[y][x] && !flagged[y][x])
                    this.remainingTilesToClear--;

                covered[y][x] = false;
            default:

                if (covered[y][x])
                    this.remainingTilesToClear--;

                covered[y][x] = false;
        }

        if (this.remainingTilesToClear == mines)
            gameState = GameSession.GAMESTATE_FININSHED;


    }


    private void floodUncover(int x, int y) {
        if (covered[y][x] && !flagged[y][x]) {

            if (map[y][x] == POSITION_BLANK) {

                if (covered[y][x])
                    this.remainingTilesToClear--;

                covered[y][x] = false;

                if (x > 0)
                    floodUncover(x - 1, y);

                if ((x + 1) < WIDTH)
                    floodUncover(x + 1, y);

                if (y > 0)
                    floodUncover(x, y - 1);

                if ((y + 1) < HEIGHT)
                    floodUncover(x, y + 1);

                if (x > 0 && y > 0)
                    floodUncover(x - 1, y - 1);

                if ((x + 1) < WIDTH && (y + 1) < HEIGHT)
                    floodUncover(x + 1, y + 1);

                if (y > 0 && (x + 1) < WIDTH)
                    floodUncover(x + 1, y - 1);

                if ((y + 1) < HEIGHT && x > 0)
                    floodUncover(x - 1, y + 1);


            }
            if (map[y][x] != POSITION_MINE) {

                if (covered[y][x])
                    this.remainingTilesToClear--;

                covered[y][x] = false;
            }
        }
    }

    public boolean isFinished() {

        return gameState == GameSession.GAMESTATE_GAMEOVER || isVictory();
    }

    public boolean isVictory() {

        return gameState == GameSession.GAMESTATE_FININSHED;
    }

    public void uncoverAt(int x, int y) {

        if (isValid(x, y)) {
            covered[y][x] = false;
            flagged[y][x] = false;
        }
    }

    public void flag(int x, int y) {

        if (isValid(x, y))
            flagged[y][x] = !flagged[y][x];
    }

    public boolean isFlaggedAt(int x, int y) {

        if (isValid(x, y))
            return flagged[y][x];
        else
            return false;
    }

    private boolean isValid(int x, int y) {

        if (x < 0)
            return false;

        if (y < 0)
            return false;

        if (x >= WIDTH)
            return false;

        return y < HEIGHT;
    }

    public void saveState(OutputStream os) throws IOException {

        DataOutputStream dos = new DataOutputStream(os);

        for (int c = 0; c < HEIGHT; ++c) {

            for (int d = 0; d < WIDTH; ++d) {
                dos.writeBoolean(covered[c][d]);
                dos.writeBoolean(flagged[c][d]);
                dos.writeInt(map[c][d]);
            }
        }
        dos.writeInt(remainingTilesToClear);
    }

    public void loadState(InputStream is) throws IOException {

        DataInputStream dis = new DataInputStream(is);

        for (int c = 0; c < HEIGHT; ++c) {

            for (int d = 0; d < WIDTH; ++d) {

                covered[c][d] = dis.readBoolean();
                flagged[c][d] = dis.readBoolean();
                map[c][d] = dis.readInt();
            }
        }
        remainingTilesToClear = dis.readInt();
    }
}
