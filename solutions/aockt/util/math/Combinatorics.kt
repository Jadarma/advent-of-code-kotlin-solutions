package aockt.util.math

/**
 * Consumes items from an iterable source and produces a sequence of pairs with the following properties:
 * - The elements in the pair can be in any order. For an input with elements A and B, the sequence will container
 *   either `(A, B)` or `(B, A)`, depending on input order.
 * - The pairs are not ordered in any particular way, although the order is deterministic.
 * - For `n` elements, the sequence will yield exactly `n * (n - 1) / 2` elements.
 *
 * In other words, it is the equivalent of computing all values of _n choose 2_.
 *
 * Example: `[1,2,3,4] -> (1, 2), (1, 3), (2, 3), (1, 4), (2, 4), (3, 4)`
 *
 * NOTE: The _"distinct"_ in the function name refers to distinct positional elements, not distinct by value!
 * If you wish to prevent the generation of pairs with equal elements, you should make sure the source only contains
 * distinct values, like a [Set].
 * */
fun <T : Any> Iterable<T>.distinctPairs(): Sequence<Pair<T, T>> = sequence {
    val iter = this@distinctPairs.iterator()
    if (!iter.hasNext()) return@sequence
    val previous = mutableListOf(iter.next())
    while(iter.hasNext()) {
        val second = iter.next()
        for (first in previous) yield(first to second)
        previous.add(second)
    }
}
