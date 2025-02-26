package aockt.y2024

import aockt.util.*
import aockt.util.spacial.*
import io.github.jadarma.aockt.core.Solution

object Y2024D18 : Solution {

    /**
     * Returns the minimum amount of steps needed to navigate the [area] from corner to corner, avoiding the
     * [corrupted] points, or -1 if it is impossible to do so.
     */
    private fun stepsToEscape(area: Area, corrupted: Set<Point>): Int =
        Pathfinding
            .search<Pair<Point, Direction>>(
                start = Point(0, 0) to Direction.Right,
                goalFunction = { it.first == Point(area.width - 1, area.height - 1) },
                neighbours = { (point, direction) ->
                    Direction.all
                        .asSequence()
                        .minus(direction.opposite)
                        .map { point.move(it) to it }
                        .filter { it.first in area }
                        .filterNot { it.first in corrupted }
                        .map { it to 1 }
                        .asIterable()
                },
            )
            ?.cost
            ?: -1

    /**
     * Parse the [input] and return the surrounding [Area] and the list of [Point]s that will get corrupted.
     * If [partial], only returns some of the points from the actual input.
     * The area size and partial count is determined by the size of the input, to allow for running examples.
     */
    private fun parseInput(input: String, partial: Boolean): Pair<Area, List<Point>> = parse {
        val points = input
            .lineSequence()
            .map { it.split(',', limit = 2).map(String::toInt) }
            .map { (x, y) -> Point(x, y) }
            .toList()

        val size = if (points.size > 50) 71 else 7

        val corruptedPoints = when (partial) {
            true -> if (size == 71) 1024 else 12
            else -> Int.MAX_VALUE
        }

        Area(size, size) to points.take(corruptedPoints)
    }

    override fun partOne(input: String): Int {
        val (area, points) = parseInput(input, partial = true)
        return stepsToEscape(area, points.toSet())
    }

    override fun partTwo(input: String): String {
        val (area, points) = parseInput(input, partial = false)
        var blocker = Int.MAX_VALUE

        points.indices.toList().binarySearch { index ->
            val corrupted = points.take(index + 1).toSet()
            val canEscape = stepsToEscape(area, corrupted) > -1

            if (canEscape) -1 else 1.also { blocker = index }
        }

        return with(points[blocker]) { "$x,$y" }
    }
}
