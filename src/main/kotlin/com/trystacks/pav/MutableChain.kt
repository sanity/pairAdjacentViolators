package com.trystacks.pav

import java.util.*

/**
 * Created by ian on 9/24/16.
 */

internal fun <V> List<V>.toMutableChain(): MutableChain<V> {
    if (this.isEmpty()) {
        throw IllegalArgumentException("Cannot create MutableChain with empty list")
    } else {
        var prevMC: MutableChain<V>? = null
        for (ix in this.size - 1 downTo 0) {
            prevMC = MutableChain(this[ix], prevMC)
        }
        return prevMC!!
    }
}

internal data class MutableChain<V>(var value: V, var next: MutableChain<V>?) {
    class Cursor<V>(private val previous: MutableChain<V>?, private val next: MutableChain<V>) {
        val previousValue: V?
            get() = previous?.value
        val nextValue: V
            get() = next.value
    }

    fun iterate(handler: (Cursor<V>) -> V?) {
        var previous: MutableChain<V>? = null
        var next: MutableChain<V>? = this
        while (next != null) {
            val cursor = Cursor<V>(previous, next)
            val replacementValue = handler(cursor)
            if (replacementValue != null) {
                if (previous == null) {
                    throw IllegalArgumentException("Cannot replace values at start of chain")
                } else {
                    previous.value = replacementValue
                    previous.next = next.next
                }
                next = next.next
            } else {
                previous = next
                next = next.next
            }

        }
    }

    fun toList(): List<V> {
        val arrayList = ArrayList<V>()
        iterate {
            arrayList.add(it.nextValue)
            null
        }
        return arrayList
    }

    override fun toString() = "MC[$value]"
}



