package aockt.y2021

import io.github.jadarma.aockt.core.Solution

object Y2021D04 : Solution {

    /**
     * A 5x5 BingoBoard.
     * @constructor Sets up a new bingo board with these 25 numbers, from left to right, top to bottom.
     */
    private class BingoBoard(numbers: List<Int>) {

        private val grid: List<BingoSquare> = numbers.map { BingoSquare(it, false) }

        init {
            require(numbers.size == 25) { "A bingo board has exactly 25 numbers on it." }
        }

        /** Simple holder for a square and its state. */
        private class BingoSquare(val number: Int, var isSet: Boolean = false)

        /** If the [number] appears on the board, marks it, and returns if the board is now in BINGO! */
        fun callAndCheck(number: Int): Boolean {
            grid.firstOrNull { it.number == number }?.apply { isSet = true }
            return isBingo()
        }

        /** Checks if the board is in BINGO! */
        fun isBingo(): Boolean {
            for (x in 0..4) {
                var horizontalBingo = 0
                var verticalBingo = 0
                for (y in 0..4) {
                    if (grid[x * 5 + y].isSet) horizontalBingo++
                    if (grid[y * 5 + x].isSet) verticalBingo++
                }
                if (horizontalBingo == 5 || verticalBingo == 5) return true
            }
            return false
        }

        /** Returns the sum of all the numbers that have not been marked on this board. */
        fun sumOfUnsetNumbers(): Int = grid.filterNot { it.isSet }.sumOf { it.number }
    }

    /** Parses the input and returns the list of numbers to be played and all the available [BingoBoard]s. */
    private fun parseInput(input: String): Pair<List<Int>, List<BingoBoard>> = runCatching {
        val lines = input.lines().filterNot { it.isBlank() }
        val numbers = lines.first().split(",").map { it.toInt() }
        val bingoBoards = lines
            .drop(1).chunked(5)
            .map { rows -> rows.flatMap { row -> row.trim().split(Regex("""\s+""")).map(String::toInt) } }
            .map(::BingoBoard)
        numbers to bingoBoards
    }.getOrElse { throw IllegalArgumentException("Invalid input.") }

    override fun partOne(input: String): Any {
        val (numbers, boards) = parseInput(input)
        numbers.forEach { number ->
            val bingo = boards.firstOrNull { it.callAndCheck(number) }
            if (bingo != null) {
                return bingo.sumOfUnsetNumbers() * number
            }
        }
        error("No winning board found.")
    }

    override fun partTwo(input: String): Any {
        val (numbers, boards) = parseInput(input)
        val candidates = boards.toMutableList()
        numbers.forEach { number ->
            while (true) {
                val board = candidates.firstOrNull { it.callAndCheck(number) } ?: break
                candidates.remove(board)
                if (candidates.isEmpty()) return board.sumOfUnsetNumbers() * number
            }
        }
        error("There is a tie for which board wins last.")
    }
}
