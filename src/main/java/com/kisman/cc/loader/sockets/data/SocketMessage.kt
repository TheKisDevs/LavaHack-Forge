/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package com.kisman.cc.loader.sockets.data

import com.kisman.cc.loader.sockets.utils.ConvertUtils
import java.io.File

@Suppress("unused")
class SocketMessage(var byteArray: ByteArray) {
    var text
        get() = ConvertUtils.toText(byteArray)
        set(value) {
            byteArray = ConvertUtils.textToByteArray(value!!)
        }

    var file
        get() = ConvertUtils.toFile(byteArray)
        set(value) {
            byteArray = value!!.data
        }

    val type
        get() = when {
            ConvertUtils.isFile(byteArray) -> Type.File
            ConvertUtils.isText(byteArray) -> Type.Text
            else -> Type.Bytes
        }

    constructor(text: String) : this(ConvertUtils.textToByteArray(text))
    constructor(file: SocketFile) : this(file.data)
    constructor(file: File) : this(SocketFile(file))

    enum class Type {
        Text, File, Bytes
    }
}