/*
 * Copyright (c) 2022. ya-ilya, _kisman_
 */

package me.yailya.sockets.utils

import me.yailya.sockets.Constants
import me.yailya.sockets.data.SocketFile

@Suppress("MemberVisibilityCanBePrivate")
object ConvertUtils {
    private val fileRegex = "${Constants.FILE_PREFIX}:(.*?):${Constants.FILE_PREFIX}".toRegex()
    private val textRegex = "${Constants.TEXT_PREFIX}:${Constants.TEXT_PREFIX}".toRegex()

    /**
     * Checks if [byteArray] is a file
     */
    fun isFile(byteArray: ByteArray): Boolean {
        return byteArray.toString(Charsets.UTF_8).contains(fileRegex)
    }

    /**
     * Checks if [byteArray] is a text
     */
    fun isText(byteArray: ByteArray): Boolean {
        return byteArray.toString(Charsets.UTF_8).contains(textRegex)
    }

    /**
     * If [byteArray] is a valid file, convert it to a file
     *
     * @see isFile
     */
    fun toFile(byteArray: ByteArray): SocketFile? {
        try {
            if (!isFile(byteArray)) return null

            val (all, info) = fileRegex.find(byteArray.toString(Charsets.UTF_8))?.groupValues ?: return null
            val infoSplit = info.split(":")
            if (infoSplit.size != 2) return null
            val data = byteArray.sliceArray(all.toByteArray().size..byteArray.lastIndex)

            return SocketFile(infoSplit[0], data, infoSplit[1])
        } catch (ex: Exception) {
            ex.printStackTrace()

            return null
        }
    }

    /**
     * If [byteArray] is a valid text, convert it to a text
     *
     * @see isText
     */
    fun toText(byteArray: ByteArray): String? {
        try {
            if (!isText(byteArray)) return null

            val (all) = textRegex.find(byteArray.toString(Charsets.UTF_8))?.groupValues ?: return null

            return byteArray.toList().subList(all.toByteArray().size, byteArray.size).toByteArray()
                .toString(Charsets.UTF_8)
        } catch (ex: Exception) {
            ex.printStackTrace()

            return null
        }
    }

    /**
     * Converts text to ByteArray
     */
    fun textToByteArray(text: String): ByteArray {
        return textRegex.toString().toByteArray() + text.toByteArray()
    }
}