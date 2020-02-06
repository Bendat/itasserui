package itasserui.lib

import kotlin.math.acos
import kotlin.math.sqrt

data class Coord(
    val x: Double,
    val y: Double,
    val z: Double
) {

    private var hash = 0

    fun distance(x1: Double, y1: Double, z1: Double): Double {
        val a = x - x1
        val b = y - y1
        val c = z - z1
        return sqrt(a * a + b * b + c * c)
    }

    fun distance(point: Coord): Double =
        distance(point.x, point.y, point.z)

    fun add(x: Double, y: Double, z: Double): Coord =
        Coord(x + x, y + y, z + z)

    fun add(point: Coord): Coord =
        add(point.x, point.y, point.z)

    fun subtract(x: Double, y: Double, z: Double): Coord =
        Coord(x - x, y - y, z - z)

    fun subtract(point: Coord): Coord =
        subtract(point.x, point.y, point.z)

    fun multiply(factor: Double): Coord =
        Coord(x * factor, y * factor, z * factor)

    fun normalize(): Coord {
        val mag = magnitude()

        return if (mag == 0.0) {
            Coord(0.0, 0.0, 0.0)
        } else Coord(
            x / mag,
            y / mag,
            z / mag)

    }

    fun midpoint(x: Double, y: Double, z: Double): Coord {
        return Coord(
            x + (x - x) / 2.0,
            y + (y - y) / 2.0,
            z + (z - z) / 2.0)
    }

    fun midpoint(point: Coord): Coord =
        midpoint(point.x, point.y, point.z)

    fun angle(x: Double, y: Double, z: Double): Double {
        val ax = x
        val ay = y
        val az = z

        val delta = (ax * x + ay * y + az * z) / sqrt((ax * ax + ay * ay + az * az) * (x * x + y * y + z * z))

        return angleDelta(delta)
    }

    fun angle(point: Coord): Double =
        angle(point.x, point.y, point.z)

    fun angle(p1: Coord, p2: Coord): Double {
        val x = x
        val y = y
        val z = z

        val ax = p1.x - x
        val ay = p1.y - y
        val az = p1.z - z
        val bx = p2.x - x
        val by = p2.y - y
        val bz = p2.z - z

        val delta = (ax * bx + ay * by + az * bz) / sqrt((ax * ax + ay * ay + az * az) * (bx * bx + by * by + bz * bz))

        return angleDelta(delta)
    }

    private fun angleDelta(delta: Double): Double {
        if (delta > 1.0) {
            return 0.0
        }
        return if (delta < -1.0) {
            180.0
        } else Math.toDegrees(acos(delta))
    }

    fun magnitude(): Double {
        val x = x
        val y = y
        val z = z

        return Math.sqrt(x * x + y * y + z * z)
    }

    fun dotProduct(x: Double, y: Double, z: Double): Double =
        x * x + y * y + z * z

    fun dotProduct(vector: Coord): Double =
        dotProduct(vector.x, vector.y, vector.z)


    fun crossProduct(x: Double, y: Double, z: Double): Coord {
        val ax = x
        val ay = y
        val az = z

        return Coord(
            ay * z - az * y,
            az * x - ax * z,
            ax * y - ay * x)
    }

    fun crossProduct(vector: Coord): Coord =
        crossProduct(vector.x, vector.y, vector.z)

    fun interpolate(endValue: Coord, t: Double): Coord {
        if (t <= 0.0) return this
        return if (t >= 1.0) endValue else Coord(
            x + (endValue.x - x) * t,
            y + (endValue.y - y) * t,
            z + (endValue.z - z) * t
        )
    }


    companion object {

        val ZERO = Coord(0.0, 0.0, 0.0)
    }
}