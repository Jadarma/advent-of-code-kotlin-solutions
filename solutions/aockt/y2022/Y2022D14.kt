package aockt.y2022

import aockt.y2022.Y2022D14.FallingSandSimulation.*
import io.github.jadarma.aockt.core.Solution

object Y2022D14 : Solution {

    /** Represents a discrete point in 2D space. */
    private data class Point(val x: Int, val y: Int)

    /** Simulates a potentially infinite grid of [Cell]s. If [maxDepth] is specified, an infinite wall is spawned. */
    private class FallingSandSimulation(private val maxDepth: Int? = null) {

        /** The possible cell types in the simulation. */
        enum class Cell { Empty, Wall, Sand }

        private val cells: MutableMap<Int, MutableMap<Int, Cell>> = mutableMapOf()

        /** Get the [Cell] value in the simulation at this [point]. */
        operator fun get(point: Point): Cell =
            cells
                .getOrElse(point.x) { emptyMap() }
                .getOrDefault(point.y, if (maxDepth != null && point.y == maxDepth) Cell.Wall else Cell.Empty)

        /** Update the contents at this [point] in the simulation with the new [cell] value. */
        operator fun set(point: Point, cell: Cell) = cells.getOrPut(point.x) { mutableMapOf() }.apply {
            if (cell == Cell.Empty) {
                remove(point.y)
                if (isEmpty()) cells.remove(point.x)
            } else put(point.y, cell)
        }

        /** Draw an orthogonal, straight line wall from [a] to [b], overriding any existing cells. */
        fun drawWall(a: Point, b: Point) {
            when {
                a.x == b.x -> (minOf(a.y, b.y)..maxOf(a.y, b.y)).forEach { y -> this[Point(a.x, y)] = Cell.Wall }
                a.y == b.y -> (minOf(a.x, b.x)..maxOf(a.x, b.x)).forEach { x -> this[Point(x, a.y)] = Cell.Wall }
                else -> throw IllegalArgumentException("Wall lines may only have 90 degree turns.")
            }
        }

        /** Draw a complex [line] defined by these points. */
        fun drawWall(line: List<Point>) = line.windowed(2).forEach { (a, b) -> drawWall(a, b) }

        /**
         * Spawn a [Cell.Sand] cell at the given [point] and let it fall.
         * The cell on which it spawns is overwritten if not empty.
         * Returns the final resting position of the cell, or null if it goes out of bounds.
         */
        fun dropAndSet(point: Point): Point? {
            this[point] = Cell.Empty
            var p = point

            while (maxDepth != null || cells.getOrDefault(p.x, emptyMap()).any { (k, _) -> k > p.y }) {
                val below = p.copy(y = p.y + 1)
                val left = p.copy(x = p.x - 1, y = p.y + 1)
                val right = p.copy(x = p.x + 1, y = p.y + 1)
                when (Cell.Empty) {
                    this[below] -> p = below
                    this[left] -> p = left
                    this[right] -> p = right
                    else -> {
                        this[p] = Cell.Sand
                        return p
                    }
                }
            }

            return null
        }
    }

    /** Parse the input and return the list of lines. */
    private fun parseInput(input: String): Sequence<List<Point>> =
        input
            .lineSequence()
            .map { line -> line.split(" -> ").flatMap { it.split(',') } }
            .map { it.map(String::toInt).chunked(2) { (a, b) -> Point(a, b) } }

    override fun partOne(input: String): Any {
        val dropPoint = Point(500, 0)
        val sim = FallingSandSimulation().apply { parseInput(input).forEach(::drawWall) }

        (0..Int.MAX_VALUE).forEach { iteration ->
            if (sim.dropAndSet(dropPoint) == null) return iteration
        }

        return -1
    }

    override fun partTwo(input: String): Any {
        val dropPoint = Point(500, 0)
        val lines = parseInput(input)
        val sim = FallingSandSimulation(maxDepth = lines.flatten().maxOf { it.y } + 2)
            .apply { lines.forEach(::drawWall) }

        (1..Int.MAX_VALUE).forEach { iteration ->
            sim.dropAndSet(dropPoint) ?: error("Didn't hit the bottom of an infinite floor.")
            if (sim[dropPoint] == Cell.Sand) return iteration
        }

        return -1
    }
}
