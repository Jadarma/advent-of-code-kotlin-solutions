package aockt.y2024

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2024D05 : Solution {

    /** A rule that states the [first] page must come before the [second] one. */
    private data class OrderingRule(val first: Int, val second: Int)

    /** A safety manual update, with a list of [pages] to print. */
    private data class Update(val pages: List<Int>) {

        private val locations: Map<Int, Int> = pages.withIndex().associate { (i, p) -> p to i }

        /** Keep only the rules relevant to this update. */
        private fun Set<OrderingRule>.filterRelevant(): Set<OrderingRule> =
            filter { rule -> rule.first in locations && rule.second in locations }.toSet()

        /** Check if this update matches all the [rules]. */
        infix fun matches(rules: Set<OrderingRule>): Boolean =
            rules
                .filterRelevant()
                .all { rule -> locations.getValue(rule.first) < locations.getValue(rule.second) }

        /**
         * Attempt to reorder the pages in this update such that it conforms to the [rules].
         * Returns `null` if no valid reconfiguration was found.
         * If this update is already correct, returns the same instance.
         */
        fun correctOrNull(rules: Set<OrderingRule>): Update? {
            // Not necessary for the puzzle, but… I need it.
            if (this matches rules) return this

            @Suppress("NAME_SHADOWING")
            val rules = rules.filterRelevant()

            val pageRules = pages.associateWith { page ->
                val before = rules.filter { it.second == page }.map { it.first }
                val after = rules.filter { it.first == page }.map { it.second }
                before to after
            }

            return buildList(capacity = pages.size) {
                for (page in pages) {
                    val (before, after) = pageRules.getValue(page)
                    val atLeast = before.maxOfOrNull(::indexOf) ?: -1
                    val atMost = after.minOfOrNull(::indexOf).takeIf { it != -1 } ?: pages.size.dec()
                    val validRange = atLeast + 1..atMost
                    if (validRange.isEmpty()) return null
                    add(validRange.first, page)
                }
            }.let(::Update).takeIf { it matches rules }
        }
    }

    /** Parse the [input] and return the set of [OrderingRule]s and the set of safety manual [Update]s. */
    private fun parseInput(input: String): Pair<Set<OrderingRule>, Set<Update>> = parse {
        val (rulesRaw, pagesRaw) = input.split("\n\n", limit = 2)

        val rules = rulesRaw
            .lineSequence()
            .map { line -> line.split('|', limit = 2).map(String::toInt) }
            .map { (l, r) -> OrderingRule(l, r) }
            .toSet()

        val pages = pagesRaw
            .lineSequence()
            .map { line -> line.split(',').map(String::toInt) }
            .map(::Update)
            .toSet()

        rules to pages
    }

    override fun partOne(input: String): Int {
        val (rules, pages) = parseInput(input)
        return pages
            .filter { it matches rules }
            .sumOf { with(it.pages) { get(size / 2) } }
    }

    override fun partTwo(input: String): Int {
        val (rules, pages) = parseInput(input)
        return pages
            .asSequence()
            .filterNot { it matches rules }
            .mapNotNull { it.correctOrNull(rules) }
            .sumOf { with(it.pages) { get(size / 2) } }
    }
}
