package aockt.y2022

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2022D15 : Solution {

    /** Represents a discrete point in 2D space. */
    private data class Point(val x: Int, val y: Int)

    /** Returns the Manhattan distance between the two points. */
    private infix fun Point.manhattanDistanceTo(other: Point): Int =
        (x - other.x).absoluteValue + (y - other.y).absoluteValue

    /** Generates all points that are [radius] units away (using Manhattan distance) from the original point. */
    private fun Point.manhattanCircle(radius: Int): Sequence<Point> = sequence {
        var cx = x - radius
        var cy = y
        repeat(radius) { yield(Point(cx, cy)); cx++; cy++ }
        repeat(radius) { yield(Point(cx, cy)); cx++; cy-- }
        repeat(radius) { yield(Point(cx, cy)); cx--; cy-- }
        repeat(radius) { yield(Point(cx, cy)); cx--; cy++ }
    }

    /** A distress beacon sensor and its current detection [range]. */
    private data class Sensor(val location: Point, val range: Int)

    /** Tests if the [point] is inside the sensor's detection range. */
    private fun Sensor.observes(point: Point): Boolean = location.manhattanDistanceTo(point) <= range

    /** Expression that validates a sensor reading. */
    private val inputRegex = Regex("""^Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)$""")

    /** Parse the [input] and return the sequence of sensor readings as pairs of sensor to beacon locations. */
    private fun parseInput(input: String): Map<Sensor, Point> =
        input
            .lineSequence()
            .map { line -> inputRegex.matchEntire(line)!!.groupValues.drop(1).map(String::toInt) }
            .associate { (sx, sy, bx, by) ->
                val sensor = Point(sx, sy)
                val beacon = Point(bx, by)
                Sensor(sensor, sensor.manhattanDistanceTo(beacon)) to beacon
            }

    override fun partOne(input: String): Int {
        val scan = parseInput(input)
        val range = scan.keys.minOf { it.location.x - it.range }.rangeTo(scan.keys.maxOf { it.location.x + it.range })
        val targetLine = 2_000_000
        val beaconsOnLine = scan.values.filter { it.y == targetLine }.toSet()

        return range.count { x ->
            val p = Point(x, targetLine)
            p !in beaconsOnLine && scan.keys.any { it.observes(p) }
        }
    }

    override fun partTwo(input: String): Long {
        val scan = parseInput(input)
        val scanRange = 0..4_000_000

        return scan.keys
            .asSequence()
            .flatMap { (location, range) -> location.manhattanCircle(range + 1) }
            .filter { it.x in scanRange && it.y in scanRange }
            .first { p -> scan.keys.none { it.observes(p) } }
            .let { it.x * 4_000_000L + it.y }
    }
}
