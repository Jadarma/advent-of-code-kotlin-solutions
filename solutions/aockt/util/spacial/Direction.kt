package aockt.util.spacial

import aockt.util.spacial.Direction.Down
import aockt.util.spacial.Direction.Left
import aockt.util.spacial.Direction.Right
import aockt.util.spacial.Direction.Up

/** Represents a direction to move in. */
sealed interface Direction {

    /** A horizontal direction, either left or right. */
    sealed interface Horizontal : Direction

    /** A vertical direction, either up or down. */
    sealed interface Vertical : Direction

    /** An upward direction. */
    data object Up : Vertical

    /** A downward direction. */
    data object Down : Vertical

    /** A leftward direction. */
    data object Left : Horizontal

    /** A rightward direction. */
    data object Right : Horizontal

    companion object {
        val all: List<Direction> = listOf(Up, Right, Down, Left)
    }
}

/** Returns the direction opposite this one. */
val Direction.opposite: Direction
    get() = when (this) {
        Left -> Right
        Right -> Left
        Down -> Up
        Up -> Down
    }

/** Returns the direction a quarter clockwise turn away. */
val Direction.turnedClockwise: Direction
    get() = when (this) {
        Left -> Up
        Right -> Down
        Down -> Left
        Up -> Right
    }

/** Returns the direction a quarter counter-clockwise turn away. */
val Direction.turnedCounterClockwise: Direction
    get() = when (this) {
        Left -> Down
        Right -> Up
        Down -> Right
        Up -> Left
    }

/** Returns the point one unit distance away from this one in a given [direction]. */
fun Point.move(direction: Direction, distance: Long = 1): Point = when (direction) {
    Left -> Point(x - distance, y)
    Right -> Point(x + distance, y)
    Down -> Point(x, y - distance)
    Up -> Point(x, y + distance)
}
