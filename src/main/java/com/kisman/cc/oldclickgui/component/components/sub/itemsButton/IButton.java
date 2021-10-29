package com.kisman.cc.oldclickgui.component.components.sub.itemsButton;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IButton {
    private Minecraft mc = Minecraft.getMinecraft();

    public int x, y, offset;
    private int armourCompress = 2;
    private int armourSpacing = 20;
    public ItemStack item;
    private boolean hover;
    private boolean toggle;

    public IButton(int x, int y, ItemStack item, int offset) {
        this.x = x;
        this.y = y;
        this.item = item;
    }

    public void render() {
        RenderItem itemRender = mc.getRenderItem();

        GlStateManager.enableDepth();

        itemRender.zLevel = 200F;
        itemRender.renderItemAndEffectIntoGUI(item, x, y + offset);
        itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, item, x, y + offset, "");
        itemRender.zLevel = 0F;

        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
    }

    public void update(int mouseX, int mouseY) {
        hover = isMouseOnButton(mouseX, mouseY);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isMouseOnButton(mouseX, mouseY) && button == 0) {
            toggle = !toggle;
        }
    }

    public void setOff(int newOff) {
        offset = newOff;
    }

    public boolean isMouseOnButton(int x, int y) {
        if(x >= this.x && x <= this.x + this.armourSpacing && y >= this.y && y <= this.y + this.armourSpacing) return true;

        return false;
    }
}
