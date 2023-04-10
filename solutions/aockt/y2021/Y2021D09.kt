package aockt.y2021

import io.github.jadarma.aockt.core.Solution

object Y2021D09 : Solution {

    /** Represents a discrete point in 2D space. */
    private data class Point(val x: Int, val y: Int)

    /** Represents the scanning of the lava tubes inside the cave system. */
    private class LavaTubesMap(val width: Int, val height: Int, private val data: IntArray) {
        init {
            require(data.size == width * height) { "The map size does not match given data." }
            require(data.all { it in 0..9 }) { "Map data contains invalid readings." }
        }

        operator fun get(point: Point) = data[point.x * width + point.y]

        /** Return all the local minima from the map. */
        fun lowPoints(): List<Point> = buildList {
            for (x in 0 until height) {
                for (y in 0 until width) {
                    val point = Point(x, y)
                    val value = this@LavaTubesMap[point]
                    if (adjacentTo(point).all { get(it) > value }) add(point)
                }
            }
        }

        /** Return a list of all basins, represented as the set of points that it contains. */
        fun basins(): List<Set<Point>> {
            val visited = mutableSetOf<Point>()
            return lowPoints().map { searchBasin(it, visited) }
        }

        /** Return all the points orthogonally adjacent to the [point], adjusting for map borders. */
        private fun adjacentTo(point: Point): List<Point> = buildList(8) {
            with(point) {
                if (x > 0) add(Point(x - 1, y))
                if (x < height - 1) add(Point(x + 1, y))
                if (y > 0) add(Point(x, y - 1))
                if (y < width - 1) add(Point(x, y + 1))
            }
        }

        /** Recursively build a basin starting [from] a point, and keeping track of [visited] points. */
        private fun searchBasin(from: Point, visited: MutableSet<Point>): Set<Point> = buildSet {
            add(from)
            adjacentTo(from)
                .filter { this@LavaTubesMap[it] < 9 }
                .filter { it !in visited }
                .forEach {
                    add(it)
                    visited.add(it)
                    addAll(searchBasin(from = it, visited))
                }
        }
    }

    /** Parse the [input] and return the [LavaTubesMap] associated to the cave system reading. */
    private fun parseInput(input: String): LavaTubesMap {
        var width = -1
        var height = 0
        val data = input
            .lineSequence()
            .onEach {
                height++
                if (width == -1) width = it.length else require(it.length == width)
            }
            .flatMap { line -> line.map { it.digitToInt() } }
            .toList()
            .toIntArray()
        return LavaTubesMap(width, height, data)
    }

    override fun partOne(input: String) =
        parseInput(input).run {
            lowPoints().sumOf { this[it] + 1 }
        }

    override fun partTwo(input: String) =
        parseInput(input)
            .basins()
            .sortedByDescending { it.size }
            .take(3)
            .fold(1) { acc, basin -> acc * basin.size }
}
