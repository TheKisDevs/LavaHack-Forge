package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.render.RenderUtil;
import com.kisman.cc.util.render.Rendering;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class  NameTags extends Module {
    private final Setting range = register(new Setting("Range", this, 0, 50 ,100, false));
    private final Setting scale = register(new Setting("Scale", this, 0.1f, 0.1f, 0.3f, false));
    private final Setting bgAlpha = register(new Setting("BG Alpha", this, 128, 0, 250, true));
    private final Setting ping = register(new Setting("Ping", this, true));
    private final Setting items = register(new Setting("Items", this, true));
    private final Setting damageDisplay = register(new Setting("Damage Display", this, true));
    private final Setting atheist = register(new Setting("Atheist", this, true));
    private final Setting desc = register(new Setting("Desc", this, false));
    private final Setting noBots = register(new Setting("No Bots", this, false));

    private final MultiThreaddableModulePattern threads = threads();

    public static NameTags instance;

    private int counter2;
    private final HashMap<String, Integer> tagList = new HashMap<>();
    private final HashMap<String, String> damageList = new HashMap<>();

    private ArrayList<EntityPlayer> list = new ArrayList<>();

    public NameTags() {
        super("NameTags", Category.RENDER);
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        threads.reset();
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        threads.update(() -> {
            ArrayList<EntityPlayer> list = new ArrayList<>();

            for(EntityPlayer player : mc.world.playerEntities) {
                if(noBots.getValBoolean()) {
                    try {
                        mc.player.connection.getPlayerInfo(player.getUniqueID()).getResponseTime();//ping = -1
                    } catch (NullPointerException ignored) {
                        continue;
                    }
                }

                if(player != mc.player && mc.player.getDistance(player) <= range.getValInt() && player != mc.getRenderViewEntity() && player.isEntityAlive()) {
                    if (damageDisplay.getValBoolean()) {
                        if (!tagList.containsKey(player.getName())) {
                            tagList.put(player.getName(), (int) player.getHealth());
                            damageList.put(player.getName(), "");
                        }
                        if (player.isDead || player.getHealth() <= 0.0f) {
                            tagList.remove(player.getName());
                            damageList.remove(player.getName());
                        }
                    }

                    list.add(player);
                }

                if (counter2 == 601 && damageDisplay.getValBoolean()) {
                    tagList.remove(player.getName());
                    damageList.remove(player.getName());
                }
            }

            if (counter2 == 601) counter2 = 0;
            ++counter2;

            this.list = list;
        });

        for(EntityPlayer player : list) {
            double pX = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks;
            double pY = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks;
            double pZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks;
            Entity renderEntity = mc.getRenderManager().renderViewEntity;
            if (renderEntity == null) renderEntity = mc.player;
            if (renderEntity == null) return;
            double rX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * mc.timer.renderPartialTicks;
            double rY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * mc.timer.renderPartialTicks;
            double rZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * mc.timer.renderPartialTicks;
            renderNametag(player, pX - rX, pY - rY, pZ - rZ);
        }
    }

    public void renderNametag(final EntityPlayer player, final double x, final double y, final double z) {
        Rendering.setup();
        TextFormatting clr;
        TextFormatting clrf = TextFormatting.WHITE;
        String cross = "";
        if (FriendManager.instance.isFriend(player.getName())) {
            clrf = TextFormatting.AQUA;
            if (!atheist.getValBoolean()) cross = "\u271d ";
        }
        int pingy = -1;
        try {pingy = mc.player.connection.getPlayerInfo(player.getUniqueID()).getResponseTime();} catch (NullPointerException ignored) {}
        String playerPing = pingy + "ms  ";
        if (!ping.getValBoolean()) playerPing = "";
        int health = MathHelper.ceil(player.getHealth() + player.getAbsorptionAmount());
        boolean damageDisplay = this.damageDisplay.getValBoolean();
        if (health > 16) clr = TextFormatting.GREEN;
        else if (health > 12) clr = TextFormatting.YELLOW;
        else if (health > 8) clr = TextFormatting.GOLD;
        else if (health > 5) clr = TextFormatting.RED;
        else clr = TextFormatting.DARK_RED;
        int lasthealth = 0;
        try {
            lasthealth = this.tagList.get(player.getName());
        } catch(Exception ignored) {}
        if (damageDisplay) {
            if (lasthealth > health) this.damageList.put(player.getName(), TextFormatting.RED + " -" + (lasthealth - health));
            this.tagList.put(player.getName(), health);
        }
        String dmgtext = "";
        try {
            if (damageDisplay) dmgtext = this.damageList.get(player.getName());
        } catch(Exception ignored) {}
        String name = cross + clrf + playerPing + player.getName() + " " + clr + health + dmgtext;
        name = name.replace(".0", "");
        float var14 = 0.016666668f * this.getNametagSize(player);
        GL11.glTranslated(x, y + 2.5 + var14 * 10.0f, z);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(-var14, -var14, var14);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GL11.glDisable(2929);
        int width = mc.fontRenderer.getStringWidth(name) / 2;
        double widthBackGround = bgAlpha.getValDouble();
        int[] counter = { 1 };
        RenderUtil.drawSmoothRect((float)(-width - 3), 9.0f, (float)(width + 4), 23.0f, new Color(0, 0, 0, (int)widthBackGround).getRGB());
        int[] array = counter;
        int n = 0;
        ++array[n];//9 + 14 / 2 - font.fontHeight / 2
        mc.fontRenderer.drawStringWithShadow(name, -width, 9 + 7 - (mc.fontRenderer.FONT_HEIGHT) / 2f, Color.red.getRGB());
        boolean item = this.items.getValBoolean();
        if (item) {
            int xOffset = -8;
            for (final ItemStack armourStack : player.inventory.armorInventory) if (armourStack != null) xOffset -= 8;
            if (!player.getHeldItemMainhand().isEmpty()) {
                xOffset -= 8;
                final ItemStack renderStack = player.getHeldItemMainhand().copy();
                this.renderItem(renderStack, xOffset, -10);
                xOffset += 16;
            }
            for (int index = 3; index >= 0; --index) {
                ItemStack armourStack2 = player.inventory.armorInventory.get(index);
                if (!armourStack2.isEmpty()) {
                    final ItemStack renderStack2 = armourStack2.copy();
                    this.renderItem(renderStack2, xOffset, -10);
                    xOffset += 16;
                }
            }
            if (!player.getHeldItemOffhand().isEmpty()) {
                ItemStack renderOffhand = player.getHeldItemOffhand().copy();
                renderItem(renderOffhand, xOffset, -10);
                xOffset += 8;
            }
        }
        Rendering.release();
    }

    public float getNametagSize(final EntityLivingBase player) {
        ScaledResolution scaledRes = new ScaledResolution(mc);
        double twoDscale = scaledRes.getScaleFactor() / Math.pow(scaledRes.getScaleFactor(), 2.0);
        double scale = this.scale.getValDouble();
        return (float)scale * 6.0f * ((float)twoDscale + (float)(player.getDistance(mc.renderViewEntity.posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ) / 10.5));
    }

    public void renderItem(ItemStack stack, int x, int y) {
        GL11.glPushMatrix();
        GL11.glDepthMask(true);
        GlStateManager.clear(256);
        GlStateManager.disableDepth();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -100.0f;
        GlStateManager.scale(1.0f, 1.0f, 0.01f);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y / 2 - 12);
        if (stack.getItem() == Items.GOLDEN_APPLE) mc.renderItem.renderItemOverlays(mc.fontRenderer, stack, x - 5, y / 2 - 28);
        else mc.renderItem.renderItemOverlays(mc.fontRenderer, stack, x, y / 2 - 8);
        mc.getRenderItem().zLevel = 0.0f;
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.disableDepth();
        boolean enchants = desc.getValBoolean();
        if (enchants) renderEnchantText(stack, x, y - 18);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GL11.glPopMatrix();
    }

    public void renderEnchantText(ItemStack stack, int x, int y) {
        int encY = y - 18;
        int yCount = encY + 5;
        NBTTagList enchants = stack.getEnchantmentTagList();
        if (!enchants.hasNoTags()) {
            for (int index = 0; index < enchants.tagCount(); ++index) {
                short id = enchants.getCompoundTagAt(index).getShort("id");
                short level = enchants.getCompoundTagAt(index).getShort("lvl");
                Enchantment enc = Enchantment.getEnchantmentByID(id);
                if (enc != null) {
                    String encName = enc.getTranslatedName(level).substring(0, 1).toLowerCase();
                    encName = encName + level;
                    GL11.glPushMatrix();
                    GL11.glScalef(1.0f, 1.0f, 0.0f);
                    if (level == 1) mc.fontRenderer.drawStringWithShadow(encName, x * 2 + 10, yCount, new Color(202, 202, 202, 255).getRGB());
                    else if (level == 2) mc.fontRenderer.drawStringWithShadow(encName, x * 2 + 10, yCount, new Color(246, 218, 45, 255).getRGB());
                    else if (level == 3) mc.fontRenderer.drawStringWithShadow(encName, x * 2 + 10, yCount, new Color(229, 128, 0, 255).getRGB());
                    else if (level == 4) mc.fontRenderer.drawStringWithShadow(encName, x * 2 + 10, yCount, new Color(156, 59, 253, 255).getRGB());
                    else mc.fontRenderer.drawStringWithShadow(encName, x * 2 + 10, yCount, new Color(239, 0, 0, 255).getRGB());
                    GL11.glScalef(1.0f, 1.0f, 1.0f);
                    GL11.glPopMatrix();
                    encY += 8;
                    yCount -= 10;
                }
            }
        }
    }
}