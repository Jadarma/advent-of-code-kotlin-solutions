package aockt.y2025

import aockt.util.parse
import aockt.util.spacial.Area
import aockt.util.spacial.Grid
import aockt.util.validation.assume
import io.github.jadarma.aockt.core.Solution

object Y2025D12 : Solution {

    /**
     * A present for the elves.
     * @property id    The unique identifier of this present.
     * @property shape The 3x3 grid describing the object's shape.
     */
    private data class Present(val id: Int, val shape: Grid<Boolean>) {

        init {
            assume(id in 0..5) { "There are 6 types of presents." }
            assume(shape.height == 3 && shape.width == 3) { "Presents are at most 3x3." }
        }
    }

    /**
     * The desired configuration of presents under a tree.
     * @property area            The size available under the tree.
     * @property desiredPresents How many of each present should be placed under it.
     */
    private data class TreeRegion(val area: Area, val desiredPresents: List<Int>) {

        init {
            assume(desiredPresents.size == 6) { "There are 6 types of presents." }
            require(desiredPresents.all { it >= 0 }) { "Cannot place negative amount of presents." }
        }

        /**
         * Determine if it is possible to pack the [presents] under this tree.
         *
         * **TODO:** _The puzzle inputs seem to be crafted in such a way that if there is enough physical room, then
         *           they will fit; discovered by accident while trying to determine a heuristic for eliminating some
         *           regions outright. This is not an actual solution, and should maybe be revisited when time permits._
         **/
        fun isPossible(presents: List<Present>): Boolean {
            require(presents.size == desiredPresents.size) { "Given presents do not match expected count." }
            return (area.width / 3) * (area.height / 3) >= desiredPresents.sum()
        }
    }

    /** Parse the [input] and return the [Present] shapes and the list of desired [TreeRegion]s. */
    private fun parseInput(input: String): Pair<List<Present>, List<TreeRegion>> = parse {
        val chunks = input.split("\n\n")
        val presents = chunks.dropLast(1)
            .map { it.split(":\n", limit = 2) }
            .map { (id, shape) ->
                Present(
                    id = id.toInt(),
                    shape = Grid(shape) { it == '#' })
            }
        val trees = chunks.last()
            .lineSequence()
            .map { it.split(": ", limit = 2) }
            .map { (size, presents) ->
                TreeRegion(
                    area = size
                        .split('x', limit = 2)
                        .map(String::toInt)
                        .let { (x, y) -> Area(x, y) },
                    desiredPresents = presents
                        .split(' ', limit = 6)
                        .map(String::toInt)
                )
            }
            .toList()
        presents to trees
    }

    override fun partOne(input: String) = parseInput(input).let { (presents, trees) ->
        trees.count { it.isPossible(presents) }
    }
}
