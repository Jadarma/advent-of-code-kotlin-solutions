package aockt.y2022

import io.github.jadarma.aockt.core.Solution
import java.util.*

object Y2022D16 : Solution {

    /**
     * A valve in the optimised volcano.
     *
     * @property index Numeric identifier of this valve, calculated as it's order in the lexicographic order of valves.
     * @property name The valve's original name, unused but useful for debugging.
     * @property flowRate How much pressure per minute this valve can release if open.
     * @property tunnels The cost to travel to and open a valve having the index the same as the position in the list.
     */
    private data class Valve(
        val index: Int,
        val name: String,
        val flowRate: Int,
        val tunnels: List<Int>,
    )

    /**
     * A minimalist bitset implementation for defining a set of up to sixteen [Valve]s.
     *
     * @property bits The value the bits are stored in.
     *   In the binary representation, the N-th bit starting from the least significant is set if the valve with that
     *   index belongs in the set this mask describes.
     */
    @JvmInline
    private value class ValveMask(val bits: UShort) {

        /** Whether the mask contains the valve with this [index]. */
        operator fun get(index: Int): Boolean = (1u shl index).toUShort() and bits != 0u.toUShort()

        /** Syntactic sugar for checking if the mask contains this [valve]. */
        operator fun contains(valve: Valve): Boolean = this[valve.index]

        /** Returns a mask value identical to this one, but including or excluding the valve of an [index]. */
        fun withValve(index: Int, isIncluded: Boolean = true): ValveMask {
            if (this[index] == isIncluded) return this
            val bitAtPosition = (1u shl index).toUShort()
            val correctedBits = when (isIncluded) {
                true -> bits or bitAtPosition
                false -> bits and bitAtPosition.inv()
            }
            return ValveMask(correctedBits)
        }
    }

    /**
     * A search state for exploring the volcano.
     *
     * @property timeLeft How many minutes are left to search.
     * @property lastValve What was the last opened valve.
     * @property releasedPressure The total pressure released up until this point.
     * @property openValves Which valves have already been opened.
     */
    private data class State(
        val timeLeft: Int,
        val lastValve: Int,
        val releasedPressure: Int,
        val openValves: ValveMask,
    )

    /**
     * Solver for a single player attempting to open volcano valves.
     * @param valves The list of valves this player has access to.
     */
    private class VolcanoSolo(valves: List<Valve>) : Graph<State> {

        /** The total flow rate per minute if all of the [valves] would be open. */
        private val maximumFlowRate = valves.sumOf { it.flowRate }

        /** Use an array for valves so that it can be indexed easily. */
        private val valves: Array<Valve?> = Array(16) { null }

        init {
            require(valves.size <= 16) { "Your volcano is too big and would overheat the device." }
            valves.forEach { this.valves[it.index] = it }
        }

        /**
         * Computes all the possible decisions to take to produce new states starting from the current one.
         * - If no time left, stop here.
         * - If no valves can be opened (either because none left or not enough time to reach them), skip to the end.
         * - For all valid valve choices available, calculate the next state by traveling there.
         */
        private fun neighbors(node: State): List<State> {
            if (node.timeLeft == 0) return emptyList()

            return valves
                .filterNotNull()
                .filter { it !in node.openValves }
                .map { next -> next.index to valves[node.lastValve]!!.tunnels[next.index] }
                .filter { it.second <= node.timeLeft }
                .ifEmpty { listOf(0 to node.timeLeft) }
                .map { (next, time) ->
                    val flowRate = valves.filterNotNull().filter { it in node.openValves }.sumOf { it.flowRate }
                    State(
                        timeLeft = node.timeLeft - time,
                        lastValve = next,
                        releasedPressure = node.releasedPressure + flowRate * time,
                        openValves = node.openValves.withValve(next),
                    )
                }
        }

