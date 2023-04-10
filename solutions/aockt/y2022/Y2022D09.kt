package aockt.y2022

import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2022D09 : Solution {

    /** The valid representation of an orthogonal rope movement. */
    private val inputRegex = Regex("""^([LRUD]) ([1-9]\d*)$""")

    /** Parses the [input] and returns the sequence of motions actioned on the rope head. */
    private fun parseInput(input: String): Sequence<Motion> =
        input
            .lineSequence()
            .flatMap { line ->
                val (direction, times) = inputRegex.matchEntire(line)?.destructured ?: error("Invalid input")
                val motion = when (direction) {
                    "L" -> Motion.Left
                    "R" -> Motion.Right
                    "U" -> Motion.Up
                    "D" -> Motion.Down
                    else -> error("Impossible direction.")
                }
                List(times.toInt()) { motion }
            }

    /** A position in 2D space. */
    private data class Position(val x: Int, val y: Int) {
        operator fun plus(delta: PositionDelta): Position = copy(x = x + delta.dx, y = y + delta.dy)
        operator fun minus(delta: PositionDelta): Position = copy(x = x - delta.dx, y = y - delta.dy)
        operator fun minus(other: Position): PositionDelta = PositionDelta(x - other.x, y - other.y)
    }

    /** The four possible directions of motions with a Planck Lengths of magnitude. */
    private enum class Motion(val delta: PositionDelta) {
        Left(PositionDelta(-1, 0)),
        Right(PositionDelta(1, 0)),
        Up(PositionDelta(0, 1)),
        Down(PositionDelta(0, -1));
    }

    /** The difference between two [Position]s. */
    private data class PositionDelta(
        val dx: Int,
        val dy: Int,
    ) {
        /** Scale down the delta to a unit vector. */
        val normal: PositionDelta get() = PositionDelta(dx.coerceIn(-1, 1), dy.coerceIn(-1, 1))
    }

    /** A rope simulation with configurable length. */
    private class Rope(knots: Int, initialPosition: Position) : Iterable<Position> {

        init {
            require(knots >= 2) { "Rope must have at least two knots." }
        }

        /** Positions of the rope knots, starting from the head towards the tail. */
        private val knots: Array<Position> = Array(knots) { initialPosition }
        override fun iterator(): Iterator<Position> = knots.iterator()

        val head: Position get() = knots.first()
        val tail: Position get() = knots.last()

        /** Apply this [motion] to the rope's [head] and propagate the motion towards the [tail]. */
        fun move(motion: Motion) {
            knots[0] = knots[0] + motion.delta
            for (i in knots.indices.drop(1)) {
                val link = knots[i]
                val delta = knots[i - 1] - link

                val step = when {
                    delta.dx.absoluteValue > 2 || delta.dy.absoluteValue > 2 -> error("Planck rope physics broke.")
                    delta.dx.absoluteValue <= 1 && delta.dy.absoluteValue <= 1 -> continue
                    else -> delta.normal
                }

                knots[i] = link + step
            }
        }
    }

    /** Simulate a rope with a given number of [knots] and return all positions visited by the tail. */
    private fun simulateRopeTail(knots: Int, motions: Sequence<Motion>): Set<Position> = buildSet {
        val rope = Rope(knots, Position(0, 0).also { add(it) })
        motions.forEach { motion ->
            rope.move(motion)
            add(rope.tail)
        }
    }

    override fun partOne(input: String) = simulateRopeTail(2, parseInput(input)).size
    override fun partTwo(input: String) = simulateRopeTail(10, parseInput(input)).size
}
