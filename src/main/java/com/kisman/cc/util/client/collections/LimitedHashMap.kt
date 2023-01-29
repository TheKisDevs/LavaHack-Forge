package com.kisman.cc.util.client.collections

/**
 * @author _kisman_
 * @since 11:55 of 02.01.2023
 */
class LimitedHashMap<K, V>(
    private val maxSize : Int
) : LinkedHashMap<K, V>() {
    override fun removeEldestEntry(
        eldest : MutableMap.MutableEntry<K, V>
    ) : Boolean = size > maxSize
}