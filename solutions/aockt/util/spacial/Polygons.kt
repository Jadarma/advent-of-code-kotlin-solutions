package aockt.util.spacial

import kotlin.math.absoluteValue


// TODO: Add unit test, and consider whether having a list instead of an iterable is enough.
/**
 * Calculates the area of a polygon defined by the given points using the shoelace formula, given the following:
 * - There are at least 3 points.
 * - The points should be distinct.
 * - The polygon is drawn on a grid, with only 90 degree bends.
 *
 * @param includingPerimeter Whether to include the points defining the perimeter as part of the area.
 */
fun Iterable<Point>.polygonArea(includingPerimeter: Boolean = true): Long {
    val iter = iterator()
    if(!iter.hasNext()) return 0
    val start = iter.next()
    var perimeter = 0L
    var points = 1L
    var sum = 0L
    var last = start

    fun continueLace(from: Point, until: Point) {
        check(from.x == until.x || from.y == until.y) { "Polygon corners are not 90 degrees!" }
        perimeter += from distanceTo until
        sum += from.x * until.y
        sum -= from.y * until.x
    }

    while(iter.hasNext()) {
        val next = iter.next()
        points += 1
        continueLace(last, next)
        last = next
    }

    continueLace(last, start)

    val insideArea = sum.absoluteValue / 2 - perimeter / 2 + 1

    return when {
        points < 3 -> 0
        includingPerimeter -> insideArea + perimeter
        else -> insideArea
    }
}
