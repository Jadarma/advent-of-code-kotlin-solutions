package aockt.y2024

import aockt.util.parse
import aockt.util.spacial.*
import aockt.util.spacial.Direction.*
import aockt.y2024.Y2024D06.LabMap.*
import aockt.y2024.Y2024D06.LabMap.CellState.*
import io.github.jadarma.aockt.core.Solution

object Y2024D06 : Solution {

    /** A map of the laboratory. */
    private class LabMap(private val grid: Grid<CellState>) : Grid<CellState> by grid {

        /** The possible cell states of a lab map. */
        sealed interface CellState {
            data object Empty : CellState
            data object Occupied : CellState
            data class Guard(val facing: Direction) : CellState
        }

        /** The starting position of the guard. */
        val guardLocation: Point get() = grid.points().single { it.value is Guard }.position

        /**
         * Simulate the guard movement and return the sequence of points visited and the direction the guard faces along
         * the path.
         * The guard's [start]ing cell and [facing] direction can be overridden.
         * An [extraObstacle] can be placed, if desired.
         * The sequence ends either when the guard leaves the lab, or when a loop is detected.
         * If the path starts a loop, the last element will be a (-1,-1) point.
         */
        fun guardWalk(
            start: Point = guardLocation,
            facing: Direction = Up,
            extraObstacle: Point? = null,
        ): Sequence<Pair<Point, Direction>> = sequence {
            require(start in grid) { "Starting location out of bounds." }
            require(extraObstacle != start) { "Cannot put obstacle on top of guard!" }

            var guard = start to facing
            val seen = mutableSetOf<Pair<Point, Direction>>()

            while (guard.first in this@LabMap) {
                yield(guard)
                if (guard in seen) break
                seen.add(guard)

                guard = generateSequence(guard.second) { it.turnedClockwise }
                    .take(3)
                    .map { guard.first.move(it) to it }
                    .firstOrNull { (point, _) -> point != extraObstacle && getOrNull(point) != Occupied }
                    ?: break
            }

            if (guard.first in this@LabMap) yield(Point(-1, -1) to guard.second)
        }
    }

    /** Parse the [input] and return the [LabMap]. */
    private fun parseInput(input: String): LabMap = parse {
        MutableGrid(input) {
            when (it) {
                '.' -> Empty
                '#' -> Occupied
                '^' -> Guard(Up)
                else -> error("Invalid input.")
            }
        }.let(::LabMap)
    }

    override fun partOne(input: String): Int =
        parseInput(input)
            .guardWalk()
            .distinctBy { it.first }
            .count()

    override fun partTwo(input: String): Int = with(parseInput(input)) {
        guardWalk()
            .filterNot { it.first == guardLocation }
            .distinctBy { it.first }
            .count { (position, facing) ->
                guardWalk(
                    start = position.move(facing.opposite),
                    facing = facing,
                    extraObstacle = position,
                ).last().first == Point(-1, -1)
            }
    }
}
