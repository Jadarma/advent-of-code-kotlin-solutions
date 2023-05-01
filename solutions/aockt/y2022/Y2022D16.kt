package aockt.y2022

import aockt.util.Graph
import aockt.util.pathTo
import aockt.util.search
import io.github.jadarma.aockt.core.Solution

object Y2022D16 : Solution {

    private data class Valve(
        val name: String,
        val flowRate: Int,
        val tunnels: Map<String, Int>,
    )

    private data class State(
        val timeLeft: Int,
        val nextValve: String?,
        val releasedPressure: Int,
        val openValves: Set<String>,
    )

    private class VolcanoSolver(private val valves: Map<String, Valve>) : Graph<State> {

        /** The total flow rate per minute if all of the [valves] would be open. */
        private val maximumFlowRate = valves.values.sumOf { it.flowRate }

        /** Computes all the possible decisions to take to produce new states starting from the current one. */
        private fun neighbors(node: State): List<State> {

            // If all valves have been opened, just wait until time runs out.
            if (node.openValves.size == valves.size) {
                return listOf(
                    State(
                        timeLeft = 0,
                        nextValve = null,
                        releasedPressure = node.releasedPressure + node.timeLeft * maximumFlowRate,
                        openValves = node.openValves,
                    )
                )
            }

            val closedValves = valves.keys - node.openValves - "AA"

            return closedValves
                .mapNotNull { next ->
                    val costToOpen = node.nextValve!!.let(valves::getValue).tunnels.getValue(next)
                    if (costToOpen > node.timeLeft) return@mapNotNull null

                    State(
                        timeLeft = node.timeLeft - costToOpen,
                        nextValve = next,
                        releasedPressure = node.releasedPressure + costToOpen * node.openValves.sumOf {
                            valves.getValue(
                                it
                            ).flowRate
                        },
                        openValves = node.openValves + next,
                    )
                }
                .ifEmpty {
                    // Not enough time to open any other valve, wait the rest of the time.
                    State(
                        timeLeft = 0,
                        nextValve = null,
                        releasedPressure = node.releasedPressure + node.timeLeft * node.openValves.sumOf {
                            valves.getValue(
                                it
                            ).flowRate
                        },
                        openValves = node.openValves,
                    ).let { listOf(it) }
                }
        }

        /**
         * The transition cost between two states is defined as the amount of pressure not released because of closed
         * valves.
         */
        private fun cost(from: State, to: State): Int =
            from.openValves
                .map(valves::getValue)
                .sumOf(Valve::flowRate)
                .let { currentFlowRate -> maximumFlowRate - currentFlowRate }
                .times(from.timeLeft - to.timeLeft)

        override fun neighboursOf(node: State) = neighbors(node).map { next -> next to cost(node, next) }

        fun solve(): Int = search(
            start = State(30, "AA", 0, emptySet()),
            goalFunction = { it.timeLeft == 0 }
        ).destination!!.releasedPressure
    }

    /**
     * Parses the [input] and returns the valves, keyed by their name.
     * Instead of returning the actual graph of all the valves, returns an equivalent fully-connected graph that only
     * contains the valves that have a positive flow rate, with "virtual tunnels" to all other valves in the volcano,
     * with their cost adjusted to the minimum possible value for pathfinding in the real volcano.
     */
    private fun parseInput(input: String): VolcanoSolver {
        val inputRegex = Regex("""^Valve ([A-Z]{2}) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z, ]+)$""")

        val valves = input
            .lineSequence()
            .mapNotNull(inputRegex::matchEntire)
            .map(MatchResult::destructured)
            .associate { (name, flow, tunnel) ->
                name to Valve(
                    name,
                    flow.toInt(),
                    tunnel.split(", ").toSet().associateWith { 1 }.toMap()
                )
            }

        val unoptimisedVolcano = object : Graph<Valve> {
            override fun neighboursOf(node: Valve): List<Pair<Valve, Int>> = valves
                .getValue(node.name)
                .tunnels.keys
                .map(valves::getValue)
                .map { it to 1 }
        }

        val importantValves = valves.filterValues { it.name == "AA" || it.flowRate > 0 }.map { it.value }

        return importantValves
            .map { valve -> valve to unoptimisedVolcano.search(valve, goalFunction = { false }) }
            .map { (valve, search) ->
                Valve(
                    name = valve.name,
                    flowRate = valve.flowRate,
                    tunnels = importantValves
                        .asSequence()
                        .filterNot { it == valve || it.name == "AA" }
                        .associate { it.name to search.pathTo(it)!!.size }
                )
            }
            .associateBy { it.name }
            .let(::VolcanoSolver)
    }

    override fun partOne(input: String) = parseInput(input).solve()
}
