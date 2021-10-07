package com.kisman.cc.module.render;

import com.google.common.collect.Lists;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRenderEntityName;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.gui.ChatFormatting;
import kisman.pasta.salhack.util.customfont.FontManager;
import kisman.pasta.salhack.util.render.GLUProjection;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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

    @EventHandler
    private final Listener<EventRenderEntityName> listener = new Listener<>(event -> {
        event.cancel();
    });

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        mc.world.playerEntities.stream().filter(this::shouldRender).forEach(entityPlayer -> {
            Vec3d vec3d = findEntityVec3d(entityPlayer);
            renderNameTags(entityPlayer, vec3d.x, vec3d.y, vec3d.z);
        });
    }

    private boolean shouldRender(EntityPlayer entityPlayer) {
        if (entityPlayer == mc.player) return false;

        if (entityPlayer.isDead || entityPlayer.getHealth() <= 0) return false;

        return !(entityPlayer.getDistance(mc.player) > range.getValDouble());
    }

    private Vec3d findEntityVec3d(EntityPlayer entityPlayer) {
        double posX = balancePosition(entityPlayer.posX, entityPlayer.lastTickPosX);
        double posY = balancePosition(entityPlayer.posY, entityPlayer.lastTickPosY);
        double posZ = balancePosition(entityPlayer.posZ, entityPlayer.lastTickPosZ);

        return new Vec3d(posX, posY, posZ);
    }

    private double balancePosition(double newPosition, double oldPosition) {
        return oldPosition + (newPosition - oldPosition) * mc.timer.renderPartialTicks;
    }

    private void renderNameTags(EntityPlayer entityPlayer, double posX, double posY, double posZ) {
        double adjustedY = posY + (entityPlayer.isSneaking() ? 1.9 : 2.1);

        String[] name = new String[1];
        name[0] = buildEntityNameString(entityPlayer);

        RenderUtil.drawNametag(posX, adjustedY, posZ, name, findTextColor(entityPlayer), 2);
        renderItemsAndArmor(entityPlayer, 0, 0);
        GlStateManager.popMatrix();
    }

    private String buildEntityNameString(EntityPlayer entityPlayer) {
        String name = entityPlayer.getName();

        if (entityId.getValBoolean()) {
            name = name + " ID: " + entityPlayer.getEntityId();
        }

        if (gamemode.getValBoolean()) {
            if (entityPlayer.isCreative()) {
                name = name + " [C]";
            }
            else if (entityPlayer.isSpectator()) {
                name = name + " [I]";
            }
            else {
                name = name + " [S]";
            }
        }

/*        if (showTotem.getValue()) {
            name = name + " [" + TotemPopManager.INSTANCE.getPlayerPopCount(entityPlayer.getName()) +"]";
        }*/

        if (health.getValBoolean()) {
            int value = 0;

            if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) != null) {
                value = mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()).getResponseTime();
            }

            name = name + " " + value + "ms";
        }

        if (health.getValBoolean()) {
            int health = (int) (entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount());
            TextFormatting textFormatting = findHealthColor(health);

            name = name + " " + textFormatting + health;
        }

        return name;
    }

    private TextFormatting findHealthColor(int health) {
        if (health <= 0) {
            return TextFormatting.DARK_RED;
        } else if (health <= 5) {
            return TextFormatting.RED;
        } else if (health <= 10) {
            return TextFormatting.GOLD;
        } else if (health <= 15) {
            return TextFormatting.YELLOW;
        } else if (health <= 20) {
            return TextFormatting.DARK_GREEN;
        }

        return TextFormatting.GREEN;
    }

    private Colour findTextColor(EntityPlayer entityPlayer) {
/*        ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        if (SocialManager.isFriend(entityPlayer.getName())) {
            return colorMain.getFriendGSColor();
        } else if (SocialManager.isEnemy(entityPlayer.getName())) {
            return colorMain.getEnemyGSColor();*/
        if (entityPlayer.isInvisible()) {
            return new Colour(128, 128, 128);
        } else if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(entityPlayer.getUniqueID()) == null) {
            return new Colour(239, 1, 71);
        } else if (entityPlayer.isSneaking()) {
            return new Colour(255, 153, 0);
        }

        return new Colour(255, 255, 255);
    }

    private void renderItemsAndArmor(EntityPlayer entityPlayer, int posX, int posY) {
        ItemStack mainHandItem = entityPlayer.getHeldItemMainhand();
        ItemStack offHandItem = entityPlayer.getHeldItemOffhand();

        int armorCount = 3;
        for (int i = 0; i <= 3; i++) {
            ItemStack itemStack = entityPlayer.inventory.armorInventory.get(armorCount);

            if (!itemStack.isEmpty()) {
                posX -= 8;

                int size = EnchantmentHelper.getEnchantments(itemStack).size();

                if (items.getValBoolean() && size > posY) {
                    posY = size;
                }
            }
            armorCount --;
        }

        if (!mainHandItem.isEmpty() && (items.getValBoolean() || durability.getValBoolean() && offHandItem.isItemStackDamageable())) {
            posX -= 8;

            int enchantSize = EnchantmentHelper.getEnchantments(offHandItem).size();
            if (items.getValBoolean() && enchantSize > posY) {
                posY = enchantSize;
            }
        }

        if (!mainHandItem.isEmpty()) {

            int enchantSize = EnchantmentHelper.getEnchantments(mainHandItem).size();

            if (items.getValBoolean() && enchantSize > posY) {
                posY = enchantSize;
            }

            int armorY = findArmorY(posY);

            if (items.getValBoolean() || (durability.getValBoolean() && mainHandItem.isItemStackDamageable())) {
                posX -= 8;
            }

            if (items.getValBoolean()) {
                renderItem(mainHandItem, posX, armorY, posY);
                armorY -= 32;
            }

            if (durability.getValBoolean() && mainHandItem.isItemStackDamageable()) {
                renderItemDurability(mainHandItem, posX, armorY);
            }

            armorY -= mc.fontRenderer.FONT_HEIGHT;

            if (itemName.getValBoolean()) {
                renderItemName(mainHandItem, armorY);
            }

            if (items.getValBoolean() || (durability.getValBoolean() && mainHandItem.isItemStackDamageable())) {
                posX += 16;
            }
        }

        int armorCount2 = 3;
        for (int i = 0; i <= 3; i++) {
            ItemStack itemStack = entityPlayer.inventory.armorInventory.get(armorCount2);

            if (!itemStack.isEmpty()) {
                int armorY = findArmorY(posY);

                if (items.getValBoolean()) {
                    renderItem(itemStack, posX, armorY, posY);
                    armorY -= 32;
                }

                if (durability.getValBoolean() && itemStack.isItemStackDamageable()) {
                    renderItemDurability(itemStack, posX, armorY);
                }
                posX += 16;
            }
            armorCount2--;
        }

        if (!offHandItem.isEmpty()) {
            int armorY = findArmorY(posY);

            if (items.getValBoolean()) {
                renderItem(offHandItem, posX, armorY, posY);
                armorY -= 32;
            }

            if (durability.getValBoolean() && offHandItem.isItemStackDamageable()) {
                renderItemDurability(offHandItem, posX, armorY);
            }
        }
    }

    private int findArmorY(int posY) {
        int posY2 = items.getValBoolean() ? -26 : -27;
        if (posY > 4) {
            posY2 -= (posY - 4) * 8;
        }

        return posY2;
    }

    private void renderItemName(ItemStack itemStack, int posY) {
        GlStateManager.enableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        mc.fontRenderer.drawStringWithShadow(itemStack.getDisplayName(), -mc.fontRenderer.getStringWidth(itemStack.getDisplayName()) / 2, posY, new Colour(255, 255, 255).getRGB());
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
    }

    private void renderItemDurability(ItemStack itemStack, int posX, int posY) {
        float damagePercent = (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (float) itemStack.getMaxDamage();

        float green = damagePercent;
        if (green > 1) green = 1;
        else if (green < 0) green = 0;

        float red = 1 - green;

        GlStateManager.enableTexture2D();
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        mc.fontRenderer.drawStringWithShadow((int) (damagePercent * 100) + "%", posX * 2, posY, new Colour((int) (red * 255), (int) (green * 255), 0).getRGB());
        GlStateManager.popMatrix();
        GlStateManager.disableTexture2D();
    }

    private void renderItem(ItemStack itemStack, int posX, int posY, int posY2) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();

        final int posY3 = (posY2 > 4) ? ((posY2 - 4) * 8 / 2) : 0;

        mc.getRenderItem().zLevel = -150.0f;
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, posX, posY + posY3);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, itemStack, posX, posY + posY3);
        RenderHelper.disableStandardItemLighting();
        mc.getRenderItem().zLevel = 0.0f;
        RenderUtil.prepare();
        GlStateManager.pushMatrix();
        GlStateManager.scale(.5, .5, .5);
        renderEnchants(itemStack, posX, posY - 24);
        GlStateManager.popMatrix();
    }

    private void renderEnchants(ItemStack itemStack, int posX, int posY) {
        GlStateManager.enableTexture2D();

        for (Enchantment enchantment : EnchantmentHelper.getEnchantments(itemStack).keySet()) {
            if (enchantment == null) {
                continue;
            }

            if (ench.getValBoolean()) {
                int level = EnchantmentHelper.getEnchantmentLevel(enchantment, itemStack);
                mc.fontRenderer.drawStringWithShadow(findStringForEnchants(enchantment, level), posX * 2, posY, new Colour(255, 255, 255).getRGB());
            }
            posY += 8;
        }

        if (itemStack.getItem().equals(Items.GOLDEN_APPLE) && itemStack.hasEffect()) {
            mc.fontRenderer.drawStringWithShadow("God", posX * 2, posY, new Colour(195, 77, 65).getRGB());
        }

        GlStateManager.disableTexture2D();
    }

    private String findStringForEnchants(Enchantment enchantment, int level) {
        ResourceLocation resourceLocation = Enchantment.REGISTRY.getNameForObject(enchantment);

        String string = resourceLocation == null ? enchantment.getName() : resourceLocation.toString();

        int charCount = (level > 1) ? 12 : 13;

        if (string.length() > charCount) {
            string = string.substring(10, charCount);
        }

        return string.substring(0, 1).toUpperCase() + string.substring(1) + ColorUtil.settingToTextFormatting(color) + ((level > 1) ? level : "");
    }
}
