package com.kisman.cc.util.render.customfont

import com.kisman.cc.features.module.client.CustomFontModule
import java.awt.Font
import java.util.function.Supplier

/**
 * @author _kisman_
 * @since 17:01 of 11.06.2022
 */
class SizechangeableFontRenderer(
    font : Font
) : ExtendedFontRenderer(font) {
    private val fonts = arrayListOf(
        ExtendedFontRenderer(font)
    )

    private val size = Supplier { if(CustomFontModule.instance != null && CustomFontModule.instance.customSize.valBoolean) CustomFontModule.instance.size.valInt else font.size }


    override fun getCurrentFont(): CustomFontRenderer {
        for(font in fonts) {
            if(font.font.size == size.get()) {
                return font.getCurrentFont();
            }
        }

        val font = ExtendedFontRenderer(this.font.deriveFont(size.get()))
        fonts.add(font)
        return font.getCurrentFont()
    }
}