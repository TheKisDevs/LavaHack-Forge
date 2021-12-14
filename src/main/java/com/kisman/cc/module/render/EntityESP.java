package com.kisman.cc.module.render;

import com.kisman.cc.event.events.EventRender2D;
import com.kisman.cc.event.events.EventRenderEntityName;
import com.kisman.cc.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Render2DUtil;
import com.kisman.cc.util.RenderUtil;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.kisman.cc.Kisman;

import com.kisman.cc.util.shaders.*;
import com.sun.javafx.geom.Vec4d;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import me.zero.alpine.listener.*;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.*;
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
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL11.*;

public class EntityESP extends Module{
    private Setting range = new Setting("Range", this, 50, 0, 100, true);

    //modes
    private Setting playerMode = new Setting("Players", this, "None", new ArrayList<>(Arrays.asList("None", "Box1", "Box2", "2D", "2D #2", "Glow")));

    //colors
    private Setting playerColor = new Setting("PlayerColor", this, "PlayerColor", new float[] {0.54f, 0.11f, 0.92f, 1});
    private Setting playerShader = new Setting("PlayerShader", this, false);
    private Setting glowPlayer = new Setting("GlowPlayer", this, false);
    private Setting quality = new Setting("Quality", this, 1, 0, 5, false);
    private Setting radius = new Setting("Radius", this, 2, 0, 5, false);
    private Setting monstersColor = new Setting("MonstersColor", this, "MonsterColor", new float[] {0.11f, 0.92f, 0.73f, 1});
    private Setting itemsColor = new Setting("ItemsColor", this, "ItemsColor", new float[] {0.11f, 0.51f, 0.92f, 1});
    private Setting entityColor = new Setting("EntityColor", this, "EntityColor", new float[] {0.92f, 0.57f, 0.11f, 1});

    //2d
    private Setting twodimetsionespline = new Setting("2DESP", this, "2D ESP setting");

    private Setting box = new Setting("Box", this, true);
    private Setting tags = new Setting("Tags", this, false);
    private Setting health = new Setting("Health", this, true);
/*    private Setting hunger = new Setting("Hunger", this, false);
    private Setting health = new Setting("Health", this, true);
    private Setting healthValue = new Setting("HealthValue", this, true);
    private Setting box = new Setting("Box", this, true);
    private Setting tag = new Setting("Tag", this, true);
    private Setting currentItem = new Setting("CurrentItem", this, false);
    private Setting local = new Setting("Local", this, false);*/

    public final ArrayList<Entity> collectedEntities = new ArrayList<>();
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
    private final int backgroundColor = (new Color(0, 0, 0, 120)).getRGB();
    private final int black = Color.BLACK.getRGB();

