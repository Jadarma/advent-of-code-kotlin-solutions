package aockt.y2022

import aockt.y2022.Y2022D02.RpsChoice.*
import aockt.y2022.Y2022D02.RpsChoice.Companion.versus
import io.github.jadarma.aockt.core.Solution

object Y2022D02 : Solution {

    private enum class RpsChoice(val score: Int) {
        Rock(1),
        Paper(2),
        Scissors(3);

        val winsAgainst: RpsChoice get() = winTable[this]!!
        val losesAgainst: RpsChoice get() = loseTable[this]!!

        companion object {
            /** Lookup table that determines which choice beats which other. */
            val winTable = mapOf(Rock to Scissors, Paper to Rock, Scissors to Paper)

            /** Lookup table that determines which choice loses to which other. */
            val loseTable = mapOf(Rock to Paper, Paper to Scissors, Scissors to Rock)

            /**
             * Compares two choices from the perspective of the first and returns the round score.
             * Returns 3 for draw, 6 for player win, and 0 for [other] win.
             */
            infix fun RpsChoice.versus(other: RpsChoice): Int = when (other) {
                this -> 3
                winsAgainst -> 6
                else -> 0
            }
        }
    }

    @JvmInline
    private value class RpsStrategy(
        private val strategy: List<Pair<RpsChoice, RpsChoice>>,
    ) : List<Pair<RpsChoice, RpsChoice>> by strategy {

        /** Play a game of Rock-Paper-Scissors and return the final score. */
        fun simulateGame(): Int = strategy.sumOf { (opponent, player) -> player.score + (player versus opponent) }

        companion object {
            private val inputRegex = Regex("""^([ABC]) ([XYZ])$""")

            /** Converts a character to an [RpsChoice]. */
            private fun Char.toRpsChoice() = when (this) {
                'A' -> Rock
                'B' -> Paper
                'C' -> Scissors
                else -> throw IllegalArgumentException("Character '$this' is not a valid encoding for RPS.")
            }

            /** Converts a character to an [RpsChoice] depending on the [opponent] move. */
            private fun Char.toOutcomeChoice(opponent: RpsChoice): RpsChoice = when (this) {
                'X' -> opponent.winsAgainst
                'Y' -> opponent
                'Z' -> opponent.losesAgainst
                else -> throw IllegalArgumentException("Character '$this' is not a valid encoding for RPS.")
            }

            /** Returns the [RpsStrategy] described by the [input], throwing [IllegalArgumentException] if invalid. */
            fun parse(input: String, partTwo: Boolean = false): RpsStrategy =
                input
                    .lines()
                    .map {
                        val match = inputRegex.matchEntire(it)
                        requireNotNull(match) { "Line '$it' is not a valid strategy." }
                        val opponent = match.groupValues[1].first().toRpsChoice()
                        val player = match.groupValues[2].first().let { p ->
                            if (partTwo) p.toOutcomeChoice(opponent)
                            else (p - ('X' - 'A')).toRpsChoice()
                        }
                        opponent to player
                    }
                    .let(::RpsStrategy)
        }
    }

    override fun partOne(input: String) = RpsStrategy.parse(input, partTwo = false).simulateGame()
    override fun partTwo(input: String) = RpsStrategy.parse(input, partTwo = true).simulateGame()
}
