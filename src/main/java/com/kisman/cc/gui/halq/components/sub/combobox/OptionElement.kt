package com.kisman.cc.gui.halq.components.sub.combobox

import com.kisman.cc.features.Binder
import com.kisman.cc.util.client.interfaces.IBindable
import com.kisman.cc.gui.api.Component
import com.kisman.cc.gui.api.shaderable.ShaderableImplementation
import com.kisman.cc.gui.halq.HalqGui
import com.kisman.cc.gui.halq.components.sub.ModeButton
import com.kisman.cc.gui.halq.util.getModifiedWidth
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
    private var x : Int,
    private var y : Int,
    var offset : Int,
    private var count : Int,
    private var layer : Int
) : ShaderableImplementation(),
    Component {
    private var width = getModifiedWidth(
        layer,
        HalqGui.width
    )

    override fun drawScreen(
        mouseX : Int,
        mouseY : Int
    ) {
        super<ShaderableImplementation>.drawScreen(mouseX, mouseY)

        normalRender = Runnable {
            Render2DUtil.drawRectWH(
                x.toDouble(),
                (y + offset).toDouble(),
                width.toDouble(),
                height.toDouble(),
                HalqGui.backgroundColor.rgb
            )

            HalqGui.drawString(
                name,
                x,
                y + offset,
                width,
                rawHeight
            )

            if (IBindable.valid(binder)) {
                HalqGui.drawSuffix(
                    IBindable.getName(binder),
                    name,
                    x.toDouble(),
                    y.toDouble() + offset,
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

    private fun isMouseOnButton(
        x : Int,
        y : Int
    ) : Boolean = x > this.x && x < this.x + width && y > this.y + offset && y < this.y + offset + rawHeight

    override fun updateComponent(
        x : Int,
        y : Int
    ) {
        this.x = x
        this.y = y
    }

    override fun getHeight() : Int = rawHeight

    override fun setWidth(
        width : Int
    ) {
        this.width = width
    }

    override fun setOff(
        newOff : Int
    ) {
        this.offset = newOff
    }

    override fun setCount(
        count : Int
    ) {
        this.count = count
    }

    override fun getCount() : Int = count

    override fun setX(
        x : Int
    ) {
        this.x = x
    }

    override fun getX() : Int = x

    override fun setY(
        y : Int
    ) {
        this.y = y
    }

    override fun getY() : Int = y + offset

    override fun setLayer(
        layer : Int
    ) {
        this.layer = layer;
    }

    override fun getLayer() : Int = layer

    override fun visible() : Boolean = visible.asBoolean && combobox.selected != this
}