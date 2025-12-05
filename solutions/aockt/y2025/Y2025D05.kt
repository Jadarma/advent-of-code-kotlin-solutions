package aockt.y2025

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2025D05 : Solution {

    /** Given a set of ranges, build another set of ranges such that there are no overlaps between them. */
    private fun Iterable<LongRange>.merge(): List<LongRange> = buildList {
        val ranges = this@merge.sortedBy { it.first }.ifEmpty { return emptyList() }
        add(ranges.first())

        for (range in ranges.drop(1)) {
            val tail = last()

            when {
                range.first > tail.last -> {
                    addLast(range)
                }

                range.last > tail.last -> {
                    removeLast()
                    addLast(tail.first..range.last)
                }
            }
        }
    }

    /** Parse the [input] and return the set of fresh item ID ranges, and the item IDs of stored foodstuff. */
    private fun parseInput(input: String): Pair<Set<LongRange>, LongArray> = parse {
        val (rawRanges, rawIds) = input.split("\n\n", limit = 2)
        val ranges = rawRanges
            .lineSequence()
            .map { it.split('-', limit = 2) }
            .map { (start, end) -> start.toLong()..end.toLong() }
            .onEach { require(!it.isEmpty()) { "Database contains empty ranges." } }
            .toSet()
        val ids = rawIds.lines().map(String::toLong).toLongArray()
        ranges to ids
    }

    override fun partOne(input: String): Int {
        val (ranges, ids) = parseInput(input)
        return ids.count { id -> ranges.any { range -> id in range } }
    }

    override fun partTwo(input: String): Long {
        val (ranges, _) = parseInput(input)
        return ranges.merge().sumOf { it.last - it.first + 1 }
    }
}
