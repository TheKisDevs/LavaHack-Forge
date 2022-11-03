package com.kisman.cc.util.collections

import java.util.*

/**
 * @author _kisman_
 * @since 15:48 of 03.11.2022
 */
class LimitedSortedMap<K, V>(
    private val maxSize : Int
) : TreeMap<K, V>() {
    override fun put(
        key : K,
        value : V
    ) : V? {
        val v = super.put(key, value)

        if(size > maxSize) {
            val firstToRemove = keys.toList()[maxSize]

            for(entry in tailMap(firstToRemove)) {
                remove(entry.key)
            }
        }

        return v
    }

    override fun putAll(
        from : Map<out K, V>
    ) {
        super.putAll(from)

        if(size > maxSize) {
            val firstToRemove = keys.toList()[maxSize]

            for(entry in tailMap(firstToRemove)) {
                remove(entry.key)
            }
        }
    }
}