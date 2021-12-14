package com.kisman.cc.module.render;

import com.google.common.collect.Lists;
import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.EventRender2D;
import com.kisman.cc.event.events.EventRenderEntityName;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontUtil;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.sun.javafx.geom.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import kisman.pasta.salhack.util.customfont.FontManager;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySpectralArrow;
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
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;

public class NameTags extends Module {
    private Setting range = new Setting("Range", this, 0, 50 ,100, false);
    private Setting scale = new Setting("Scale", this, 0.8, 0.1, 1.5, false);
    private Setting yPos = new Setting("YPos", this, 0.1, 0, 1.5, false);
//    private Setting color = new Setting("Color", this, "Color", new float[] {1, 1, 1, 1}, false);
    private Setting bg = new Setting("BackGround", this, true);
    private Setting bgLight = new Setting("BGLight", this, 15, 0, 100, true);
    private Setting bgAlpha = new Setting("BGAlpha", this, 0, 0, 30, true);
    private Setting outline = new Setting("OutLine", this, true);
    private Setting outlineColor = new Setting("OutlineColor", this, "OutLine", new float[] {0.3f, 0.3f, 0.3f, 1});

    private Setting textR = new Setting("TextR", this, 200, 0, 255, true);
    private Setting textG = new Setting("TextG", this, 200, 0, 255, true);
    private Setting textB = new Setting("TextB", this, 200, 0, 255, true);
    private Setting textA = new Setting("TextA", this, 255, 0, 255, true);
    private Setting textRainbow = new Setting("TextRainBow", this, true);
/*    private Setting rect = new Setting("Rectangle", this, true);
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
    private Setting ench = new Setting("Enchantments", this, true);*/
//    private Setting color = new Setting("Color", this, "Green", ColorUtil.colours);

//    private Setting bcolor = new Setting("BorderedColor", this, "BorderedColor", new float[] {0, 0, 0, 0}, false);

    public static NameTags instance;

