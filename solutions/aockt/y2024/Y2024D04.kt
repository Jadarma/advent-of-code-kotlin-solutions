package aockt.y2024

import aockt.util.parse
import aockt.util.spacial.Direction.*
import aockt.util.spacial.Grid
import aockt.util.spacial.get
import aockt.util.spacial.Point
import aockt.util.spacial.move
import aockt.util.spacial.points
import io.github.jadarma.aockt.core.Solution

object Y2024D04 : Solution {

    /** Parse the [input] and return the word-search as a grid of characters. */
    private fun parseInput(input: String): Grid<Char> = parse { Grid(input) }

    override fun partOne(input: String): Int = with(parseInput(input)) {

        val directions = listOf<(Point, Any) -> Point>(
            { p, _ -> p.move(Up) },
            { p, _ -> p.move(Right).move(Up) },
            { p, _ -> p.move(Right) },
            { p, _ -> p.move(Right).move(Down) },
            { p, _ -> p.move(Down) },
            { p, _ -> p.move(Left).move(Down) },
            { p, _ -> p.move(Left) },
            { p, _ -> p.move(Left).move(Up) },
        )

        points()
            .filter { (_, v) -> v == 'X' }
            .sumOf { (p, _) ->
                directions
                    .map { direction -> move(p, direction).take(4).joinToString("") { it.value.toString() } }
                    .count { it == "XMAS" }
            }
    }

    override fun partTwo(input: String): Int = with(parseInput(input)) {
        val mas = setOf("MAS", "SAM")
        points()
            .filter { (p, v) -> v == 'A' && p.x in 1..<width - 1 && p.y in 1..<height - 1 }
            .count { (p, v) ->
                val mainDiag = "${get(p.move(Left).move(Up))}$v${get(p.move(Right).move(Down))}"
                val sideDiag = "${get(p.move(Right).move(Up))}$v${get(p.move(Left).move(Down))}"
                mainDiag in mas && sideDiag in mas
            }
    }
}
