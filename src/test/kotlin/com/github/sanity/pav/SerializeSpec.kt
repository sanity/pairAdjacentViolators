package com.github.sanity.pav

import io.kotlintest.matchers.*
import io.kotlintest.specs.FreeSpec
import java.io.*

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

        "Point should be serializable and deserizable" {
            val serializedPoint = serialize(point, "point")
            val deserializedPoint: Point = deserialize(serializedPoint)

            deserializedPoint.x shouldBe exactly(point.x)
            deserializedPoint.y shouldBe exactly(point.y)
            deserializedPoint.weight shouldBe exactly(point.weight)
        }

        "PairAdjacentViolators should be serializable and deserizable" {
            val serializedPAV = serialize(pav, "pav")
            val deserializedPAV: PairAdjacentViolators = deserialize(serializedPAV)

            deserializedPAV.isotonicPoints shouldEqual pav.isotonicPoints
        }
    }
}

private fun <T> deserialize(byteArray: ByteArray): T {
    val bais = ByteArrayInputStream(byteArray)
    val obj = ObjectInputStream(bais).readObject()
    bais.close()

    return obj as T
}

private fun <T> serialize(obj: T, path: String): ByteArray {
    val baos = ByteArrayOutputStream()
    ObjectOutputStream(baos).writeObject(obj)
    baos.close()
    return baos.toByteArray()
}