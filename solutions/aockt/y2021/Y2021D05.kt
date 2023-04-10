package aockt.y2021

import io.github.jadarma.aockt.core.Solution
import kotlin.math.sign

object Y2021D05 : Solution {

    /** Represents a discrete point in 2D space. */
    private data class Point(val x: Int, val y: Int) {
        operator fun plus(delta: Delta): Point = Point(x + delta.dx, y + delta.dy)
    }

    /** Represents a direction vector in 2D space. */
    private data class Delta(val dx: Int, val dy: Int)

    /** Represents a line in 2D space. */
    private data class Line(val p1: Point, val p2: Point) {

        init {
            require(p1 != p2) { "A line must connect two distinct points." }
        }

        /** Get the delta step between the two points, or the direction vector of a unitary step. */
        val pointDelta: Delta get() = Delta((p2.x - p1.x).sign, (p2.y - p1.y).sign)
    }

    /** Holds a square map with side of give [size] of an ocean floor, used to track vents. */
    private class OceanFloor(val size: Int) {
        private val data = ByteArray(size * size) { 0 }
        private var _intersections: Int = 0

        operator fun get(point: Point): Boolean = data[point.x * size + point.y] > 0
        operator fun set(point: Point, value: Boolean) {
            val current = data[point.x * size + point.y]
            data[point.x * size + point.y] = when (value) {
                true -> {
                    if (current == 1.toByte()) _intersections++
                    if (current < Byte.MAX_VALUE) current.inc() else 0.toByte()
                }

                false -> {
                    if (current == 2.toByte()) _intersections--
                    maxOf(current.dec(), 0.toByte())
                }
            }
        }

        /** The number of points where at least two lines intersect, making it especially dangerous. */
        fun intersections(): Int = _intersections

        /** Mark the [line] as dangerous. */
        fun setLine(line: Line) {
            var pencil = line.p1
            val delta = line.pointDelta
            this[pencil] = true
            while (pencil != line.p2) {
                pencil += delta
                this[pencil] = true
            }
        }
    }

    /** The format of each line of input. */
    private val lineRegex = Regex("""^(\d+),(\d+) -> (\d+),(\d+)$""")

    /** Parse the input and return a sequence of [Line]s defined in it. */
    private fun parseInput(input: String): Sequence<Line> = input
        .lineSequence()
        .map { lineRegex.matchEntire(it)?.groupValues ?: throw IllegalArgumentException() }
        .map { group -> group.drop(1).map { it.toIntOrNull() ?: throw IllegalArgumentException() } }
        .map { (x1, y1, x2, y2) -> Line(Point(x1, y1), Point(x2, y2)) }

    // Might be nice to determine the map bounds manually instead of defaulting to 1000x1000, but that would
    // require either parsing the input twice, or holding it all in memory.
    override fun partOne(input: String): Any = parseInput(input)
        .filter { with(it.pointDelta) { dx == 0 || dy == 0 } }
        .fold(OceanFloor(1000)) { acc, line -> acc.apply { setLine(line) } }
        .intersections()

    override fun partTwo(input: String): Any = parseInput(input)
        .fold(OceanFloor(1000)) { acc, line -> acc.apply { setLine(line) } }
        .intersections()
}
