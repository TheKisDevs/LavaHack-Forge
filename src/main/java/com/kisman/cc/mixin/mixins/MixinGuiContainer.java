package com.kisman.cc.mixin.mixins;

import com.kisman.cc.module.misc.ItemScroller;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.*;

@Mixin(value = GuiContainer.class, priority = 10000)
public class MixinGuiContainer extends GuiScreen {
    @Shadow
    private Slot hoveredSlot;

    /**
     * @author _kisman_
     */
    @Overwrite
    protected void renderHoveredToolTip(int p_191948_1_, int p_191948_2_) {
        if(this.mc.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            this.renderToolTip(this.hoveredSlot.getStack(), p_191948_1_, p_191948_2_ + getScrollWheel());
        }
    }

    private int getScrollWheel() {
        int dWheel = Mouse.getDWheel();

        if(dWheel < 0){
            return -20;
        } else if(dWheel > 0){
            return 20;
        }

        return 0;
    }
}
