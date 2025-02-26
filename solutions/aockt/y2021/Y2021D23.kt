package aockt.y2021

import aockt.util.parse
import aockt.util.Pathfinding
import io.github.jadarma.aockt.core.Solution
import kotlin.math.abs

object Y2021D23 : Solution {

    /**
     * The types of amphipods.
     * @property preferredPosition The coordinate of the destination room.
     * @property energyPerStep     How much cost does every step incur.
     */
    private enum class AmphipodType(val preferredPosition: Int, val energyPerStep: Int) {
        Amber(2, 1),
        Bronze(4, 10),
        Copper(6, 100),
        Desert(8, 1000),
    }

    /**
     * The state of a single amphipod.
     * @property type     The type of amphipod.
     * @property position The horizontal position in the area.
     * @property depth    The vertical position in the room, or zero if in the hallway.
     */
    private data class Amphipod(val type: AmphipodType, val position: Int, val depth: Int)

    /**
     * A search state.
     * @property pods      The positions of all amphipods.
     * @property roomDepth How many amphipods can fit in a room.
     */
    private data class State(val pods: Set<Amphipod>, val roomDepth: Int = 2) :
        Set<Amphipod> by pods {

        fun neighbors(): Set<Pair<State, Int>> = buildSet {

            // Get pods that are still not in their final position:
            // - Any pod in the hallway.
            // - Any pod in the wrong room.
            // - Any pod in a room that obstructs another pod that is in the wrong room.
            val needToMove = pods.filter {
                it.depth == 0
                    || it.position != it.type.preferredPosition
                    || pods.any { other ->
                    other.position == it.position && other.depth > it.depth && other.type.preferredPosition != it.position
                }
            }

            val (inHallway, inRooms) = needToMove.partition { it.depth == 0 }

            // A pod that needs to move out of the hallway can only do so if its preferred room is empty or only
            // contains similar pods, and there is no other pod occupying the hallway path to said room.
            val canMoveInRooms = inHallway
                .filter {
                    pods
                        .filter { other -> other.position == it.type.preferredPosition }
                        .none { other -> other.type != it.type }
                }
                .filter {
                    val hallwaySegment =
                        if (it.position < it.type.preferredPosition) it.position + 1..it.type.preferredPosition
                        else it.type.preferredPosition..<it.position
                    inHallway.none { other -> other.position in hallwaySegment }
                }

            canMoveInRooms.forEach {
                val destinationDepth = pods
                    .filter { other -> other.position == it.type.preferredPosition }
                    .minOfOrNull { other -> other.depth }
                    ?.dec()
                    ?: roomDepth

                val newPod = it.copy(position = it.type.preferredPosition, depth = destinationDepth)
                val moveCost = (abs(it.type.preferredPosition - it.position) + destinationDepth) * it.type.energyPerStep

                val newState = copy(pods = pods.minus(it).plus(newPod))
                add(newState to moveCost)
            }

            // A pod that needs to move out of the room can do so if it is the first one and there is hallway space.
            val takenHallwaySlots = inHallway.map { it.position }.toSet()
            val canMoveOutOfRooms = inRooms
                .filter {
                    it.depth == 1 || pods.none { other -> other.position == it.position && other.depth < it.depth }
                }
                .map {
                    val leftHalf = (it.position - 1 downTo 0).takeWhile { slot -> slot !in takenHallwaySlots }
                    val rightHalf = (it.position + 1..10).takeWhile { slot -> slot !in takenHallwaySlots }
                    it to (leftHalf + rightHalf).filter { slot -> slot in hallwaySlots }
                }

            canMoveOutOfRooms.forEach { (pod, hallWaySlot) ->
                for (slot in hallWaySlot) {
                    val newPod = pod.copy(position = slot, depth = 0)
                    val moveCost = (abs(slot - pod.position) + pod.depth) * pod.type.energyPerStep
                    val newState = copy(pods = pods.minus(pod).plus(newPod))
                    add(newState to moveCost)
                }
            }
        }

        /** The state is final if none of the pods stand in the hallway and all of them are in their preferred room. */
        fun isFinal(): Boolean = pods.all { it.depth > 0 && it.position == it.type.preferredPosition }

        private companion object {
            /** Resting places in the hallway. */
            val hallwaySlots = setOf(0, 1, 3, 5, 7, 9, 10)
        }
    }

    /** Parse the [input] and return the starting [State], optionally making the rooms [deep]. */
    private fun parseInput(input: String, deep: Boolean): State = parse {
        val regex = Regex("""^#{13}\n#\.{11}#\n#{3}(?:[A-D]#){4}##\n {2}#(?:[A-D]#){4}\n {2}#{9}$""")
        require(input.matches(regex)) { "Invalid input." }

        val pods = input
            .filter { it in "ABCD" }
            .run { if (deep) "${substring(0, 4)}DCBADBAC${substring(4, 8)}" else this }
            .chunked(4)
            .withIndex()
            .flatMap { (depth, chunk) ->
                chunk.mapIndexed { position, c ->
                    Amphipod(
                        position = position * 2 + 2,
                        depth = depth + 1,
                        type = when (c) {
                            'A' -> AmphipodType.Amber
                            'B' -> AmphipodType.Bronze
                            'C' -> AmphipodType.Copper
                            'D' -> AmphipodType.Desert
                            else -> error("Unreachable.")
                        },
                    )
                }
            }
            .toSet()

        State(pods = pods, roomDepth = if (deep) 4 else 2)
    }

    /** Common solution for both parts. */
    private fun solve(input: String, deep: Boolean): Int =
        Pathfinding
            .search(
                start = parseInput(input, deep),
                neighbours = { it.neighbors() },
                goalFunction = { it.isFinal() },
            )
            ?.cost
            ?: -1

    override fun partOne(input: String): Int = solve(input, deep = false)
    override fun partTwo(input: String): Int = solve(input, deep = true)
}
