package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D05 : Solution {

    /** Returns true if there are any values that are contained in both ranges. */
    private infix fun LongRange.overlaps(other: LongRange): Boolean = last >= other.first && first <= other.last

    /** Returns true if there are no values contained in the [other] range, which are not also contained in this. */
    private infix fun LongRange.fullyContains(other: LongRange): Boolean = first <= other.first && other.last <= last

    /** Returns the range between the next after last element in this range, and before the first of the [other], or `null` */
    private infix fun LongRange.bridgeTo(other: LongRange): LongRange? = when {
        last >= other.first - 1 -> null
        else -> last.inc()..<other.first
    }

    /**
     * Splits a range into one or more ranges, at the splitting points defined by the [chunks].
     * Assumptions of [chunks]: They do not overlap, and are sorted.
     *
     * Examples:
     * - Splitting `55..67` by `[50..97, 98..99]` returns `[55..67]`.
     * - Splitting `57..69` by `[0..6, 7..10, 11..52, 53..60]` returns `[57..60, 61..69]`.
     * - Splitting `68..80` by `[0..68, 69..69]` returns `[68..68, 69..69, 70..80]`.
     * - Splitting `74..87` by `[45..63, 64..76, 77..99]` returns `[74..76, 77..87]`.
     */
    private fun LongRange.splitBy(chunks: List<LongRange>): List<LongRange> {
        val ranges = chunks
            .filter { it overlaps this }
            .map { it.first.coerceAtLeast(first)..it.last.coerceAtMost(last) }
            .windowed(2, partialWindows = false)
            .flatMap { window ->
                when (window.size) {
                    1 -> window
                    2 -> listOfNotNull(window.first(), window[0] bridgeTo window[1])
                    else -> error("Impossible state.")
                }
            }
            .ifEmpty { return listOf(this) }

        val rangeMinimum = ranges.first().first
        val rangeMaximum = ranges.last().last

        return buildList {
            if (first < rangeMinimum) add(first..<rangeMinimum)
            addAll(ranges)
            if (last > rangeMaximum) add(rangeMaximum.inc()..last)
        }
    }

    /**
     * Converts between a value based on the [ranges].
     * @property ranges A list of range definitions based on which to convert values.
     */
    private class EntryMap(val ranges: List<RangeMap>) {

        /** Map the [value] to another one. If no mapping rules apply, returns the same value. */
        operator fun get(value: Long): Long =
            ranges
                .firstNotNullOfOrNull { it[value] }
                ?: value

        /** Map a [range] of values in "parallel", returning all output ranges. */
        operator fun get(range: LongRange): List<LongRange> =
            range
                .splitBy(ranges.map { it.source })
                .map { split -> ranges.firstNotNullOfOrNull { it[split] } ?: split }

        /**
         * Defines a range mapping between
         * @property source The range to map from.
         * @property destination The range to map to. Must be of same size with [source].
         */
        data class RangeMap(val source: LongRange, val destination: LongRange) {

            /** Map a single [value] to its destination, or `null` if it is not contained in the source. */
            operator fun get(value: Long): Long? = when (value in source) {
                true -> destination.first + (value - source.first)
                false -> null
            }

            /** Map an entire [range] of values to its destination, or `null` if it is not contained in the source. */
            operator fun get(range: LongRange): LongRange? = when (source fullyContains range) {
                true -> (destination.first - source.first).let { delta -> (range.first + delta)..(range.last + delta) }
                else -> null
            }
        }
    }

    /** An elvish Almanac, specialized for gardening and food production. */
    private class Almanac(val seeds: List<Long>, private val maps: List<EntryMap>) {

        /** Interprets seeds as ranges instead of individual values. */
        fun seedsAsRanges(): List<LongRange> = seeds.chunked(2) { (start, size) -> start..<start + size }

        /** Given a single [value], applies all conversion steps and returns the answer. */
        fun process(value: Long): Long = maps.fold(value) { acc, em -> em[acc] }

        /** Given a list of value [ranges], applies all conversion steps "in parallel" and returns all output ranges. */
        fun process(ranges: List<LongRange>): List<LongRange> = maps.fold(ranges) { acc, em -> acc.flatMap { em[it] } }
    }

    /** Parse the [input] and return an [Almanac]. */
    private fun parseInput(input: String): Almanac = runCatching {
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
                    val (destinationStart, sourceStart, size) = line.split(' ').map(String::toLong)
                    EntryMap.RangeMap(
                        source = sourceStart..<sourceStart + size,
                        destination = destinationStart..<destinationStart + size,
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

        Almanac(seeds, stages)
    }.getOrElse { cause -> throw IllegalArgumentException("Invalid input", cause) }

    override fun partOne(input: String) = parseInput(input).run { seeds.minOf(this::process) }
    override fun partTwo(input: String) = parseInput(input).run { process(seedsAsRanges()).minOf(LongRange::first) }
}
