package com.kisman.cc.hud.hudmodule.combat;

import com.kisman.cc.Kisman;
import com.kisman.cc.hud.hudmodule.HudCategory;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.util.customfont.CustomFontUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class PvpResources extends HudModule {
    private int dOffset = 20;
    public PvpResources() {
        super("PvpResources", "PvpResources", HudCategory.COMBAT);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        ScaledResolution sr = event.getResolution();
        final int x = sr.getScaledWidth() / 2 + 5;
        final int y = sr.getScaledHeight() / 2 + 5;
        final int totemCount = getTotemCount();
        final int crystalCount = getCrystalCount();
        final int expCount = getExpCount();
        final int gAppleCount = getGAppleCount();

        int count = 0;
        int offset = 0;
        int offsetY = 90;

        //totems render
        if(totemCount != 0) {
            mc.getRenderItem().renderItemIntoGUI(new ItemStack(Items.TOTEM_OF_UNDYING), x, y);
            String s = totemCount > 1 ? totemCount + "" : "";
            CustomFontUtil.drawStringWithShadow(s, x + 19 - 2 - CustomFontUtil.getStringWidth(s), y + 9, 0xffffff);
            count++;
            offset += 90;
        }

        //crystals render
        if(crystalCount != 0) {
            mc.renderItem.renderItemIntoGUI(new ItemStack(Items.END_CRYSTAL), x + offset, y);
            String s1 = crystalCount > 1 ? crystalCount + "" : "";
            CustomFontUtil.drawStringWithShadow(s1, x + 19 - 2 - CustomFontUtil.getStringWidth(s1) + offset, y + 9, 0xffffff);
            count++;
            offset += 90;
        }

        //exps render
        if(expCount != 0) {
            if(count >= 2) {
                offset = 0;
            }
            mc.renderItem.renderItemIntoGUI(new ItemStack(Items.END_CRYSTAL), x + offset, y);
            String s2 = crystalCount > 1 ? crystalCount + "" : "";
            CustomFontUtil.drawStringWithShadow(s2, x + 19 - 2 - CustomFontUtil.getStringWidth(s2) + offset, y + 9 + offsetY, 0xffffff);
            count++;
            offset += 90;
        }

        if(gAppleCount != 0) {
            mc.renderItem.renderItemIntoGUI(new ItemStack(Items.END_CRYSTAL), x + offset, y);
            String s2 = crystalCount > 1 ? crystalCount + "" : "";
            CustomFontUtil.drawStringWithShadow(s2, x + 19 - 2 - CustomFontUtil.getStringWidth(s2) + offset, y + 9 + offset, 0xffffff);
            count++;
            offset += 90;
        }
    }

    private int getTotemCount() {
        int totemCount = 0;
        for(int i = 0; i < 45; i++) {
            if(mc.player.inventory.getStackInSlot(i).getItem().equals(Items.TOTEM_OF_UNDYING)) {
                totemCount++;
            }
        }

        if(mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING)) {
            totemCount++;
        }

        return totemCount;
    }

    private int getGAppleCount() {
        int gAppleCount = 0;
        for(int i = 0; i < 45; i++) {
            if(mc.player.inventory.getStackInSlot(i).getItem().equals(Items.GOLDEN_APPLE)) {
                gAppleCount++;
            }
        }

        if(mc.player.getHeldItemOffhand().getItem().equals(Items.GOLDEN_APPLE)) {
            gAppleCount++;
        }

        return gAppleCount;
    }

    private int getCrystalCount() {
        int crystalCount = 0;
        for(int i = 0; i < 45; i++) {
            if(mc.player.inventory.getStackInSlot(i).getItem().equals(Items.END_CRYSTAL)) {
                crystalCount++;
            }
        }

        if(mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL)) {
            crystalCount++;
        }

        return crystalCount;
    }

    private int getExpCount() {
        int expCount = 0;
        for(int i = 0; i < 45; i++) {
            if(mc.player.inventory.getStackInSlot(i).getItem().equals(Items.EXPERIENCE_BOTTLE)) {
                expCount++;
            }
        }

        if(mc.player.getHeldItemOffhand().getItem().equals(Items.EXPERIENCE_BOTTLE)) {
            expCount++;
        }

        return expCount;
    }
}