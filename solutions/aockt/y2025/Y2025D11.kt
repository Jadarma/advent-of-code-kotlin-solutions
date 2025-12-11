package aockt.y2025

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2025D11 : Solution {

    /** Parse the [input] and return the adjacency graph of the reactor devices. */
    private fun parseInput(input: String): Map<String, Set<String>> = parse {
        input
            .lineSequence()
            .map { it.split(": ", limit = 2) }
            .associate { (name, paths) -> name to paths.split(' ').toSet() }
    }

    /**
     * Count the number of paths through the reactor devices until node `out`.
     * @param graph     The adjacency table for the device nodes.
     * @param from      The node to start the search from.
     * @param mustVisit Only count a path as viable if it passed through these nodes as well.
     */
    private fun solve(graph: Map<String, Set<String>>, from: String, mustVisit: Set<String>): Long {
        require(from in graph) { "Start node is not in graph." }
        val pathCounts = mutableMapOf<String, Long>()

        fun recurse(node: String, leftToVisit: Set<String>): Long {
            if (node == "out") return if (leftToVisit.isEmpty()) 1 else 0

            val pathKey = "$node:+$leftToVisit"
            pathCounts[pathKey]?.let { return it }

            return graph.getValue(node)
                .sumOf { next -> recurse(next, leftToVisit - next) }
                .also { pathCounts[pathKey] = it }
        }

        return recurse(from, mustVisit)
    }

    override fun partOne(input: String) = solve(parseInput(input), from = "you", mustVisit = emptySet())
    override fun partTwo(input: String) = solve(parseInput(input), from = "svr", mustVisit = setOf("dac", "fft"))
}
