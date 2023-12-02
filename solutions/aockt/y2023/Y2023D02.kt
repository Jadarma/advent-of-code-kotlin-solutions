package aockt.y2023

import io.github.jadarma.aockt.core.Solution

object Y2023D02 : Solution {

    /**
     * Holds observations for one instance of the elf's game.
     * @property id The ID of the game.
     * @property rounds The color-count of the balls revealed during the game.
     */
    private data class Game(val id: Int, val rounds: List<Balls>) {

        /** A set of balls, associated by their color. */
        data class Balls(val red: Int, val green: Int, val blue: Int) {
            val power: Int get() = red * green * blue
        }

        /** Whether the game's [rounds] could have been played with a [set] of this dimension. */
        fun isPossibleWith(set: Balls): Boolean = rounds.all {
            it.red <= set.red && it.green <= set.green && it.blue <= set.blue
        }

        /** The smallest set of balls required to play the [rounds] of this game successfully. */
        fun minimumSet(): Balls = Balls(
            red = rounds.maxOf { it.red },
            green = rounds.maxOf { it.green },
            blue = rounds.maxOf { it.blue },
        )

        companion object {
            /** Attempt to parse a [Game] notation from the [input] line. */
            fun parse(input: String): Game = runCatching {
                Game(
                    id = input.substringBefore(": ").substringAfter("Game ").toInt(),
                    rounds = input
                        .substringAfter(": ")
                        .split("; ")
                        .map { round ->
                            round
                                .split(", ", limit = 3)
                                .filterNot(String::isEmpty)
                                .map { it.split(" ", limit = 2) }
                                .associate { (count, color) -> color to count.toInt() }
                                .run {
                                    Balls(
                                        red = getOrDefault("red", 0),
                                        green = getOrDefault("green", 0),
                                        blue = getOrDefault("blue", 0),
                                    )
                                }
                        },
                )
            }.getOrElse { cause -> throw IllegalArgumentException("Invalid input.", cause) }
        }
    }

    /** Parse the [input] and return the list of [Game]s the elf played. */
    private fun parseInput(input: String): Sequence<Game> = input.lineSequence().map(Game.Companion::parse)

    override fun partOne(input: String) = parseInput(input)
        .filter { game -> game.isPossibleWith(Game.Balls(red = 12, green = 13, blue = 14)) }
        .sumOf { it.id }

    override fun partTwo(input: String) = parseInput(input)
        .map { it.minimumSet() }
        .sumOf { set -> set.power }
}
