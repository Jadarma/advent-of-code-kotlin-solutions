package aockt.y2023

import io.github.jadarma.aockt.core.Solution
import kotlin.math.pow

object Y2023D04 : Solution {

    /**
     * Information about an elven scratchcard.
     *
     * @property id The ID.
     * @property winningNumbers The winning numbers, written on the left of the pipe.
     * @property cardNumbers    The numbers you have, written on the right of the pipe.
     * @property matchingCount  How many [cardNumbers] are also [winningNumbers].
     * @property value          The estimated numerical value of the card based on its [matchingCount].
     * @property prizeCards     The IDs of the cards that can be claimed for this card based on its [matchingCount].
     */
    private data class Scratchcard(val id: Int, val winningNumbers: Set<Int>, val cardNumbers: Set<Int>) {

        val matchingCount = cardNumbers.intersect(winningNumbers).size

        val value: Int = when (matchingCount) {
            0 -> 0
            else -> 2.0.pow(matchingCount - 1).toInt()
        }

        val prizeCards: Set<Int> = when (matchingCount) {
            0 -> emptySet()
            else -> List(size = matchingCount) { id + 1 + it }.toSet()
        }
    }

    /** Parses the [input] and returns the scratchcards. */
    private fun parseInput(input: String): Set<Scratchcard> = runCatching {
        val cardRegex = Regex("""^Card\s+(\d+): ([\d ]+) \| ([\d ]+)$""")

        fun parseNumberSet(input: String): Set<Int> =
            input.split(' ').filter(String::isNotBlank).map(String::toInt).toSet()

        input
            .lineSequence()
            .map { line -> cardRegex.matchEntire(line.also(::println))!!.destructured }
            .map { (id, left, right) -> Scratchcard(id.toInt(), parseNumberSet(left), parseNumberSet(right)) }
            .toSet()
    }.getOrElse { cause -> throw IllegalArgumentException("Invalid input.", cause) }

    /**
     * Takes a set of [Scratchcard]s and collects other cards as prises until all cards cave been claimed.
     * Assumes the set contains cards of consecutive IDs without gaps.
     * Returns pairs of scratchcards and their total count, indexed by id.
     */
    private fun Set<Scratchcard>.tradeIn(): Map<Int, Pair<Scratchcard, Int>> = buildMap {
        this@tradeIn.forEach { put(it.id, it to 1) }
        val maxLevel = keys.maxOf { it }
        for (id in 1..maxLevel) {
            require(id in keys) { "Invalid input. Missing info for card #$id/$maxLevel." }
        }

        for (id in 1..maxLevel) {
            val (card, copies) = getValue(id)
            for (prizeId in card.prizeCards.filter { it <= maxLevel }) {
                put(prizeId, getValue(prizeId).run { copy(second = second + copies) })
            }
        }
    }

    override fun partOne(input: String) = parseInput(input).sumOf(Scratchcard::value)
    override fun partTwo(input: String) = parseInput(input).tradeIn().values.sumOf { it.second }
}
