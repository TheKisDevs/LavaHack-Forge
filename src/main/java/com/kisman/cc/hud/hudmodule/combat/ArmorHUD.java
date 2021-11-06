package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.util.customfont.CustomFontUtil;
import i.gishreloaded.gishcode.wrappers.Wrapper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArmorHUD extends HudModule {
    private int offHandHeldItemCount;
    private int armourCompress;
    private int armourSpacing;

    public ArmorHUD() {
        super("ArmorHUD", "ArmorHUD", HudCategory.COMBAT);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        if(mc.player == null && mc.world == null) return;

        ScaledResolution resolution = new ScaledResolution(Wrapper.INSTANCE.mc());
        RenderItem itemRender = mc.getRenderItem();

        int i = resolution.getScaledWidth() / 2;
        int iteration = 0;
        int y = resolution.getScaledHeight() - 55 - (mc.player.isInWater() ? 10 : 0);

        for (ItemStack is : mc.player.inventory.armorInventory) {

            iteration++;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * armourSpacing + armourCompress;
            GlStateManager.enableDepth();

            itemRender.zLevel = 200F;
            itemRender.renderItemAndEffectIntoGUI(is, x, y);
            itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            itemRender.zLevel = 0F;

            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            CustomFontUtil.drawStringWithShadow(s, x + 19 - 2 - Wrapper.INSTANCE.fontRenderer().getStringWidth(s), y + 9, 0xffffff);

            if (HUD.instance.armDmg.getValBoolean()) {
                float green = ((float) is.getMaxDamage() - (float) is.getItemDamage()) / (float) is.getMaxDamage();
                float red = 1 - green;
                int dmg = 100 - (int) (red * 100);
                CustomFontUtil.drawStringWithShadow(dmg + "", x + 8 - CustomFontUtil.getStringWidth(dmg + "") / 2, y - 11, 0xFFFFFF);
            }

            if (HUD.instance.armExtra.getValBoolean()) {
                for (ItemStack itemStack : Wrapper.INSTANCE.inventory().offHandInventory) {
                    Item helfInOffHand = Wrapper.INSTANCE.player().getHeldItemOffhand().getItem();
                    offHandHeldItemCount = getItemsOffHand(helfInOffHand);
                    GlStateManager.pushMatrix();
                    GlStateManager.disableAlpha();
                    GlStateManager.clear(256);
                    GlStateManager.enableBlend();
                    GlStateManager.pushAttrib();
                    RenderHelper.enableGUIStandardItemLighting();
                    GlStateManager.disableDepth();

                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, 572, y);
                    itemRender.renderItemOverlayIntoGUI(Wrapper.INSTANCE.fontRenderer(), itemStack, 572, y, String.valueOf(offHandHeldItemCount));
                    GlStateManager.enableDepth();
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popAttrib();
                    GlStateManager.disableBlend();

                    GlStateManager.disableDepth();
                    GlStateManager.disableLighting();
                    GlStateManager.enableDepth();
                    GlStateManager.enableAlpha();
                    GlStateManager.popMatrix();

                }
            }

            if (HUD.instance.armExtra.getValBoolean()) {
                Item currentHeldItem = Wrapper.INSTANCE.inventory().getCurrentItem().getItem();
                int currentHeldItemCount = Wrapper.INSTANCE.inventory().getCurrentItem().getCount();

                ItemStack stackHeld = new ItemStack(currentHeldItem, 1);
                GlStateManager.pushMatrix();
                GlStateManager.disableAlpha();
                GlStateManager.clear(256);
                GlStateManager.enableBlend();
                GlStateManager.pushAttrib();
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.disableDepth();
                Wrapper.INSTANCE.mc().getRenderItem().renderItemAndEffectIntoGUI(stackHeld, 556, y);

                itemRender.renderItemOverlayIntoGUI(Wrapper.INSTANCE.fontRenderer(), stackHeld, 556, y, String.valueOf(currentHeldItemCount));

                GlStateManager.enableDepth();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popAttrib();
                GlStateManager.disableBlend();

                GlStateManager.disableDepth();
                GlStateManager.disableLighting();
                GlStateManager.enableDepth();
                GlStateManager.enableAlpha();
                GlStateManager.popMatrix();

            }

            GlStateManager.enableDepth();
            GlStateManager.disableLighting();

            if (HUD.instance.armExtra.getValBoolean()) {
                armourCompress = 14;
                armourSpacing = 17;
            } else {
                armourCompress = 2;
                armourSpacing = 20;
            }

            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }

        GlStateManager.enableTexture2D();

        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    int getItemsOffHand(Item i) {
        return Wrapper.INSTANCE.inventory().offHandInventory.stream().
                filter(itemStack -> itemStack.getItem() == i).mapToInt(ItemStack::getCount).sum();
    }
}
