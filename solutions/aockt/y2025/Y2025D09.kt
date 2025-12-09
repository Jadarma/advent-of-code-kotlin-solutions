package aockt.y2025

import aockt.util.math.distinctPairs
import aockt.util.parse
import aockt.util.spacial.Area
import aockt.util.spacial.Point
import aockt.util.spacial.overlaps
import aockt.util.spacial.size
import io.github.jadarma.aockt.core.Solution

object Y2025D09 : Solution {

    /** A straight line segment, going from [start] to [end]. */
    private data class Edge(val start: Point, val end: Point) {

        init {
            require(start.x == end.x || start.y == end.y) { "Polygon edge cannot be diagonal." }
        }

        val area: Area = Area(start, end)
    }

    /** From a list of points, construct all possible rectangles, ordered from largest to smallest. */
    private fun List<Point>.toRectangles(): List<Area> {
        if (size < 2) return emptyList()
        return this
            .distinctPairs()
            .map { (a, b) -> Area(a, b) }
            .sortedByDescending { it.size }
            .toList()
    }

    /** From a list of points, construct a segment loop and return the edges. */
    private fun List<Point>.toEdges(): Set<Edge> {
        if (size < 2) return emptySet()
        return this
            .plusElement(this.first())
            .zipWithNext { a, b -> Edge(a, b) }
            .toSet()
    }

    /**
     * Determines if the area is valid, as in only contains red and green tiles.
     * The area is _invalid_ if _any_ of the [edges] intersects its inside
     * _(i.e.: overlapping the perimeter is allowed)_.
     *
     * _NOTE: This does not handle the edge cases where segments are "glued" to one another, but who tiles their floors
     *        like that anyway. Right?_
     */
    private fun Area.isValid(edges: Set<Edge>): Boolean {
        val inside = Area(
            xRange = (xRange.first + 1)..(xRange.last - 1),
            yRange = (yRange.first + 1)..(yRange.last - 1),
        )
        return edges.none { it.area overlaps inside }
    }

    /** Parse the [input] and return the coordinates of the red tiles. */
    private fun parseInput(input: String): List<Point> = parse {
        input
            .lineSequence()
            .map { line -> line.split(',', limit = 2) }
            .map { (x, y) -> Point(x.toLong(), y.toLong()) }
            .toList()
    }

    override fun partOne(input: String): Long = parseInput(input).toRectangles().maxOf { it.size }

    override fun partTwo(input: String): Long = parseInput(input).run {
        val edges = toEdges()
        toRectangles().filter { it.isValid(edges) }.maxOf { it.size }
    }
}
