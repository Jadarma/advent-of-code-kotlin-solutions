package aockt.util

import java.util.PriorityQueue

object Pathfinding {

    /**
     * Performs a search.
     * If a [heuristic] is given, it is A*, otherwise, Dijkstra's algorithm.
     *
     * @param start        The node or state to begin the search from.
     * @param neighbours   A function that returns all possible transitions from a node and their associated cost.
     *                     The cost _must_ be a non-negative value.
     * @param goalFunction A predicate that determines whether a state is the search destination.
     *                     Search stops upon reaching the first node that evaluates to `true`.
     * @param heuristic    A function that estimates the lower bound cost of reaching a destination from a given node.
     *                     Must never overestimate, otherwise the search result might not be the most cost-effective.
     * @param onVisit      An optional callback invoked on each node visit, useful for debugging.
     * @param maximumCost  An optional upper bound, prevents any transitions that would exceed this value.
     * @param trackPath    If `true`, keeps track of intermediary nodes to be able to construct a search path.
     *                     If `false` _(the default)_, only the costs to reach the nodes are computed.
     *
     * @return The search result, or `null` if a suitable destination couldn't be reached.
     */
    fun <T : Any> search(
        start: T,
        neighbours: (T) -> Iterable<Pair<T, Int>>,
        goalFunction: (T) -> Boolean,
        heuristic: (T) -> Int = { 0 },
        onVisit: (T) -> Unit = {},
        maximumCost: Int = Int.MAX_VALUE,
        trackPath: Boolean = false,
    ): SearchResult<T>? {
        require(maximumCost > 0) { "Maximum cost must be positive." }

        val previous = mutableMapOf<T, T>()
        val distance = mutableMapOf(start to 0)
        val visited = mutableSetOf<Pair<T, Int>>()

        @Suppress("UNUSED_DESTRUCTURED_PARAMETER_ENTRY")
        val queue = PriorityQueue(compareBy<Triple<T, Int, Int>> { (node, costSoFar, priority) -> priority })
        queue.add(Triple(start, 0, 0))

        if (trackPath) previous[start] = start

        while (queue.isNotEmpty()) {
            val (node, costSoFar, _) = queue.poll()
            if (!visited.add(node to costSoFar)) continue
            onVisit(node)
            if (goalFunction(node)) return SearchResult(start, node, distance, previous)

            for ((nextNode, nextCost) in neighbours(node)) {
                check(nextCost >= 0) { "Transition cost between nodes cannot be negative." }
                if (maximumCost - nextCost < costSoFar) continue

                val totalCost = costSoFar + nextCost

                if (totalCost > (distance[nextNode] ?: Int.MAX_VALUE)) continue

                distance[nextNode] = totalCost
                if (trackPath) previous[nextNode] = node

                val heuristicValue = heuristic(node)
                check(heuristicValue >= 0) { "Heuristic value must be positive." }
                queue.add(Triple(nextNode, totalCost, totalCost + heuristicValue))
            }
        }

        return null
    }

    /**
     * The result of a [Pathfinding] search.
     *
     * @property start    The node the search started from.
     * @property end      The destination node, or the last visited node if an exhaustive flood search was requested.
     * @property cost     The cost from [start] to [end], or the maximum cost if an exhaustive flood search was requested.
     * @property path     The path from [start] to [end], each node associated with the running cost.
     * @property distance The cost from the [start] to all the visited intermediary nodes.
     * @property previous The previous node in the path of all the visited intermediary nodes.
     *                    Following it recursively will lead back to the [start] node.
     */
    class SearchResult<out T> internal constructor(
        val start: T,
        val end: T,
        private val distance: Map<T, Int>,
        private val previous: Map<T, T>,
    ) {
        val cost: Int get() = distance.getValue(end)

        val path: List<Pair<T, Int>> by lazy {
            check(previous.isNotEmpty()) { "Cannot generate path as search was performed with `trackPath = false`." }
            buildList {
                var current = end
                while (true) {
                    add(current to distance.getValue(current))
                    val previous = previous.getValue(current)
                    if (previous == current) break
                    current = previous
                }
            }.asReversed()
        }
    }
}
