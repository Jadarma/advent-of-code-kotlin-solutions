package aockt.y2021

import io.github.jadarma.aockt.core.Solution

object Y2021D12 : Solution {

    /** A pathfinding service able to navigate between the `start` and `end` caves via the given connections. */
    private class Navigator(connections: Iterable<Pair<String, String>>) {

        /** Simple data holder representing a cave node, which knows if it is small or large. */
        private data class CaveNode(val name: String) {
            val isSmall = name.all { it.isLowerCase() }
        }

        private val cave: Map<CaveNode, Set<CaveNode>> = run {
            val connectionsMap = buildMap<String, MutableSet<String>> {
                connections.forEach { (a, b) ->
                    getOrPut(a) { mutableSetOf() }.add(b)
                    getOrPut(b) { mutableSetOf() }.add(a)
                }
            }
            buildMap {
                val nodes = connectionsMap.keys.associateWith { name -> CaveNode(name) }
                require(nodes.containsKey("start")) { "Missing start node." }
                require(nodes.containsKey("end")) { "Missing end node." }
                connectionsMap.forEach { (name, linkedCaves) ->
                    put(nodes.getValue(name), linkedCaves.map { nodes.getValue(it) }.toSet())
                }
            }
        }
        val start = cave.keys.first { it.name == "start" }
        val end = cave.keys.first { it.name == "end" }

        private suspend fun SequenceScope<List<String>>.continueVia(
            node: CaveNode,
            pathSoFar: List<CaveNode>,
            canMakeException: Boolean,
        ) {
            val nextPath = pathSoFar + node
            if (node == end) {
                yield(nextPath.map { it.name })
                return
            }
            cave.getValue(node).forEach { nextNode ->
                val isEndNode = nextNode == start || nextNode == end
                val isInPathAlready = nextNode in pathSoFar
                val isAnException = (nextNode.isSmall && isInPathAlready)
                when {
                    nextNode.isSmall && isInPathAlready && !canMakeException -> Unit
                    isAnException && isEndNode -> Unit
                    else -> continueVia(nextNode, nextPath, canMakeException && !isAnException)
                }
            }
        }

        /**
         * Generates all possible paths between the start and end nodes that only pass through small games at most once.
         * If [makeExceptionForOneSmallCave] flag is set, only one small cave is allowed to be visited twice.
         */
        fun allPaths(makeExceptionForOneSmallCave: Boolean = false): Sequence<List<String>> = sequence {
            continueVia(start, emptyList(), makeExceptionForOneSmallCave)
        }
    }

    /** Parse the [input], compute the cave system, and return a [Navigator] instance calibrated to the map. */
    private fun parseInput(input: String): Navigator =
        input
            .lineSequence()
            .map { it.split('-').let { (a, b) -> a to b } }
            .asIterable()
            .let(::Navigator)

    override fun partOne(input: String) = parseInput(input).allPaths().count()
    override fun partTwo(input: String) = parseInput(input).allPaths(makeExceptionForOneSmallCave = true).count()
}
