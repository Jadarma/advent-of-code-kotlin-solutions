package aockt.y2023

import aockt.util.parse
import io.github.jadarma.aockt.core.Solution

object Y2023D19 : Solution {

    /** The Xmas rating system categories. */
    private enum class XmasCategory { X, M, A, S }

    /** A mechanical part, rated by its XMAS category scores. */
    @JvmInline
    private value class Part(val xmas: Map<XmasCategory, Int>) : Map<XmasCategory, Int> by xmas {

        /** The sum of all XMAS scores. */
        val totalScore: Long
            get() = values.sumOf { it.toLong() }
    }

    /** A spec for a mechanical part, with acceptable ranges for the XMAS category scores. */
    @JvmInline
    private value class PartSpec(val xmas: Map<XmasCategory, IntRange>) : Map<XmasCategory, IntRange> by xmas {

        /** Counts how many distinct XMAS score combinations exist that satisfy this spec. */
        val totalPossibleParts: Long
            get() = values.map { if (it.isEmpty()) 0 else it.last - it.first + 1L }.reduce(Long::times)

        /** Returns a new specification with the [category] swapped to the [newValue]. */
        fun with(category: XmasCategory, newValue: IntRange): PartSpec =
            toMutableMap()
                .apply { put(category, newValue) }
                .let(::PartSpec)
    }

    /**
     * A part sorting workflow rule.
     * @property success   The workflow the part should be sent to if it passes the [threshold].
     * @property threshold The value that will be compared against the score of the [selector].
     * @property greater   If true, the [selector] must be greater than the [threshold], otherwise smaller.
     * @property selector  Extract the relevant category score from a part.
     */
    private data class Rule(val success: String, val threshold: Int, val greater: Boolean, val selector: XmasCategory) {

        /** Checks whether the [part] matches this rule. */
        fun evaluate(part: Part): Boolean {
            val partScore = part.getValue(selector)
            return if (greater) partScore > threshold else partScore < threshold
        }

        /**
         * Given a [partSpec], returns two new ones which would always pass and always fail this rule, respectively.
         * If it is impossible to for a subpart of this spec to fail or pass, that value shall be `null` instead.
         */
        fun split(partSpec: PartSpec): Pair<PartSpec?, PartSpec?> {
            val selectedRange = partSpec.getValue(selector)
            val wouldPass = if (greater) threshold.inc()..selectedRange.last else selectedRange.first..<threshold
            val wouldFail = if (greater) selectedRange.first..threshold else threshold..selectedRange.last

            val passingSpec = wouldPass.takeUnless(IntRange::isEmpty)?.let { partSpec.with(selector, it) }
            val failingSpec = wouldFail.takeUnless(IntRange::isEmpty)?.let { partSpec.with(selector, it) }

            return passingSpec to failingSpec
        }
    }

    /**
     * A part sorting workflow.
     * @property id       The ID of this workflow.
     * @property rules    The rules this workflow applies.
     * @property fallback The workflow the parts that did not trigger any of the [rules] will be sent to.
     */
    private data class Workflow(val id: String, val rules: List<Rule>, val fallback: String) {

        /** Runs the [part] through the workflow and returns the next workflow the part is sent to. */
        fun evaluate(part: Part): String = rules.firstOrNull { it.evaluate(part) }?.success ?: fallback
    }

    /** Parse the [input] and return the list of [Workflow]s and the list of [Part]s to sort. */
    private fun parseInput(input: String): Pair<List<Workflow>, List<Part>> = parse {
        val workflowRegex = Regex("""^([a-z]+)\{(.+),([a-z]+|A|R)}$""")
        val ruleRegex = Regex("""^([xmas])[<>](\d+):([a-z]+|A|R)$""")
        val partRegex = Regex("""^\{x=(\d+),m=(\d+),a=(\d+),s=(\d+)}$""")

        fun parsePart(line: String): Part =
            partRegex.matchEntire(line)!!
                .groupValues.drop(1)
                .map(String::toInt)
                .zip(XmasCategory.entries)
                .associate { (score, category) -> category to score }
                .let(::Part)

        fun parseRule(line: String): Rule =
            ruleRegex.matchEntire(line)!!
                .destructured
                .let { (selector, filter, workflow) ->
                    Rule(
                        success = workflow,
                        threshold = filter.toInt(),
                        greater = line.contains('>'),
                        selector = selector.uppercase().let(XmasCategory::valueOf),
                    )
                }

        fun parseWorkflow(line: String) =
            workflowRegex.matchEntire(line)!!
                .destructured
                .let { (id, rules, fallback) ->
                    Workflow(
                        id = id,
                        rules = rules.split(',').filterNot(String::isEmpty).map(::parseRule),
                        fallback = fallback,
                    )
                }

        val (workflows, parts) = input.split("\n\n", limit = 2)
        workflows.lines().map(::parseWorkflow) to parts.lines().map(::parsePart)
    }

    override fun partOne(input: String): Long {
        val (pipeline, parts) = parseInput(input)
        val workflows = pipeline.associateBy { it.id }
        return parts
            .filter { part ->
                var work = "in"
                while (work != "A" && work != "R") work = workflows.getValue(work).evaluate(part)
                work == "A"
            }
            .sumOf(Part::totalScore)
    }

    override fun partTwo(input: String): Long {
        val workflows = parseInput(input).first.associateBy { it.id }

        val passingSpecs = buildList {
            fun analyzeSpec(work: String, partSpec: PartSpec) {
                if (work == "R") return
                if (work == "A") {
                    add(partSpec)
                    return
                }

                val workflow = workflows.getValue(work)

                val fallbackSpec = workflow.rules.fold(partSpec) { spec, rule ->
                    val (success, failure) = rule.split(spec)
                    if (success != null) analyzeSpec(rule.success, success)
                    failure ?: return
                }

                analyzeSpec(workflow.fallback, fallbackSpec)
            }

            analyzeSpec("in", PartSpec(XmasCategory.entries.associateWith { 1..4000 }))
        }

        return passingSpecs.sumOf(PartSpec::totalPossibleParts)
    }
}
