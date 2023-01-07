package com.kisman.cc.websockets.api.enums

/**
 * Enum which represents the states a handshake may be in
 */
enum class HandshakeState {
    /**
     * Handshake matched this Draft successfully
     */
    MATCHED,

    /**
     * Handshake is does not match this Draft
     */
    NOT_MATCHED
}