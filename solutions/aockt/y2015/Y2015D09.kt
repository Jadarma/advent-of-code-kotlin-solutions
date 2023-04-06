package aockt.y2015

import aockt.util.generatePermutations
import io.github.jadarma.aockt.core.Solution

object Y2015D09 : Solution {

    private val inputRegex = Regex("""^(\w+) to (\w+) = (\d+)$""")

    /** Parses a single line of input and returns a triple containing two locations and the distance between them. */
    private fun parseInput(input: String): Triple<String, String, Int> {
        val (from, to, distance) = inputRegex.matchEntire(input)!!.destructured
        return Triple(from, to, distance.toInt())
    }

    /**
     * Given a list of distances between all pairs of locations on the map, returns all possible paths that visit all
     * of them and their total distance.
     */
    private fun bruteForceRoutes(locationData: List<Triple<String, String, Int>>): Sequence<Pair<List<String>, Int>> {
        val locations = mutableSetOf<String>()
        val distances = mutableMapOf<Pair<String, String>, Int>()

        locationData.forEach { (from, to, distance) ->
            locations.add(from)
            locations.add(to)
            distances[from to to] = distance
            distances[to to from] = distance
        }

        return locations
            .toList()
            .generatePermutations()
            .map { route -> route to route.windowed(2).sumOf { distances.getValue(it[0] to it[1]) } }
    }

    override fun partOne(input: String) =
        input
            .lines()
            .map(this::parseInput)
            .let(this::bruteForceRoutes)
            .minOf { it.second }

    override fun partTwo(input: String) =
        input
            .lines()
            .map(this::parseInput)
            .let(this::bruteForceRoutes)
            .maxOf { it.second }
}
