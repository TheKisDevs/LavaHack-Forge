package com.kisman.cc.features.hud.modules

import com.kisman.cc.Kisman
import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.render.customfont.CustomFontUtil
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard

class BindList : HudModule(
        "BindList",
        "Bind list like Abyss",
        true
) {
    private val offsets = register(Setting("Offsets", this, 2.0, 0.0, 10.0, true))

    private val colorG = register(SettingGroup(Setting("Colors", this)))
    private val colorActive = register(colorG.add(Setting("Active Color", this, "Active Color", Colour(0, 255, 0, 255))))
    private val colorInactive = register(colorG.add(Setting("Inactive Color", this, "Inactive Color", Colour(255, 0, 0, 255))))

    @SubscribeEvent fun onRender(event : RenderGameOverlayEvent.Text) {
        val x = x
        val y = y

        val list : ArrayList<Element> = ArrayList()

        for(module in Kisman.instance.moduleManager.modules) {
            if(module.key != Keyboard.KEY_NONE && module.key != Keyboard.KEY_ESCAPE) {
                list += Element("${module.name} [${Keyboard.getKeyName(module.key)}]", module.isToggled)
            }
        }

        for(setting in Kisman.instance.settingsManager.settings) {
            if(setting.key != Keyboard.KEY_NONE && setting.isCheck) {
                list += Element("${setting.parentMod.name}->${setting.name} [${Keyboard.getKeyName(setting.key)}]", setting.valBoolean)
            }
        }

        val comparator = Comparator { first: Element, second: Element ->
            val dif = (CustomFontUtil.getStringWidth(second.text) - CustomFontUtil.getStringWidth(first.text)).toFloat()
            if (dif != 0f) dif.toInt() else second.text.compareTo(first.text)
        }

        list.sortWith(comparator)

        for((count, element) in list.withIndex()) {
            CustomFontUtil.drawStringWithShadow(
                    element.text,
                    x,
                    y + count * (CustomFontUtil.getFontHeight() + offsets.valInt),
                    (if(element.state) colorActive.colour.rgb else colorInactive.colour.rgb)
            )
        }

        w = CustomFontUtil.getStringWidth(list[0].text).toDouble()
        h = list.size.toDouble() * (CustomFontUtil.getFontHeight()
            .toDouble() + 2.0)
    }

    class Element(
            val text : String,
            val state : Boolean
    )
}