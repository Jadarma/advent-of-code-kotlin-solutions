package aockt.y2022

import io.github.jadarma.aockt.core.Solution

object Y2022D04 : Solution {

    private val inputRegex = Regex("""^(\d+)-(\d+),(\d+)-(\d+)$""")

    /** Parses the [input] and returns the list of elf cleanup section assignments. */
    private fun parseInput(input: String): List<Pair<IntRange, IntRange>> =
        input
            .lineSequence()
            .map { line -> inputRegex.matchEntire(line)!!.destructured }
            .map { (x1, x2, y1, y2) -> x1.toInt()..x2.toInt() to y1.toInt()..y2.toInt() }
            .toList()

    /** Returns whether this [IntRange] is fully contained within the [other] or vice-versa. */
    private infix fun IntRange.fullyOverlapsWith(other: IntRange): Boolean = when {
        isEmpty() || other.isEmpty() -> false
        this.first <= other.first && this.last >= other.last -> true
        this.first >= other.first && this.last <= other.last -> true
        else -> false
    }

    /** Returns whether this [IntRange] has any overlap with the [other]. */
    private infix fun IntRange.overlapsWith(other: IntRange): Boolean = when {
        isEmpty() || other.isEmpty() -> false
        this.last < other.first -> false
        this.first > other.last -> false
        else -> true
    }

    override fun partOne(input: String): Any = parseInput(input).count { (x, y) -> x fullyOverlapsWith y }
    override fun partTwo(input: String): Any = parseInput(input).count { (x, y) -> x overlapsWith y }
}
