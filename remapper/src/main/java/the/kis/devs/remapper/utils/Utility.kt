package the.kis.devs.remapper.utils

import java.net.MalformedURLException
import java.net.URI
import java.net.URL

/**
 * @author _kisman_
 * @since 14:51 of 14.08.2022
 */

fun contains(
    ch : Char,
    array : CharArray
) : Boolean {
    for (c in array) {
        if (ch == c) {
            return true
        }
    }
    return false
}

fun toUrl(url : String) : URL? {
    return try {
        URL(url)
    } catch (e : MalformedURLException) {
        throw RuntimeException(e)
    }
}

fun toUrl(uri : URI) : URL? {
    return try {
        uri.toURL()
    } catch (e : MalformedURLException) {
        throw RuntimeException(e)
    }
}