    public EntityESP() {
        super("EntityESP", "esp 1", Category.RENDER);

        //TODO: optimisate settings

        setmgr.rSetting(range);

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, 100, 10, 260, true));

        Kisman.instance.settingsManager.rSetting(new Setting("PlayersLine", this, "Players"));
        setmgr.rSetting(playerColor);
        setmgr.rSetting(playerMode);
        setmgr.rSetting(playerShader);
        setmgr.rSetting(glowPlayer);
        setmgr.rSetting(quality);
        setmgr.rSetting(radius);

        setmgr.rSetting(twodimetsionespline);
        setmgr.rSetting(box);
        setmgr.rSetting(tags);
        setmgr.rSetting(health);
        /*setmgr.rSetting(hunger);
        setmgr.rSetting(health);
        setmgr.rSetting(healthValue);
        setmgr.rSetting(box);
        setmgr.rSetting(tag);
        setmgr.rSetting(currentItem);
        setmgr.rSetting(local);*/

        Kisman.instance.settingsManager.rSetting(new Setting("MonstersLine", this, "Monsters"));
        setmgr.rSetting(monstersColor);
        Kisman.instance.settingsManager.rSetting(new Setting("Monsters", this, "None", new ArrayList<>(Arrays.asList("None", "Box1", "Box2", "Glow"))));

        Kisman.instance.settingsManager.rSetting(new Setting("ItemsLine", this, "Items"));
        Kisman.instance.settingsManager.rSetting(new Setting("Items", this, "None", new ArrayList<>(Arrays.asList("None", "Box1", "Box2", "Glow"))));
        setmgr.rSetting(itemsColor);

        Kisman.instance.settingsManager.rSetting(new Setting("EntityLine", this, "Entity"));
        Kisman.instance.settingsManager.rSetting(new Setting("Entity", this, "None", new ArrayList<>(Arrays.asList("None", "Box1", "Box2", "Glow"))));
        setmgr.rSetting(entityColor);
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
        if(mc.player == null && mc.player == null && !playerMode.getValString().equalsIgnoreCase("2D #2")) {
            return;
        }

        float partialTicks = event.getPartialTicks();
        GL11.glPushMatrix();
        this.collectEntities();
        ScaledResolution sr = event.getResolution();
        int scaleFactor = sr.getScaleFactor();
        double scaling = (double)scaleFactor / Math.pow(scaleFactor, 1.0D);
        GL11.glScaled(scaling, scaling, scaling);
        int black = this.black;
        int background = this.backgroundColor;
        float scale = 0.5F;
        float upscale = 1.0F / scale;
        RenderManager renderMng = mc.getRenderManager();
        EntityRenderer entityRenderer = mc.entityRenderer;
        ArrayList<Entity> collectedEntities = this.collectedEntities;
        int i = 0;

        for(Entity entity : collectedEntities) {
            if (this.isValid(entity) && RenderUtil.isInViewFrustrum(entity)) {
                double var10000;
                double x;
                double y;
                double z;
                double width;
                double var55;
                label191: {
                    x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, partialTicks);
                    y = RenderUtil.interpolate(entity.posY, entity.lastTickPosY, partialTicks);
                    z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, partialTicks);
                    width = (double)entity.width / 1.5D;
                    var10000 = entity.height;
                    if (!entity.isSneaking()) {
                        label188: {
                            if (entity == mc.player) {
                                if (mc.player.isSneaking()) {
                                    break label188;
                                }
                            }

                            var55 = 0.1D;
                            break label191;
                        }
                    }

                    var55 = -0.0D;
                }

                double height = var10000 + var55;
                AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                Vec3d[] vectors = new Vec3d[]{new Vec3d(aabb.minX, aabb.minY, aabb.minZ), new Vec3d(aabb.minX, aabb.maxY, aabb.minZ), new Vec3d(aabb.maxX, aabb.minY, aabb.minZ), new Vec3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vec3d(aabb.minX, aabb.minY, aabb.maxZ), new Vec3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vec3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vec3d(aabb.maxX, aabb.maxY, aabb.maxZ)};
                entityRenderer.setupCameraTransform(upscale, collectedEntities.indexOf(entity));
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
                    if (box.getValBoolean()) {
                        Render2DUtil.drawRect(posX + 0.5D, posY, posX - 1.0D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                        Render2DUtil.drawRect(posX - 1.0D, endPosY, posX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                        Render2DUtil.drawRect(posX - 1.0D, posY - 0.5D, posX + (endPosX - posX) / 3.0D + 0.5D, posY + 1.0D, black);
                        Render2DUtil.drawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, posY - 0.5D, endPosX, posY + 1.0D, black);
                        Render2DUtil.drawRect(endPosX - 1.0D, posY, endPosX + 0.5D, posY + (endPosY - posY) / 4.0D + 0.5D, black);
                        Render2DUtil.drawRect(endPosX - 1.0D, endPosY, endPosX + 0.5D, endPosY - (endPosY - posY) / 4.0D - 0.5D, black);
                        Render2DUtil.drawRect(posX - 1.0D, endPosY - 1.0D, posX + (endPosX - posX) / 3.0D + 0.5D, endPosY + 0.5D, black);
                        Render2DUtil.drawRect(endPosX - (endPosX - posX) / 3.0D - 0.5D, endPosY - 1.0D, endPosX + 0.5D, endPosY + 0.5D, black);
                        Render2DUtil.drawRect(posX, posY, posX - 0.5D, posY + (endPosY - posY) / 4.0D, ColorUtils.astolfoColors(1, 10));
                        Render2DUtil.drawRect(posX, endPosY, posX - 0.5D, endPosY - (endPosY - posY) / 4.0D, ColorUtils.astolfoColors(1, 10));
                        Render2DUtil.drawRect(posX - 0.5D, posY, posX + (endPosX - posX) / 3.0D, posY + 0.5D, ColorUtils.astolfoColors(1, 10));
                        Render2DUtil.drawRect(endPosX - (endPosX - posX) / 3.0D, posY, endPosX, posY + 0.5D, ColorUtils.astolfoColors(1, 10));
                        Render2DUtil.drawRect(endPosX - 0.5D, posY, endPosX, posY + (endPosY - posY) / 4.0D, ColorUtils.astolfoColors(1, 10));
                        Render2DUtil.drawRect(endPosX - 0.5D, endPosY, endPosX, endPosY - (endPosY - posY) / 4.0D, ColorUtils.astolfoColors(1, 10));
                        Render2DUtil.drawRect(posX, endPosY - 0.5D, posX + (endPosX - posX) / 3.0D, endPosY, ColorUtils.astolfoColors(1, 10));
                        Render2DUtil.drawRect(endPosX - (endPosX - posX) / 3.0D, endPosY - 0.5D, endPosX - 0.5D, endPosY, ColorUtils.astolfoColors(1, 10));

                    }
                    boolean living = entity instanceof EntityLivingBase;
                    float itemDurability;
                    double durabilityWidth;
                    double diff1;
                    float tagX;
                    if (living && health.getValBoolean()) {
                        EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                        float hp2 = entityLivingBase.getHealth();
                        itemDurability = entityLivingBase.getMaxHealth();
                        durabilityWidth = hp2 / itemDurability;
                        diff1 = (endPosY - posY) * durabilityWidth;
                        Render2DUtil.drawRect(posX - 10.0D, posY - 1.5D, posX - 3.0D, endPosY + 1.5D, background);
                        if (hp2 > 0.1F) {
                            tagX = entityLivingBase.getAbsorptionAmount();
                            int healthColor2 = (int)hp2;
                            if (hp2 > 8.0F) {
                                healthColor2 = ColorUtils.astolfoColors(1, 1);
                            } else if (hp2 < 12.0F) {
                                healthColor2 = (new Color(255, 72, 72)).getRGB();
                            }

                            Render2DUtil.drawRect(posX - 8.0f, endPosY, posX - 5.0D, endPosY - diff1, healthColor2);

                        }
                    }

                    int maxDamage;
                    if (living && tags.getValBoolean() && !NameTags.instance.isToggled()) {
                        float scaledHeight = 16.0F;
                        String name = entity.getName();



                        if (entity instanceof EntityItem) {
                            name = ((EntityItem)entity).getItem().getDisplayName();
                        }

                        maxDamage = ColorUtils.rainbow(1, 10);
                        durabilityWidth = (endPosX - posX) / 2.0D;
                        StringBuilder var56 = (new StringBuilder()).append(name).append(" §7");
                        diff1 = (float)mc.fontRenderer.getStringWidth(var56.append((int)mc.player.getDistance(entity)).append("32").toString()) * scale;
                        tagX = (float)((posX + durabilityWidth - diff1 / 2.0D) * (double)upscale);
                        float tagY = (float)(posY * (double)upscale) - scaledHeight;
                        GL11.glPushMatrix();
                        GL11.glScalef(scale, scale, scale);
                        if (living) {
                            Render2DUtil.drawRect(tagX - 2.0F, tagY - 2.0F, (double)tagX + diff1 * (double)upscale + 2.0D, tagY + 9.0F, (new Color(0, 0, 0, 140)).getRGB());
                        }

                        var56 = (new StringBuilder()).append(name).append(" §7");
                        mc.fontRenderer.drawStringWithShadow(var56.append((int)mc.player.getDistance(entity)).append("m").toString(), (float) ((double)tagX + 0.5D), tagY + 0.5F, maxDamage);
                        GL11.glPopMatrix();
                    }
                }
            }
            GL11.glPopMatrix();
            GL11.glEnable(2929);
            GlStateManager.enableBlend();
            entityRenderer.setupOverlayRendering();
        }
    });

    @SubscribeEvent
    public void onRenderWorld(RenderGameOverlayEvent event) {
        if(mc.player == null && mc.world == null) return;

        if(event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            GL11.glPushMatrix();

            if(playerShader.getValBoolean()) {
                FlowShader.INSTANCE.startDraw(event.getPartialTicks());
                mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer && e != mc.player).forEach(e -> mc.renderManager.renderEntityStatic(e, event.getPartialTicks(), true));
                FlowShader.INSTANCE.stopDraw(Color.WHITE, 1, 1);
                FlowShader.INSTANCE.stopDraw(new Color(playerColor.getR(), playerColor.getG(), playerColor.getB(), playerColor.getA()), (float) radius.getValDouble(), (float) quality.getValDouble());
            }

            if(glowPlayer.getValBoolean()) {
                GlowShader.INSTANCE.startDraw(event.getPartialTicks());
                mc.world.loadedEntityList.stream().filter(e -> e instanceof EntityPlayer && e != mc.player).forEach(e -> mc.renderManager.renderEntityStatic(e, event.getPartialTicks(), true));
                GlowShader.INSTANCE.stopDraw(new Color(playerColor.getR(), playerColor.getG(), playerColor.getB(), playerColor.getA()), (float) radius.getValDouble(), (float) quality.getValDouble());
            }

            GL11.glPopMatrix();
        }
    }

    @EventHandler
    private final Listener<EventRenderEntityName> listener = new Listener<>(event -> {
        if(playerMode.getValString().equalsIgnoreCase("2D #2")) {
            if(tags.getValBoolean()) {
                event.cancel();
            }
        }
    });

    public void onRenderWorld(RenderWorldLastEvent e) {
        for(EntityPlayer p : mc.world.playerEntities) {
            if(p == mc.player) continue;

            glPushMatrix();

            double x = p.lastTickPosX + (p.posX - p.lastTickPosX) * e.getPartialTicks() - mc.getRenderManager().viewerPosX;
            double y = p.lastTickPosY + (p.posY - p.lastTickPosY) * e.getPartialTicks() - mc.getRenderManager().viewerPosY+1;
            double z = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * e.getPartialTicks() - mc.getRenderManager().viewerPosZ;

            glTranslated(x, y, z);

            glRotated(-mc.getRenderManager().playerViewY, 0, 1, 0);
            glRotated(mc.getRenderManager().playerViewX, 1, 0, 0);

            glDisable(GL_DEPTH_TEST);

            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable(GL_LINE_SMOOTH);

            glLineWidth(2);

            glBegin(GL_LINE_LOOP);
            glColor4d(1, 1, 1, 1);
            glVertex2d(0.4, 1);
            glVertex2d(0.4, -1);
            glVertex2d(-0.4, -1);
            glVertex2d(-0.4, 1);

            glEnd();

            glEnable(GL_DEPTH_TEST);

            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glDisable(GL_LINE_SMOOTH);

            glPopMatrix();
        }
        /*for (EntityPlayer player : mc.world.playerEntities) {
            if (!player.isEntityEqual(mc.player)){
                switch(playerMode.getValString()) {
                    case "Glow": {
                        player.glowing = true;
                    }
                    case "Box1": {
                        player.glowing = false;
                        RenderUtil.drawESP(player, playerColor.getR() / 255, playerColor.getG() / 255, playerColor.getB() / 255, playerColor.getA() / 255, event.getPartialTicks());
                    }
                    case "2D": {
                        player.glowing = false;
                        draw2D(event, player);
                    }
                }
            }
        }*/
    }

    private void draw2D(RenderWorldLastEvent e, EntityPlayer p) {
        glPushMatrix();

        double x = p.lastTickPosX + (p.posX - p.lastTickPosX) * e.getPartialTicks() - mc.getRenderManager().viewerPosX;
        double y = p.lastTickPosY + (p.posY - p.lastTickPosY) * e.getPartialTicks() - mc.getRenderManager().viewerPosY+1;
        double z = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * e.getPartialTicks() - mc.getRenderManager().viewerPosZ;

        glTranslated(x, y, z);

        glRotated(-mc.getRenderManager().playerViewY, 0, 1, 0);
        glRotated(mc.getRenderManager().playerViewX, 1, 0, 0);

        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);

        glLineWidth(2);

        glBegin(GL_LINE_LOOP);
        glColor4d(1, 1, 1, 1);
        glVertex2d(0.4, 1);
        glVertex2d(0.4, -1);
        glVertex2d(-0.4, -1);
        glVertex2d(-0.4, 1);

        glEnd();

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);

        glPopMatrix();
    }

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
        int i = 0;

        for(EntityPlayer player : mc.world.playerEntities) {
            if (this.isValid(player)) {
                this.collectedEntities.add(player);
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
