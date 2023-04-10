package aockt.y2021

import aockt.y2021.Y2021D08.Segment.*
import aockt.y2021.Y2021D08.SevenSegmentDigit.*
import io.github.jadarma.aockt.core.Solution

object Y2021D08 : Solution {

    /** The individual segment indicators of a seven segment display. */
    private enum class Segment { A, B, C, D, E, F, G }

    /** The ten digits as represented on a seven segment display. */
    private enum class SevenSegmentDigit(val digit: Int, val segments: Set<Segment>) {
        Zero(0, setOf(A, B, C, E, F, G)),
        One(1, setOf(C, F)),
        Two(2, setOf(A, C, D, E, G)),
        Three(3, setOf(A, C, D, F, G)),
        Four(4, setOf(B, C, D, F)),
        Five(5, setOf(A, B, D, F, G)),
        Six(6, setOf(A, B, D, E, F, G)),
        Seven(7, setOf(A, C, F)),
        Eight(8, setOf(A, B, C, D, E, F, G)),
        Nine(9, setOf(A, B, C, D, F, G));

        companion object {
            fun fromSegmentsOrNull(segments: Set<Segment>) = values().firstOrNull { it.segments == segments }
        }
    }

    /** A set of segments that represent a [SevenSegmentDigit], but that have been swapped in an unknown fashion. */
    private data class ScrambledDigit(private val segments: Set<Segment>) : Set<Segment> by segments {
        /**
         * Given a scrambled digit and the correct mapping [code] between segments, returns the actual encoded
         * [SevenSegmentDigit], or throws if either the mapping is invalid, or the input does not represent a digit.
         */
        fun unscramble(code: Map<Segment, Segment>): SevenSegmentDigit {
            require(code.keys.size == 7) { "Incomplete code, not all segments are mapped." }
            require(code.values.toSet().size == 7) { "Invalid code, some segments map to the same segment." }
            return SevenSegmentDigit
                .fromSegmentsOrNull(segments.map { code.getValue(it) }.toSet())
                ?: throw IllegalArgumentException("Invalid code, output is not a valid digit.")
        }
    }

    /** Reverse engineer the segment scrambling of a seven segment display given the 10 unique digit patterns. */
    private fun Set<ScrambledDigit>.reverseEngineerSevenSegmentDisplay(): Map<Segment, Segment> {
        require(size == 10) { "Cannot reverse engineer scrambling because some digits are missing." }

        val digits: Map<SevenSegmentDigit, ScrambledDigit> = buildMap {
            val unsolved = this@reverseEngineerSevenSegmentDisplay.toMutableList()
            fun <T> MutableList<T>.removeFirstWhere(predicate: (T) -> Boolean): T = first(predicate).also(::remove)
            this[One] = unsolved.removeFirstWhere { it.size == 2 }
            this[Four] = unsolved.removeFirstWhere { it.size == 4 }
            this[Seven] = unsolved.removeFirstWhere { it.size == 3 }
            this[Eight] = unsolved.removeFirstWhere { it.size == 7 }
            this[Three] = unsolved.removeFirstWhere { it.size == 5 && it.containsAll(getValue(One)) }
            this[Nine] = unsolved.removeFirstWhere { it.toSet() == getValue(Three) + getValue(Four) }
            this[Six] = unsolved.removeFirstWhere { it.size == 6 && !it.containsAll(getValue(One)) }
            this[Five] = unsolved.removeFirstWhere { it.size == 5 && (getValue(Six) - it).size == 1 }
            this[Two] = unsolved.removeFirstWhere { it.size == 5 }
            this[Zero] = unsolved.removeFirstWhere { it.size == 6 }
            require(unsolved.isEmpty())
        }

        return buildMap {
            put((digits.getValue(Seven) - digits.getValue(One)).first(), A)
            put((digits.getValue(Eight) - digits.getValue(Seven) - digits.getValue(Two)).first(), B)
            put((digits.getValue(Eight) - digits.getValue(Six)).first(), C)
            put((digits.getValue(Eight) - digits.getValue(Zero)).first(), D)
            put((digits.getValue(Eight) - digits.getValue(Nine)).first(), E)
            put((digits.getValue(One) intersect digits.getValue(Six)).first(), F)
            put((digits.getValue(Eight) - keys).first(), G)
        }
    }

    /** Converts a list of segmented digits to an integer. Might overflow. Throws if list is empty. */
    private fun List<SevenSegmentDigit>.toInt(): Int = joinToString(separator = "") { it.digit.toString() }.toInt()

    /** Parse the [input] and return the sequence of unique scrambled signals and the four output scrambled digits. */
    private fun parseInput(input: String): Sequence<Pair<Set<ScrambledDigit>, List<ScrambledDigit>>> {
        fun String.parseScrambledDigitList(): Sequence<ScrambledDigit> =
            trim()
                .splitToSequence(' ')
                .map { digit -> ScrambledDigit(digit.map { Segment.valueOf(it.uppercase()) }.toSet()) }

        return input
            .lineSequence()
            .map { line ->
                val (digitsRaw, outputRaw) = line.split('|')
                val digits = digitsRaw.parseScrambledDigitList().toSet()
                val output = outputRaw.parseScrambledDigitList().toList()
                require(digits.size == 10 && output.size == 4) { "Invalid input." }
                digits to output
            }
    }

    override fun partOne(input: String) =
        input
            .lineSequence()
            .map { it.substringAfter('|').trim() }
            .flatMap { digits -> digits.split(' ').filter { it.length in setOf(2, 3, 4, 7) } }
            .count()

    override fun partTwo(input: String) =
        parseInput(input)
            .map { (scrambled, output) -> scrambled.reverseEngineerSevenSegmentDisplay() to output }
            .map { (code, output) -> output.map { it.unscramble(code) } }
            .fold(0) { acc, digits -> acc + digits.toInt() }
}
