package aockt.util

/**
 * Generate a [Sequence] of permutations of this collection.
 * For obvious reasons, will throw if the collection is too large.
 */
fun <T> Collection<T>.generatePermutations(): Sequence<List<T>> = when (size) {
    !in 0 .. 9 -> throw Exception("Too many permutations. This is probably not what you want to use.")
    0 -> emptySequence()
    1 -> sequenceOf(this.toList())
    else -> {
        val first = first()
        drop(1)
            .generatePermutations()
            .flatMap { perm -> (0..perm.size).map { perm.toMutableList().apply { add(it, first) } } }
    }
}

/**
 * Returns the power set of the collection (all possible unordered combinations) except it's a [List] and not a [Set]
 * because repeated values are allowed.
 */
fun <T> Collection<T>.powerSet(): List<List<T>> = when (size) {
    0 -> listOf(emptyList())
    else -> {
        val first = listOf(first())
        val next = drop(1).powerSet()
        next.map { first + it } + next
    }
}
