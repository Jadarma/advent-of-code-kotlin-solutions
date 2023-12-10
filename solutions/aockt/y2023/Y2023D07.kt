package aockt.y2023

import aockt.util.parse
import aockt.y2023.Y2023D07.HandType.*
import io.github.jadarma.aockt.core.Solution

object Y2023D07 : Solution {

    /** A type of playing card for Camel Cards. */
    private enum class Card(val symbol: Char) : Comparable<Card> {
        Joker('*'), Two('2'), Three('3'), Four('4'), Five('5'),
        Six('6'), Seven('7'), Eight('8'), Nine('9'), Ten('T'),
        Jack('J'), Queen('Q'), King('K'), Ace('A');

        companion object {
            private val symbolMap: Map<Char, Card> = Card.entries.associateBy { it.symbol }
            fun valueOf(char: Char): Card = symbolMap.getValue(char)
        }
    }

    /** A type of hand */
    private enum class HandType { HighCard, OnePair, TwoPair, ThreeOfAKind, FullHouse, FourOfAKind, FiveOfAKind }

    /**
     * A dealt hand of Camel Cards.
     * @property cards The dealt cards, in order.
     * @property handType What kind of [HandType] the [cards] make up.
     */
    private data class Hand(val cards: List<Card>) : Comparable<Hand> {

        val handType: HandType = run {
            require(cards.size == 5) { "A Camel Cards hand must have exactly 5 cards." }
            val (jokers, plain) = cards.partition { it == Card.Joker }
            val numberOfJokers = jokers.size
            val stats: List<Int> = plain.groupingBy { it }.eachCount().values.sortedDescending()

            if (numberOfJokers >= 4) return@run FiveOfAKind

            when (stats.first()) {
                5 -> FiveOfAKind
                4 -> if (numberOfJokers == 1) FiveOfAKind else FourOfAKind
                3 -> when (numberOfJokers) {
                    0 -> if (2 in stats.drop(1)) FullHouse else ThreeOfAKind
                    1 -> FourOfAKind
                    2 -> FiveOfAKind
                    else -> error("Impossible state.")
                }
                2 -> when (numberOfJokers) {
                    0 -> if (2 in stats.drop(1)) TwoPair else OnePair
                    1 -> if (2 in stats.drop(1)) FullHouse else ThreeOfAKind
                    2 -> FourOfAKind
                    3 -> FiveOfAKind
                    else -> error("Impossible state.")
                }
                1 -> when (numberOfJokers) {
                    3 -> FourOfAKind
                    2 -> ThreeOfAKind
                    1 -> OnePair
                    0 -> HighCard
                    else -> error("Impossible state.")
                }
                else -> error("Impossible state.")
            }
        }

        /** Compare this hand to the [other], it either wins based on its [handType], or by the first highest card. */
        override fun compareTo(other: Hand) = comparator.compare(this, other)

        private companion object {
            val comparator: Comparator<Hand> = compareBy(Hand::handType).thenComparator { a, b ->
                val index = (0..<5).firstOrNull { a.cards[it] != b.cards[it] } ?: 0
                a.cards[index] compareTo b.cards[index]
            }
        }
    }

    /**
     * Parse the [input] and returns the list of hands and their associated bids.
     * @param input The puzzle input.
     * @param withJokers If true, all Jacks will be instead replaced by Jokers.
     */
    private fun parseInput(input: String, withJokers: Boolean): List<Pair<Hand, Long>> = parse {
        input
            .lineSequence()
            .map { if (withJokers) it.replace('J', '*') else it }
            .map { it.split(' ', limit = 2) }
            .map { (hand, bid) -> hand.map(Card.Companion::valueOf).let(::Hand) to bid.toLong() }
            .toList()
    }

    /** Sort a list of hands by rank, and return the sum of their rank multiplied by their respective bid. */
    private fun List<Pair<Hand, Long>>.solve() =
        sortedBy { it.first }
            .withIndex()
            .sumOf { it.index.inc() * it.value.second }

    override fun partOne(input: String) = parseInput(input, withJokers = false).solve()
    override fun partTwo(input: String) = parseInput(input, withJokers = true).solve()
}
