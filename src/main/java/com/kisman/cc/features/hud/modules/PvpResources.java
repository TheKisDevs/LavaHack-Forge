package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.enums.PvpResourcesStyles;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.*;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class PvpResources extends HudModule {
    private final Setting style = register(new Setting("Style", this, PvpResourcesStyles.Vertical));

    public PvpResources() {
        super("PvpResources", "PvpResources", true);
    }

    public static int getItemCount(Item item) {
        return mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == item).mapToInt(ItemStack::getCount).sum() + ((mc.player.getHeldItemOffhand().getItem() == item) ? mc.player.getHeldItemOffhand().getCount() : 0);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        final int x = (int) getX();
        final int y = (int) getY();

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        if(style.getValEnum() == PvpResourcesStyles.Vertical) {
            drawVertical(x, y);
        } else {
            drawSquare(x, y);
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    private void drawVertical(int x, int y) {
        int offset = 0;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(426), 1), (int) x , (int) y + offset);
        renderItemOverlayIntoGUI(x, y + offset , "" + getItemCount(Item.getItemById(426)));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(384), 1), (int)x,  (int) y + offset);
        renderItemOverlayIntoGUI(x , y +  offset, "" + getItemCount(Item.getItemById(384)));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(322), 1), (int) x, (int )y + offset);
        renderItemOverlayIntoGUI(x, y + offset, "" + getItemCount(Item.getItemById(322)));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(449), 1), (int) x, (int) y + offset);
        renderItemOverlayIntoGUI(x , y + offset, "" + getItemCount(Item.getItemById(449)));

        setW(20);
        setH(20 * 4);
    }

    private void drawSquare(int x, int y) {
        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(426), 1), (int) x , (int) y);
        renderItemOverlayIntoGUI(x, y , "" + getItemCount(Item.getItemById(426)));

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(384), 1), (int)x,  (int) y + 20);
        renderItemOverlayIntoGUI(x , y + 20, "" + getItemCount(Item.getItemById(384)));

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(322), 1), (int) x + 20, (int )y);
        renderItemOverlayIntoGUI(x + 20, y, "" + getItemCount(Item.getItemById(322)));

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(449), 1), (int) x + 20, (int) y + 20);
        renderItemOverlayIntoGUI(x + 20, y + 20, "" + getItemCount(Item.getItemById(449)));

        setW(40);
        setH(40);
    }

    public void renderItemOverlayIntoGUI(int xPosition, int yPosition, String s) {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        CustomFontUtil.drawStringWithShadow(s, (float) (xPosition + 19 - 2 - CustomFontUtil.getStringWidth(s)), (float) (yPosition + 6 + 3), new Color(255, 255, 255, 255).hashCode());
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
    }
}