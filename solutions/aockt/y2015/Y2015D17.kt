package aockt.y2015

import aockt.util.powerSet
import io.github.jadarma.aockt.core.Solution

object Y2015D17 : Solution {

    /** Parses the input and returns all valid ways of filling the containers. */
    private fun commonPart(input: String): List<List<Int>> = input
        .lines().map(String::toInt)
        .powerSet()
        .filter { it.sum() == 150 }

    override fun partOne(input: String) = commonPart(input).count()

    override fun partTwo(input: String) = commonPart(input).run {
        val minimumBucketCount = minOf { it.size }
        count { it.size == minimumBucketCount }
    }
}
