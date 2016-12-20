package com.github.sanity.pav

import com.github.sanity.pav.PairAdjacentViolators.InterpolationStrategy.SPLINE
import com.github.sanity.pav.spline.MonotoneSpline
import java.io.Serializable
import java.util.*

/**
 * Implements the "pair adjacent violators" algorithm, also known as "pool adjacent violators", for isotonic regression.
 */


class PairAdjacentViolators @JvmOverloads constructor(originalPoints: Iterable<Point>, mode: PAVMode = PAVMode.INCREASING) : Serializable {

    /**
     * The points after the regression, should either be increasing or decreasing depending
     * on how the PairAdjacentViolators object is configured.
     */
    val isotonicPoints: ArrayList<Point>

    init {
        val points: PairSubstitutingDoublyLinkedList<Point> = PairSubstitutingDoublyLinkedList.createFromList(originalPoints.sortedBy { it.x })
        points.iterate { cursor ->
            val previous = cursor.previousValue
            val next = cursor.nextValue
            if (previous != null) {
                val shouldMerge =
                        previous.x == next.x || (
                                when (mode) {
                    PAVMode.INCREASING -> previous.y >= next.y
                    PAVMode.DECREASING -> previous.y <= next.y
                                })
                if (shouldMerge) {
                    previous.merge(next)
                } else {
                    null
                }
            } else {
                null
            }
        }

        isotonicPoints = points.toArrayList()
    }

    @JvmOverloads fun interpolator(strategy: InterpolationStrategy = SPLINE): (Double) -> Double {
        when (strategy) {
            SPLINE -> return {
                MonotoneSpline(isotonicPoints).interpolate(it)
            }
        }
    }

    @JvmOverloads fun inverseInterpolator(strategy: InterpolationStrategy = SPLINE): (Double) -> Double {
        when (strategy) {
            SPLINE -> return {
                val spline = MonotoneSpline(isotonicPoints.map {Point(it.y, it.x)})
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
