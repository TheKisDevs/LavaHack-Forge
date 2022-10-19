package com.kisman.cc.features.hud.modules

import com.google.gson.JsonParser
import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.util.MultiThreaddableModulePattern
import com.kisman.cc.util.Colour
import com.kisman.cc.util.math.max
import com.kisman.cc.util.render.ColorUtils
import com.kisman.cc.util.render.customfont.CustomFontUtil
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.BufferedReader

import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


/**
 * @author _kisman_
 * @since 1:36 of 19.08.2022
 */
class TwoBeeTwoTeeQueue : HudModule(
    "2b2tQueue",
    "Shows regular/prio queues",
    true
) {
    private val offsets = register(Setting("Offsets", this, 2.0, 0.0, 5.0, true))
    private val astolfo = register(Setting("Astolfo", this, true))
    private val color = register(Setting("Color", this, Colour(255, 255, 255, 255)))

    private val regular = register(Setting("Regular", this, true))
    private val prio = register(Setting("Prio", this, true))

    private val threads = MultiThreaddableModulePattern(this).init().also { it.multiThread.valBoolean = true }

    private var regularValue = "Updating regular queue info"
    private var prioValue = "Updating prio queue info"

    override fun onEnable() {
        super.onEnable()
        threads.reset()
    }

    @SubscribeEvent fun onRender(event : RenderGameOverlayEvent.Text) {
        threads.update(Runnable {
            regularValue = parseJson(URL("https://2bqueue.info/*"), "regular")
            prioValue = parseJson(URL("https://2bqueue.info/*"), "prio")
        })

        var height = 0
        var width = 0

        if(regular.valBoolean) {
            CustomFontUtil.drawStringWithShadow(
                "Regular: ${TextFormatting.GRAY}$regularValue",
                getX(),
                getY(),
                if(astolfo.valBoolean) ColorUtils.astolfoColors(100, 100) else color.colour.rgb
            )

            height += CustomFontUtil.getFontHeight() + offsets.valInt
            width = CustomFontUtil.getStringWidth("Regular: $regularValue")
        }

        if(prio.valBoolean) {
            CustomFontUtil.drawStringWithShadow(
                "Prio: ${TextFormatting.GRAY}$prioValue",
                getX(),
                getY() + height,
                if(astolfo.valBoolean) ColorUtils.astolfoColors(100, 100) else color.colour.rgb
            )

            height += CustomFontUtil.getFontHeight()
            width = width.max(CustomFontUtil.getStringWidth("Prio: $prioValue"))
        }

        setW(width.toDouble())
        setH(height.toDouble())
    }

    @Throws(IOException::class)
    fun parseJson(
        url : URL,
        params : String
    ) : String {
        val request = url.openConnection() as HttpURLConnection
        request.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)")
        request.connect()
        val jp = JsonParser() // GSON
        val root = jp.parse(BufferedReader(InputStreamReader(request.inputStream)))
        val `object` = root.asJsonObject
        return `object`.get(params).asString
    }
}