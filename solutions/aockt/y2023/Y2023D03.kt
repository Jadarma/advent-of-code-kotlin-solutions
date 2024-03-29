package aockt.y2023

import aockt.util.spacial.Point
import aockt.util.spacial.Area
import aockt.util.spacial.coerceIn
import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2023D03 : Solution {

    /** A part number label in the engine schematics, drawn at [coords] with a numeric [value]. */
    private data class PartNumber(val coords: Area, val value: Int)

    /** A symbol in the engine schematics, drawn at [coords] with the symbol [value]. */
    private data class Symbol(val coords: Point, val value: Char)

    /** Parse the [input] and build a representation of the engine schematics, mapping each symbol to adjacent parts. */
    private fun parseInput(input: String): Map<Symbol, List<PartNumber>> = parse {
        val lines: Array<String> = input.lines().toTypedArray().also {
            require(it.isNotEmpty())
            require(it.all { line -> line.length == it.first().length })
        }
        val bounds = Area(0..<lines.first().length, lines.indices)

        fun adjacentSymbolOrNull(part: PartNumber): Symbol? {
            val searchSpace = with(part.coords) {
                Area(
                    xRange = xRange.run { first - 1..last + 1 },
                    yRange = yRange.run { first - 1..last + 1 },
                )
            }.coerceIn(bounds)

            for (point in searchSpace) {
                val value = lines[point.y.toInt()][point.x.toInt()]
                if (value != '.' && value.isLetterOrDigit().not()) {
                    return Symbol(point, value)
                }
            }

            return null
        }

        buildMap<Symbol, MutableList<PartNumber>> {
            val numberRegex = Regex("""[1-9]\d*""")
            lines.forEachIndexed { y, row ->
                numberRegex.replace(row) { label ->
                    val part = PartNumber(Area(label.range, y..y), label.value.toInt())
                    adjacentSymbolOrNull(part)?.let { symbol -> getOrPut(symbol) { mutableListOf() }.add(part) }
                    ""
                }
            }
        }
    }

    override fun partOne(input: String) =
        parseInput(input)
            .values
            .sumOf { parts -> parts.sumOf { it.value } }

    override fun partTwo(input: String) =
        parseInput(input)
            .filterKeys { it.value == '*' }
            .filterValues { it.size == 2 }
            .values
            .sumOf { (l, r) -> l.value * r.value }
}
