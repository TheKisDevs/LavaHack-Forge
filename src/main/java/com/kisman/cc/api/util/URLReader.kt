package com.kisman.cc.api.util

import com.kisman.cc.api.util.exception.URLReaderException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

/**
 * @author _kisman_
 * @since 11:57 of 15.05.2022
 */
class URLReader(
        val url : URL
) {
    constructor(url : String) : this(URL(url))

    fun get() : ArrayList<String> {
        val list : ArrayList<String> = ArrayList()
        try {
            val reader = BufferedReader(InputStreamReader(url.openStream()))
            var line: String
            while (reader.readLine().also { line = it } != null) {
                list += line
            }
        } catch(e : Exception) {
//            throw URLReaderException("URL: $url")
        }

        return list
    }
}