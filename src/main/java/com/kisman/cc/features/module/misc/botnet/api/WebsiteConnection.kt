package com.kisman.cc.features.module.misc.botnet.api

import com.kisman.cc.util.chat.other.ChatUtils
import org.jsoup.Jsoup

class WebsiteConnection(private var input_url: String) {
    fun checkConnection(): Boolean {
        return try {
            var test = getInput()
            ChatUtils.message("Joined the botnet successfully!")
            true
        } catch (e: Exception) {
            ChatUtils.message("Error joining botnet!")
            false
        }
    }


    fun getInput() : String {
        return try {
            Jsoup.connect(input_url).get().selectFirst("p").text()

        } catch (e: Exception) {
            ""
        }
    }
}