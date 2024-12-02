package aockt.y2024

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution
import kotlin.math.absoluteValue

object Y2024D01 : Solution {

    /** Parse the [input] and return the two lists. */
    private fun parseInput(input: String): Pair<List<Int>, List<Int>> = parse {
        val firstList = mutableListOf<Int>()
        val secondList = mutableListOf<Int>()

        input
            .lineSequence()
            .map { line -> line.split("   ", limit = 2).map(String::toInt) }
            .forEach { (x, y) -> firstList.add(x); secondList.add(y) }

        firstList to secondList
    }

    override fun partOne(input: String): Int {
        val (firstList, secondList) = parseInput(input)
        return firstList.sorted()
            .zip(secondList.sorted())
            .sumOf { (x, y) -> (x - y).absoluteValue }
    }

    override fun partTwo(input: String): Int {
        val (firstList, secondList) = parseInput(input)
        val appearances = secondList.groupingBy { it }.eachCount()
        return firstList.sumOf { x -> x * (appearances[x] ?: 0) }
    }
}
