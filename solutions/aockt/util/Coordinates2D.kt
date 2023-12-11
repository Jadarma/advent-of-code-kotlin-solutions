package aockt.util

import kotlin.math.absoluteValue

/** Represents a discrete point in 2D space. */
data class Point2D(val x: Long, val y: Long) {

    /** Alternate constructor that converts from integer coordinates, for convenience. */
    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())
}

/** Returns the cartesian distance between two points. */
infix fun Point2D.distanceTo(other: Point2D): Long = (x - other.x).absoluteValue + (y - other.y).absoluteValue

/**
 * A rectangular region defined by coordinate ranges.
 *
 * @property width  The width of the region.
 * @property height The height of the region.
 * @property xRange The value range for the x coordinate of all points within the region.
 * @property yRange The value range for the y coordinate of all points within the region.
 */
data class Region2D(val xRange: LongRange, val yRange: LongRange) : Iterable<Point2D> {

    /** Alternate constructor that calculates the region from the [bottomLeft] and [topRight] points of a rectangle. */
    constructor(bottomLeft: Point2D, topRight: Point2D) : this(bottomLeft.x..topRight.x, bottomLeft.y..topRight.y)

    /** Alternate constructor that converts from integer ranges, for convenience. */
    constructor(xRange: IntRange, yRange: IntRange) : this (xRange.first.toLong() .. xRange.last.toLong(), yRange.first.toLong() .. yRange.last.toLong())

    val width: Long get() = xRange.last - xRange.first + 1
    val height: Long get() = yRange.last - yRange.first + 1

    operator fun contains(point: Point2D) = point.x in xRange && point.y in yRange

    override fun iterator(): Iterator<Point2D> = iterator {
        for (x in xRange) {
            for (y in yRange) {
                yield(Point2D(x, y))
            }
        }
    }
}

/** Returns a new region, removing all points that are outside the bounds of the [other] region. */
fun Region2D.coerceIn(other: Region2D) = Region2D(
    xRange = maxOf(xRange.first, other.xRange.first) .. minOf(xRange.last, other.xRange.last),
    yRange = maxOf(yRange.first, other.yRange.first) .. minOf(yRange.last, other.yRange.last),
)
