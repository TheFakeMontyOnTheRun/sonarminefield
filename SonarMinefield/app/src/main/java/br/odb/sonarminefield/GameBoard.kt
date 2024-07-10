package br.odb.sonarminefield

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.RadioGroup
import androidx.lifecycle.MutableLiveData
import kotlin.math.ceil

class GameBoard : View, OnTouchListener, RadioGroup.OnCheckedChangeListener {
    private var revealed: Boolean = false
    private var smaller = 0
    private var gameSession: GameSession? = null
    private lateinit var palette: Array<Bitmap?>
    private var manager: PlayGameActivity? = null
    private var cameraPosition: Position2D? = null
    private var lastTouchPosition: Position2D? = null
    private var playerAction = MinefieldOperations.POKE
    private var pressTime: Long = 0
    private var releaseTime: Long = 0

    enum class GameOutcome {
        PLAYING,
        WON,
        LOST
    }

    val outcome = MutableLiveData<GameOutcome>()

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
        playerAction = MinefieldOperations.POKE
        setOnTouchListener(this)

        outcome.value = GameOutcome.PLAYING
    }

    fun setSession(session: GameSession?) {
        gameSession = session
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (gameSession == null) {
            return
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
            revealed = true
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

        val diffX = lastTouchPosition!!.x - event.x.toInt()
        val diffY = lastTouchPosition!!.y - event.y.toInt()

        when (playerAction) {
            MinefieldOperations.POKE -> if (!revealed) {
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
                        outcome.value = gameSession!!.poke(downX, downY)
                    }
                    pressTime = -1
                    releaseTime = -1
                }
            }

            MinefieldOperations.FLAG -> if (event.action == MotionEvent.ACTION_DOWN) {
                gameSession!!.flag(downX, downY)
            }

            MinefieldOperations.MOVE -> if (event.action == MotionEvent.ACTION_MOVE) {
                cameraPosition!!.x += diffX
                cameraPosition!!.y += diffY
            }
        }

        lastTouchPosition!!.x = event.x.toInt()
        lastTouchPosition!!.y = event.y.toInt()

        if (outcome.value != GameOutcome.PLAYING) {
            revealAll()
        }

        if (cameraPosition!!.x < (-smaller)) {
            cameraPosition!!.x = (-smaller)
        }

        if (cameraPosition!!.y < (-smaller)) {
            cameraPosition!!.y = (-smaller)
        }

        if (cameraPosition!!.x > (this.width - ((gameSession!!.width - 1) * smaller))) {
            cameraPosition!!.x = (width - ((gameSession!!.width - 1) * smaller))
        }


        val heightInTiles = ceil((height / smaller).toDouble()).toInt()

        if (cameraPosition!!.y > (((gameSession!!.height - heightInTiles + 1) * smaller))) {
            cameraPosition!!.y = (((gameSession!!.height - heightInTiles + 1) * smaller))
        }

        this.invalidate()
        return true
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.rdoBrowse ->
                playerAction = MinefieldOperations.MOVE

            R.id.rdoFlag ->
                playerAction = MinefieldOperations.FLAG

            R.id.rdoReveal ->
                playerAction = MinefieldOperations.POKE
        }
    }
}