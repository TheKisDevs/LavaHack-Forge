/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package com.kisman.cc.loader.websockets.data

import com.kisman.cc.loader.websockets.util.Constants
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class SocketFile(
    val name : String,
    val byteArray : ByteArray,
    val description : String
) {
    val data get() = "${Constants.FILE_PREFIX}:$name:$description:${Constants.FILE_PREFIX}".toByteArray() + byteArray

    constructor(
        file : File,
        description : String = ""
    ) : this(
        file.name,
        file.readBytes(),
        description
    )
}