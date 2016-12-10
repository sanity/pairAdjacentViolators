package com.github.sanity.pav.spline

/**
 * Created by ian on 12/7/16.
 */
class CubicHermiteSpline(val x1: Double, val y1: Double, val m1: Double, val x2: Double, val y2: Double, val m2: Double) {

    fun interpolate(x: Double): Double {
        val t = (x - x1) / (x2 - x1)
        val pk = y1
        val pk1 = y2
        val xk = x1
        val xk1 = x2
        val t1 = h00(t) * pk
        val t2 = h10(t) * (xk1 - xk) * m1
        val t3 = h01(t) * pk1
        val t4 = h11(t) * (xk1 - xk) * m2
        return t1 +
                t2 +
                t3 +
                t4
    }

    private fun h00(t: Double) = 2.0 * (t pow 3) - 3.0 * (t pow 2) + 1.0
    private fun h10(t: Double) = (t pow 3) - 2.0 * (t pow 2) + t
    private fun h01(t: Double) = -2.0 * (t pow 3) + 3.0 * (t pow 2)
    private fun h11(t: Double) = (t pow 3) - (t pow 2)

    infix private fun Double.pow(d: Int) = Math.pow(this, d.toDouble())
}