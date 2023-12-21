package aockt.util.validation

/**
 * Some puzzles cannot be solved on the general case, but rather depend on reverse engineering specially crafted inputs.
 * This exception is thrown when validation of those assumptions fails.
 *
 * *NOTE: This does not mean the input is necessarily invalid, or the solution is necessarily wrong, just that they are
 * incompatible with one another.*
 */
class AssumptionFailedException(assumption: String) : IllegalStateException("Assumption failed: $assumption")

/** Throws an [AssumptionFailedException] with the result of calling [lazyMessage] if the [value] is false. */
inline fun assume(value: Boolean, lazyMessage: () -> String) {
    if (!value) throw AssumptionFailedException(lazyMessage())
}
