package com.github.sanity.pav
/**
/**
 * Creates a monotone cubic spline from a given set of control points.

 * The spline is guaranteed to pass through each control point exactly.
 * Moreover, assuming the control points are monotonic (Y is non-decreasing or
 * non-increasing) then the interpolated values will also be monotonic.

 * This function uses the Fritsch-Carlson method for computing the spline parameters.
 * http://en.wikipedia.org/wiki/Monotone_cubic_interpolation

 * @param xPoints The X component of the control points, strictly increasing.
 * *
 * @param yPoints The Y component of the control points, monotonic.
 * *
 * @return
 * *
 * *
 * @throws IllegalArgumentException if the X or Y arrays are null, have
 * * different lengths or have fewer than 2 values.
 * *
 * @throws IllegalArgumentException if the control points are not monotonic.
*/
 *
 * This implementation converted from java from https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/util/MonotoneSpline.java
 *
 * Includes a fix for the following bug: https://code.google.com/p/android/issues/detail?id=222470
 */
class MonotoneSpline(private val xPoints: DoubleArray, private val yPoints: DoubleArray) {

    constructor(points : Iterable<PairAdjacentViolators.Point>) : this(points.map { it.x }.toDoubleArray(), points.map { it.y }.toDoubleArray())

    private val tangents: DoubleArray

    init {
        if (xPoints.size != yPoints.size || xPoints.size < 2) {
            throw IllegalArgumentException("There must be at least two control points and the arrays must be of equal length.")
        }
        val pointsCount = xPoints.size
        val d = DoubleArray(pointsCount - 1) // could optimize this out
        tangents = DoubleArray(pointsCount)
        // Compute slopes of secant lines between successive points.
        for (i in 0..pointsCount - 1 - 1) {
            val h = xPoints[i + 1] - xPoints[i]
            if (h <= 0f) {
                throw IllegalArgumentException("The control points must all have strictly increasing X values.")
            }
            d[i] = (yPoints[i + 1] - yPoints[i]) / h
        }
        // Initialize the tangents as the average of the secants.
        tangents[0] = d[0]
        for (i in 1..pointsCount - 1 - 1) {
            tangents[i] = (d[i - 1] + d[i]) * 0.5f
        }
        tangents[pointsCount - 1] = d[pointsCount - 2]
        // Update the tangents to preserve monotonicity.
        for (i in 0..pointsCount - 1 - 1) {
            if (d[i] == 0.0) {
                // successive Y values are equal
                tangents[i] = 0.0
                tangents[i + 1] = 0.0
            } else {
                val a = tangents[i] / d[i]
                val b = tangents[i + 1] / d[i]
                if (a < 0.0 || b < 0.0) {
                    throw IllegalArgumentException("The control points must have monotonic Y values.")
                }
                val h = Math.hypot(a, b)
                if (h > 3.0) {
                    val t = 3.0 / h
                    tangents[i] = t * a * d[i]
                    tangents[i + 1] = t * b * d[i]
                }
            }
        }
    }

    /**
     * Interpolates the value of Y = f(X) for given X.
     * Clamps X to the domain of the spline.

     * @param x The X value.
     * *
     * @return The interpolated Y = f(X) value.
     */
    fun interpolate(x: Double): Double {
        if (x < xPoints.first()) return yPoints.first()
        if (x > xPoints.last()) return yPoints.last()
        val xIndex = xPoints.betterBinarySearch(x)
        return when (xIndex) {
            is BinarySearchResult.Exact -> yPoints[xIndex.index]
            is BinarySearchResult.Between -> {
                val x1 = xPoints[xIndex.lowIndex]
                val y1 = yPoints[xIndex.lowIndex]
                val m1 = tangents[xIndex.lowIndex]
                val x2 = xPoints[xIndex.highIndex]
                val y2 = yPoints[xIndex.highIndex]
                val m2 = tangents[xIndex.highIndex]
                val chs = CubicHermiteSpline(x1 = x1, y1 = y1, m1 = m1, x2 = x2, y2 = y2, m2 = m2)
                return chs.interpolate(x)
            }
        }
    }
}