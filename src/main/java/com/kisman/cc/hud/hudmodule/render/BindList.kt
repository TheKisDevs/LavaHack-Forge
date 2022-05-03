package com.kisman.cc.hud.hudmodule.render

import com.kisman.cc.Kisman
import com.kisman.cc.hud.hudmodule.HudCategory
import com.kisman.cc.hud.hudmodule.HudModule
import com.kisman.cc.module.Module
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

        val list : ArrayList<Module> = ArrayList()

        for(module in Kisman.instance.moduleManager.modules) {
            if(module.key != Keyboard.KEY_NONE && module.key != Keyboard.KEY_ESCAPE) {
                list += module
            }
        }

        val comparator = Comparator { first: Module, second: Module ->
            val firstName = "${first.name} [${Keyboard.getKeyName(first.key)}]"
            val secondName = "${second.name} [${Keyboard.getKeyName(second.key)}]"
            val dif = (CustomFontUtil.getStringWidth(secondName) - CustomFontUtil.getStringWidth(firstName)).toFloat()
            if (dif != 0f) dif.toInt() else secondName.compareTo(firstName)
        }

        list.sortWith(comparator)

        for((count, module) in list.withIndex()) {
            CustomFontUtil.drawStringWithShadow(
                    "${
                        if(module.isToggled) TextFormatting.GREEN
                        else TextFormatting.RED
                    }${module.name} [${Keyboard.getKeyName(module.key)}]",
                    x,
                    y + count * (CustomFontUtil.getFontHeight() + 2),
                    -1
            )
        }

        w = CustomFontUtil.getStringWidth("${list[0].name} [${Keyboard.getKeyName(list[0].key)}]").toDouble()
        h = list.size.toDouble() * (CustomFontUtil.getFontHeight().toDouble() + 2.0)
    }
}