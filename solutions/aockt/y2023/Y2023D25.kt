package aockt.y2023

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution
import kotlin.random.Random

object Y2023D25 : Solution {

    /**
     * The internal wiring of a Snow Machine.
     * @property nodes The names of the parts.
     * @property edges Pairs of parts, determining bidirectional connections.
     */
    private class SnowMachineWiring(val nodes: Set<String>, val edges: Set<Pair<String, String>>)

    /** Parses the [input] and return the [SnowMachineWiring]. */
    private fun parseInput(input: String): SnowMachineWiring = parse {
        val lines = input
            .lineSequence()
            .map { line -> line.split(": ", " ").filterNot(String::isBlank) }
            .toList()

        val nodes = lines.flatten().toSet()
        val edges = lines.flatMap { line -> line.drop(1).map { line.first() to it } }.toSet()
        SnowMachineWiring(nodes, edges)
    }

    /**
     * Finds the two subsets of machine parts, by removing three wires.
     *
     * Notes:
     * - Implemented using [Karger's Algorithm](https://en.wikipedia.org/wiki/Karger%27s_algorithm).
     * - A solution will eventually be found, but speed depends on luck because graph contractions are chosen at random.
     * - Implementation adapted from [u/4HbQ](https://old.reddit.com/r/adventofcode/comments/18qbsxs/2023_day_25_solutions/ketzp94/)'s
     *   beautiful Python rendition.
     *
     * @param random The random instance to use when deciding which nodes to contract. Has default.
     * @return The product of the sizes of the two subsets after removing the 3 bridge edges.
     */
    private fun SnowMachineWiring.solve(random: Random = Random): Int {
        while (true) {
            // We start by having each node be its own subgroup, nothing interconnected yet.
            val subgroups: MutableSet<MutableSet<String>> = nodes.map { mutableSetOf(it) }.toMutableSet()
            val subgroupContaining = { node: String -> subgroups.first { node in it } }

            // We pick random edges to contract. Since the actual graph has no isolated nodes, it is guaranteed we will
            // reach two subgroups before we run out of edges, so instead of picking ones at random, we shuffle the
            // order, to avoid processing the same edge twice.
            val randomEdge = edges.shuffled(random).map { it.toList() }.iterator()

            // While we still have more than two subgroups, we contract edges:
            // - Picking a random edge, check if its nodes are part of the same subgroup.
            // - If they are, there's nothing to do, as the nodes in that subgroup are already interconnected.
            // - Otherwise, we now know that every node from s1 has a way of reaching s2 via this edge and vice-versa.
            // - We can therefore join them in a single subgroup and discard the other, shrinking our subgroup count.
            while (subgroups.size > 2) {
                val (s1, s2) = randomEdge.next().map(subgroupContaining)
                if (s1 === s2) continue
                s1.addAll(s2)
                subgroups.removeIf { it === s2 } // subgroups.remove(s2) does NOT work!
            }

            // On a correctly partitioned wiring graph, the only edges that cross them are the three wires we need to
            // cut. If we find a fourth, it means we did not partition correctly and should try again.
            if (edges.count { (a, b) -> subgroupContaining(a) != subgroupContaining(b) } > 3) continue

            return subgroups.map { it.size }.reduce(Int::times)
        }
    }

    // Seed unnecessary for solution to work, but this one solves it fast for my input, and it helps with not wasting
    // time when running unit tests. Without a seed, returns an answer in 5-20 seconds.
    override fun partOne(input: String) = parseInput(input).solve(Random(seed = -8_285_910_637_769_521_595L))
}
