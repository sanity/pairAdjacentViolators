package com.trystacks.pav

import com.trystacks.pav.PairAdjacentViolators.InterpolationStrategy.SPLINE

/**
 * Implements the "pair adjacent violators" algorithm, also known as "pool adjacent violators", for isotonic regression.
 */


class PairAdjacentViolators @JvmOverloads constructor(originalPoints: Iterable<Point>, mode: PAVMode = PAVMode.INCREASING) {

    /**
     * A point in 2D space, with an optional weight (defaults to 1).
     */
    data class Point @JvmOverloads constructor(val x: Double, val y: Double, val weight: Double = 1.0) {
        fun merge(other: Point): Point {
            val combinedWeight = weight + other.weight
            val nx = ((x * weight) + (other.x * other.weight)) / combinedWeight
            val ny = ((y * weight) + (other.y * other.weight)) / combinedWeight
            return Point(nx, ny, combinedWeight)
        }

        override fun toString() = "($x, $y${if (weight != 1.0) " :$weight" else ""})"
    }

    /**
     * The points after the regression, should either be increasing or decreasing depending
     * on how the PairAdjacentViolators object is configured.
     */
    val isotonicPoints: List<Point>

    init {
        val points: MutableChain<Point> = originalPoints.sortedBy { it.x }.toMutableChain()
        points.iterate { cursor ->
            val previous = cursor.previousValue
            val next = cursor.nextValue
            if (previous != null) {
                val shouldMerge = when (mode) {
                    PAVMode.INCREASING -> previous.y >= next.y
                    PAVMode.DECREASING -> previous.y <= next.y
                }
                if (shouldMerge) {
                    previous.merge(next)
                } else {
                    null
                }
            } else {
                println()
                null
            }
        }

        isotonicPoints = points.toList()
    }

    @JvmOverloads fun interpolator(strategy: InterpolationStrategy = SPLINE): (Double) -> Double {
        when (strategy) {
            SPLINE -> return {
                val spline = Spline.createMonotoneCubicSpline(isotonicPoints.map { it.x }.toDoubleArray(), isotonicPoints.map { it.y }.toDoubleArray())
                spline.interpolate(it)
            }
        }
    }

    @JvmOverloads fun inverseInterpolator(strategy: InterpolationStrategy = SPLINE): (Double) -> Double {
        when (strategy) {
            SPLINE -> return {
                val spline = Spline.createMonotoneCubicSpline(isotonicPoints.map { it.y }.toDoubleArray(), isotonicPoints.map { it.x }.toDoubleArray())
                spline.interpolate(it)
            }
        }
    }

    enum class InterpolationStrategy {
        SPLINE
    }

    enum class PAVMode {
        INCREASING, DECREASING
    }
}
