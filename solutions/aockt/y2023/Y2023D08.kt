package aockt.y2023

import aockt.util.lcm
import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2023D08 : Solution {

    /**
     * A node in the desert map.
     * @property id      The ID of this node.
     * @property leftId  The ID of the node reached if navigating leftward.
     * @property rightId The ID of the node reached if navigating rightward.
     */
    private data class Node(val id: String, val leftId: String, val rightId: String)

    /**
     * A ghost's map for navigating the Desert Island and avoid sandstorms.
     * @property nodes        The nodes on the map, indexed by their [Node.id].
     * @property instructions The directions given on the map, deciding how to navigate.
     */
    private class DesertGhostMap(private val nodes: Map<String, Node>, private val instructions: String) {

        /** From the given node, at the given [step], decide which is the next visited node. */
        private fun Node.next(step: Int): Node = when (instructions[step % instructions.length]) {
            'L' -> nodes.getValue(leftId)
            'R' -> nodes.getValue(rightId)
            else -> error("Impossible state.")
        }

        /**
         * Navigate from a [start]ing node to a [destination], and return the steps required to reach it, or -1 if it
         * could not be determined.
         */
        private fun navigate(start: Node, destination: (Node) -> Boolean): Int {
            var node = start
            var step = 0

            while (destination(node).not()) {
                if (step < 0) return -1
                node = node.next(step)
                step += 1
            }

            return step
        }

        /**
         * Calculates the total steps needed to reach a node safe from sandstorms if traversing as a ghost.
         * Since we are mere mortals, we make assumptions about the desert map in order to have any hope to
         * compete with a ghost. If these assumptions don't hold, you're on your own.
         * Returns the answer, or -1 if we cannot determine the correct path.
         */
        fun navigateAsGhost(): Long {
            fun navigateAsSuperpositionComponent(start: Node): Int {
                var node = start
                val result = navigate(node) { it.id.endsWith('Z') }

                // Check assumption that the step cycle will keep yielding destination nodes.
                if (result.times(2).rem(instructions.length) != 0) return -1
                for (i in 0..<result) node = node.next(i)
                return if (node.id.endsWith('Z')) result else -1
            }

            return nodes.values
                .filter { it.id.endsWith('A') }
                .map(::navigateAsSuperpositionComponent)
                .onEach { if (it == -1) return -1 }
                .lcm()
        }

        /**
         * Returns in how many steps will a human be able to navigate from `AAA` to `ZZZ` by following the directions
         * on the map, or -1 if such a path cannot be determined.
         */
        fun navigateAsHuman(): Int {
            val start = nodes["AAA"] ?: return -1
            return navigate(start) { it.id == "ZZZ" }
        }
    }

    /** Parse the [input] and return the [DesertGhostMap]. */
    private fun parseInput(input: String): DesertGhostMap = parse {
        val nodeRegex = Regex("""^(\w{3}) = \((\w{3}), (\w{3})\)$""")
        val instructionRegex = Regex("""^[LR]+$""")

        val directions = input.substringBefore('\n')
        require(directions.matches(instructionRegex)) { "Invalid instruction set." }

        val nodes = input
            .lineSequence()
            .drop(2)
            .map { line -> nodeRegex.matchEntire(line)!!.destructured }
            .map { (id, left, right) -> Node(id, left, right) }
            .associateBy { it.id }

        DesertGhostMap(nodes, directions)
    }

    override fun partOne(input: String) = parseInput(input).navigateAsHuman()
    override fun partTwo(input: String) = parseInput(input).navigateAsGhost()
}
