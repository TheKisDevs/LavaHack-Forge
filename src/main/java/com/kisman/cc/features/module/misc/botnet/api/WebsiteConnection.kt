package com.kisman.cc.features.module.misc.botnet.api

import com.kisman.cc.util.chat.cubic.ChatUtility
import org.jsoup.Jsoup

class WebsiteConnection(private var input_url: String) {
    fun checkConnection(): Boolean {
        return try {
            var test = getInput()
            ChatUtility.message().printClientModuleMessage("Joined the botnet successfully!")
            true
        } catch (e: Exception) {
            ChatUtility.message().printClientModuleMessage("Error joining botnet!")
            false
        }
    }


    fun getURL(): String {
        return input_url
    }


    fun getInput() : String {
        return try {
            Jsoup.connect(input_url).get().selectFirst("p").text()

        } catch (e: Exception) {
            ""
        }
    }
}