package aockt.util.spacial

import kotlin.math.absoluteValue

/**
 * Represents a discrete point in 2D space.
 *
 * **NOTE:** _The origin point is by convention on the bottom-left-hand side._
 *
 * @property x The horizontal component, positive values on the right of the origin.
 * @property y The vertical component, positive values on above the origin.
 */
data class Point(val x: Long, val y: Long) {

    /** Alternate constructor that converts from integer coordinates, for convenience. */
    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())

    /** Encoded as a human and debugger friendly way. */
    override fun toString(): String = "($x, $y)"
}

/**
 * Returns the distance between two points on a discrete cartesian plane.
 * Since the plane is a grid, this is also the manhattan distance.
 */
infix fun Point.distanceTo(other: Point): Long = (x - other.x).absoluteValue + (y - other.y).absoluteValue
