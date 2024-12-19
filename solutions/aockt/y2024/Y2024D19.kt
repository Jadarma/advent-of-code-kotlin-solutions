package aockt.y2024

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2024D19 : Solution {

    /** Counts the number of ways the [model] towel can be made by arranging [patterns] together. */
    private fun combos(patterns: Set<String>, model: String): Long {
        val cache: MutableMap<String, Long> = mutableMapOf("" to 1L)

        fun recurse(towel: String): Long = cache.getOrPut(towel) {
            patterns
                .filter { towel.startsWith(it) }
                .sumOf { recurse(towel.removePrefix(it)) }
        }

        return recurse(model)
    }

    /** Parse the [input] and return the set of available String patterns and the list of wanted designs. */
    private fun parseInput(input: String): Pair<Set<String>, List<String>> = parse {
        val (patterns, models) = input.split("\n\n", limit = 2)
        patterns.split(", ").toSet() to models.lines()
    }

    override fun partOne(input: String): Any {
        val (patterns, models) = parseInput(input)
        return models.count { combos(patterns, it) > 0 }
    }

    override fun partTwo(input: String): Any {
        val (patterns, models) = parseInput(input)
        return models.sumOf { combos(patterns, it) }
    }
}
