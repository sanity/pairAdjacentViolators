package com.trystacks.pav

import com.trystacks.pav.PairAdjacentViolators.InterpolationStrategy.SPLINE
import java.util.function.Supplier

/**
 * Created by ian on 9/5/16.
 */


class PairAdjacentViolators(originalPoints: Iterable<Point>, mode: PAVMode = PAVMode.INCREASING) : Supplier<List<PairAdjacentViolators.Point>> {

    data class Point(val x: Double, val y: Double, val weight: Double = 1.0) {
        fun merge(other: Point): Point {
            val combinedWeight = weight + other.weight
            val nx = ((x * weight) + (other.x * other.weight)) / combinedWeight
            val ny = ((y * weight) + (other.y * other.weight)) / combinedWeight
            return Point(nx, ny, combinedWeight)
        }
    }

    override fun get(): List<Point> = isotonicPoints

    private val isotonicPoints: List<Point>

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
                null
            }
        }

        isotonicPoints = points.toList()
    }

    fun interpolator(strategy: InterpolationStrategy = SPLINE): (Double) -> Double {
        when (strategy) {
            SPLINE -> return {
                val spline = Spline.createMonotoneCubicSpline(isotonicPoints.map { it.x }.toDoubleArray(), isotonicPoints.map { it.y }.toDoubleArray())
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
