package the.kis.devs.server.capeapi

import the.kis.devs.server.capeapi.cape.Cape
import the.kis.devs.server.capeapi.cape.Capes
import the.kis.devs.server.keyauth.KeyAuthApp
import the.kis.devs.server.sendMessage

/**
 * @author _kisman_
 * @since 10:06 of 01.11.2022
 */
object CapeAPIManager {
    val capes = ArrayList<Cape>()

    fun getLatestCape() : Capes? {
        return try {
            when (KeyAuthApp.keyAuth.userData?.subscription) {
                "1" -> Capes.Release
                "2" -> Capes.Beta
                "3" -> Capes.Developer
                else -> null
            }
        } catch(e : Exception) {
            null
        }
    }

    fun syncCapes() {
        sendMessage("cape clear")

        for(cape in capes) {
            sendMessage("cape add ${cape.name.name0} ${cape.uuid}")
        }
    }
}