package aockt.y2015

import aockt.util.generatePermutations
import io.github.jadarma.aockt.core.Solution

object Y2015D13 : Solution {

    private val inputRegex = Regex("""^(\w+) would (lose|gain) (\d+) happiness units by sitting next to (\w+).$""")

    /**
     * Parses a single line of input and returns a triple containing two people and the happiness difference applied if
     * the first person stands next to the second.
     */
    private fun parseInput(input: String): Triple<String, String, Int> {
        val (personA, sign, amount, personB) = inputRegex.matchEntire(input)!!.destructured
        val happiness = amount.toInt() * if (sign == "gain") 1 else -1
        return Triple(personA, personB, happiness)
    }

    /**
     * Given a list of happiness modifiers between all pairs of guests, returns all possible table arrangements and
     * their total happiness modifier.
     * If [includeApatheticSelf], includes an extra neutral person in the list.
     */
    private fun bruteForceArrangement(
        guestData: List<Triple<String, String, Int>>,
        includeApatheticSelf: Boolean = false,
    ): Sequence<Pair<List<String>, Int>> {
        val guests = mutableSetOf<String>()
        val happinessScores = mutableMapOf<String, MutableMap<String, Int>>()

        @Suppress("ReplacePutWithAssignment")
        guestData.forEach { (guest, other, happiness) ->
            guests.add(guest)
            guests.add(other)
            happinessScores
                .getOrPut(guest) { mutableMapOf() }
                .put(other, happiness)
        }

        if (includeApatheticSelf) {
            happinessScores["Self"] = mutableMapOf()
            guests.forEach { guest ->
                happinessScores[guest]!!["Self"] = 0
                happinessScores["Self"]!![guest] = 0
            }
            guests.add("Self")
        }

        return guests
            .toList()
            .generatePermutations()
            .map { arrangement ->
                arrangement to arrangement
                    .plusElement(arrangement.first())
                    .windowed(2)
                    .sumOf { happinessScores[it[0]]!![it[1]]!! + happinessScores[it[1]]!![it[0]]!! }
            }
    }

    override fun partOne(input: String) =
        input
            .lines()
            .map(this::parseInput)
            .let(this::bruteForceArrangement)
            .maxOf { it.second }

    override fun partTwo(input: String) =
        input
            .lines()
            .map(this::parseInput)
            .let { this.bruteForceArrangement(it, includeApatheticSelf = true) }
            .maxOf { it.second }
}
