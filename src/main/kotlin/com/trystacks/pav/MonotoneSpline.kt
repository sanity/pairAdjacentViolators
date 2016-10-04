package com.trystacks.pav
/**
 * Performs spline interpolation given a set of control points.
 *
 * This implementation converted from java from https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/util/MonotoneSpline.java
 *
 * Includes a fix for the following bug: https://code.google.com/p/android/issues/detail?id=222470
 */
class MonotoneSpline(private val controlXPoints: DoubleArray, private val controlYPoints: DoubleArray) {

    private val tangents: DoubleArray

    init {
        if (controlXPoints.size != controlYPoints.size || controlXPoints.size < 2) {
            throw IllegalArgumentException("There must be at least two control points and the arrays must be of equal length.")
        }
        val n = controlXPoints.size
        val d = DoubleArray(n - 1) // could optimize this out
        tangents = DoubleArray(n)
        // Compute slopes of secant lines between successive points.
        for (i in 0..n - 1 - 1) {
            val h = controlXPoints[i + 1] - controlXPoints[i]
            if (h <= 0f) {
                throw IllegalArgumentException("The control points must all have strictly increasing X values.")
            }
            d[i] = (controlYPoints[i + 1] - controlYPoints[i]) / h
        }
        // Initialize the tangents as the average of the secants.
        tangents[0] = d[0]
        for (i in 1..n - 1 - 1) {
            tangents[i] = (d[i - 1] + d[i]) * 0.5f
        }
        tangents[n - 1] = d[n - 2]
        // Update the tangents to preserve monotonicity.
        for (i in 0..n - 1 - 1) {
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
                val h = Math.hypot(a.toDouble(), b.toDouble()).toFloat()
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
        // Handle the boundary cases.
        val n = controlXPoints.size
        if (x.isNaN()) {
            return x
        }
        if (x <= controlXPoints[0]) {
            return controlYPoints[0]
        }
        if (x >= controlXPoints[n - 1]) {
            return controlYPoints[n - 1]
        }
        // Find the index 'i' of the last point with smaller X.
        // We know this will be within the spline due to the boundary tests.
        var i = 0
        while (x >= controlXPoints[i + 1]) {
            i += 1
            if (x == controlYPoints[i]) {
                return controlYPoints[i]
            }
        }
        // Perform cubic Hermite spline interpolation.
        val h = controlXPoints[i + 1] - controlXPoints[i]
        val t = (x - controlXPoints[i]) / h
        return (controlYPoints[i] * (1 + 2 * t) + h * tangents[i] * t) * (1 - t) * (1 - t) + (tangents[i + 1] * (3 - 2 * t) + h * tangents[i + 1] * (t - 1)) * t * t
    }

    // For debugging.
    override fun toString(): String {
        val str = StringBuilder()
        val n = controlXPoints.size
        str.append("[")
        for (i in 0..n - 1) {
            if (i != 0) {
                str.append(", ")
            }
            str.append("(").append(controlXPoints[i])
            str.append(", ").append(controlYPoints[i])
            str.append(": ").append(tangents[i]).append(")")
        }
        str.append("]")
        return str.toString()
    }

    companion object {
        /**
         * Creates a monotone cubic spline from a given set of control points.

         * The spline is guaranteed to pass through each control point exactly.
         * Moreover, assuming the control points are monotonic (Y is non-decreasing or
         * non-increasing) then the interpolated values will also be monotonic.

         * This function uses the Fritsch-Carlson method for computing the spline parameters.
         * http://en.wikipedia.org/wiki/Monotone_cubic_interpolation

         * @param x The X component of the control points, strictly increasing.
         * *
         * @param y The Y component of the control points, monotonic.
         * *
         * @return
         * *
         * *
         * @throws IllegalArgumentException if the X or Y arrays are null, have
         * * different lengths or have fewer than 2 values.
         * *
         * @throws IllegalArgumentException if the control points are not monotonic.
         */

    }
}