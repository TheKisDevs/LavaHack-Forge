package com.kisman.cc.mixin.mixins;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.client.Changer;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @author _kisman_
 * @since 15:01 of 27.06.2022
 */
@Mixin(FontRenderer.class)
public class MixinFontRenderer {
    @Shadow protected void enableAlpha() {}
    @Shadow private void resetStyles() {}
    @Shadow private int renderString(String text, float x, float y, int color, boolean dropShadow) {return 0;}

    /**
     * @author _kisman_
     */
    @Overwrite
    public int drawString(String text, float x, float y, int color, boolean dropShadow) {
        enableAlpha();
        resetStyles();
        int i;
        if (dropShadow) {
            Changer changer = (Changer) Kisman.instance.moduleManager.getModule("Changer");
            i = renderString(text, x + (changer.getShadowTextModifier().getValBoolean() ? changer.getShadowX().getValFloat() : 1), y + (changer.getShadowTextModifier().getValBoolean() ? changer.getShadowY().getValFloat() : 1), color, true);
            return Math.max(i, renderString(text, x, y, color, false));
        } else return renderString(text, x, y, color, false);
    }
}
