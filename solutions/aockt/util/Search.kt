package aockt.util

import java.util.PriorityQueue

/** A type implementing this interface can represent a network of nodes usable for search algorithms. */
interface Graph<T : Any> {

    /** Returns all the possible nodes to visit starting from this [node] associated with the cost of travel. */
    fun neighboursOf(node: T): Iterable<Pair<T, Int>>
}

/**
 * The result of a [Graph] search.
 *
 * @property startedFrom The origin node from where the search began.
 * @property destination The closest node that fulfilled the search criteria, or `null` if not found.
 *   If multiple such nodes exist, the one with the lowest cost is chosen.
 * @property searchTree Extra information about the search, associating visited nodes to their previous node in the
 *   path back to the origin, as well as their total cost from the origin.
 */
data class SearchResult<T : Any>(
    val startedFrom: T,
    val destination: T?,
    val searchTree: Map<T, Pair<T, Int>>,
)

/**
 * A slice of a [SearchResult], showing the complete path towards a particular node.
 *
 * @property path The list of all nodes in this path, including the origin and destination nodes.
 * @property cost The total cost of this path.
 */
data class SearchPath<T : Any>(
    val path: List<T>,
    val cost: Int,
) : List<T> by path

/**
 * Narrow down a [SearchResult] to a [node] of interest.
 * Returns the shortest known path towards that [node], or `null` if the node is unreachable from the origin, or if the
 * node has not been visited by the search algorithm before reaching a destination.
 */
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

/**
 * Narrow down a [SearchResult] to the node of interest.
 * Returns the shortest known path towards the node that fulfilled the destination criteria.
 * If multiple such nodes exist, the one with the lowest cost is chosen.
 */
fun <T : Any> SearchResult<T>.path(): SearchPath<T>? = when(destination) {
    null -> null
    else -> pathTo(destination)
}

/**
 * Performs a search on the graph.
 * If a [heuristic] is given, it is A*, otherwise Dijkstra.
 *
 * @param start The origin node from where to start searching.
 * @param maximumCost If specified, will stop searching if no destination was found with a cost smaller than this value.
 * @param onVisited Side effect callback triggered every time a new node is searched.
 * @param heuristic A function that estimates the cost of reaching a destination from the given node.
 *   Must never overestimate, otherwise the search result might not be the most cost-effective.
 * @param goalFunction A predicate to determine which nodes are a destination.
 *   The search will stop upon the first such node to be found.
 *   If no function is defined, the entire graph will be searched.
 */
fun <T : Any> Graph<T>.search(
    start: T,
    maximumCost: Int = Int.MAX_VALUE,
    onVisited: (T) -> Unit = {},
    heuristic: (T) -> Int = { 0 },
    goalFunction: (T) -> Boolean = { false },
): SearchResult<T> {
    val queue = PriorityQueue(compareBy<Pair<T, Int>> { it.second })
    queue.add(start to 0)
    val searchTree = mutableMapOf(start to (start to 0))

    while (true) {
        val (node, costSoFar) = queue.poll() ?: return SearchResult(start, null, searchTree)
        onVisited(node)

        if (goalFunction(node)) return SearchResult(start, node, searchTree)

        neighboursOf(node)
            .filter { it.first !in searchTree }
            .forEach { (next, cost) ->
                val nextCost = costSoFar + cost
                if (nextCost <= maximumCost && nextCost <= (searchTree[next]?.second ?: Int.MAX_VALUE)) {
                    queue.add(next to heuristic(next).plus(nextCost))
                    searchTree[next] = node to nextCost
                }
            }
    }
}
