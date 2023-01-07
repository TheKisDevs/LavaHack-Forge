package com.kisman.cc.websockets.api.exceptions

/**
 * exception which indicates the frame payload is not sendable
 */
class NotSendableException : RuntimeException {
    /**
     * constructor for a NotSendableException
     *
     * @param s the detail message.
     */
    constructor(
        s : String
    ) : super(
        s
    )

    /**
     * constructor for a NotSendableException
     *
     * @param t the throwable causing this exception.
     */
    constructor(
        t : Throwable
    ) : super(
        t
    )

    /**
     * constructor for a NotSendableException
     *
     * @param s the detail message.
     * @param t the throwable causing this exception.
     */
    constructor(
        s : String,
        t : Throwable
    ) : super(
        s,
        t
    )

    companion object {
        /**
         * Serializable
         */
        private const val serialVersionUID = -6468967874576651628L
    }
}