package aockt.y2023

import aockt.util.parse
import aockt.y2023.Y2023D12.Condition.Damaged
import aockt.y2023.Y2023D12.Condition.Operational
import aockt.y2023.Y2023D12.Condition.Unknown
import io.github.jadarma.aockt.core.Solution

object Y2023D12 : Solution {

    /** The condition record of a spring. If unknown, can be assumed to be either of the other two. */
    private enum class Condition { Operational, Damaged, Unknown }

    /**
     * An entry in the spring ledger.
     * @property springs  The condition of the springs in this entry.
     * @property checksum The number and size of the runs of damaged springs.
     */
    private data class ConditionRecord(val springs: List<Condition>, val checksum: List<Int>) {

        /**
         * The number of valid solutions tanking into account [Unknown] spring conditions, if any.
         * If there are no unknown [springs], this value will be 1 if the [checksum] is valid, or 0 otherwise.
         */
        fun validSolutions(cache: MutableMap<ConditionRecord, Long>): Long {
            // If the valid solution has already been computed, reuse it.
            cache[this]?.let { return it }

            // If no springs left, either no checksums left to satisfy, or the record is invalid.
            if (springs.isEmpty()) return if (checksum.isEmpty()) 1 else 0

            // If some springs left, but no checksums, then all remaining springs must be operational.
            // Unknowns can be assumed as operational, but if any known damaged left, the record is invalid.
            if (checksum.isEmpty()) return if (springs.contains(Damaged)) 0 else 1

            val spring = springs.first()
            val expectedDamaged = checksum.first()

            // If the next spring could be operational, then nothing to check, just ignore it.
            val solutionsIfOperational =
                if (spring != Damaged) ConditionRecord(springs.drop(1), checksum).validSolutions(cache)
                else 0

            // If the next spring could be damaged, check that all conditions for a valid damaged streak are met:
            // - There must be at least as many springs left as the next expected checksum value.
            // - The next checksum value's worth of springs must all be (or can be disambiguated into being) damaged.
            // - After the streak, either there are no springs left to check, or the streak is properly ended by an
            //   operational spring (or one that can be disambiguated as such).
            // If these conditions are met, then we should skip the current damaged streak (and maybe its terminator)
            // entirely, satisfy the checksum, and process the remaining springs.
            val solutionsIfSpringDamaged = when {
                spring == Operational -> 0
                expectedDamaged > springs.size -> 0
                springs.take(expectedDamaged).any { it == Operational } -> 0
                springs.size != expectedDamaged && springs[expectedDamaged] == Damaged -> 0
                else -> ConditionRecord(springs.drop(expectedDamaged + 1), checksum.drop(1)).validSolutions(cache)
            }

            return solutionsIfOperational
                .plus(solutionsIfSpringDamaged)
                .also { result -> cache[this] = result }
        }
    }

    /**
     * Parse the [input] and return the list of condition records.
     * @param input  The puzzle input.
     * @param unfold Whether to unfold the records before reading them.
     */
    private fun parseInput(input: String, unfold: Boolean): List<ConditionRecord> = parse {
        val lineRegex = Regex("""^([#.?]+) (\d[\d,]*\d)$""")

        fun String.unfold(separator: Char): String = buildString(length.inc() + 5) {
            val original = this@unfold
            append(original)
            repeat(4) { append(separator, original) }
        }

        input
            .lineSequence()
            .map { lineRegex.matchEntire(it)!!.destructured }
            .map { (record, checksum) ->
                if(unfold) record.unfold('?') to checksum.unfold(',')
                else record to checksum
            }
            .map { (record, checksum) ->
                ConditionRecord(
                    springs = record.map {
                        when(it) {
                            '.' -> Operational
                            '#' -> Damaged
                            '?' -> Unknown
                            else -> error("Impossible state")
                        }
                    },
                    checksum = checksum.split(',').map(String::toInt),
                )
            }
            .toList()
    }

    /**
     * Calculate the sum of valid solutions to all records.
     * Cached only in the context of a solve, to keep parts isolated, and to not let it "cheat" if part function called
     * multiple times.
     */
    private fun List<ConditionRecord>.solve(): Long {
        val cache: MutableMap<ConditionRecord, Long> = mutableMapOf()
        return sumOf { it.validSolutions(cache) }
    }

    override fun partOne(input: String) = parseInput(input, unfold = false).solve()
    override fun partTwo(input: String) = parseInput(input, unfold = true).solve()
}
