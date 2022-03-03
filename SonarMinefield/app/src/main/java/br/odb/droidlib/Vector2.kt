package br.odb.droidlib

import br.odb.droidlib.Vector2

class Vector2 {
    @JvmField
    var x = 0f
    @JvmField
    var y = 0f

    /**
     * Creates a new instance of Vec2
     */
    constructor(aX: Int, aY: Int) {
        x = aX.toFloat()
        y = aY.toFloat()
    }

    constructor(position: Vector2) {
        x = position.x
        y = position.y
    }

    constructor() {
        x = 0f
        y = 0f
    }

    constructor(x: Float, y: Float) {
        set(x, y)
    }

    override fun equals(o: Any?): Boolean {
        return if (o is Vector2) {
            val v = o
            v.x == x && v.y == y
        } else {
            false
        }
    }

    private operator fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun negate() {
        x = -x
        y = -y
    }

    fun sub(other: Vector2): Vector2 {
        return Vector2(x - other.x, y - other.y)
    }

    fun set(myPos: Vector2) {
        set(myPos.x, myPos.y)
    }

    fun add(other: Vector2): Vector2 {
        return Vector2(x + other.x, y + other.y)
    }

    fun normalize(): Vector2 {
        val normalized = Vector2(this)
        normalized.normalizeInPlace()
        return normalized
    }

    private fun normalizeInPlace() {
        val length = length
        x = x / length
        y = y / length
    }

    private val length: Float
        private get() = Math.sqrt((x * x + y * y).toDouble()).toFloat()

    fun scale(factor: Int): Vector2 {
        val scaled = Vector2(this)
        scaled.scaleInPlace(factor)
        return scaled
    }

    private fun scaleInPlace(factor: Int) {
        x = x * factor
        y = y * factor
    }
}