package the.kis.devs.server.util.collections

import java.util.concurrent.ConcurrentHashMap

/**
 * @author _kisman_
 * @since 14:28 of 20.11.2022
 */
class ConcurrentList<E : Any> : ConcurrentHashMap<E, Any>() {
    fun add(
        element : E
    ) {
        put(element, true)
    }
}