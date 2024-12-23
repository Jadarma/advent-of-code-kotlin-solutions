package aockt.y2024

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2024D23 : Solution {

    /**
     * Find all subsets of nodes such that every element has [connections] to every other, with at most [limit] nodes.
     * The nodes in the cliques are sorted alphabetically.
     */
    private fun cliques(
        connections: Map<String, Set<String>>,
        limit: Int = Int.MAX_VALUE,
    ): Set<List<String>> = buildSet {

        fun recurse(node: String, clique: Set<String>) {
            clique.sorted()
                .takeUnless { it in this }
                ?.also(::add)
                ?.takeIf { it.size < limit }
                ?: return

            for (neighbor in connections.getValue(node)) {
                if (neighbor in clique) continue
                if (!connections.getValue(neighbor).containsAll(clique)) continue
                recurse(neighbor, clique + neighbor)
            }
        }

        for (x in connections.keys) recurse(x, setOf(x))
    }

    /** Parse the [input] and return a connection map from a computer to all directly linked computers from the LAN. */
    private fun parseInput(input: String): Map<String, Set<String>> = parse {
        buildMap {
            input
                .lineSequence()
                .map { it.split('-', limit = 2) }
                .forEach { (a, b) ->
                    compute(a) { _, cons -> ((cons ?: mutableSetOf()) as MutableSet<String>).apply { add(b) } }
                    compute(b) { _, cons -> ((cons ?: mutableSetOf()) as MutableSet<String>).apply { add(a) } }
                }
        }
    }

    override fun partOne(input: String): Int =
        cliques(parseInput(input), limit = 3)
            .filter { it.size == 3 }
            .count { it.any { node -> node.startsWith('t') } }

    override fun partTwo(input: String): String =
        cliques(parseInput(input), limit = 99)
            .maxBy { it.size }
            .joinToString(",")
}
