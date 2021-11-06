package com.kisman.cc.module.render;

import com.google.common.collect.Lists;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderEntityName;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.gui.ChatFormatting;
import kisman.pasta.salhack.util.customfont.FontManager;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.*;
import java.util.List;

public class NameTags extends Module {
    private Setting range = new Setting("Range", this, 0, 50 ,100, false);
    private Setting scale = new Setting("Size", this, 0.8f, 0.1f, 20, false);
    private Setting lineWidth = new Setting("LineWidth", this, 1.5f, 0.1f, 5, false);
    private Setting onlyFov = new Setting("OnlyFov", this, true);
    private Setting factor = new Setting("Factor", this, 0.3f, 0.1f, 1, false);
    private Setting smallScale = new Setting("SmallScale", this, false);
    private Setting invisibles = new Setting("Invisibles", this, true);
    private Setting scaleing = new Setting("Scale", this, false);
//    private Setting color = new Setting("Color", this, "Color", new float[] {1, 1, 1, 1}, false);
    private Setting outline = new Setting("OutLine", this, true);
    private Setting rect = new Setting("Rectangle", this, true);
    private Setting armor = new Setting("Armor", this, true);
    private Setting heldStackName = new Setting("StackName", this, false);
    private Setting health = new Setting("Health", this, true);
    private Setting totemPop = new Setting("TotemPops", this, true);
    private Setting gamemode = new Setting("GameMode", this, true);
    private Setting entityId = new Setting("EntityID", this, true);
    private Setting whiter = new Setting("Write", this, false);
    private Setting ping = new Setting("Ping", this, true);
    private Setting sneak = new Setting("Sneak", this, false);
    private Setting items = new Setting("Items", this, true);
    private Setting durability = new Setting("Durability", this, true);
    private Setting itemName = new Setting("ItemName", this, true);
    private Setting ench = new Setting("Enchantments", this, true);
    private Setting color = new Setting("Color", this, "Green", ColorUtil.colours);

//    private Setting bcolor = new Setting("BorderedColor", this, "BorderedColor", new float[] {0, 0, 0, 0}, false);

    public static NameTags instance;

    private ICamera camera = new Frustum();

