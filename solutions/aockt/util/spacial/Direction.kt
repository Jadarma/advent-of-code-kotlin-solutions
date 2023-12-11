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
}

/** Returns the direction opposite this one. */
val Direction.opposite: Direction
    get() = when(this) {
    Left -> Right
    Right -> Left
    Down -> Up
    Up -> Down
}

/** Returns the point one unit distance away from this one in a given [direction]. */
fun Point.move(direction: Direction): Point = when(direction) {
    Left -> Point(x - 1, y)
    Right -> Point(x + 1, y)
    Down -> Point(x, y - 1)
    Up -> Point(x, y + 1)
}
