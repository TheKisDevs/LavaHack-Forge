package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.RenderEntityEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.features.subsystem.subsystems.EnemyManager;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.client.interfaces.IFakeEntity;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.render.Render2DUtil;
import com.kisman.cc.util.render.Rendering;
import me.zero.alpine.listener.Listener;
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
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;

@SuppressWarnings("ConstantConditions")
@ModuleInfo(
        name = "NameTags",
        category = Category.RENDER
)
public class NameTags extends Module {
    private final Setting scale = register(new Setting("Scale", this, 0.1f, 0.1f, 0.3f, false));
    private final Setting bgAlpha = register(new Setting("BG Alpha", this, 128, 0, 250, true));
    private final Setting ping = register(new Setting("Ping", this, true));
    private final Setting items = register(new Setting("Items", this, true));
    private final Setting damageDisplay = register(new Setting("Damage Display", this, true));
    private final Setting atheist = register(new Setting("Atheist", this, true));
    private final Setting desc = register(new Setting("Desc", this, false));
    private final Setting noBots = register(new Setting("No Bots", this, false));
    private final Setting self = register(new Setting("Self", this, false));

    @ModuleInstance
    public static NameTags instance;

    private final HashMap<String, Integer> tagList = new HashMap<>();
    private final HashMap<String, String> damageList = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        Kisman.EVENT_BUS.subscribe(renderEntity);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(renderEntity);
    }

    private final Listener<RenderEntityEvent.All.Post> renderEntity = new Listener<>(event -> {
        if(event.getEntity() instanceof EntityPlayer && !(event.getEntity() instanceof IFakeEntity) && (event.getEntity() != mc.player || (self.getValBoolean() && mc.gameSettings.thirdPersonView != 0))) {
            EntityPlayer player = (EntityPlayer) event.getEntity();

            int ping = -1;

            try {
                ping = mc.player.connection.getPlayerInfo(player.getUniqueID()).getResponseTime();
            } catch (NullPointerException ignored) {
                if(noBots.getValBoolean()) return;
            }

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

            double pX = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks;
            double pY = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks;
            double pZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks;
            Entity renderEntity = mc.renderManager.renderViewEntity;
            if (renderEntity == null) renderEntity = mc.player;
            if (renderEntity == null) return;
            double rX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * mc.timer.renderPartialTicks;
            double rY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * mc.timer.renderPartialTicks;
            double rZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * mc.timer.renderPartialTicks;

            renderNametag(player, pX - rX, pY - rY, pZ - rZ, ping);
        }
    });

    public void renderNametag(EntityPlayer player, double x, double y, double z, int ping0) {
        Rendering.setup();
        TextFormatting colorHealth;
        TextFormatting colorStatus = TextFormatting.WHITE;
        String cross = "";
        String playerPing = ping.getValBoolean() ? ping0 + "ms  " : "";
        String damageText = "";
        int lastHealth = 0;
        int health = MathHelper.ceil(player.getHealth() + player.getAbsorptionAmount());
        int width = 0;
        double widthBackGround = bgAlpha.getValDouble();
        float scale0 = 0.016666668f * getNametagSize(player);
        boolean damageDisplay = this.damageDisplay.getValBoolean();

        if (FriendManager.instance.isFriend(player.getName())) {
            colorStatus = TextFormatting.AQUA;
            if (!atheist.getValBoolean()) cross = "\u271d ";
        } else if(EnemyManager.INSTANCE.enemy(player)) {
            colorStatus = TextFormatting.RED;
        }

        if (health > 16) colorHealth = TextFormatting.GREEN;
        else if (health > 12) colorHealth = TextFormatting.YELLOW;
        else if (health > 8) colorHealth = TextFormatting.GOLD;
        else if (health > 5) colorHealth = TextFormatting.RED;
        else colorHealth = TextFormatting.DARK_RED;

        try {
            lastHealth = tagList.get(player.getName());
        } catch(Exception ignored) { }

        if (damageDisplay) {
            if (lastHealth > health) this.damageList.put(player.getName(), TextFormatting.RED + " -" + (lastHealth - health));
            tagList.put(player.getName(), health);
        }

        try {
            if (damageDisplay && damageList.containsKey(player.getName())) damageText = damageList.get(player.getName());
        } catch(Exception ignored) {}

        String name = cross + colorStatus + playerPing + player.getName() + " " + colorHealth + health + damageText;
        name = name.replace(".0", "");

        width = mc.fontRenderer.getStringWidth(name) / 2;

        GL11.glTranslated(x, y + 2.5 + scale0 * 10.0f, z);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(-scale0, -scale0, scale0);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GL11.glDisable(2929);

        Render2DUtil.drawSmoothRect((float)(-width - 3), 9.0f, (float)(width + 4), 23.0f, new Color(0, 0, 0, (int)widthBackGround).getRGB());
        mc.fontRenderer.drawStringWithShadow(name, -width, 9 + 7 - (mc.fontRenderer.FONT_HEIGHT) / 2f, Color.red.getRGB());

        if (items.getValBoolean()) {
            int xOffset = -8;

            for (ItemStack armourStack : player.inventory.armorInventory) if (armourStack != null) xOffset -= 8;

            if (!player.getHeldItemMainhand().isEmpty()) {
                xOffset -= 8;
                ItemStack renderStack = player.getHeldItemMainhand().copy();
                renderItem(renderStack, xOffset, -10);
                xOffset += 16;
            }

            for (int index = 3; index >= 0; --index) {
                ItemStack armourStack2 = player.inventory.armorInventory.get(index);

                if (!armourStack2.isEmpty()) {
                    ItemStack renderStack2 = armourStack2.copy();
                    renderItem(renderStack2, xOffset, -10);
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

    public float getNametagSize(EntityLivingBase player) {
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