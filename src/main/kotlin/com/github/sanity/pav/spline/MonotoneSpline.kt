package com.github.sanity.pav.spline

import com.github.sanity.pav.*
import com.github.sanity.pav.spline.MonotoneSpline.ExtrapolationStrategy.*
import java.io.Serializable
import java.util.*

/**
 * Creates a monotone cubic spline from a given set of control points, implementing
 * the algorithm described [here](https://en.wikipedia.org/wiki/Monotone_cubic_interpolation).

 * The spline is guaranteed to pass through each control point exactly.
 * Moreover, assuming the control points are monotonic (Y is non-decreasing or
 * non-increasing) then the interpolated values will also be monotonic.

 * @param inputPoints The control points through which the spline must pass
 *
 * @param tangentStrategy The strategy used to determine the tangents at each control point
 * *
 * @throws IllegalArgumentException if the control points are not monotonic.
*/

class MonotoneSpline @JvmOverloads constructor(inputPoints: List<Point>, tangentStrategy: TangentStrategy = FritschCarlsonTangentStrategy()) : Serializable {

    private val minimumXDistance = 0.000001

    private val points: ArrayList<PointWithTangents>

    init {
        var direction : Int = 0
        var lastPoint : Point? = null
        for (point in inputPoints) {
            if (lastPoint != null) {
                if (point.x - lastPoint.x < minimumXDistance) {
                    throw IllegalArgumentException("x position of $point and $lastPoint are below minimum threshold of $minimumXDistance")
                }
                val cd = point.y.compareTo(lastPoint.y)
                if (direction == 0) {
                    direction = cd
                } else if ((cd != 0) && cd != direction) {
                    throw IllegalArgumentException("Input is not monotonic, cd: $cd, direction: $direction, point: $point, lastPoint: $lastPoint, inputPoints: $inputPoints")
                }
            }
            lastPoint = point
        }

        val pointsWithSecants = SecantsCalculator.calculate(inputPoints)
        points = tangentStrategy.compute(pointsWithSecants)
    }

    /**
     * Interpolates the value of Y = f(X) for given X.
     * Clamps X to the domain of the spline.

     * @param x The X value
     * @param extrapolationStrategy How will x values be handled that are outside the x range of the control points
     * @return The interpolated Y = f(X) value
     */
    @JvmOverloads fun interpolate(x: Double, extrapolationStrategy: ExtrapolationStrategy = TANGENT): Double {
        val firstPoint = points.first()
        val lastPoint = points.last()
        if ((x < firstPoint.x) || (x > lastPoint.x)) {
            return when (extrapolationStrategy) {
                FLAT -> {
                    if (x < firstPoint.x) firstPoint.y else lastPoint.y
                }
                TANGENT -> {
                    if (x < firstPoint.x) {
                        val xDelta = firstPoint.x - x
                        val yDelta = firstPoint.tangent * xDelta
                        firstPoint.y - yDelta
                    } else {
                        val xDelta = x - lastPoint.x
                        val yDelta = lastPoint.tangent * xDelta
                        lastPoint.y + yDelta
                    }
                }
            }
        }
        val binarySearchResult = points.map { it.x }.toDoubleArray().betterBinarySearch(x)
        return when (binarySearchResult) {
            is BinarySearchResult.Exact -> points[binarySearchResult.index].y
            is BinarySearchResult.Between -> {
                val x1 = points[binarySearchResult.lowIndex].x
                val y1 = points[binarySearchResult.lowIndex].y
                val m1 = points[binarySearchResult.lowIndex].tangent
                val x2 = points[binarySearchResult.highIndex].x
                val y2 = points[binarySearchResult.highIndex].y
                val m2 = points[binarySearchResult.highIndex].tangent
                val chs = CubicHermiteSpline(x1 = x1, y1 = y1, m1 = m1, x2 = x2, y2 = y2, m2 = m2)
                return chs.interpolate(x)
            }
        }
    }

    /**
     * Determines how the spline will handle points that are outside of the x range of its
     * control points.
     */
    enum class ExtrapolationStrategy {
        /**
         * Will return the y value of the closest point, resulting in a flat line
         * before and after the spline
         */
        FLAT,
        /**
         * Will extrapolate using the computed tangents of the first and last points
         */
        TANGENT
    }
}

