package aockt.util.spacial

import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * A rectangular region defined by coordinate ranges.
 *
 * @property width  The width of the region.
 * @property height The height of the region.
 * @property xRange The value range for the x coordinate of all points within the region.
 * @property yRange The value range for the y coordinate of all points within the region.
 */
data class Area(val xRange: LongRange, val yRange: LongRange) : Iterable<Point> {

    /** Alternate constructor that taxes a width and height and returns an area starting from the origin. */
    constructor(width: Long, height: Long) : this(0L..<width, 0L..<height)

    /** Alternate constructor that taxes a width and height and returns an area starting from the origin. */
    constructor(width: Int, height: Int) : this(0L..<width, 0L..<height)

    /** Alternate constructor that calculates the region from two corner points of a rectangle. */
    constructor(p1: Point, p2: Point) : this(
        xRange = min(p1.x, p2.x)..max(p1.x, p2.x),
        yRange = min(p1.y, p2.y)..max(p1.y, p2.y),
    )

    /** Alternate constructor that converts from integer ranges, for convenience. */
    constructor(xRange: IntRange, yRange: IntRange) : this(
        xRange = xRange.first.toLong()..xRange.last.toLong(),
        yRange = yRange.first.toLong()..yRange.last.toLong(),
    )

    /** Alternate constructor that determines the bounding box of a number of [points]. */
    constructor(points: Iterable<Point>) : this(
        xRange = points.minOf { it.x }..points.maxOf { it.x },
        yRange = points.minOf { it.y }..points.maxOf { it.y },
    )

    val width: Long get() = if (xRange.isEmpty()) 0 else xRange.last - xRange.first + 1
    val height: Long get() = if (xRange.isEmpty()) 0 else yRange.last - yRange.first + 1

    operator fun contains(point: Point) = point.x in xRange && point.y in yRange

    override fun iterator(): Iterator<Point> = iterator {
        for (x in xRange) {
            for (y in yRange) {
                yield(Point(x, y))
            }
        }
    }
}

/** Returns a new region, removing all points that are outside the bounds of the [other] region. */
fun Area.coerceIn(other: Area) = Area(
    xRange = maxOf(xRange.first, other.xRange.first)..minOf(xRange.last, other.xRange.last),
    yRange = maxOf(yRange.first, other.yRange.first)..minOf(yRange.last, other.yRange.last),
)

/** Checks whether this and the [other] areas have any points in common. */
infix fun Area.overlaps(other: Area): Boolean {
    val horizontalOverlap = maxOf(xRange.first, other.xRange.first) <= minOf(xRange.last, other.xRange.last)
    val verticalOverlap = maxOf(yRange.first, other.yRange.first) <= minOf(yRange.last, other.yRange.last)
    return horizontalOverlap && verticalOverlap
}

/** Calculates the size of the area. */
val Area.size: Long
    get() =
        (xRange.last - xRange.first).absoluteValue.inc() * (yRange.last - yRange.first).absoluteValue.inc()