    public NameTags() {
        super("NameTags", "f", Category.RENDER);

        instance = this;

        setmgr.rSetting(range);
        setmgr.rSetting(scale);
        setmgr.rSetting(lineWidth);
        setmgr.rSetting(onlyFov);
        setmgr.rSetting(factor);
        setmgr.rSetting(smallScale);
        setmgr.rSetting(invisibles);
        setmgr.rSetting(scaleing);
        setmgr.rSetting(color);
        setmgr.rSetting(outline);
        setmgr.rSetting(rect);
        setmgr.rSetting(armor);
        setmgr.rSetting(heldStackName);
        setmgr.rSetting(health);
        setmgr.rSetting(totemPop);
        setmgr.rSetting(gamemode);
        setmgr.rSetting(entityId);
        setmgr.rSetting(whiter);
        setmgr.rSetting(sneak);
        setmgr.rSetting(ping);
        setmgr.rSetting(items);
        setmgr.rSetting(durability);
        setmgr.rSetting(itemName);
        setmgr.rSetting(ench);
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        for (final Entity e : this.mc.world.playerEntities) {
            if (e != this.mc.player) {
                final double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * event.getPartialTicks() - this.mc.getRenderManager().renderPosX;
                final double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * event.getPartialTicks() - this.mc.getRenderManager().renderPosY;
                final double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * event.getPartialTicks() - this.mc.getRenderManager().renderPosZ;
                GL11.glPushMatrix();
                GL11.glDisable(2929);
                GL11.glDisable(3553);
                GL11.glNormal3f(0.0f, 1.0f, 0.0f);
                GlStateManager.disableLighting();
                GlStateManager.enableBlend();
                final float size = Math.min(Math.max(1.2f * (this.mc.player.getDistance(e) * 0.15f), 1.25f), 6.0f) * 0.02f;
                GL11.glTranslatef((float)x, (float)y + e.height + 0.4f, (float)z);
                GlStateManager.glNormal3f(0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(-this.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(this.mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
                GL11.glScalef(-size, -size, size);
                final int health = (int)(((EntityPlayer)e).getHealth() / ((EntityPlayer)e).getMaxHealth() * 100.0f);
                Gui.drawRect(-this.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2 - 2, -2, this.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2 + 2, 9, new Color(17, 17, 17).hashCode());
                this.mc.fontRenderer.drawString(e.getName() + " " + TextFormatting.GREEN + health + "%", -this.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2, 0, -1);
                GL11.glEnable(3553);
                int posX = -this.mc.fontRenderer.getStringWidth(e.getName() + " " + health + "%") / 2 - 8;
                if (Item.getIdFromItem(((EntityPlayer)e).inventory.getCurrentItem().getItem()) != 0) {
                    this.mc.getRenderItem().zLevel = -100.0f;
                    this.mc.getRenderItem().renderItemIntoGUI(new ItemStack(((EntityPlayer)e).inventory.getCurrentItem().getItem()), posX - 2, -20);
                    this.mc.getRenderItem().zLevel = 0.0f;
                    int posY = -30;
                    final Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(((EntityPlayer)e).inventory.getCurrentItem());
                    for (final Enchantment enchantment : enchantments.keySet()) {
                        final int level = EnchantmentHelper.getEnchantmentLevel(enchantment, ((EntityPlayer)e).inventory.getCurrentItem());
                        CustomFontUtil.drawCenteredStringWithShadow(String.valueOf(enchantment.getName().substring(12).charAt(0)).toUpperCase() + level, posX + 6, posY, -1);
                        posY -= 12;
                    }
                    posX += 15;
                }
                for (final ItemStack item : e.getArmorInventoryList()) {
                    this.mc.getRenderItem().zLevel = -100.0f;
                    this.mc.getRenderItem().renderItemIntoGUI(new ItemStack(item.getItem()), posX, -20);
                    this.mc.getRenderItem().zLevel = 0.0f;
                    int posY2 = -30;
                    final Map<Enchantment, Integer> enchantments2 = EnchantmentHelper.getEnchantments(item);
                    for (final Enchantment enchantment2 : enchantments2.keySet()) {
                        final int level2 = EnchantmentHelper.getEnchantmentLevel(enchantment2, item);
                        CustomFontUtil.drawCenteredStringWithShadow(String.valueOf(enchantment2.getName().substring(12).charAt(0)).toUpperCase() + level2, posX + 9, posY2, -1);
                        posY2 -= 12;
                    }
                    posX += 17;
                }
                int gapples = 0;
                if (Item.getIdFromItem(((EntityPlayer)e).inventory.getCurrentItem().getItem()) == 322) {
                    gapples = ((EntityPlayer)e).inventory.getCurrentItem().stackSize;
                }
                else if (Item.getIdFromItem(((EntityPlayer)e).getHeldItemOffhand().getItem()) == 322) {
                    gapples = ((EntityPlayer)e).getHeldItemOffhand().stackSize;
                }
                if (gapples > 0) {
                    this.mc.getRenderItem().zLevel = -100.0f;
                    this.mc.getRenderItem().renderItemIntoGUI(new ItemStack(Items.GOLDEN_APPLE), posX, -20);
                    this.mc.getRenderItem().zLevel = 0.0f;
                    CustomFontUtil.drawCenteredStringWithShadow(String.valueOf(gapples), posX + 9, -30.0f, -1);
                }
                GL11.glEnable(2929);
                GL11.glPopMatrix();
            }
        }
    }

    @EventHandler
    private final Listener<EventRenderEntityName> listener = new Listener<>(event -> {
        event.cancel();
    });
}
