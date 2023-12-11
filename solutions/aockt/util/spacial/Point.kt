package aockt.util.spacial

import kotlin.math.absoluteValue

/** Represents a discrete point in 2D space. */
data class Point(val x: Long, val y: Long) {

    /** Alternate constructor that converts from integer coordinates, for convenience. */
    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())
}

/**
 * Returns the distance between two points on a discrete cartesian plane.
 * Since the plane is a grid, this is also the manhattan distance.
 */
infix fun Point.distanceTo(other: Point): Long = (x - other.x).absoluteValue + (y - other.y).absoluteValue
