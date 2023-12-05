package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D05 : Solution {

    /**
     * Converts between a value based on the [ranges].
     * @property ranges A list of range definitions based on which to convert values.
     */
    private class EntryMap(private val ranges: List<RangeMap>) {

        /** Map the [value] to another one. If no mapping rules apply, returns the same value. */
        operator fun get(value: Long): Long = ranges.firstNotNullOfOrNull { it[value] } ?: value

        /**
         * Defines a range mapping between
         * @property source The range to map from.
         * @property destination The range to map to. Must be of same size with [source].
         */
        data class RangeMap(val source: LongRange, val destination: LongRange) {
            operator fun get(value: Long): Long? = when (value in source) {
                true -> destination.first + (value - source.first)
                false -> null
            }
        }
    }

    /** An elvish Almanac, specialized for gardening and food production. */
    private class Almanac(val seeds: List<Long>, private val maps: List<EntryMap>) {

        /** Interprets seeds as ranges instead of individual values. */
        fun seedsAsRanges(): List<LongRange> = seeds.chunked(2) { (start, size) -> start..<start + size }

        /** Given a [value], applies all conversion steps and returns the answer. */
        fun process(value: Long): Long = maps.fold(value) { acc, map -> map[acc] }
    }

    /**
     * Parse the [input] and return an [Almanac].
     * @param input The puzzle input.
     * @param inverse If true, the almanac will convert from location to seed, instead of seed to location.
     */
    private fun parseInput(input: String, inverse: Boolean): Almanac = runCatching {
        val mappingSteps = listOf("seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location")
        val mapTypeRegex = Regex("""^([a-z]+)-to-([a-z]+) map:$""")

        fun parseEntryMap(entryIndex: Int, input: String): EntryMap {
            val lines = input.lines()

            val (source, destination) = mapTypeRegex.matchEntire(lines.first())!!.destructured
            check(source == mappingSteps[entryIndex]) { "Almanac records not in expected order." }
            check(destination == mappingSteps[entryIndex + 1]) { "Almanac mappings don't map in expected steps." }

            return lines
                .drop(1)
                .map { line ->
                    val (first, second, size) = line.split(' ').map(String::toLong)
                    val sourceRange = second..<second + size
                    val destinationRange = first..<first + size
                    EntryMap.RangeMap(
                        source = if (inverse) destinationRange else sourceRange,
                        destination = if (inverse) sourceRange else destinationRange,
                    )
                }
                .sortedBy { it.source.first }
                .let(::EntryMap)
        }

        val parts = input.split("\n\n")

        val seeds = parts
            .first()
            .removePrefix("seeds: ")
            .split(' ')
            .also { check(it.size % 2 == 0) { "Seed numbers did not come in pairs!" } }
            .map(String::toLong)

        val stages = parts
            .drop(1)
            .mapIndexed(::parseEntryMap)
            .also { check(it.size == mappingSteps.size - 1) { "Some mapping stages missing." } }
            .run { if (inverse) reversed() else this }

        Almanac(seeds, stages)
    }.getOrElse { cause -> throw IllegalArgumentException("Invalid input", cause) }

    override fun partOne(input: String) = parseInput(input, false).run { seeds.minOf(this::process) }

    override fun partTwo(input: String) = parseInput(input, true).run {
        val seedRanges = seedsAsRanges()
        var location = 0L
        while (true) {
            val idealSeed = process(location)
            if (seedRanges.any { idealSeed in it }) break
            location += 1
        }
        location
    }
}
