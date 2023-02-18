package com.kisman.cc.gui.halq.components.sub.combobox

import com.kisman.cc.features.Binder
import com.kisman.cc.gui.api.SettingComponent
import com.kisman.cc.util.client.interfaces.IBindable
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.components.sub.ModeButton
import com.kisman.cc.settings.Setting
import com.kisman.cc.util.render.Render2DUtil
import java.util.function.BooleanSupplier

/**
 * @author _kisman_
 * @since 11:57 of 25.08.2022
 */
class OptionElement(
    private val combobox : ModeButton,
    private val binder : Binder,
    val name : String,
    val index : Int,
    val visible : BooleanSupplier,
    x : Int,
    y : Int,
    offset : Int,
    count : Int,
    layer : Int
) : ShaderableImplementation(
    x,
    y,
    count,
    offset,
    layer
), SettingComponent {
    override fun drawScreen(
        mouseX : Int,
        mouseY : Int
    ) {
        super.drawScreen(mouseX, mouseY)

        normalRender = Runnable {
            Render2DUtil.drawRectWH(
                x.toDouble(),
                y.toDouble(),
                width.toDouble(),
                height.toDouble(),
                HalqGui.backgroundColor.rgb
            )

            HalqGui.drawString(
                name,
                x,
                y,
                width,
                rawHeight
            )

            if (IBindable.valid(binder)) {
                HalqGui.drawSuffix(
                    IBindable.getName(binder),
                    name,
                    x.toDouble(),
                    y.toDouble(),
                    width.toDouble(),
                    rawHeight.toDouble(),
                    count,
                    3
                )
            }
        }
    }

    override fun mouseClicked(
        mouseX : Int,
        mouseY : Int,
        button : Int
    ) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            combobox.setting.index = index
            combobox.setting.valString = name
            combobox.selected = this
            combobox.open = false
        }
    }

    override fun visible() : Boolean = visible.asBoolean && combobox.selected != this
    override fun setting() : Setting = combobox.setting
}