package com.github.sanity.pav

import io.kotlintest.specs.FreeSpec
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Created by musachev on 20.12.2016.
 */

class SerializeSpec : FreeSpec() {
    init {
        val point = Point(1.0, 42.0)
        val pav = PairAdjacentViolators(listOf(
                point,
                Point(point.y, point.x))
        )

        "serializable Point" - {
            val savedPoint = save(point, "point")
            val loadedPoint = open("point") as Point

            savedPoint.x shouldBe exactly(loadedPoint.x)
            savedPoint.y shouldBe exactly(loadedPoint.y)
            savedPoint.weight shouldBe exactly(loadedPoint.weight)
        }

        "serializable PairAdjacentViolators" - {
            val savedPav = save(pav, "pav")
            val loadedPav = open("pav") as PairAdjacentViolators

            savedPav.isotonicPoints shouldEqual loadedPav.isotonicPoints
        }
    }
}

fun open(path: String) : Any // todo: object type from context
{
    print("opening \"$path\" : ")

    val fin = FileInputStream(path)
    val obj = ObjectInputStream(fin).readObject()
    fin.close()

    println("complete")

    return obj
}

fun <T> save(obj: T, path: String) : T
{
    print("saving \"$path\" : ")

    val fout = FileOutputStream(path)
    ObjectOutputStream(fout).writeObject(obj)
    fout.close()

    println("complete")

    return obj
}