package aockt.y2021

import io.github.jadarma.aockt.core.Solution
import java.util.PriorityQueue

object Y2021D15 : Solution {

    /** Represents a discrete point in 2D space. */
    private data class Point(val x: Int, val y: Int)

    /** A 2D map of risk levels, used for navigation. */
    private class ChironRiskMap(val width: Int, val height: Int, private val risk: IntArray) {

        operator fun get(point: Point): Int = risk[point.x * width + point.y]
        private operator fun contains(point: Point): Boolean = with(point) { x in 0 until height && y in 0 until width }

        /** Returns the points that are orthogonally adjacent to this [point] (valid point coordinates assumed). */
        private fun neighborsOf(point: Point): List<Point> = buildList(4) {
            if (point.x > 0) add(Point(point.x - 1, point.y))
            if (point.x < height - 1) add(Point(point.x + 1, point.y))
            if (point.y > 0) add(Point(point.x, point.y - 1))
            if (point.y < width - 1) add(Point(point.x, point.y + 1))
        }

        /**
         * Calculates the shortest path between the [start] and [end] points, and returns the path and the total risk.
         * If no such path exists, the list is empty and the total risk is `-1`.
         */
        fun shortestPathBetween(start: Point, end: Point): Pair<List<Point>, Int> {
            require(start in this && end in this)
            val distance = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
            val previous = mutableMapOf<Point, Point>()
            val queue = PriorityQueue<Point> { a, b -> distance.getValue(a) compareTo distance.getValue(b) }
            distance[start] = 0
            queue.add(start)

            while (queue.isNotEmpty()) {
                val point = queue.remove()
                if (point == end) break
                neighborsOf(point).forEach { neighbor ->
                    val altCost = distance.getValue(point) + this[neighbor]
                    if (altCost < distance.getValue(neighbor)) {
                        val isInQueue = distance[neighbor] != null
                        distance[neighbor] = altCost
                        previous[neighbor] = point
                        if (!isInQueue) queue.add(neighbor)
                    }
                }
            }

            return when (val totalRisk = distance[end]) {
                null -> emptyList<Point>() to -1
                else -> buildList {
                    var current = end.also { add(it) }
                    while (current != start) current = previous.getValue(current).also { add(it) }
                }.reversed() to totalRisk
            }
        }
    }

    /** Parse the input and return the [ChironRiskMap] as received from the submarine sensors. */
    private fun parseInput(input: String): ChironRiskMap {
        var width = -1
        var height = 0
        val risk = input
            .lineSequence()
            .onEach { if (width == -1) width = it.length else require(it.length == width) }
            .onEach { height++ }
            .flatMap { line -> line.map { it.digitToInt() } }
            .toList()
            .toIntArray()
        return ChironRiskMap(width, height, risk)
    }

    override fun partOne(input: String) =
        parseInput(input)
            .run { shortestPathBetween(Point(0, 0), Point(width - 1, height - 1)) }
            .second

    override fun partTwo(input: String): Any {
        val map = parseInput(input)
        val scale = 5
        val biggerMap = ChironRiskMap(
            width = map.width * scale,
            height = map.height * scale,
            risk = IntArray(map.width * scale * map.height * scale) { index ->
                val point = Point(index / (map.height * scale), index % (map.width * scale))
                val pointInOriginal = Point(point.x % map.height, point.y % map.width)
                var risk = map[pointInOriginal]
                risk += point.x / map.height; if (risk > 9) risk -= 9
                risk += point.y / map.width; if (risk > 9) risk -= 9
                risk
            },
        )
        return biggerMap
            .run { shortestPathBetween(Point(0, 0), Point(width - 1, height - 1)) }
            .second
    }
}