    public final List<Entity> collectedEntities = new ArrayList<>();
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);

    public NameTags() {
        super("NameTags", "f", Category.RENDER);

        instance = this;

        setmgr.rSetting(range);
        setmgr.rSetting(scale);
/*        setmgr.rSetting(lineWidth);
        setmgr.rSetting(onlyFov);
        setmgr.rSetting(factor);
        setmgr.rSetting(smallScale);
        setmgr.rSetting(invisibles);
        setmgr.rSetting(scaleing);*/
//        setmgr.rSetting(color);
        setmgr.rSetting(new Setting("BackGroundLine", this, "BackGround"));
        setmgr.rSetting(bg);
        setmgr.rSetting(bgLight);
        setmgr.rSetting(bgAlpha);
        setmgr.rSetting(outline);
        setmgr.rSetting(outlineColor);

        setmgr.rSetting(new Setting("TextLine", this, "Text"));
        setmgr.rSetting(textR);
        setmgr.rSetting(textG);
        setmgr.rSetting(textB);
        setmgr.rSetting(textA);
        setmgr.rSetting(textRainbow);
        /*setmgr.rSetting(rect);
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
        setmgr.rSetting(ench);*/
    }

    public void onEnable() {
        Kisman.EVENT_BUS.subscribe(listener);
        Kisman.EVENT_BUS.subscribe(listener1);
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
        Kisman.EVENT_BUS.unsubscribe(listener1);
    }

    @EventHandler
    private final Listener<EventRender2D> listener1 = new Listener<>(event -> {
        float partialTicks = event.getPartialTicks();
        GL11.glPushMatrix();
        collectEntities();
        ScaledResolution sr = event.getResolution();
        int scaleFactor = sr.getScaleFactor();
        double scaling = (double)scaleFactor / Math.pow(scaleFactor, 1.0D);
        GL11.glScaled(scaling, scaling, scaling);
        float upscale = 1 / (float) scale.getValDouble();
        RenderManager renderMng = mc.getRenderManager();
        EntityRenderer entityRenderer = mc.entityRenderer;
        List<Entity> collectedEntities = this.collectedEntities;
        int i = 0;

        for(int collectedEntitiesSize = collectedEntities.size(); i < collectedEntitiesSize; ++i) {
            Entity entity = collectedEntities.get(i);
            if (this.isValid(entity) && RenderUtil.isInViewFrustrum(entity)) {
                double x;
                double y;
                double z;
                double width;
                double height;
                label191: {
                    x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
                    y = RenderUtil.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
                    z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
                    width = (double)entity.width / 1.5D;
                    height = entity.height;
                    if (!entity.isSneaking()) {
                        label188: {
                            if (entity == mc.player) {
                                if (mc.player.isSneaking()) {
                                    break label188;
                                }
                            }
                        }
                    }
                }

                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                Vec3d[] vectors = new Vec3d[]{new Vec3d(aabb.minX, aabb.minY, aabb.minZ), new Vec3d(aabb.minX, aabb.maxY, aabb.minZ), new Vec3d(aabb.maxX, aabb.minY, aabb.minZ), new Vec3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vec3d(aabb.minX, aabb.minY, aabb.maxZ), new Vec3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vec3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vec3d(aabb.maxX, aabb.maxY, aabb.maxZ)};
                entityRenderer.setupCameraTransform(upscale, collectedEntitiesSize);
                Vec4d position = null;
                int var33 = vectors.length;

                for(int j = 0; j < var33; ++j) {
                    Vec3d vector = vectors[j];
                    vector = this.project2D(scaleFactor, vector.x - renderMng.viewerPosX, vector.y - renderMng.viewerPosY, vector.z - renderMng.viewerPosZ);
                    if (vector != null && vector.z >= 0.0D && vector.z < 1.0D) {
                        if (position == null) {
                            position = new Vec4d(vector.x, vector.y, vector.z, 0.0D);
                        }

                        position.x = Math.min(vector.x, position.x);
                        position.y = Math.min(vector.y, position.y);
                        position.z = Math.max(vector.x, position.z);
                        position.w = Math.max(vector.y, position.w);
                    }
                }

                if (position != null) {
                    entityRenderer.setupOverlayRendering();
                    double posX = position.x;
                    double posY = position.y;
                    double endPosX = position.z;
                    double endPosY = position.w;

                    boolean living = entity instanceof EntityLivingBase;
                    float itemDurability;
                    double durabilityWidth;
                    double diff1;
                    float tagX;

                    EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                    float hp2 = entityLivingBase.getHealth();
                    itemDurability = entityLivingBase.getMaxHealth();
                    durabilityWidth = hp2 / itemDurability;

                    int maxDamage;

                    float scaledHeight = 17.0F;
                    String name = entity.getName();

                    if (entity instanceof EntityItem) {
                        name = ((EntityItem)entity).getItem().getDisplayName();
                    }

                    maxDamage = ColorUtils.getColor(textR.getValInt(), textG.getValInt(), textB.getValInt(), textA.getValInt());

                    if (textRainbow.getValBoolean()) {
                        maxDamage = ColorUtils.rainbow(1, 10);
                    }

                    durabilityWidth = (endPosX - posX) / 2.0D;
                    StringBuilder var56 = (new StringBuilder()).append(name).append(" §7");
                    diff1 = (float) mc.fontRenderer.getStringWidth(var56.append((int)mc.player.getDistance(entity)).append("32").toString()) * scale.getValDouble() - 2;
                    tagX = (float)((posX + durabilityWidth - diff1 / 2.0D) * (double)upscale);
                    float tagY = (float)(posY * (double)upscale) - scaledHeight;
                    GL11.glPushMatrix();
                    GL11.glScaled(scale.getValDouble(), scale.getValDouble(), scale.getValDouble());
                    if (living) {
                        if (bg.getValBoolean()) {
                            Render2DUtil.drawRect(tagX - 1.0F, tagY - 1.0F, (double)tagX + diff1 * (double)upscale + 1.0D, tagY + 8.0F, ColorUtils.getColor(bgLight.getValInt(), 26 + bgAlpha.getValInt()));
                            Render2DUtil.drawRect(tagX - 2.5F, tagY - 2.5F, (double)tagX + diff1 * (double)upscale + 2.5D, tagY + 9.5F, ColorUtils.getColor(bgLight.getValInt(), 26 + bgAlpha.getValInt()));
                            Render2DUtil.drawRect(tagX - 3.0F, tagY - 3.0F, (double)tagX + diff1 * (double)upscale + 3.0D, tagY + 10.0F, ColorUtils.getColor(bgLight.getValInt(), 26 + bgAlpha.getValInt()));
                            Render2DUtil.drawRect(tagX - 3.5F, tagY - 3.5F, (double)tagX + diff1 * (double)upscale + 3.5D, tagY + 10.5F, ColorUtils.getColor(bgLight.getValInt(), 26 + bgAlpha.getValInt()));
                            Render2DUtil.drawRect(tagX - 4.0F, tagY - 4.0F, (double)tagX + diff1 * (double)upscale + 4.0D, tagY + 11.0F, ColorUtils.getColor(bgLight.getValInt(), 26 + bgAlpha.getValInt()));
                        }
                    }

                    var56 = (new StringBuilder()).append(name).append(" §6");
                    mc.fontRenderer.drawStringWithShadow(var56.append((int) mc.player.getDistance(entity)).append("m").toString(), (float) ((double)tagX + 0.5D), tagY + 0.5F, maxDamage);
                    GL11.glPopMatrix();
                }
            }
        }
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GlStateManager.enableBlend();
        entityRenderer.setupOverlayRendering();
    });

    @EventHandler
    private final Listener<EventRenderEntityName> listener = new Listener<>(event -> {
        event.cancel();
    });

    private boolean isValid(Entity entity) {
        if (mc.gameSettings.thirdPersonView == 0) {
            if (entity == mc.player) {
                return false;
            }
        }

        if (entity.isDead) {
            return false;
        } else if (entity instanceof EntityAnimal) {
            return false;
        } else if (entity instanceof EntityPlayer) {
            return true;
        } else if (entity instanceof EntityArmorStand) {
            return false;
        } else if (entity instanceof IAnimals) {
            return false;
        } else if (entity instanceof EntityItemFrame) {
            return false;
        } else if (!(entity instanceof EntityArrow) && !(entity instanceof EntitySpectralArrow)) {
            if (entity instanceof EntityMinecart) {
                return false;
            } else if (entity instanceof EntityBoat) {
                return false;
            } else if (entity instanceof EntityDragonFireball) {
                return false;
            } else if (entity instanceof EntityXPOrb) {
                return false;
            } else if (entity instanceof EntityMinecartChest) {
                return false;
            } else if (entity instanceof EntityTNTPrimed) {
                return false;
            } else if (entity instanceof EntityMinecartTNT) {
                return false;
            } else if (entity instanceof EntityVillager) {
                return false;
            } else if (entity instanceof EntityExpBottle) {
                return false;
            } else if (entity instanceof EntityLightningBolt) {
                return false;
            } else if (entity instanceof EntityPotion) {
                return false;
            } else if (entity instanceof Entity) {
                return false;
            } else if (!(entity instanceof EntityMob) && !(entity instanceof EntitySlime) && !(entity instanceof EntityDragon) && !(entity instanceof EntityGolem)) {
                return entity != mc.player;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void collectEntities() {
        this.collectedEntities.clear();
        List<Entity> playerEntities = mc.world.loadedEntityList;
        int i = 0;

        for(int playerEntitiesSize = playerEntities.size(); i < playerEntitiesSize; ++i) {
            Entity entity = playerEntities.get(i);
            if (this.isValid(entity)) {
                this.collectedEntities.add(entity);
            }
        }

    }

    private Vec3d project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, this.modelview);
        GL11.glGetFloat(2983, this.projection);
        GL11.glGetInteger(2978, this.viewport);
        return GLU.gluProject((float)x, (float)y, (float)z, this.modelview, this.projection, this.viewport, this.vector) ? new Vec3d(this.vector.get(0) / (float)scaleFactor, ((float) Display.getHeight() - this.vector.get(1)) / (float)scaleFactor, this.vector.get(2)) : null;
    }
}
