package com.kisman.cc.features.hud.modules;

import com.kisman.cc.features.hud.ShaderableHudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.enums.PvpResourcesStyles;
import com.kisman.cc.util.interfaces.AdvancedRunnable;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class PvpResources extends ShaderableHudModule {
    private final Setting style = register(new Setting("Style", this, PvpResourcesStyles.Vertical));

    public PvpResources() {
        super("PvpResources", "PvpResources", true, false, false);
    }

    private final AdvancedRunnable stringRenderer = objects -> {
        int x = (int) objects[0];
        int y = (int) objects[1];

        String string = (String) objects[2];

        drawStringWithShadow(string, x + 19 - 2 - CustomFontUtil.getStringWidth(string), y + 6 + 3, -1);
    };

    public static int getItemCount(Item item) {
        return mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == item).mapToInt(ItemStack::getCount).sum() + ((mc.player.getHeldItemOffhand().getItem() == item) ? mc.player.getHeldItemOffhand().getCount() : 0);
    }

    public void handleRender() {
        final int x = (int) getX();
        final int y = (int) getY();

        GL11.glEnable(GL11.GL_DEPTH_TEST);

        if(style.getValEnum() == PvpResourcesStyles.Vertical) {
            drawVertical(x, y);
        } else if(style.getValEnum() == PvpResourcesStyles.Horizontal) {
            drawHorizontal(x, y);
        } else if(style.getValEnum() == PvpResourcesStyles.Square) {
            drawSquare(x, y);
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    private void drawVertical(int x, int y) {
        int offset = 0;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(426), 1), x,  y + offset);
        Render2DUtil.renderItemOverlayIntoGUI(x, y + offset , stringRenderer, String.valueOf(getItemCount(Item.getItemById(426))));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(384), 1), x,  y + offset);
        Render2DUtil.renderItemOverlayIntoGUI(x , y +  offset, stringRenderer, String.valueOf(getItemCount(Item.getItemById(384))));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(322), 1), x, y + offset);
        Render2DUtil.renderItemOverlayIntoGUI(x, y + offset, stringRenderer, String.valueOf(getItemCount(Item.getItemById(322))));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(449), 1), x, y + offset);
        Render2DUtil.renderItemOverlayIntoGUI(x , y + offset, stringRenderer, String.valueOf(getItemCount(Item.getItemById(449))));

        setW(20);
        setH(20 * 4);
    }

    private void drawHorizontal(int x, int y) {
        int offset = 0;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(426), 1), x + offset , y);
        Render2DUtil.renderItemOverlayIntoGUI(x + offset, y , stringRenderer, String.valueOf(getItemCount(Item.getItemById(426))));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(384), 1), x + offset, y);
        Render2DUtil.renderItemOverlayIntoGUI(x + offset, y, stringRenderer, String.valueOf(getItemCount(Item.getItemById(384))));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(322), 1),  x + offset, y);
        Render2DUtil.renderItemOverlayIntoGUI(x + offset, y, stringRenderer, String.valueOf(getItemCount(Item.getItemById(322))));
        offset += 20;

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(449), 1),  x + offset, y);
        Render2DUtil.renderItemOverlayIntoGUI(x + offset, y, stringRenderer, String.valueOf(getItemCount(Item.getItemById(449))));

        setW(20 * 4);
        setH(20);
    }

    private void drawSquare(int x, int y) {
        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(426), 1), x, y);
        Render2DUtil.renderItemOverlayIntoGUI(x, y , stringRenderer, String.valueOf(getItemCount(Item.getItemById(426))));

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(384), 1), x, y + 20);
        Render2DUtil.renderItemOverlayIntoGUI(x , y + 20, stringRenderer, String.valueOf(getItemCount(Item.getItemById(384))));

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(322), 1), x + 20, y);
        Render2DUtil.renderItemOverlayIntoGUI(x + 20, y, stringRenderer, String.valueOf(getItemCount(Item.getItemById(322))));

        mc.getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(Item.getItemById(449), 1), x + 20, y + 20);
        Render2DUtil.renderItemOverlayIntoGUI(x + 20, y + 20, stringRenderer, String.valueOf(getItemCount(Item.getItemById(449))));

        setW(40);
        setH(40);
    }
}