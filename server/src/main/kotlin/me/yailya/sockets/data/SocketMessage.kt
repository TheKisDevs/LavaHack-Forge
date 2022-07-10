/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.data

import me.yailya.sockets.utils.SocketFileUtils
import java.io.File

@Suppress("unused")
class SocketMessage(var byteArray: ByteArray) {
    var text
        get() = byteArray.toString(Charsets.UTF_8)
        set(value) {
            byteArray = value.toByteArray()
        }

    var file
        get() = SocketFileUtils.toFile(byteArray)
        set(value) {
            byteArray = value!!.data
        }

    val type
        get() = when {
            SocketFileUtils.isFile(byteArray) -> Type.File
            else -> Type.Text
        }

    constructor(text: String) : this(text.toByteArray())
    constructor(file: SocketFile) : this(file.data)
    constructor(file: File) : this(SocketFile(file))

    enum class Type {
        Text, File
    }
}