package com.kisman.cc.util.client.collections

import com.kisman.cc.util.render.customfont.CustomFontUtil

/**
 * @author _kisman_
 * @since 10:05 of 23.02.2023
 */
class Sorter<T>(
    private val stringGetter : (T) -> String
) {
    val length : Comparator<T> = Comparator.comparingInt<T> { CustomFontUtil.getStringWidth(stringGetter(it)) }.reversed()
    val alphabet : Comparator<T> = Comparator.comparing<T, String> { stringGetter(it) }

    fun length() : SorterEntry<T> = SorterEntry("Length", length)
    fun alphabet() : SorterEntry<T> = SorterEntry("Alphabet", alphabet)

    fun array() : List<SorterEntry<T>> = listOf(
        length(),
        alphabet()
    )
}

class SorterEntry<T>(
    private val name : String,
    val comparator : Comparator<T>
) {
    override fun toString() : String = name
}