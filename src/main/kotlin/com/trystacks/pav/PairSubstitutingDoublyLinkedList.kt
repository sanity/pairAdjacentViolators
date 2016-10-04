package com.trystacks.pav

import java.util.*

/**
 * A doubly-linked-list with functionality that is particularly useful for implementing the
 * pool-adjacent violators algorithm.  Specifically, supports iterating through the list while replacing
 * pairs of elements in the list.
 */

internal fun <V> List<V>.toPairSubstitutingDoublyLinkedList(): PairSubstitutingDoublyLinkedList<V> {
    if (this.isEmpty()) {
        throw IllegalArgumentException("Cannot create PairSubstitutingDoublyLinkedList with empty list")
    } else {
        val mcArray = this.map { PairSubstitutingDoublyLinkedList(it) }.toTypedArray()
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

internal data class PairSubstitutingDoublyLinkedList<V>(var value: V, var previous: PairSubstitutingDoublyLinkedList<V>? = null, var next: PairSubstitutingDoublyLinkedList<V>? = null) {
    class Cursor<V>(private val after: PairSubstitutingDoublyLinkedList<V>) {
        val previousValue: V?
            get() = after.previous?.value
        val nextValue: V
            get() = after.value
    }

    /**
     * Iterate through the list.  The cursor is positioned between two elements unless at the first element.  The
     * cursor can be used to retrieve the element before or after the cursor.
     *
     * The handler may optionally return a value, if it does this value will replace the elements before and after the cursor,
     * and the cursor will be repositioned before the new replacement element.
     */
    fun iterate(handler: (Cursor<V>) -> V?) {
        var afterCursor: PairSubstitutingDoublyLinkedList<V>? = this
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

    /**
     * Transform the PairSubstitutingDoublyLinkedList into a List
     */
    fun toList(): List<V> {
        val arrayList = ArrayList<V>()
        iterate {
            arrayList.add(it.nextValue)
            null
        }
        return arrayList
    }

    /**
     * Check that the bi-directonal links are consistent, this is used for testing purposes
     * and should never return anything but *true*
     */
    internal fun checkListIntegrity() : Boolean {
        var mc: PairSubstitutingDoublyLinkedList<V>? = this
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



