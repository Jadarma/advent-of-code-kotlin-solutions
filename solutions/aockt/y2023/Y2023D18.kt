package aockt.y2023

import aockt.util.parse
import aockt.util.spacial.Direction
import aockt.util.spacial.Direction.*
import aockt.util.spacial.Point
import aockt.util.spacial.move
import aockt.util.spacial.polygonArea
import io.github.jadarma.aockt.core.Solution

object Y2023D18 : Solution {

    /**
     * A single step of a dig plan.
     * @property direction The direction to dig in from the current point.
     * @property length    How long should the next segment be.
     */
    private data class DigPlanStep(val direction: Direction, val length: Int)

    /** Follow the [DigPlanStep]s from a [start] point, and return the coordinates of all corners of the polygon. */
    private fun List<DigPlanStep>.digTrench(start: Point): List<Point> = buildList {
        var point = start
        for ((direction, length) in this@digTrench) {
            point = point.move(direction, length.toLong())
            add(point)
        }
        check(point == start) { "Trench invalid, did not loop back to start." }
    }

    /**
     * Parse the input and return the dig plan.
     * @param input           The puzzle input.
     * @param inColorEncoding Whether to take the steps from the colors or from the plain text.
     */
    private fun parseInput(input: String, inColorEncoding: Boolean): List<DigPlanStep> = parse {
        val stepRegex = Regex("""^([LRUD]) (\d+) \(#([0-9a-f]{6})\)$""")
        val directions = listOf(Right, Down, Left, Up)

        fun String.parseDirection(): Direction = directions["RDLU".indexOf(first())]

        fun String.parseColorPlan(): DigPlanStep = DigPlanStep(
            direction = directions[last().digitToInt()],
            length = substring(0, 5).toInt(16),
        )

        input
            .lineSequence()
            .map { stepRegex.matchEntire(it)!!.destructured }
            .map { (direction, length, color) ->
                if (inColorEncoding) color.parseColorPlan()
                else DigPlanStep(direction.parseDirection(), length.toInt())
            }
            .toList()
    }

    /** Simulate digging a trench following the steps, and return the total volume of the hollowed out structure. */
    private fun List<DigPlanStep>.solve(): Long = digTrench(Point(0, 0)).polygonArea(includingPerimeter = true)

    override fun partOne(input: String) = parseInput(input, inColorEncoding = false).solve()
    override fun partTwo(input: String) = parseInput(input, inColorEncoding = true).solve()
}
