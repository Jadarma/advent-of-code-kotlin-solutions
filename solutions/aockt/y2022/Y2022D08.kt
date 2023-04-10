package aockt.y2022

import io.github.jadarma.aockt.core.Solution

object Y2022D08 : Solution {

    /** Parse the input and return the 2D [ForestMap]. */
    private fun parseInput(input: String): ForestMap =
        input
            .lines()
            .map { line -> IntArray(line.length) { i -> line[i].digitToInt() } }
            .toTypedArray()
            .let(::ForestMap)

    /**
     * All known information about a forest tree.
     * @property x The X coordinate.
     * @property y The Y coordinate.
     * @property height The height of the tree in meters.
     * @property isVisible Whether the tree is visible from outside the forest.
     * @property scenicScore How cool a view you'd get from the POV of this tree.
     */
    private data class TreeInfo(
        val x: Int,
        val y: Int,
        val height: Int,
        val isVisible: Boolean,
        val scenicScore: Int,
    ) {
        init {
            require(x >= 0 && y >= 0) { "Tree location cannot have negative coordinates." }
            require(height >= 0) { "Height cannot be negative." }
            require(scenicScore >= 0) { "Scenic score cannot be negative." }
        }
    }

    /**
     * A 2D map of a forest with computed visibility information.
     * @constructor Builds a map from the raw values of the tree heights.
     */
    private class ForestMap(heights: Array<IntArray>) : Iterable<TreeInfo> {

        /** The width and height of the forest, respectively. */
        val dimensions: Pair<Int, Int>

        /** Info of each tree, indexed by location. */
        private val trees: Map<Pair<Int, Int>, TreeInfo>

        init {
            require(heights.isNotEmpty() && heights.first().isNotEmpty()) { "Cannot accept empty map." }
            require(heights.all { it.size == heights.first().size }) { "2D Map is missing some coordinates." }
            dimensions = heights[0].size to heights.size

            trees = buildMap {
                val (width, height) = dimensions
                for (row in 0 until height) {
                    for (col in 0 until width) {
                        val treeHeight = heights[row][col]
                        val directionView = listOf(
                            (col - 1 downTo 0).map { heights[row][it] }, // Left
                            (col + 1 until width).map { heights[row][it] }, // Right
                            (row - 1 downTo 0).map { heights[it][col] }, // Up,
                            (row + 1 until height).map { heights[it][col] }, // Down
                        )

                        val isVisible = directionView.any { direction -> direction.all { it < treeHeight } }

                        val scenicScore = directionView
                            .map { direction -> direction to direction.takeWhile { it < treeHeight }.count() }
                            .map { (trees, count) -> count + if (count < trees.size) 1 else 0 }
                            .reduce(Int::times)

                        put(row to col, TreeInfo(row, col, treeHeight, isVisible, scenicScore))
                    }
                }
            }
        }

        /** Get info about a tree at the given location; (0,0) is the top left corner. */
        operator fun get(x: Int, y: Int): TreeInfo = trees.getValue(x to y)

        /** Iterates through the forest.*/
        override fun iterator(): Iterator<TreeInfo> = trees.values.iterator()
    }

    override fun partOne(input: String) = parseInput(input).count { it.isVisible }
    override fun partTwo(input: String) = parseInput(input).maxOf { it.scenicScore }
}