        /**
         * Because we want to maximise the pressure released, the transition cost between two states is defined as the
         * total amount of pressure not released because of closed valves.
         */
        private fun cost(from: State, to: State): Int =
            valves
                .filterNotNull()
                .filter { it in from.openValves }
                .sumOf(Valve::flowRate)
                .let { currentFlowRate -> maximumFlowRate - currentFlowRate }
                .times(from.timeLeft - to.timeLeft)

        override fun neighboursOf(node: State) = neighbors(node).map { next -> next to cost(node, next) }

        /**
         * Using Djikstra's algorithm, explore the volcano and find the maximum amount of pressure that can be released.
         * Unfortunately, due to "optimisations", the actual path to take is not returned, only the pressure value,
         * but it can be computed if you wanted to: `solution.path()!!.path.map { it.lastValve }`, and then convert the
         * map the valve indexes back to their names again.
         *
         * TODO: A possible optimisation is to find a heuristic function to speed up the search.
         *       However, due to how the cost is calculated, initial attempts of a heuristic made it worse, because
         *       estimation takes more than just "winging it".
         *
         * @param time How many minutes are available to explore.
         */
        fun solve(time: Int): Int {
            val start = State(time, 0, 0, ValveMask(1u))
            val solution = search(start) { it.timeLeft == 0 }.destination
            checkNotNull(solution) { "A solution must exist because time moves forwards." }
            return solution.releasedPressure
        }
    }

    /**
     * Solver for two players attempting to open volcano valves.
     * @param valves The list of all valves the players can split between themselves.
     */
    private class VolcanoTeam(private val valves: List<Valve>) {

        init {
            require(valves.size <= 16) { "Your volcano is too big and would overheat the device." }
        }

        /** Divide the volcano into a smaller one using the [mask]. */
        private fun partition(mask: ValveMask): VolcanoSolo =
            valves
                .filter { it in mask }
                .map { valve -> valve.copy(tunnels = valve.tunnels.mapIndexed { index, cost -> if (mask[index]) cost else -1 }) }
                .let(::VolcanoSolo)

        /**
         * Explore the volcano using two players and find the maximum amount of pressure that can be released.
         * Generates all possible partitions of the volcano, assigning a subset of valves to the first player and the
         * rest to the second, ensuring no overlaps.
         * Simulate all possibilities as a single player, then find the total best by adding the result of each one with
         * the result of its complement, and returning the maximum sum.
         * Again, because of optimisation, the actual path taken is not returned, though it could be determined in
         * theory.
         *
         * @param time How many minutes are available to explore.
         */
        fun solve(time: Int): Int {
            val totalPartitions = (1u shl valves.size).dec().toUShort()
            val solutionCache: MutableMap<ValveMask, Int> = mutableMapOf()

            (0u..totalPartitions.toUInt())
                .asSequence()
                .map { ValveMask((it shl 1).or(1u).toUShort()) }
                .forEach { solutionCache[it] = partition(it).solve(time) }

            return solutionCache.maxOf { (myValves, myPressure) ->
                val elephantValves = ValveMask(myValves.bits.inv() and totalPartitions or 1u)
                val elephantPressure = solutionCache.getValue(elephantValves)
                myPressure + elephantPressure
            }
        }
    }

