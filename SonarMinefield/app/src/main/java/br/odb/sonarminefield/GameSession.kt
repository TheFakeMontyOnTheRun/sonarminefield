package br.odb.sonarminefield

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

/**
 * @author monty
 */
class GameSession {
    private val map: Array<IntArray?>
    private val covered: Array<BooleanArray?>
    private val flagged: Array<BooleanArray?>
    private var remainingTilesToClear: Int
    private var mines = 0

    val width = 10
    val height = 15

    companion object {
        const val POSITION_MINE = 9
        const val POSITION_COVERED = 10
        const val POSITION_MINE_POKED = 11
        const val POSITION_FLAGGED = 12
        const val POSITION_BLANK = 0
    }

    fun placeRandomMines(n: Int) {
        var n = n
        var x: Int
        var y: Int
        val random = Random()
        mines = n
        while (n > 0) {
            x = random.nextInt(width - 2) + 1
            y = random.nextInt(height - 2) + 1
            if (map[y]!![x] != POSITION_MINE) {
                --n
                map[y]!![x] = POSITION_MINE
            }
        }
        placeNumbersOnBoard()
    }

    private fun placeNumbersOnBoard() {
        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                if (map[y]!![x] == POSITION_MINE) {
                    if (map[y - 1]!![x - 1] != POSITION_MINE) ++map[y - 1]!![x - 1]
                    if (map[y]!![x - 1] != POSITION_MINE) ++map[y]!![x - 1]
                    if (map[y + 1]!![x - 1] != POSITION_MINE) ++map[y + 1]!![x - 1]
                    if (map[y - 1]!![x] != POSITION_MINE) ++map[y - 1]!![x]
                    if (map[y + 1]!![x] != POSITION_MINE) ++map[y + 1]!![x]
                    if (map[y - 1]!![x + 1] != POSITION_MINE) ++map[y - 1]!![x + 1]
                    if (map[y]!![x + 1] != POSITION_MINE) ++map[y]!![x + 1]
                    if (map[y + 1]!![x + 1] != POSITION_MINE) ++map[y + 1]!![x + 1]
                }
            }
        }
    }

    fun isCoveredAt(x: Int, y: Int): Boolean {
        return if (x < 0 || x >= width || y < 0 || y >= height) true else covered[y]!![x]
    }

    fun getPos(x: Int, y: Int): Int {
        return if (x < 0 || x >= width || y < 0 || y >= height) POSITION_BLANK else map[y]!![x]
    }

    fun poke(x: Int, y: Int) : GameBoard.GameOutcome {

        if (x < 0 || x >= width || y < 0 || y >= height) return GameBoard.GameOutcome.kPlaying

        if (flagged[y]!![x]) {
            flagged[y]!![x] = false
            return GameBoard.GameOutcome.kPlaying
        }
        when (map[y]!![x]) {
            POSITION_MINE -> {
                map[y]!![x] = POSITION_MINE_POKED
                return GameBoard.GameOutcome.kLost
            }
            POSITION_BLANK -> {
                floodUncover(x, y)
                if (covered[y]!![x] && !flagged[y]!![x]) remainingTilesToClear--
                covered[y]!![x] = false
                if (covered[y]!![x]) remainingTilesToClear--
                covered[y]!![x] = false
            }
            else -> {
                if (covered[y]!![x]) remainingTilesToClear--
                covered[y]!![x] = false
            }
        }
        if (remainingTilesToClear == mines) return GameBoard.GameOutcome.kWon


        return GameBoard.GameOutcome.kPlaying
    }

    private fun floodUncover(x: Int, y: Int) {
        if (covered[y]!![x] && !flagged[y]!![x]) {
            if (map[y]!![x] == POSITION_BLANK) {
                if (covered[y]!![x]) remainingTilesToClear--
                covered[y]!![x] = false
                if (x > 0) floodUncover(x - 1, y)
                if (x + 1 < width) floodUncover(x + 1, y)
                if (y > 0) floodUncover(x, y - 1)
                if (y + 1 < height) floodUncover(x, y + 1)
                if (x > 0 && y > 0) floodUncover(x - 1, y - 1)
                if (x + 1 < width && y + 1 < height) floodUncover(x + 1, y + 1)
                if (y > 0 && x + 1 < width) floodUncover(x + 1, y - 1)
                if (y + 1 < height && x > 0) floodUncover(x - 1, y + 1)
            }
            if (map[y]!![x] != POSITION_MINE) {
                if (covered[y]!![x]) remainingTilesToClear--
                covered[y]!![x] = false
            }
        }
    }

    fun uncoverAt(x: Int, y: Int) {
        if (isValid(x, y)) {
            covered[y]!![x] = false
            flagged[y]!![x] = false
        }
    }

    fun flag(x: Int, y: Int) {
        if (isValid(x, y)) flagged[y]!![x] = !flagged[y]!![x]
    }

    fun isFlaggedAt(x: Int, y: Int): Boolean {
        return if (isValid(x, y)) flagged[y]!![x] else false
    }

    private fun isValid(x: Int, y: Int): Boolean {
        if (x < 0) return false
        if (y < 0) return false
        return if (x >= width) false else y < height
    }

    fun saveState(outputStream: OutputStream?) {
        val dos = DataOutputStream(outputStream)
        for (c in 0 until height) {
            for (d in 0 until width) {
                dos.writeBoolean(covered[c]!![d])
                dos.writeBoolean(flagged[c]!![d])
                dos.writeInt(map[c]!![d])
            }
        }
        dos.writeInt(remainingTilesToClear)
    }

    fun loadState(inputStream: InputStream?) {
        val dis = DataInputStream(inputStream)
        for (c in 0 until height) {
            for (d in 0 until width) {
                covered[c]!![d] = dis.readBoolean()
                flagged[c]!![d] = dis.readBoolean()
                map[c]!![d] = dis.readInt()
            }
        }
        remainingTilesToClear = dis.readInt()
    }

    fun clearBorders() {
        for (c in 0 until height) {
            poke(0, c)
            poke(width - 1, c)
        }
        for (d in 0 until width) {
            poke(d, 0)
            poke(d, height - 1)
        }
    }


    init {
        map = arrayOfNulls(height)
        covered = arrayOfNulls(height)
        flagged = arrayOfNulls(height)
        for (c in 0 until height) {
            map[c] = IntArray(width)
            covered[c] = BooleanArray(width)
            flagged[c] = BooleanArray(width)
            for (d in 0 until width) {
                covered[c]!![d] = true
                flagged[c]!![d] = false
                map[c]!![d] = POSITION_BLANK
            }
        }
        remainingTilesToClear = width * height
    }
}