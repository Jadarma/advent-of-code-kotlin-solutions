package aockt.y2023

import aockt.util.math.lcm
import aockt.util.parse
import aockt.y2023.Y2023D20.Pulse.*
import io.github.jadarma.aockt.core.Solution

object Y2023D20 : Solution {

    /** The type of pulses emitted by modules. */
    private enum class Pulse { Low, High }

    /**
     * A signal sent by a [Module].
     * @property source      The ID of the module which emitted this signal.
     * @property pulse       The type of pulse emitted by this signal.
     * @property destination The ID of the module which will receive the [pulse].
     */
    private data class Signal(val source: String, val pulse: Pulse, val destination: String)

    /**
     * A module for the control panel of a sand producing machine.
     * @property name    The ID of the module.
     * @property inputs  The IDs of the modules which this module can receive pulses from.
     * @property outputs The IDs of the modules which this module can send pulses to.
     */
    private sealed interface Module {
        val name: String
        val inputs: List<String>
        val outputs: List<String>

        /** Process a [signal], and return the pulse that should be output, or `null` if no output is produced. */
        fun process(signal: Signal): Pulse?
    }

    /** Process a [signal] as an input to this module and return the list of all output signals of this module. */
    private fun Module.receiveAndEmit(signal: Signal): List<Signal> {
        check(signal.destination == name) { "Received signal for a different module!" }
        val output = process(signal) ?: return emptyList()
        return outputs.map { Signal(name, output, it) }
    }

    /** A module which will relay the input pulse to all its outputs. */
    private data class BroadcastModule(
        override val name: String,
        override val inputs: List<String>,
        override val outputs: List<String>,
    ) : Module {
        override fun process(signal: Signal): Pulse = signal.pulse
    }

    /**
     * A module which remembers its own state:
     * - Ignores all high pulses.
     * - It's state toggles on every low pulse, and also outputs it.
     */
    private data class FlipFlopModule(
        override val name: String,
        override val inputs: List<String>,
        override val outputs: List<String>,
    ) : Module {

        private var state: Pulse = Low

        override fun process(signal: Signal): Pulse? {
            if (signal.pulse == High) return null
            state = if (state == High) Low else High
            return state
        }
    }

    /**
     * A module which remembers the last pulse of all its inputs:
     * - If the last pulse of all inputs was high, outputs a low pulse.
     * - Otherwise outputs a high pulse.
     */
    private data class ConjunctionModule(
        override val name: String,
        override val inputs: List<String>,
        override val outputs: List<String>,
    ) : Module {
        val state: MutableMap<String, Pulse> = inputs.associateWith { Low }.toMutableMap()

        override fun process(signal: Signal): Pulse {
            state[signal.source] = signal.pulse
            return if (state.values.all { it == High }) Low else High
        }
    }

    /** A module with no outputs. */
    private data class DebugModule(override val name: String, override val inputs: List<String>) : Module {
        override val outputs: List<String> = emptyList()
        override fun process(signal: Signal) = null
    }

    /** Simulates a sand making machine made up of [modules]. */
    private class SignalProcessor(val modules: Map<String, Module>) {

        /** The total amount of times the button has been pressed. */
        private var totalPresses: Int = 0

        /** Simulate a button press, and return the [Signal]s emitted by the machines, in order of their processing. */
        private fun pressButton(): Sequence<Signal> = sequence {
            totalPresses++
            val queue = ArrayDeque<Signal>()
            queue.add(Signal("button", Low, "broadcaster"))
            while (queue.isNotEmpty()) {
                val signal = queue.removeFirst()
                yield(signal)
                modules
                    .getValue(signal.destination)
                    .receiveAndEmit(signal)
                    .let(queue::addAll)
            }
        }

        /** Presses the button [iterations] times, counting emitted pulses of each type, returning their product. */
        fun signalScoreAfter(iterations: Int): Long {
            check(totalPresses == 0) { "If not evaluating from a fresh state, the prediction won't be accurate." }
            var low = 0
            var high = 0
            repeat(iterations) { pressButton().forEach { if (it.pulse == Low) low++ else high++ } }
            return low.toLong() * high
        }

        /**
         * Estimates how many button presses it would take for the `rx` module to receive a single low signal.
         * Has the following assumptions:
         *  - The `rx` is a debug module has a single input, which is a conjunction module.
         *  - Each input to `rx` input produces a high signal cyclically.
         *  - Every such cycle does not have an offset _(i.e.: all cycles start from the first button press)_.
         */
        fun estimatedPressesForRx(): Long {
            check(totalPresses == 0) { "If not evaluating from a fresh state, the prediction won't be accurate." }

            val rx = modules["rx"]
            checkNotNull(rx) { "No module with ID 'rx' found. " }
            check(rx is DebugModule) { "Assumption failed: 'rx' should be a debug module." }
            check(rx.inputs.size == 1) { "Assumption failed: 'rx' should have a single input." }

            val rxIn = rx.inputs.first().let(modules::getValue)
            check(rxIn is ConjunctionModule) { "Assumption failed: 'rx' input is not a conjunction module." }

            val cycles: MutableMap<String, Int> = rxIn.inputs.associateWith { -1 }.toMutableMap()
            while (cycles.values.any { it == -1 }) {
                pressButton()
                    .filter { signal -> signal.destination == rxIn.name && signal.pulse == High }
                    .filter { signal -> cycles[signal.source] == -1 }
                    .forEach { signal -> cycles[signal.source] = totalPresses }
            }
            return cycles.values.lcm()
        }
    }

    /** Parse the [input] and return a [SignalProcessor] with all modules wired. */
    private fun parseInput(input: String): SignalProcessor = parse {
        val moduleRegex = Regex("""^([%&]?)([a-z]+) -> ([a-z]+(?:, [a-z]+)*)$""")
        val names: MutableSet<String> = mutableSetOf()
        val types: MutableMap<String, String> = mutableMapOf()
        val outputs: MutableMap<String, List<String>> = mutableMapOf()
        val inputs: MutableMap<String, MutableList<String>> = mutableMapOf()

        input
            .lineSequence()
            .map { line -> moduleRegex.matchEntire(line)!!.destructured }
            .forEach { (type, name, outputList) ->
                val outputModules = outputList.split(", ").filter(String::isNotEmpty)
                outputModules.forEach { inputs.getOrPut(it) { mutableListOf() }.add(name) }
                names.add(name)
                names.addAll(outputModules)
                outputs[name] = outputModules
                types[name] = type
            }

        names
            .map { module ->
                val inModules = inputs[module] ?: emptyList()
                val outModules = outputs[module] ?: emptyList()
                when (types[module]) {
                    "" -> BroadcastModule(module, inModules, outModules)
                    "%" -> FlipFlopModule(module, inModules, outModules)
                    "&" -> ConjunctionModule(module, inModules, outModules)
                    null -> DebugModule(module, inModules)
                    else -> error("Impossible state.")
                }
            }
            .associateBy { it.name }
            .let(::SignalProcessor)
    }

    override fun partOne(input: String) = parseInput(input).signalScoreAfter(iterations = 1000)
    override fun partTwo(input: String) = parseInput(input).estimatedPressesForRx()
}
