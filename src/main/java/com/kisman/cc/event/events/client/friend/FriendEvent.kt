package com.kisman.cc.event.events.client.friend

/**
 * @author _kisman_
 * @since 23:07 of 19.08.2022
 */
class FriendEvent(
    val name : String,
    val type : Type
) {
    enum class Type {
        Add, Remove
    }
}