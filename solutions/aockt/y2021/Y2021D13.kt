package aockt.y2021

import aockt.util.OcrDecoder
import io.github.jadarma.aockt.core.Solution

object Y2021D13 : Solution {

    /** Represents a discrete point in 2D space. */
    private data class Point(val x: UInt, val y: UInt)

    /** Represents a possible origami fold. */
    private sealed interface Fold {
        val amount: UInt

        /** Fold the paper upwards from the horizontal line [amount] of units down. */
        data class Up(override val amount: UInt) : Fold

        /** Fold the paper to the left from the vertical line [amount] of units to the right. */
        data class Left(override val amount: UInt) : Fold
    }

    /** Given a point, determines where it will land after the [fold], or `null` if it's on the line or under-flows. */
    private fun Point.origamiFold(fold: Fold): Point? {
        val axisValue = when (fold) {
            is Fold.Left -> x
            is Fold.Up -> y
        }
        return when (axisValue) {
            in 0u until fold.amount -> this
            fold.amount -> null
            else -> if (axisValue > fold.amount * 2u) null else Point(
                x = if (fold is Fold.Left) fold.amount * 2u - x else x,
                y = if (fold is Fold.Up) fold.amount * 2u - y else y,
            )
        }
    }

    /** Performs the origami [fold] on all points and returns the new set of unique points. */
    private fun Set<Point>.origamiFold(fold: Fold): Set<Point> = buildSet {
        for (point in this@origamiFold) point.origamiFold(fold)?.let { add(it) }
    }

    /** Builds a string containing all the dots marked on the origami paper. */
    private fun Set<Point>.visualize(): String {
        if (isEmpty()) return ""
        val width = maxOf { it.x }.toInt() + 1
        val height = maxOf { it.y }.toInt() + 1
        val dotMap = BooleanArray(width * height) { false }
        forEach { dot -> dotMap[(dot.y.toInt() * width + dot.x.toInt())] = true }
        return buildString {
            dotMap.forEachIndexed { i, dot ->
                append(if (dot) '#' else '.')
                if ((i + 1) % width == 0 && i != dotMap.lastIndex) appendLine('.')
            }
            appendLine('.')
        }
    }

    /** Parse the [input] and return the position of the dots on the paper and the list of folding instructions. */
    private fun parseInput(input: String): Pair<Set<Point>, List<Fold>> {
        val (rawFolds, rawPoints) = input
            .lineSequence()
            .filterNot { it.isBlank() }
            .partition { it.startsWith("fold along") }
        val points = rawPoints
            .map { it.split(',') }
            .map { (a, b) -> Point(a.toUInt(), b.toUInt()) }
            .toSet()
        val folds = rawFolds
            .map { it.removePrefix("fold along ") }
            .map { it.split('=') }
            .map { (axis, amount) ->
                when (axis) {
                    "x" -> Fold::Left
                    "y" -> Fold::Up
                    else -> throw IllegalArgumentException()
                }(amount.toUIntOrNull() ?: throw IllegalArgumentException())
            }
        return points to folds
    }

    override fun partOne(input: String) =
        parseInput(input).let { (points, folds) ->
            points.origamiFold(folds.first()).count()
        }

    override fun partTwo(input: String) =
        parseInput(input).let { (points, folds) ->
            folds
                .fold(points) { dots, fold -> dots.origamiFold(fold) }
                .visualize()
                .let(OcrDecoder::decode)
        }
}
