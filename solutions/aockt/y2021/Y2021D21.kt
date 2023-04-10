package aockt.y2021

import io.github.jadarma.aockt.core.Solution

object Y2021D21 : Solution {

    /**
     * Simulates a practice round of the Dirac Dice game with a 100-sided deterministic die and a winning score of 1000.
     * @return The number of times the dice was rolled, and the scores of each player.
     */
    private fun playPracticeRound(
        playerOneStartSquare: Int = 0,
        playerTwoStartSquare: Int = 0,
    ): Triple<Int, Int, Int> {
        require(playerOneStartSquare in 1..10) { "Invalid starting position for player 1." }
        require(playerOneStartSquare in 1..10) { "Invalid starting position for player 2." }

        val position = mutableListOf(playerOneStartSquare - 1, playerTwoStartSquare - 1)
        val score = mutableListOf(0, 0)
        var player = 0
        var rolls = 0

        fun nextRoll() = rolls++ % 100 + 1
        fun gameWon() = score.any { it >= 1000 }

        fun play(player: Int) {
            val increment = List(3) { nextRoll() }.sum()
            val current = position[player]
            position[player] = (current + increment) % 10
            score[player] += position[player] + 1
        }

        while (!gameWon()) {
            play(player)
            player = (player + 1) % 2
        }

        return Triple(rolls, score[0], score[1])
    }

    /**
     * Simulates all the parallel universes with a three sided quantum die and returns the win rate of the players.
     * @return In how many universes each player won.
     */
    private fun playQuantumRound(
        playerOneStartSquare: Int = 0,
        playerTwoStartSquare: Int = 0,
    ): Pair<Long, Long> {
        require(playerOneStartSquare in 1..10) { "Invalid starting position for player 1." }
        require(playerOneStartSquare in 1..10) { "Invalid starting position for player 2." }

        val possibleRolls = with(listOf(1, 2, 3)) { flatMap { a -> flatMap { b -> map { c -> listOf(a, b, c) } } } }
            .groupingBy { it.sum() }
            .eachCount()
            .mapValues { it.value.toLong() }

        fun play(position: List<Int>, score: List<Int>, player: Int): Pair<Long, Long> =
            possibleRolls.map { (roll, count) ->
                val nextPosition = position.toMutableList().apply { set(player, (position[player] + roll) % 10) }
                val nextScore = score.toMutableList().apply { set(player, (score[player] + nextPosition[player] + 1)) }

                when (nextScore.indexOfFirst { it >= 21 }) {
                    0 -> count to 0L
                    1 -> 0L to count
                    else -> play(nextPosition, nextScore, (player + 1) % 2).run { first * count to second * count }
                }
            }.reduce { acc, wins -> acc.first + wins.first to acc.second + wins.second }

        return play(
            position = listOf(playerOneStartSquare - 1, playerTwoStartSquare - 1),
            score = listOf(0, 0),
            player = 0,
        )
    }

    /** Parse the [input] and return the starting positions of player one and two respectively. */
    private fun parseInput(input: String): Pair<Int, Int> = runCatching {
        val (line1, line2) = input.lines()
        val lineRegex = Regex("""^Player (\d+) starting position: (\d+)$""")
        val (p1, pos1) = lineRegex.matchEntire(line1)!!.destructured
        val (p2, pos2) = lineRegex.matchEntire(line2)!!.destructured
        require(p1 == "1" && p2 == "2")
        pos1.toInt() to pos2.toInt()
    }.getOrElse { throw IllegalArgumentException("Invalid input.", it) }

    override fun partOne(input: String): Int {
        val (p1, p2) = parseInput(input)
        val (rolls, scoreP1, scoreP2) = playPracticeRound(p1, p2)
        return rolls * minOf(scoreP1, scoreP2)
    }

    override fun partTwo(input: String): Long {
        val (p1, p2) = parseInput(input)
        val (universesWhereP1Won, universesWhereP2Won) = playQuantumRound(p1, p2)
        return maxOf(universesWhereP1Won, universesWhereP2Won)
    }
}
