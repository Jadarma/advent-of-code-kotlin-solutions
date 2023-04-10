package aockt.y2022

import io.github.jadarma.aockt.core.Solution

object Y2022D11 : Solution {

    /** Regex that extracts information on monkeys. */
    private val inputRegex = Regex(
        """
        Monkey (?<id>\d+):
         {2}Starting items: (?<startingItems>[0-9, ]+)
         {2}Operation: new = old (?<operator>[*+]) (?<operand>\d+|old)
         {2}Test: divisible by (?<divisibilityTest>\d+)
         {4}If true: throw to monkey (?<monkeyIfTrue>\d+)
         {4}If false: throw to monkey (?<monkeyIfFalse>\d+)
    """.trimIndent()
    )

    /** Parse the [input] and return the list of [Monkey]s playing with items. */
    private fun parseInput(input: String): List<Monkey> =
        input
            .splitToSequence("\n\n")
            .map { inputRegex.matchEntire(it)!!.groups as MatchNamedGroupCollection }
            .map { groups ->
                Monkey(
                    id = groups["id"]!!.value.toInt(),
                    operation = run {
                        val operand = groups["operand"]!!.value.toLongOrNull()
                        when (groups["operator"]!!.value) {
                            "*" -> { x -> x * (operand ?: x) }
                            "+" -> { x -> x + (operand ?: x) }
                            else -> error("Invalid input.")
                        }
                    },
                    divisibility = groups["divisibilityTest"]!!.value.toLong(),
                    monkeyIfTrue = groups["monkeyIfTrue"]!!.value.toInt(),
                    monkeyIfFalse = groups["monkeyIfFalse"]!!.value.toInt(),
                    startingItems = groups["startingItems"]!!.value.split(", ").map(String::toLong)
                )
            }
            .toList()

    /**
     * A simulated monkey brain tasked with tossing items based on worry levels.
     * @property id An identifier.
     * @property operation How the worry level modifies upon inspection.
     * @property divisibility The number used to test divisibility and decide on where to throw the item next.
     */
    private class Monkey(
        val id: Int,
        private val operation: (Long) -> Long,
        val divisibility: Long,
        monkeyIfTrue: Int,
        monkeyIfFalse: Int,
        startingItems: List<Long>,
    ) {
        /** Worry levels associated with each item this monkey handles. */
        private val items: MutableList<Long> = startingItems.toMutableList()

        /** Total number of items inspected during this monkey's lifetime. */
        var inspections: Long = 0L
            private set

        /** Based on an item's worry level, returns the id of the monkey to throw it to. */
        private val testAndThrow: (Long) -> Int = { worryLevel ->
            if (worryLevel.rem(divisibility) == 0L) monkeyIfTrue else monkeyIfFalse
        }

        /** Catch an item from another monkey. */
        fun receive(itemWithWorryLevel: Long) {
            items.add(itemWithWorryLevel)
        }

        /**
         * Simulate the monkey's turn and throw out all carried items and return a list of pairs between the worry level
         * of the item to be thrown and the ID of the monkey to throw it to.
         *
         * @param isVeryWorried If true, worry levels won't be reduced after an inspection.
         * @param worryCycle The product of all [divisibility] numbers of the monkeys in the round, needed as an
         *   optimisation if simulating lots of rounds to prevent overflows.
         */
        fun throwItems(isVeryWorried: Boolean = false, worryCycle: Long? = null): List<Pair<Long, Int>> =
            items
                .asSequence()
                .onEach { inspections++ }
                .map(operation)
                .map { if (isVeryWorried) it else it / 3L }
                .map { if (worryCycle != null) it.rem(worryCycle) else it }
                .map { it to testAndThrow(it) }
                .toList()
                .also { items.clear() }
    }

    /**
     * Simulate the monkey tossing game and return the product of the number of items carried by the two most burdened
     * monkeys at the end of the simulation.
     *
     * @param input The problem input which configures the monkey brains.
     * @param rounds How many rounds to play.
     * @param isVeryWorried Whether worry drop-off after monkey inspection is disabled.
     */
    private fun simulateMonkeyBehavior(input: String, rounds: Int, isVeryWorried: Boolean): Long =
        parseInput(input)
            .apply {
                val worryCycle = map { it.divisibility }.reduce(Long::times)
                repeat(rounds) {
                    forEach { monkey ->
                        monkey
                            .throwItems(isVeryWorried, worryCycle)
                            .forEach { (itemWithWorryLevel, toMonkey) -> get(toMonkey).receive(itemWithWorryLevel) }
                    }
                }
            }
            .sortedByDescending { it.inspections }
            .take(2)
            .map { it.inspections }
            .reduce(Long::times)

    override fun partOne(input: String) = simulateMonkeyBehavior(input, 20, false)
    override fun partTwo(input: String) = simulateMonkeyBehavior(input, 10000, true)
}
