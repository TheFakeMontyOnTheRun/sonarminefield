/**
 *
 */
package br.odb.sonarminefield

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

/**
 * @author monty
 */
class GameBoard : View, OnTouchListener {
    private var smaller = 0
    var gameSession: GameSession? = null
    private lateinit var palette: Array<Bitmap?>
    var manager: PlayGameActivity? = null
    private var cameraPosition: Position2D? = null
    private var lastTouchPosition: Position2D? = null
    private var playerAction = MinefieldOperations.POKE
    private var pressTime: Long = 0
    private var releaseTime: Long = 0

    constructor(appContext: Context, attrs: AttributeSet?, defStyle: Int) : super(
        appContext,
        attrs,
        defStyle
    ) {
        init(appContext)
    }

    constructor(appContext: Context) : super(appContext) {
        init(appContext)
    }

    constructor(appContext: Context, set: AttributeSet?) : super(appContext, set) {
        init(appContext)
    }

    private fun init(appContext: Context) {
        count = 0
        manager = appContext as PlayGameActivity
        palette = arrayOfNulls(13)
        palette[0] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.blanksvg
        )
        palette[1] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.n1svg
        )
        palette[2] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.n2svg
        )
        palette[3] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.n3svg
        )
        palette[4] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.n4svg
        )
        palette[5] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.n5svg
        )
        palette[6] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.n6svg
        )
        palette[7] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.n7svg
        )
        palette[8] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.n8svg
        )
        palette[9] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.minesvg
        )
        palette[10] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.coveredsvg
        )
        palette[11] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.minespokedvg
        )
        palette[12] = BitmapFactory.decodeResource(
            appContext.getResources(),
            R.drawable.flagged
        )
        cameraPosition = Position2D()
        lastTouchPosition = Position2D()
        setToPoke()
        setOnTouchListener(this)
    }

    fun setSession(session: GameSession?) {
        gameSession = session
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (gameSession == null) {
            return;
        }

        val rectSrc = Rect()
        val rectDst = Rect()
        val newWidth: Int = width / gameSession!!.width
        val newHeight: Int = height / gameSession!!.height
        smaller = if (newWidth <= newHeight) newHeight else newWidth
        var pos: Int
        var bitmap: Bitmap?
        val paint = Paint()
        if (gameSession != null) {
            for (x in 0 until gameSession!!.width) {
                for (y in 0 until gameSession!!.height) {
                    pos = gameSession!!.getPos(x, y)
                    bitmap = if (gameSession!!.isCoveredAt(x, y)) if (gameSession!!.isFlaggedAt(
                            x,
                            y
                        )
                    ) palette[GameSession.POSITION_FLAGGED] else palette[GameSession.POSITION_COVERED] else {
                        palette[pos]
                    }
                    rectDst.top = (-cameraPosition!!.y + y * smaller)
                    rectDst.left = (-cameraPosition!!.x + x * smaller)
                    rectDst.bottom = (-cameraPosition!!.y + (y + 1) * smaller)
                    rectDst.right = (-cameraPosition!!.x + (x + 1) * smaller)
                    rectSrc.top = 0
                    rectSrc.left = 0
                    rectSrc.right = bitmap!!.width
                    rectSrc.bottom = bitmap.height
                    canvas.drawBitmap(bitmap, rectSrc, rectDst, paint)
                }
            }
        } else {
            paint.color = Color.MAGENTA
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }
    }

    private fun revealAll() {
        if (gameSession != null) {
            for (x in 0 until gameSession!!.width) {
                for (y in 0 until gameSession!!.height) {
                    gameSession!!.uncoverAt(x, y)
                }
            }
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val touch = Position2D()
        val downX: Int
        val downY: Int
        val newWidth: Int = width / gameSession!!.width
        val newHeight: Int = height / gameSession!!.height
        touch.x = (cameraPosition!!.x + event.x).toInt()
        touch.y = (cameraPosition!!.y + event.y).toInt()
        val smaller: Int = if (newWidth <= newHeight) newHeight else newWidth
        downX = (touch.x / smaller)
        downY = (touch.y / smaller)
        when (playerAction) {
            MinefieldOperations.POKE -> {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    pressTime = System.currentTimeMillis()
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    releaseTime = System.currentTimeMillis()
                    if (releaseTime - pressTime > 1000) {
                        gameSession!!.flag(downX, downY)
                        invalidate()
                        return true
                    } else {
                        gameSession!!.poke(downX, downY)
                    }
                    pressTime = -1
                    releaseTime = -1
                }
            }
            MinefieldOperations.FLAG -> if (event.action == MotionEvent.ACTION_DOWN) {
                gameSession!!.flag(downX, downY)
            }
            MinefieldOperations.MOVE -> if (event.action == MotionEvent.ACTION_MOVE) {
                cameraPosition!!.x += lastTouchPosition!!.x - event.x.toInt()
                cameraPosition!!.y += lastTouchPosition!!.y - event.y.toInt()
            }
        }

        lastTouchPosition!!.x = event.x.toInt()
        lastTouchPosition!!.y = event.y.toInt()
        if (gameSession!!.isFinished) {
            revealAll()
            postDelayed(FinishGameRunnable(), 5000)
        }
        if (cameraPosition!!.x < -width + smaller) cameraPosition!!.x = (-width + smaller)
        if (cameraPosition!!.y < -height + smaller) cameraPosition!!.y =
            (-height + smaller)
        if (cameraPosition!!.x > this.width - smaller) cameraPosition!!.x =
            (width - smaller)
        if (cameraPosition!!.y > this.height - smaller) cameraPosition!!.y =
            (height - smaller)
        this.invalidate()
        return true
    }

    fun setToFlag() {
        playerAction = MinefieldOperations.FLAG
    }

    fun setToPoke() {
        playerAction = MinefieldOperations.POKE
    }

    fun setToMove() {
        playerAction = MinefieldOperations.MOVE
    }

    private inner class FinishGameRunnable : Runnable {
        override fun run() {
            val intent = manager!!.intent
            intent.putExtra("result", if (gameSession!!.isVictory) "victory" else "failure")
            manager!!.setResult(Activity.RESULT_OK, intent)
            manager!!.finish()
        }
    }

    companion object {
        var count = 0
    }
}