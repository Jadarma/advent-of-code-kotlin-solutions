package aockt.y2025

import aockt.util.Pathfinding.search
import aockt.util.parse
import com.microsoft.z3.* // 🔔 Shame!
import io.github.jadarma.aockt.core.Solution

object Y2025D10 : Solution {

    /**
     * The configuration of a factory machine.
     * @property buttons        The list of buttons and their numeric labels.
     * @property targetLights   The machine is functional if its lights show this configuration.
     * @property targetJoltages The machine is functional if its counters show this configuration.
     */
    private data class MachineConfiguration(
        val buttons: List<Set<Int>>,
        val targetLights: List<Boolean>,
        val targetJoltages: List<Int>,
    )

    /**
     * Given a factory machine [config], find the minimum number of buttons to press in order to get the initially off
     * lights to the desired target state.
     */
    private fun solveLights(config: MachineConfiguration): Int = search(
        start = List(config.targetLights.size) { false },
        goalFunction = { state -> state == config.targetLights },
        neighbours = { state ->
            config.buttons
                .map { flips -> state.mapIndexed { index, on -> if (index in flips) !on else on } }
                .map { it to 1 }
        },
    )?.cost ?: error("No solution found for machine: $config")

    /**
     * Given a factory machine [config], find the minimum number of buttons to press in order to get the initially
     * zeroed counters to the desired joltages.
     */
    private fun solveJoltages(config: MachineConfiguration): Int = Context().use { ctx ->
        val solver = ctx.mkOptimize()
        val zero = ctx.mkInt(0)

        // Counts number of presses for each button, and ensures it is positive.
        val buttons = config.buttons.indices
            .map { ctx.mkIntConst("button#$it") }
            .onEach { button -> solver.Add(ctx.mkGe(button, zero)) }
            .toTypedArray()

        // For each joltage counter, require that the sum of presses of all buttons that increment it is equal to the
        // target value specified in the config.
        config.targetJoltages.forEachIndexed { counter, targetValue ->
            val buttonsThatIncrement = config.buttons
                .withIndex()
                .filter { (_, counters) -> counter in counters }
                .map { buttons[it.index] }
                .toTypedArray()
            val target = ctx.mkInt(targetValue)
            val sumOfPresses = ctx.mkAdd(*buttonsThatIncrement) as IntExpr
            solver.Add(ctx.mkEq(sumOfPresses, target))
        }

        // Describe that the presses (solution answer) is the sum of all individual button presses, and should be as
        // low as possible.
        val presses = ctx.mkIntConst("presses")
        solver.Add(ctx.mkEq(presses, ctx.mkAdd(*buttons)))
        solver.MkMinimize(presses)

        // Find solution and return.
        if (solver.Check() != Status.SATISFIABLE) error("No solution found for machine: $config.")
        solver.getModel().evaluate(presses, false).let { it as IntNum }.int
    }

    /** Parse the [input] and return the [MachineConfiguration]s of the factory. */
    private fun parseInput(input: String): List<MachineConfiguration> = parse {
        val machineConfigRegex = Regex("""^\[([.#]+)] ([(,)\s\d]*) \{([\d,]+)}$""")

        fun parseMachine(line: String): MachineConfiguration {
            val (lights, buttons, joltages) = machineConfigRegex.matchEntire(line)!!.destructured
            return MachineConfiguration(
                buttons = buttons
                    .split(' ')
                    .map { it.removeSurrounding("(", ")") }
                    .map { it.split(',').map(String::toInt).toSet() },
                targetLights = lights.map { it == '#' },
                targetJoltages = joltages.split(',').map(String::toInt),
            )
        }

        input.lineSequence().map(::parseMachine).toList()
    }

    override fun partOne(input: String) = parseInput(input).sumOf(::solveLights)
    override fun partTwo(input: String) = parseInput(input).sumOf(::solveJoltages)
}
