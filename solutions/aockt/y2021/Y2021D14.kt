package aockt.y2021

import io.github.jadarma.aockt.core.Solution

object Y2021D14 : Solution {

    /**
     * Holds metadata about a polymer after repeated polymerization via some rules.
     * The submarine does not have enough memory to store exponentially growing polymers, unfortunately.
     */
    private class PolymerizationStatistics(template: String, rules: Iterable<Pair<String, Char>>) {

        init {
            require(template.all { it in 'A'..'Z' }) { "Invalid polymer template." }
        }

        /** After polymerization, the last element is always the same, and is needed to compute occurrences. */
        private val lastElement: Char = template.last()

        /** The polymerization rules, mapping from a valid pair of elements to the new element inserted between. */
        private val replace: Map<String, Char> = rules
            .onEach { (pair, element) ->
                require(pair.length == 2) { "Invalid rule." }
                require("$pair$element".all { it in 'A'..'Z' }) { "Invalid rule." }
            }
            .toMap()

        /** Counts how many times an element pair occurs in the currently simulated polymer. */
        private val twoGram: MutableMap<String, Long> = mutableMapOf<String, Long>().apply {
            template.windowed(2).forEach { pair -> updateCount(pair) { it + 1L } }
        }

        /**
         * Applies the polymerisation operation a number of [times].
         * This operation **mutates** the internal statistics state and returns a reference to itself.
         */
        fun polymerize(times: Int = 1): PolymerizationStatistics = apply {
            repeat(times) {
                val replacementDeltas = buildMap {
                    for (rule in replace) {
                        val (pair, element) = rule
                        val occurrences = twoGram[pair] ?: continue
                        updateCount("${pair.first()}$element", true) { it + occurrences }
                        updateCount("$element${pair.last()}", true) { it + occurrences }
                        updateCount(pair, true) { it - occurrences }
                    }
                }
                replacementDeltas.forEach { (pair, delta) -> twoGram.updateCount(pair) { it + delta } }
            }
        }

        /** Computes how many times each distinct element occurs in the simulated polymer. */
        fun occurrences(): Map<Char, Long> = buildMap {
            twoGram.forEach { (pair, occurrences) -> updateCount(pair.first()) { it + occurrences } }
            updateCount(lastElement) { it + 1L }
        }

        /**
         * Helper to handle counting map operations. Modifies the count of the [key], by supplying its current value
         * (or `zero` if it is not tracked) to the [mapper] function. If [allowNonPositive] flag is set, negative counts
         * are allowed; otherwise the [key] will be removed.
         */
        private inline fun <T> MutableMap<T, Long>.updateCount(
            key: T,
            allowNonPositive: Boolean = false,
            mapper: (Long) -> Long,
        ) = apply {
            val nextCount = mapper(getOrDefault(key, 0L))
            when {
                nextCount in Long.MIN_VALUE..0L && !allowNonPositive -> remove(key)
                else -> put(key, nextCount)
            }
        }
    }

    /** Parse the [input] and return the [PolymerizationStatistics] for the given polymer template. */
    private fun parseInput(input: String): PolymerizationStatistics = runCatching {
        val template = input.substringBefore('\n')
        val rules = input
            .lineSequence()
            .drop(2)
            .map { it.split(" -> ") }
            .map { (pair, element) -> pair to element.first() }
            .asIterable()
        PolymerizationStatistics(template, rules)
    }.getOrElse { throw IllegalArgumentException("Invalid input", it) }

    override fun partOne(input: String) =
        parseInput(input)
            .polymerize(times = 10)
            .occurrences()
            .run { maxOf { it.value } - minOf { it.value } }

    override fun partTwo(input: String) =
        parseInput(input)
            .polymerize(times = 40)
            .occurrences()
            .run { maxOf { it.value } - minOf { it.value } }
}
