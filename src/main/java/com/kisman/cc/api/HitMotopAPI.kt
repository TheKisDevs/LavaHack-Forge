package com.kisman.cc.api

import com.kisman.cc.api.util.URLReader
import org.jsoup.Jsoup
import java.net.URL

/**
 * Bad API
 *
 * @author _kisman_
 * @since 16:49 of 21.05.2022
 */
class HitMotopAPI(
    val url : String
) {
    private val page : ArrayList<String> = URLReader(URL(url)).get()

    fun getNameWithAuthor() {
        val html = Jsoup.parse(url)
//        val name = html.getElementById("DBdesktopBillboard").getElementsByClass("container").get
    }

    fun findName(): String {
        val index = findProperty("track__title")

        if (index == -1) {
            return "NULL"
        }

        val line = page[index + 1]

        return clean(line)
    }

    fun findAuthor() : String {
        val index = findProperty("track__desc")

        if(index == -1) {
            return "NULL"
        }

        val line = page[index]

        return line.substring(line.indexOf("track__desc") + 3 + 11, line.indexOf("<", line.indexOf("track__desc")))
    }

    private fun clean(string : String) : String {
        var toReturn = ""

        for(ch in string.toCharArray()) {
            if(ch != ' ') toReturn += ch
        }

        return toReturn
    }

    private fun findProperty(name : String) : Int { // <-- Index
        for((index, line) in page.withIndex()) {
            if(line.contains(name)) {
                return index
            }
        }
        return -1
    }

    companion object {
        fun getAPIByMP3Link(link : String) : HitMotopAPI? {
            if(!link.contains(".mp3")) {
                return null
            }
            if(!link.contains("hitmotop")) {
                return null
            }
            if(!link.contains("music/")) {
                return null
            }

            return HitMotopAPI("https://ru.hitmotop.com/song/${link.substring(link.lastIndexOf(" _ ") + 1, link.indexOf(".mp3"))}")
        }
    }

    enum class FindMode {
        First, Second
    }
}