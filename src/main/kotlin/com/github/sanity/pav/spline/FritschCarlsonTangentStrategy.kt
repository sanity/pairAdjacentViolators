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
        val tangents = initTangentsToSecantAverages(points)
        return updateTangentsToEnsureMonotonicity(tangents)

    }

    internal fun initTangentsToSecantAverages(points: Iterable<PointWithSecants>): ArrayList<PointWithTangents> {
        // See steps 1 and 2
        return points.map {
            /* Wikipedia says that if secant signs differ then set tangent to zero, but how could secant
             * signs differ if input points are monotonic?
             */
            val tangent: Double =
                    when {
                it.secantBefore != null && it.secantAfter != null -> (it.secantBefore.slope + it.secantAfter.slope) / 2.0
                it.secantBefore != null -> it.secantBefore.slope
                else -> it.secantAfter!!.slope // !! shouldn't be necessary, but smartcasting not smart enough yet
            }
            PointWithTangents(it, tangent)
        }.toArrayList()
    }

    internal fun updateTangentsToEnsureMonotonicity(pointsWithTangents: List<PointWithTangents>): ArrayList<PointWithTangents> {
        // See steps 3 to 5
        val m = pointsWithTangents.map { it.tangent }.toArrayList()
        val Δ = pointsWithTangents.map { it.pointWithSecants.secantAfter }.filterNotNull().map { it.slope }.toArrayList()
        val y = pointsWithTangents.map { it.y }.toArrayList()
        val ignoreSteps4and5 = setTangentsTo0WhenTwoSuccessiveYValuesAreTheSame(m, y)
        val α = ArrayList<Double>()
        val β = ArrayList<Double>()
        for (k in 0..(m.size - 2)) {
            if (!ignoreSteps4and5.contains(k)) {
                α[k] = m[k] / Δ[k]
                β[k] = m[k + 1] / Δ[k]
                ensureInputDataPointsAreStrictlyMonotone(k, α, β)
                ensureTangentsMeetConstraints(k, m, Δ, α, β)
            }
        }
        return pointsWithTangents.mapIndexed { k, pointWithTangents -> pointWithTangents.copy(tangent = m[k]) }.toArrayList()
    }

    internal fun setTangentsTo0WhenTwoSuccessiveYValuesAreTheSame(m: ArrayList<Double>, y: ArrayList<Double>): Set<Int> {
        // See Step 3
        val skipNextSteps = HashSet<Int>()
        for (k in 0..(m.size - 2)) {
            if (y[k] == y[k + 1]) {
                m[k] = 0.0
                skipNextSteps.add(k)
                m[k + 1] = 0.0
                skipNextSteps.add(k + 1)
            }
        }
        return skipNextSteps
    }


    internal fun ensureTangentsMeetConstraints(k: Int, m: ArrayList<Double>, Δ: ArrayList<Double>, α: ArrayList<Double>, β: ArrayList<Double>) {
        // See Step 5
        val vectorMagnitude = sqrt(pow(α[k], 2.0) + pow(β[k], 2.0))
        if (vectorMagnitude > 3.0) {
            val Γk = 3.0 / vectorMagnitude
            m[k] = Γk * α[k] * Δ[k]
            m[k + 1] = Γk * β[k] * Δ[k]
        }
    }

    internal fun ensureInputDataPointsAreStrictlyMonotone(k: Int, α: ArrayList<Double>, β: ArrayList<Double>) {
        if ((α[k] < 0.0) || β[k] < 0.0) {
            throw IllegalArgumentException("Input datapoints are not strictly monotone")
        }
    }

}