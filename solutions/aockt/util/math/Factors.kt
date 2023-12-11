@file:Suppress("NOTHING_TO_INLINE")

package aockt.util.math

import java.lang.Math.multiplyExact
import kotlin.math.absoluteValue

/** Calculates the greatest common denominator of two numbers [a] and [b] using the Euclidean method. */
fun gcd(a: Long, b: Long): Long {
    var x = if (a > 0) a else -a
    var y = if (b > 0) b else -b

    while (y != 0L) {
        val temp = y
        y = x % y
        x = temp
    }
    return x
}

/** Calculates the greatest common denominator of two numbers [a] and [b] using the Euclidean method. */
inline fun gcd(a: Int, b: Int): Int = gcd(a.toLong(), b.toLong()).toInt()

/** Calculates the least common multiple of two numbers [a] and [b]. */
fun lcm(a: Long, b: Long): Long = multiplyExact(a, b).absoluteValue / gcd(a, b)

/** Calculates the least common multiple of two numbers [a] and [b]. */
inline fun lcm(a: Int, b: Int): Long = lcm(a.toLong(), b.toLong())

/** Calculates the least common multiple of all given numbers. */
@JvmName("lcmManyLong")
fun Iterable<Long>.lcm(): Long = reduce(::lcm)

/** Calculates the least common multiple of all given numbers. */
@JvmName("lcmManyInt")
inline fun Iterable<Int>.lcm(): Long = map(Int::toLong).lcm()

/** Calculates the least common multiple of all given numbers. */
fun lcm(first: Long, vararg other: Long): Long =
    if (other.isEmpty()) first
    else lcm(first, other.asIterable().lcm())

/** Calculates the least common multiple of all given numbers. */
fun lcm(first: Int, vararg other: Int): Long =
    if (other.isEmpty()) first.toLong()
    else lcm(first.toLong(), other.asIterable().lcm())
