package aockt.y2023

import aockt.util.parse
import aockt.util.spacial.Direction
import aockt.util.spacial.MutableGrid
import aockt.util.spacial.Point
import aockt.util.spacial.move
import aockt.util.spacial.render
import io.github.jadarma.aockt.core.Solution

// TODO: Could use some more code cleanup.
object Y2023D14 : Solution {

    /** The type of rock you can encounter on the dish. */
    private enum class Rock { None, Rounded, Cubic }

    /** Simulates rolling rocks on top of a parabolic reflector dish. */
    private class ParabolicDish(private val state: MutableGrid<Rock>) {

        private val height get() = state.height
        private val width get() = state.width

        /** Rolls a rounded rock from the given point in a [direction], returning the point where it rests. */
        private fun Point.lastFreeSpaceIn(direction: Direction): Point {

            val nx = when (direction) {
                Direction.Left -> run {
                    state
                        .move(this, Direction.Left)
                        .firstOrNull { (_, v) -> v != Rock.None }
                        ?.let { (p, _) -> p.x.toInt() + 1 }
                        ?: 0

                    for (dx in x.toInt() downTo 1) if (state[dx - 1, y.toInt()] != Rock.None) return@run dx
                    0
                }

                Direction.Right -> run {
                    for (dx in x.toInt()..<width.dec()) if (state[dx + 1, y.toInt()] != Rock.None) return@run dx
                    width.dec()
                }

                else -> x.toInt()
            }
            val ny = when (direction) {
                Direction.Up -> run {
                    for (dy in y.toInt()..<height.dec()) if (state[x.toInt(), dy + 1] != Rock.None) return@run dy
                    height.dec()
                }

                Direction.Down -> run {
                    for (dy in y.toInt() downTo 1) if (state[x.toInt(), dy - 1] != Rock.None) return@run dy
                    0
                }

                else -> y.toInt()
            }
            return Point(nx, ny)
        }

        /** Tilt the dish in a [direction], updating the coordinates of the rounded stones. */
        fun tilt(direction: Direction): ParabolicDish = apply {
            val xRange = if (direction == Direction.Right) width.dec() downTo 0 else 0..<width
            val yRange = if (direction == Direction.Up) height.dec() downTo 0 else 0..<height

            for (x in xRange) {
                for (y in yRange) {
                    if (state[x, y] != Rock.Rounded) continue
                    val rollStart = Point(x, y)
                    val rollEnd = rollStart.lastFreeSpaceIn(direction)
                    state[rollStart.x.toInt(),rollStart.y.toInt()] = Rock.None
                    state[rollEnd.x.toInt(),rollEnd.y.toInt()] = Rock.Rounded
                }
            }
        }

        /** Spins the dish once, tilting in all directions in counterclockwise order. */
        fun spin(): ParabolicDish = apply {
            tilt(Direction.Up)
            tilt(Direction.Left)
            tilt(Direction.Down)
            tilt(Direction.Right)
        }

        /** Returns the positions of all rounded rocks on the dish. */
        fun roundedRocks(): Sequence<Point> = sequence {
            for (x in 0..<width) {
                for (y in 0..<height) {
                    if (state[x, y] == Rock.Rounded) yield(Point(x, y))
                }
            }
        }

        /** Serializes the current state in a string. It can be either used to display, or as a cache key. */
        fun render() = state.render {
            when (it) {
                Rock.None -> '.'
                Rock.Cubic -> '#'
                Rock.Rounded -> 'O'
            }
        }
    }

    /** Parse the [input] and return the initial state of the [ParabolicDish]. */
    private fun parseInput(input: String): ParabolicDish = parse {
        MutableGrid(input) {
            when (it) {
                '.' -> Rock.None
                '#' -> Rock.Cubic
                'O' -> Rock.Rounded
                else -> error("Unknown symbol '$it'.")
            }
        }.let(::ParabolicDish)
    }

    override fun partOne(input: String) =
        parseInput(input)
            .tilt(Direction.Up)
            .roundedRocks()
            .sumOf { it.y + 1 }

    override fun partTwo(input: String) = parseInput(input).run {
        val seen = mutableMapOf<String, Long>()

        val targetSpins = 1_000_000_000L
        var spins = 0L

        while (spins < targetSpins) {
            val key = render()

            if (key in seen) {
                val detectedCycleLength = spins - seen.getValue(key)
                val remainingCycles = (targetSpins - spins) % detectedCycleLength
                for (i in 0..<remainingCycles) spin()
                break
            }

            seen[key] = spins
            spins += 1

            spin()
        }

        roundedRocks().sumOf { it.y + 1 }
    }
}
