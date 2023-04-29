package com.kisman.cc.features.hud.modules

import com.kisman.cc.Kisman
import com.kisman.cc.features.hud.HudModule
import com.kisman.cc.util.client.interfaces.IBindable
import com.kisman.cc.settings.Setting
import com.kisman.cc.settings.types.SettingArray
import com.kisman.cc.settings.types.SettingGroup
import com.kisman.cc.util.Colour
import com.kisman.cc.util.client.collections.Sorter
import com.kisman.cc.util.render.customfont.CustomFontUtil
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class BindList : HudModule(
        "BindList",
        "Bind list like Abyss",
        true
) {
    private val sorter = Sorter<Element> { it.text }

    private val offsets = register(Setting("Offsets", this, 2.0, 0.0, 10.0, true))

    private val types = register(SettingGroup(Setting("Types", this)))
    private val modules = register(types.add(Setting("Modules", this, true)))
    private val hudModules = register(types.add(Setting("Hud Modules", this, false)))
    private val checkBoxes = register(types.add(Setting("Check Boxes", this, true)))

    private val colorG = register(SettingGroup(Setting("Colors", this)))
    private val colorActive = register(colorG.add(Setting("Active Color", this, "Active Color", Colour(0, 255, 0, 255))))
    private val colorInactive = register(colorG.add(Setting("Inactive Color", this, "Inactive Color", Colour(255, 0, 0, 255))))

    private val sort = register(SettingArray("Sort", this, sorter.length(), sorter.array()))

    @SubscribeEvent fun onRender(event : RenderGameOverlayEvent.Text) {
        val x = getX()
        val y = getY()

        val list : ArrayList<Element> = ArrayList()

        if(modules.valBoolean) {
            for (module in Kisman.instance.moduleManager.modules) {
                if (IBindable.valid(module)) {
                    list += Element("${module.displayName} [${IBindable.getName(module)}]", module.isToggled)
                }
            }
        }

        if(hudModules.valBoolean) {
            for (module in Kisman.instance.hudModuleManager.modules) {
                if (IBindable.valid(module)) {
                    list += Element("${module.displayName} [${IBindable.getName(module)}]", module.isToggled)
                }
            }
        }

        if(checkBoxes.valBoolean) {
            for (setting in Kisman.instance.settingsManager.settings) {
                if (IBindable.valid(setting) && setting.isCheck) {
                    list += Element(
                        "${setting.toDisplayString()} [${IBindable.getName(setting)}]",
                        setting.valBoolean
                    )
                }
            }
        }

        list.sortWith(sort.valElement.comparator)

        for((count, element) in list.withIndex()) {
            CustomFontUtil.drawStringWithShadow(
                    element.text,
                    x,
                    y + count * (CustomFontUtil.getFontHeight() + offsets.valInt),
                    (if(element.state) colorActive.colour.rgb else colorInactive.colour.rgb)
            )
        }

        setW(if(list.isNotEmpty()) CustomFontUtil.getStringWidth(list[0].text).toDouble() else 0.0)
        setH(list.size.toDouble() * (CustomFontUtil.getFontHeight().toDouble() + offsets.valInt))
    }

    class Element(
            val text : String,
            val state : Boolean
    )
}