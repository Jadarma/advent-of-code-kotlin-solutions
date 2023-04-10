package aockt.y2022

import io.github.jadarma.aockt.core.Solution
import java.util.*

object Y2022D12 : Solution {

    /** Parse the [input] and return the map and the coordinates of the start and end [Point]s. */
    private fun parseInput(input: String): Triple<HeightMap, Point, Point> {
        lateinit var start: Point
        lateinit var end: Point

        var width = -1
        var height = 0

        val map = input
            .lines()
            .onEach { if (width == -1) width = it.length else require(it.length == width) }
            .onEach { height++ }
            .flatMapIndexed { row, line ->
                line.mapIndexed { col, value ->
                    when (value) {
                        'S' -> 0.also { start = Point(row, col) }
                        'E' -> ('z' - 'a').also { end = Point(row, col) }
                        else -> value - 'a'
                    }
                }
            }
            .toIntArray()
            .let { HeightMap(width, height, it) }

        return Triple(map, start, end)
    }

    /** Represents a discrete point in 2D space. */
    private data class Point(val x: Int, val y: Int)

    /** A square in a [HeightMap], at a given [location], which has a given [altitude]. */
    private data class Tile(val location: Point, val altitude: Int)

    /** A topographic map for hiking. */
    private class HeightMap(val width: Int, val height: Int, private val altitudes: IntArray) {

        init {
            require(altitudes.size == width * height) { "Not all tiles in the map contain values." }
        }

        operator fun get(point: Point): Tile = Tile(point, altitudes[point.x * width + point.y])
        private operator fun contains(point: Point): Boolean = with(point) { x in 0 until height && y in 0 until width }

        private val Point.neighbors: List<Point>
            get() = buildList(4) {
                if (x > 0) add(Point(x - 1, y))
                if (x < height - 1) add(Point(x + 1, y))
                if (y > 0) add(Point(x, y - 1))
                if (y < width - 1) add(Point(x, y + 1))
            }

        /**
         * Determines the shortest path between the starting point and a tile that satisfies the condition in the
         * [endSelector], returning the list of coordinates (including the starting location).
         * This path works in reverse: it assumes you are climbing down from the [start].
         */
        fun shortestPathBetween(start: Point, endSelector: (Tile) -> Boolean): List<Point> {
            require(start in this)
            var end: Point? = null
            val distance = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
            val previous = mutableMapOf<Point, Point>()
            val queue = PriorityQueue<Point> { a, b -> distance.getValue(a) compareTo distance.getValue(b) }
            distance[start] = 0
            queue.add(start)

            while (queue.isNotEmpty()) {
                val point = queue.remove()
                if (endSelector(get(point))) {
                    end = point
                    break
                }

                point.neighbors
                    .filter { get(it).altitude >= get(point).altitude - 1 }
                    .forEach { neighbor ->
                        val altCost = distance.getValue(point) + 1
                        if (altCost < distance.getValue(neighbor)) {
                            val isInQueue = distance[neighbor] != null
                            distance[neighbor] = altCost
                            previous[neighbor] = point
                            if (!isInQueue) queue.add(neighbor)
                        }
                    }
            }

            return when (distance[end]) {
                null -> emptyList()
                else -> buildList {
                    var current = end?.also { add(it) } ?: return emptyList()
                    while (current != start) current = previous.getValue(current).also { add(it) }
                }.reversed()
            }
        }
    }

    override fun partOne(input: String) =
        parseInput(input)
            .let { (map, start, end) -> map.shortestPathBetween(end) { it.location == start } }
            .count().dec()

    override fun partTwo(input: String) =
        parseInput(input)
            .let { (map, _, end) -> map.shortestPathBetween(end) { it.altitude == 0 } }
            .count().dec()
}
