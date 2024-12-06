package aockt.util.spacial

import kotlin.collections.ArrayList

/**
 * A 2D spacial array.
 *
 * Coordinates are the same as with [Point]s:
 * - `x` is horizontal, positive right of origin.
 * - `y` is vertical, positive above of origin.
 */
interface Grid<out T : Any> {

    /** The width of the grid. */
    val width: Int

    /** The height of the grid. */
    val height: Int

    /**
     * Return the value at coordinates ([x], [y]).
     * @throws IndexOutOfBoundsException If point is out of bounds.
     */
    operator fun get(x: Int, y: Int): T

    /** Return the value at coordinates ([x], [y]), or `null` if it is out of bounds. */
    fun getOrNull(x: Int, y: Int): T?
}

/** A [Grid] that allows mutability. */
interface MutableGrid<T : Any> : Grid<T> {

    /**
     * Update ([x], [y]) with a new value.
     * @throws IndexOutOfBoundsException If point is out of bounds.
     */
    operator fun set(x: Int, y: Int, value: T)
}

/** The backing implementation of [Grid] and [MutableGrid]. */
private class FiniteGridImpl<T : Any>(
    val mutable: Boolean,
    override val width: Int,
    override val height: Int,
    builder: (Int, Int) -> T,
) : MutableGrid<T> {

    private val data: ArrayList<T>

    init {
        require(width > 0) { "Width must be positive." }
        require(height > 0) { "Height must be positive." }
        require(width.toLong() * height <= Int.MAX_VALUE) { "Grid too big!" }

        data = ArrayList(width * height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                data.add(builder(x, y))
            }
        }
    }

    override fun get(x: Int, y: Int): T =
        if(x !in 0..width || y !in 0..<height) throw IndexOutOfBoundsException("Point ($x, $y) is outside the grid.")
        else data[y * width + x]

    override fun getOrNull(x: Int, y: Int): T? =
        if (x !in 0..width || y !in 0..<height) null
        else data[y * width + x]

    override fun set(x: Int, y: Int, value: T) {
        check(mutable) { "This grid is immutable." }
        data[y * width + x] = value
    }

    override fun hashCode() = data.hashCode()

    override fun equals(other: Any?) = when {
        this === other -> true
        other !is FiniteGridImpl<*> -> false
        else -> data == other.data
    }
}

//<editor-fold desc="Constructors">

/** Constructs a new [Grid] of given [width] and [height], and each point value determined by the [builder]. */
fun <T : Any> Grid(width: Int, height: Int, builder: (Int, Int) -> T): Grid<T> =
    FiniteGridImpl(mutable = false, width, height, builder)

/** Constructs a new [Grid] of given [width] and [height], and each point value determined by the [builder]. */
fun <T : Any> MutableGrid(width: Int, height: Int, builder: (Int, Int) -> T): MutableGrid<T> =
    FiniteGridImpl(mutable = true, width, height, builder)

/** Parse a string into a grid of characters. */
fun Grid(raw: String): Grid<Char> = raw.parseGridInternal(mutable = false) { it }

/** Parse a string into a mutable grid of characters. */
fun MutableGrid(raw: String): MutableGrid<Char> = raw.parseGridInternal(mutable = true) { it }

/** Parse a string into a grid, mapping each character to a custom type. */
fun <T : Any> Grid(raw: String, mapper: (Char) -> T): Grid<T> = raw.parseGridInternal(mutable = false, mapper)

/** Parse a string into a mutable grid, mapping each character to a custom type. */
fun <T : Any> MutableGrid(raw: String, mapper: (Char) -> T): MutableGrid<T> =
    raw.parseGridInternal(mutable = true, mapper)

private inline fun <T : Any> String.parseGridInternal(
    mutable: Boolean,
    crossinline mapper: (Char) -> T,
): FiniteGridImpl<T> {
    val lines = lines().asReversed()
    require(lines.isNotEmpty()) { "Cannot parse a grid from an empty string." }
    require(lines.all { it.length == lines.first().length }) { "Input string must be a perfectly rectangular grid." }
    val width = lines.first().length
    val height = lines.size
    return FiniteGridImpl(mutable, width, height) { x, y -> mapper(lines[y][x]) }
}

//</editor-fold>

//<editor-fold desc="Iterating Helpers">

/** Describes a single [Grid] cell's [position] and [value]. */
data class GridCell<T : Any>(val position: Point, val value: T)

/**
 * Returns a [Sequence] of points and their respective grid values, from the [start] point, using the [next] function to
 * determine the next point.
 * Stops emitting values when the point is out of grid bounds.
 * If the start point is already outside, returns an empty sequence.
 */
fun <T : Any> Grid<T>.move(start: Point, next: (Point, T) -> Point): Sequence<GridCell<T>> = sequence {
    val area = Area(width, height)
    var current = start
    while (current in area) {
        val value = get(current)
        yield(GridCell(current, value))
        current = next(current, value)
    }
}

/**
 * Returns a [Sequence] of points and their respective grid values, from the [start] point, moving into the [direction].
 * Stops emitting values when the point is out of grid bounds.
 * If the start point is already outside, returns an empty sequence.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> Grid<T>.move(start: Point, direction: Direction): Sequence<GridCell<T>> =
    move(start) { p, _ -> p.move(direction) }

fun <T: Any> Grid<T>.points(): Sequence<GridCell<T>> = sequence {
    for (x in 0 until width) {
        for (y in 0 until height) {
            yield(GridCell(Point(x, y), get(x, y)))
        }
    }
}


//</editor-fold>

//<editor-fold desc="Integration Extension Helpers">

/**
 * Return the value at this [point].
 * @throws IndexOutOfBoundsException If point is out of bounds.
 */
operator fun <T : Any> Grid<T>.get(point: Point): T = get(point.x.toInt(), point.y.toInt())

/** Return the value at this [point], or `null` if it is out of bounds. */
fun <T: Any> Grid<T>.getOrNull(point: Point): T? = getOrNull(point.x.toInt(), point.y.toInt())

/** Checks if the [point] is within the grid bounds. */
operator fun Grid<*>.contains(point: Point): Boolean = getOrNull(point) != null

/**
 * Updates this [point] with a new [value].
 * @throws IndexOutOfBoundsException If point is out of bounds.
 */
operator fun <T : Any> MutableGrid<T>.set(point: Point, value: T) = set(point.x.toInt(), point.y.toInt(), value)

//</editor-fold>

//<editor-fold desc="Debug Helpers">

fun <T: Any> Grid<T>.render(
    maxWidth: Int = width,
    maxHeight: Int = height,
    formatter: (T) -> Char,
): String = buildString {
    val xRange = 0 .. maxWidth.coerceIn(0, width - 1)
    val yRange = maxHeight.coerceIn(0, height - 1) downTo 0

    val verticalCutoff = yRange.first < height - 1
    val horizontalCutoff = xRange.last < width - 1
    if(verticalCutoff) {
        append("⋮".repeat(xRange.last + 1))
        if(horizontalCutoff) append('⋰')
        appendLine()
    }
    for (y in yRange) {
        for (x in xRange) {
            append(formatter(get(x, y)))
        }
        if(xRange.last < width - 1) append('…')
        if(y != yRange.last) appendLine()
    }
}

//</editor-fold>
