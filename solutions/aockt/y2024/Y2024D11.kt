package aockt.y2024

import aockt.util.parse
import aockt.y2024.Y2024D11.Stone
import io.github.jadarma.aockt.core.Solution
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

object Y2024D11 : Solution {

    /** A strange stone from Pluto, wih a [number] engraved on it. */
    @JvmInline
    private value class Stone(val number: Long) {

        /** Returns the stone(s) that will appear after this one is blinked at. */
        fun blink(): List<Stone> = buildList {
            if (number == 0L) { add(Stone(1L)); return@buildList }

            val digitCount = floor(log10(number.toDouble())).toInt() + 1
            if (digitCount % 2 == 1) { add(Stone(number * 2024L)); return@buildList }

            val split = 10.0.pow(digitCount / 2).toLong()
            add(Stone(number / split))
            add(Stone(number % split))
        }
    }

    /** Calculates how many stones will appear after a number of [blinks] from the starting [stones]. */
    private fun solve(stones: List<Stone>, blinks: Int): Long {
        val cache = mutableMapOf<Pair<Stone, Int>, Long>()

        fun recurse(stone: Stone, times: Int): Long = when (times) {
            0 -> 1
            else -> cache.getOrPut(stone to times) {
                stone.blink().sumOf { recurse(it, times - 1) }
            }
        }

        return stones.sumOf { stone -> recurse(stone, blinks) }
    }

    /** Parse the [input] and return the initial rows of [Stone]s. */
    private fun parseInput(input: String): List<Stone> = parse { input.split(' ').map { Stone(it.toLong()) } }

    override fun partOne(input: String): Long = solve(stones = parseInput(input), blinks = 25)
    override fun partTwo(input: String): Long = solve(stones = parseInput(input), blinks = 75)
}
