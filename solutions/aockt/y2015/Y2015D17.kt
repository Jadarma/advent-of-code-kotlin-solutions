package aockt.y2015

import io.github.jadarma.aockt.core.Solution

object Y2015D17 : Solution {

    /**
     * Returns the power set of the [items] (all possible unordered combinations) except it's a [List] and not a [Set]
     * because repeated values are allowed.
     * TODO: Make Util.
     */
    private fun powerSet(items: List<Int>): List<List<Int>> = when (items.size) {
        0 -> listOf(emptyList())
        else -> {
            val next = powerSet(items.drop(1))
            val nextWithThis = next.map { listOf(items.first()) + it }
            nextWithThis + next
        }
    }

    /** Parses the input and returns all valid ways of filling the containers. */
    private fun commonPart(input: String): List<List<Int>> = input
        .lines().map(String::toInt)
        .let(this::powerSet)
        .filter { it.sum() == 150 }

    override fun partOne(input: String) = commonPart(input).count()

    override fun partTwo(input: String) = commonPart(input).run {
        val minimumBucketCount = minOf { it.size }
        count { it.size == minimumBucketCount }
    }
}
