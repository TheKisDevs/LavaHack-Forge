package com.kisman.cc.websockets.api.enums

/**
 * Enum which contains the different valid opcodes
 */
enum class Opcode {
    CONTINUOUS, TEXT, BINARY, PING, PONG, CLOSING // more to come
}