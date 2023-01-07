/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package the.kis.devs.server.util

object Constants {
    /**
     * Prefix to be used when transferring the text.
     *
     * **To work correctly, it must be the same, both on the client side and on the server side.**
     */
    const val TEXT_PREFIX = "ITEXT"

    /**
     * Prefix to be used when transferring the file.
     *
     * **To work correctly, it must be the same, both on the client side and on the server side.**
     */
    const val FILE_PREFIX = "IFILE"

    /**
     * Requested maximum length of the queue of server incoming connections.
     */
    const val SERVER_BACKLOG = 50

    /**
     * The **maximum** size of a single **TCP packet**.
     * When changing, errors may occur in the work of this library.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Transmission_Control_Protocol">TCP wikipedia page</a>
     */
    const val MAX_PACKET_SIZE = 65535
}