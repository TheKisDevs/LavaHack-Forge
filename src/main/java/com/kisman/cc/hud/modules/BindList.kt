package com.kisman.cc.hud.modules

import com.kisman.cc.Kisman
import com.kisman.cc.hud.HudCategory
import com.kisman.cc.hud.HudModule
import com.kisman.cc.util.customfont.CustomFontUtil
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard

class BindList : HudModule(
        "BindList",
        "Bind list like Abyss",
        HudCategory.RENDER,
        true
) {
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
                    "${
                        if(element.state) TextFormatting.GREEN
                        else TextFormatting.RED
                    }${element.text}",
                    x,
                    y + count * (CustomFontUtil.getFontHeight() + 2),
                    -1
            )
        }

        w = CustomFontUtil.getStringWidth(list[0].text).toDouble()
        h = list.size.toDouble() * (CustomFontUtil.getFontHeight().toDouble() + 2.0)
    }

    class Element(
            val text : String,
            val state : Boolean
    )
}