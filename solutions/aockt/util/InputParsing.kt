package aockt.util

/**
 * Thrown if an error occurred while attempting to process the raw puzzle input.
 * It shows the problem is likely the data is being read wrong, rather than the solving
 * algorithm itself.
 *
 * @property cause The reason the parsing failed.
 */
class InvalidInputException(
    cause: Throwable = IllegalStateException("Could not parse input."),
) : IllegalArgumentException("Invalid input.", cause)

/**
 * Treats any error that would occur inside the [parser] block as an [InvalidInputException].
 * Replaces the boilerplate of a try catch block.
 *
 * Intended for use as a function definition. For example, the following function would interpret the `input` as a list
 * of numbers, or fail if the input is not valid, which should never happen on proper puzzle inputs:
 *
 * ```kotlin
 * fun parseInput(input: String): List<Int> = parse {
 *     input.split(' ').map(String::toInt)
 * }
 * ```
 */
inline fun <T: Any> parse(parser: () -> T): T =
    runCatching { parser() }
        .getOrElse { cause -> throw InvalidInputException(cause) }
