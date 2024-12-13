package aockt.y2024

import aockt.util.parse
import aockt.util.spacial.Point
import io.github.jadarma.aockt.core.Solution

object Y2024D13 : Solution {

    /**
     * The configuration of a crane machine. The arm starts at the [Point.Origin].
     * @property prize The coordinate of the prize.
     * @property a     The delta the arm will move when pressing the A button.
     * @property b     The delta the arm will move when pressing the B button.
     */
    private data class CraneMachine(val prize: Point, val a: Point, val b: Point) {
        init {
            require(listOf(prize.x, prize.y, a.x, a.y, b.x, b.y).all { it > 0 }) { "Crane values must be positive." }
            require(a != b) { "Crane buttons A and B must have different offsets." }
        }

        /** The total tokens required to play and win, or `null` if the machine is rigged and there is no solution. */
        val costToSolve: Long? by lazy {
            // Explanation: To win, we need to find integer solutions n and m such that:
            // a.x * n + b.x * m == prize.x and a.y * n + b.y * m == prize.y
            // Solving it on paper gives us the following formulas:
            val n = (prize.x * b.y - prize.y * b.x).toDouble() / (a.x * b.y - b.x * a.y)
            val m = (prize.x - a.x * n) / b.x
            if (n % 1 != 0.0 || m % 1 != 0.0) null
            else n.toLong() * 3 + m.toLong()
        }
    }

    /**
     * Parse the [input] and return the configurations of the [CraneMachine]s.
     * If [extraLong], will move the prize very far away.
     */
    private fun parseInput(input: String, extraLong: Boolean): List<CraneMachine> = parse {
        val regex = Regex("""Button A: X\+(\d+), Y\+(\d+)\nButton B: X\+(\d+), Y\+(\d+)\nPrize: X=(\d+), Y=(\d+)""")
        val prizeDelta = if (extraLong) 10_000_000_000_000L else 0L
        input
            .split("\n\n")
            .map { regex.matchEntire(it)!!.destructured }
            .map { (ax, ay, bx, by, px, py) ->
                CraneMachine(
                    prize = Point(px.toLong() + prizeDelta, py.toLong() + prizeDelta),
                    a = Point(ax.toLong(), ay.toLong()),
                    b = Point(bx.toLong(), by.toLong()),
                )
            }
    }

    override fun partOne(input: String): Long = parseInput(input, false).mapNotNull(CraneMachine::costToSolve).sum()
    override fun partTwo(input: String): Long = parseInput(input, true ).mapNotNull(CraneMachine::costToSolve).sum()
}
