package com.kisman.cc.websockets.api.enums

/**
 * Enum which represents the state a websocket may be in
 */
enum class ReadyState {
    NOT_YET_CONNECTED, OPEN, CLOSING, CLOSED
}