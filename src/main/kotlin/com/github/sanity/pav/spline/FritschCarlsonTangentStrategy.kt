package com.github.sanity.pav.spline

import com.github.sanity.pav.toArrayList
import java.lang.Math.pow
import java.lang.Math.sqrt
import java.util.*

/**
 * An implementation of the Fritsch-Carlson algorithm for interpolant selection described by
 * [Monotone cubic interpolation](https://en.wikipedia.org/wiki/Monotone_cubic_interpolation).
 **/
class FritschCarlsonTangentStrategy : TangentStrategy {
    /*
     * The code is intended to model the description on the Wikipedia description closely (as of 06:26, 14 May 2016),
     * right down to the use of greek letters.
     *
     * I'm sure there are more elegant ways to implement the algorithm, it's not at all functional, but this seemed like
     * the best way to ensure correctness without me having an intuitive understanding of the algorithm, which
     * I haven't bothered to do.
     *
     * - [Ian](https://github.com/sanity)
     */

    override fun compute(points: Iterable<PointWithSecants>): ArrayList<PointWithTangents> {
        val tangents = step2(points)
        return steps3to5(tangents)

    }

    internal fun step2(points: Iterable<PointWithSecants>): ArrayList<PointWithTangents> {
        return points.map {
            val tangent: Double = when {
                it.secantBefore != null && it.secantAfter != null -> (it.secantBefore.slope + it.secantAfter.slope) / 2.0
                it.secantBefore != null -> it.secantBefore.slope
                else -> it.secantAfter!!.slope // !! shouldn't be necessary, but smartcasting not smart enough yet
            }
            PointWithTangents(it, tangent)
        }.toArrayList()
    }

    internal fun steps3to5(pointsWithTangents: List<PointWithTangents>): ArrayList<PointWithTangents> {
        val m = pointsWithTangents.map { it.tangent }.toDoubleArray()
        val Δ = pointsWithTangents.map { it.pointWithSecants.secantAfter }.filterNotNull().map { it.slope }.toDoubleArray()
        val ignoreSteps4and5 = step3(m, Δ)
        val α = ArrayList<Double>()
        val β = ArrayList<Double>()
        for (k in 0..(m.size - 2)) {
            if (!ignoreSteps4and5.contains(k)) {
                α[k] = m[k] / Δ[k]
                β[k] = m[k + 1] / Δ[k]
                step4(k, α, β)
                step5(k, m, Δ, α, β)
            }
        }
        return pointsWithTangents.mapIndexed { k, pointWithTangents -> pointWithTangents.copy(tangent = m[k]) }.toArrayList()
    }

    private fun step5(k: Int, m: DoubleArray, Δ: DoubleArray, α: ArrayList<Double>, β: ArrayList<Double>) {
        val vectorMagnitude = sqrt(pow(α[k], 2.0) + pow(β[k], 2.0))
        if (vectorMagnitude > 3.0) {
            val Γk = 3.0 / vectorMagnitude
            m[k] = Γk * α[k] * Δ[k]
            m[k + 1] = Γk * β[k] * Δ[k]
        }
    }

    private fun step4(k: Int, α: ArrayList<Double>, β: ArrayList<Double>) {
        if ((α[k] < 0.0) || β[k] < 0.0) {
            throw IllegalArgumentException("Input datapoints are not strictly monotone")
        }
    }

    internal fun step3(m: DoubleArray, Δ: DoubleArray): Set<Int> {
        val ignoreSteps4and5 = HashSet<Int>()
        for (k in 1..(m.size - 2)) {
            val prevAndCurrentDeltasHaveDifferentSign = (Δ[k - 1] < 0.0 && Δ[k] >= 0.0) || (Δ[k - 1] > 0.0 && Δ[k] <= 0.0)
            if (prevAndCurrentDeltasHaveDifferentSign) {
                m[k] = 0.0
            }
            ignoreSteps4and5.add(k - 1)
            ignoreSteps4and5.add(k)
        }
        return ignoreSteps4and5
    }
}