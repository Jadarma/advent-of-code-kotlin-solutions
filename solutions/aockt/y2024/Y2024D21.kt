package aockt.y2024

import aockt.util.parse
import aockt.util.spacial.Direction.*
import aockt.util.spacial.*
import io.github.jadarma.aockt.core.Solution

object Y2024D21 : Solution {

    /**
     * A keypad with buttons.
     * @param blank   The point missing from the key pad grid.
     * @param buttons Button labels associated with their grid location.
     */
    private sealed class KeyPad(
        private val blank: Point,
        private val buttons: Map<Char, Point>,
    ) {

        /** A numerical keypad for doors. */
        data object Numerical : KeyPad(
            blank = Point(0, 0),
            buttons = mapOf(
                '0' to Point(1, 0),
                'A' to Point(2, 0),
                '1' to Point(0, 1),
                '2' to Point(1, 1),
                '3' to Point(2, 1),
                '4' to Point(0, 2),
                '5' to Point(1, 2),
                '6' to Point(2, 2),
                '7' to Point(0, 3),
                '8' to Point(1, 3),
                '9' to Point(2, 3),
            ),
        )

        /** A directional keypad for keypad-operating robots. */
        data object Directional : KeyPad(
            blank = Point(0, 1),
            buttons = mapOf(
                '<' to Point(0, 0),
                'v' to Point(1, 0),
                '>' to Point(2, 0),
                '^' to Point(1, 1),
                'A' to Point(2, 1),
            )
        )

        /** Return the optimal moves required to move to another button and press it. */
        fun move(segment: String): String {
            val start = buttons.getValue(segment[0])
            val end = buttons.getValue(segment[1])

            fun recurse(point: Point, acc: String): Sequence<String> = sequence {
                if (point == end) yield(acc + "A")
                if (end.x < point.x && point.move(Left ) != blank) yieldAll(recurse(point.move(Left) , "$acc<"))
                if (end.y > point.y && point.move(Up   ) != blank) yieldAll(recurse(point.move(Up)   , "$acc^"))
                if (end.y < point.y && point.move(Down ) != blank) yieldAll(recurse(point.move(Down) , "${acc}v"))
                if (end.x > point.x && point.move(Right) != blank) yieldAll(recurse(point.move(Right), "$acc>"))
            }

            return recurse(start, "").minBy { path -> path.zipWithNext().count { it.first != it.second } }
        }

        /** Return the optimal moves required to type out this [code]. */
        fun type(code: String): String =
            "A$code"
                .windowed(size = 2)
                .joinToString(separator = "", transform = ::move)
    }

    /**
     * Splits this string into distinct segments and returns their frequencies.
     * A segment is a series of directions followed by an 'A'.
     */
    private fun String.segmentFrequency(): Map<String, Long> =
        removeSuffix("A")
            .split("A")
            .map { it + 'A' }
            .groupingBy { it }
            .eachCount()
            .mapValues { it.value.toLong() }

    /** A three-digit door code. */
    @JvmInline
    private value class Code(private val value: String) : CharSequence by value {

        init {
            val regex = Regex("""^\d{3}A""")
            require(value.matches(regex)) { "Invalid code." }
        }

        /** Calculates the complexity of using different layers of [robots] to type out this code. */
        fun complexity(robots: Int = 0): Long {
            var segmentFrequency: Map<String, Long> = KeyPad.Numerical.type(value).segmentFrequency()

            repeat(robots) {
                segmentFrequency = buildMap {
                    for ((segment, count) in segmentFrequency) {
                        for ((seg, times) in KeyPad.Directional.type(segment).segmentFrequency()) {
                            compute(seg) { _, old -> (old ?: 0) + count * times }
                        }
                    }
                }
            }

            val minLength = segmentFrequency.entries.sumOf { it.key.length * it.value }
            val numericPart = substring(0, 3).toLong()
            return minLength * numericPart
        }
    }

    /** Parse the [input] and return the list of door codes. */
    private fun parseInput(input: String): List<Code> = parse { input.lines().map(::Code) }

    override fun partOne(input: String): Long = parseInput(input).sumOf { it.complexity(robots = 2) }
    override fun partTwo(input: String): Long = parseInput(input).sumOf { it.complexity(robots = 25) }
}
