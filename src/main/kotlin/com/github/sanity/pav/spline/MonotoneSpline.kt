package com.github.sanity.pav.spline

import com.github.sanity.pav.BinarySearchResult
import com.github.sanity.pav.Point
import com.github.sanity.pav.betterBinarySearch
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

class MonotoneSpline @JvmOverloads constructor(inputPoints: List<Point>, tangentStrategy: TangentStrategy = FritschCarlsonTangentStrategy()) {

    private val points: ArrayList<PointWithTangents>

    init {
        var lastY : Double? = null
        for (point in inputPoints) {
            if (lastY != null && lastY > point.y) throw IllegalArgumentException("inputPoints are not monotonic")
            lastY = point.y
        }

        val pointsWithSecants = SecantsCalculator.calculate(inputPoints)
        points = tangentStrategy.compute(pointsWithSecants)
    }

    /**
     * Interpolates the value of Y = f(X) for given X.
     * Clamps X to the domain of the spline.

     * @param x The X value.
     * *
     * @return The interpolated Y = f(X) value.
     */
    fun interpolate(x: Double): Double {
        val firstPoint = points.first()
        if (x < firstPoint.x) return firstPoint.y
        val lastPoint = points.last()
        if (x > lastPoint.x) return lastPoint.y
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
            else -> TODO("Remove this, shouldn't be necessary")
        }
    }
}

