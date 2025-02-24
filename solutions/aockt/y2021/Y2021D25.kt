package aockt.y2021

import aockt.util.parse
import aockt.util.spacial.*
import io.github.jadarma.aockt.core.Solution

object Y2021D25 : Solution {

    private class Trench(private val data: MutableGrid<Node>) : Grid<Trench.Node> by data {

        /** The type of nodes the trench map can have. */
        enum class Node { Empty, East, South }

        /** Return the point this cucumber will occupy, if a legal move is available. */
        private fun GridCell<Node>.moveTarget(): Point? {
            val direction = when (value) {
                Node.Empty -> return null
                Node.East -> Direction.Right
                Node.South -> Direction.Down
            }
            return position
                .move(direction)
                .run {
                    copy(
                        x = if (x >= data.width) 0 else x,
                        y = if (y < 0) data.height - 1L else y,
                    )
                }
                .takeIf { data[it] == Node.Empty }
        }

        /** Perform the cucumber move. */
        private fun GridCell<Node>.moveCucumber() {
            val target = moveTarget() ?: error("Illegal move.")
            val cucumber = data[position]
            data[position] = Node.Empty
            data[target] = cucumber
        }

        /**
         * Mutate the trench, calculating the movements of the sea cucumbers, and return whether any modification
         * took place since the previous state.
         */
        fun step(): Boolean {
            fun moveAndCountCucumbers(type: Node): Int =
                data.points()
                    .filter { it.value == type }
                    .filter { it.moveTarget() != null }
                    .toList() // Force evaluation of all cucumbers before moving.
                    .onEach { it.moveCucumber() }
                    .count()

            return moveAndCountCucumbers(Node.East) + moveAndCountCucumbers(Node.South) > 0
        }
    }

    /** Parse the [input] and return the state of the [Trench]. */
    private fun parseInput(input: String): Trench = parse {
        MutableGrid(input) {
            when (it) {
                '.' -> Trench.Node.Empty
                '>' -> Trench.Node.East
                'v' -> Trench.Node.South
                else -> error("Invalid input")
            }
        }.let(::Trench)
    }

    override fun partOne(input: String): Int {
        val trench = parseInput(input)
        var steps = 0
        while (trench.step()) {
            if (steps++ > 1000) error("Giving up.")
        }
        return steps + 1
    }
}
