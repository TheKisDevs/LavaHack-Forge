@file:Suppress("UNCHECKED_CAST")

package the.kis.devs.discordbot.util

/**
 * @author _kisman_
 * @since 22:32 of 25.11.2022
 */

fun <T> getField(
    `object` : Any,
    name : String
) : T = `object`::class.java.getDeclaredField(name).also { it.isAccessible = true }.get(`object`) as T

fun <T> getStaticField(
    `object` : Any,
    name : String
) : T = `object`::class.java.getDeclaredField(name).also { it.isAccessible = true }.get(null) as T

fun setField(
    `object` : Any,
    name : String,
    value : Any?
) {
    `object`::class.java.getDeclaredField(name).also { it.isAccessible = true }.set(`object`, value)
}

fun setStaticField(
    `object` : Any,
    name : String,
    value : Any?
) {
    `object`::class.java.getDeclaredField(name).also { it.isAccessible = true }.set(null, value)
}