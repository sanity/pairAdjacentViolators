package com.trystacks.pav

import java.util.*

/**
 * Created by ian on 9/24/16.
 */

internal fun <V> List<V>.toMutableChain(): MutableChain<V> {
    if (this.isEmpty()) {
        throw IllegalArgumentException("Cannot create MutableChain with empty list")
    } else {
        val mcArray = this.map { MutableChain(it) }.toTypedArray()
        for (i in mcArray.indices) {
            if (i > 0) {
                mcArray[i - 1].next = mcArray[i]
                mcArray[i].previous = mcArray[i - 1]
            }
        }
        return mcArray[0]
    }
}

/*
 Before replacement:
        p   n
    A   B   C   D
          |

 After replacement:
    p   n
    A   X   D
      |
 */

internal data class MutableChain<V>(var value: V, var previous: MutableChain<V>? = null, var next: MutableChain<V>? = null) {
    class Cursor<V>(private val after: MutableChain<V>) {
        val previousValue: V?
            get() = after.previous?.value
        val nextValue: V
            get() = after.value
    }

    fun iterate(handler: (Cursor<V>) -> V?) {
        var afterCursor: MutableChain<V>? = this
        while (afterCursor != null) {
            val cursor = Cursor<V>(afterCursor)
            val replacementValue = handler(cursor)
            if (replacementValue != null) {
                val C = afterCursor
                val B = afterCursor.previous
                val D = C.next
                if (B == null) throw IllegalArgumentException("Cannot replace values at start of chain")
                B.value = replacementValue; val X = B;
                X.next = D; if (D != null) D.previous = X
                afterCursor = X
            } else {
                afterCursor = afterCursor.next
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

    internal fun checkChainIntegrity() : Boolean {
        var mc: MutableChain<V>? = this
        while (mc != null) {
            val previous = mc.previous
            if (previous != null && previous.next != mc) {
                return false
            }
            mc = mc.next
        }
        return true
    }

    override fun toString() = "MC[$value]"
}



