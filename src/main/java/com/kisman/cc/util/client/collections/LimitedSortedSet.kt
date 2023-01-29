package com.kisman.cc.util.client.collections

import java.util.*

/**
 * @author _kisman_
 * @since 15:24 of 03.11.2022
 */
class LimitedSortedSet<E>(
    private val maxSize : Int
) : TreeSet<E>() {
    override fun add(
        element : E
    ) : Boolean {
        val added = super.add(element)

        if(size > maxSize) {
            val firstToRemove = toArray()[maxSize] as E

            removeAll(tailSet(firstToRemove))
        }

        return added
    }

    override fun addAll(
        elements : Collection<E>
    ) : Boolean {
        val added = super.addAll(elements)

        if(size > maxSize) {
            val firstToRemove = toArray()[maxSize] as E

            removeAll(tailSet(firstToRemove))
        }

        return added
    }
}