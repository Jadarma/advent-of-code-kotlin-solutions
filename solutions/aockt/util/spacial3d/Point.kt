package aockt.util.spacial3d

/**
 * Represents a discrete point in 3D space.
 *
 * @property x The width of a 2D slice; imagine the horizontal component of the map.
 * @property y The height of a 2D slice; imagine the vertical component of the map.
 * @property z The height of the 3D space; imagine the altitude of the map.
 */
data class Point3D(val x: Long, val y: Long, val z: Long) {

    /** Alternate constructor that converts from integer coordinates, for convenience. */
    constructor(x: Int, y: Int, z: Int) : this(x.toLong(), y.toLong(), z.toLong())

    /** Encoded as a human and debugger friendly way. */
    override fun toString(): String = "($x, $y, $z)"

    companion object {
        /** The center of the cartesian grid. */
        val Origin: Point3D = Point3D(0L, 0L, 0L)
    }
}
