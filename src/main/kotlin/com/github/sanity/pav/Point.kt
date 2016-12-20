package com.github.sanity.pav

import java.io.Serializable

/**
 * A point in 2D space, with an optional weight (defaults to 1).
 */
data class Point @JvmOverloads constructor(val x: Double, val y: Double, val weight: Double = 1.0) : Serializable {
    fun merge(other: Point): Point {
        val combinedWeight = weight + other.weight
        val nx = ((x * weight) + (other.x * other.weight)) / combinedWeight
        val ny = ((y * weight) + (other.y * other.weight)) / combinedWeight
        return Point(nx, ny, combinedWeight)
    }

    override fun toString() = "($x, $y${if (weight != 1.0) " :$weight" else ""})"
}