    /**
     * Parses the [input] and returns the valves.
     * Instead of returning the actual graph of all the valves, returns an equivalent fully-connected graph that only
     * contains the valves that have a positive flow rate, with "virtual tunnels" to all other valves in the volcano,
     * with their cost adjusted to the minimum possible value for pathfinding in the real volcano, as well as the extra
     * minute required to open them.
     *
     * TODO: For computing the optimised valve graph, Floyd-Warshall would be nicer instead of searching from every
     *       node, but for practical purposes it doesn't matter since the graphs are small enough not to have an impact
     *       in performance.
     */
    private fun parseInput(input: String): List<Valve> {
        val inputRegex = Regex("""^Valve ([A-Z]{2}) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z, ]+)$""")

        data class RawValve(val name: String, val flowRate: Int, val tunnels: Map<String, Int>)

        val valves = input
            .lineSequence()
            .mapNotNull(inputRegex::matchEntire)
            .map(MatchResult::destructured)
            .associate { (name, flow, tunnel) ->
                name to RawValve(
                    name,
                    flow.toInt(),
                    tunnel.split(", ").toSet().associateWith { 1 }
                )
            }

        val unoptimisedVolcano = object : Graph<RawValve> {
            override fun neighboursOf(node: RawValve): List<Pair<RawValve, Int>> = valves
                .getValue(node.name)
                .tunnels.keys
                .map(valves::getValue)
                .map { it to 1 }
        }

        val importantValves = valves
            .filterValues { it.name == "AA" || it.flowRate > 0 }
            .map { it.value }
            .sortedBy { it.name }

        return importantValves
            .map { valve -> valve to unoptimisedVolcano.search(valve, goalFunction = { false }) }
            .map { (valve, search) ->
                Valve(
                    index = importantValves.indexOf(valve),
                    name = valve.name,
                    flowRate = valve.flowRate,
                    tunnels = run {
                        val tunnelArray = IntArray(16) { -1 }
                        importantValves
                            .asSequence()
                            .withIndex()
                            .filterNot { it.value == valve || it.value.name == "AA" }
                            .forEach { tunnelArray[it.index] = search.pathTo(it.value)!!.size }
                        tunnelArray.toList()
                    }
                )
            }
    }

    override fun partOne(input: String) = parseInput(input).let(::VolcanoSolo).solve(30)
    override fun partTwo(input: String) = parseInput(input).let(::VolcanoTeam).solve(26)

    // TODO: This whole problem needs massive refactoring, the old search API no longer works here, because this uses
    //       a flood-fill, anyway, I'll just copy paste that here so it's isolated from the rest of the project.
    //       Everything below should be deleted.

    fun interface Graph<T : Any> {

        /** Returns all the possible nodes to visit starting from this [node] associated with the cost of travel. */
        fun neighboursOf(node: T): Iterable<Pair<T, Int>>
    }

    data class SearchResult<T : Any>(
        val startedFrom: T,
        val destination: T?,
        val searchTree: Map<T, Pair<T, Int>>,
    )

    data class SearchPath<T : Any>(
        val path: List<T>,
        val cost: Int,
    ) : List<T> by path

    fun <T : Any> SearchResult<T>.pathTo(node: T): SearchPath<T>? {
        val cost = searchTree[node]?.second ?: return null
        val path = buildList {
            var current = node
            while(true) {
                add(current)
                val previous = searchTree.getValue(current).first
                if(previous == current) break
                current = previous
            }
        }.asReversed()
        return SearchPath(path, cost)
    }

    fun <T : Any> SearchResult<T>.path(): SearchPath<T>? = when(destination) {
        null -> null
        else -> pathTo(destination)
    }

    fun <T : Any> Graph<T>.search(
        start: T,
        maximumCost: Int = Int.MAX_VALUE,
        onVisited: (T) -> Unit = {},
        heuristic: (T) -> Int = { 0 },
        goalFunction: (T) -> Boolean = { false },
    ): SearchResult<T> {
        val searchTree = mutableMapOf(start to (start to 0))

        @Suppress("UNUSED_DESTRUCTURED_PARAMETER_ENTRY")
        val queue = PriorityQueue(compareBy<Triple<T, Int, Int>> { (node, costSoFar, priority) -> priority })
        queue.add(Triple(start,0, 0))

        while (true) {
            val (node, costSoFar, _) = queue.poll() ?: return SearchResult(start, null, searchTree)
            onVisited(node)

            if (goalFunction(node)) return SearchResult(start, node, searchTree)

            neighboursOf(node)
                .filter { it.first !in searchTree }
                .forEach { (next, cost) ->
                    val nextCost = costSoFar + cost
                    if (nextCost <= maximumCost && nextCost <= (searchTree[next]?.second ?: Int.MAX_VALUE)) {
                        queue.add(Triple(next, nextCost, nextCost + heuristic(next)))
                        searchTree[next] = node to nextCost
                    }
                }
        }
    }
}
