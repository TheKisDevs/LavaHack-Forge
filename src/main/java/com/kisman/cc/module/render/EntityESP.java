package com.kisman.cc.module.render;

import com.kisman.cc.event.events.EventRenderEntityName;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import com.kisman.cc.Kisman;

import com.kisman.cc.util.customfont.CustomFontUtil;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static org.lwjgl.opengl.GL11.*;

public class EntityESP extends Module{
    //modes
    private Setting playerMode = new Setting("Players", this, "None", new ArrayList<>(Arrays.asList("None", "Box1", "Box2", "2D", "Glow")));

    //colors
    private Setting playerColor = new Setting("PlayerColor", this, "PlayerColor", new float[] {0.54f, 0.11f, 0.92f, 1});
    private Setting monstersColor = new Setting("MonstersColor", this, "MonsterColor", new float[] {0.11f, 0.92f, 0.73f, 1});
    private Setting itemsColor = new Setting("ItemsColor", this, "ItemsColor", new float[] {0.11f, 0.51f, 0.92f, 1});
    private Setting entityColor = new Setting("EntityColor", this, "EntityColor", new float[] {0.92f, 0.57f, 0.11f, 1});

    //2d
    private Setting twodimetsionespline = new Setting("2DESP", this, "2D ESP setting");
    private Setting hunger = new Setting("Hunger", this, false);
    private Setting health = new Setting("Health", this, true);
    private Setting healthValue = new Setting("HealthValue", this, true);
    private Setting box = new Setting("Box", this, true);
    private Setting tag = new Setting("Tag", this, true);
    private Setting currentItem = new Setting("CurrentItem", this, false);
    private Setting local = new Setting("Local", this, false);

    public EntityESP() {
        super("EntityESP", "esp 1", Category.RENDER);

        //TODO: optimisate settings

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, 100, 10, 260, true));

        Kisman.instance.settingsManager.rSetting(new Setting("PlayersLine", this, "Players"));
        setmgr.rSetting(playerColor);
        setmgr.rSetting(playerMode);
        setmgr.rSetting(twodimetsionespline);
        setmgr.rSetting(hunger);
        setmgr.rSetting(health);
        setmgr.rSetting(healthValue);
        setmgr.rSetting(box);
        setmgr.rSetting(tag);
        setmgr.rSetting(currentItem);
        setmgr.rSetting(local);

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
    }

    public void onDisable() {
        Kisman.EVENT_BUS.unsubscribe(listener);
    }

    @EventHandler
    private final Listener<EventRenderEntityName> listener = new Listener<>(event -> {
        if(playerMode.getValString().equalsIgnoreCase("2D")) {
            if(tag.getValBoolean()) {
                event.cancel();
            }
        }
    });

    @SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
        float distance = (float) Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();;

        String monstersMode = Kisman.instance.settingsManager.getSettingByName(this, "Monsters").getValString();
        String itemMode = Kisman.instance.settingsManager.getSettingByName(this, "Items").getValString();
        String crystalMode = Kisman.instance.settingsManager.getSettingByName(this, "Entity").getValString();

        mc.world.loadedEntityList.stream()
        .filter(e -> e != mc.player)
        .forEach(e -> {
            if (mc.player.getDistance(e) > distance) {
                return;
            } else {
                if((!playerMode.getValString().equalsIgnoreCase("None")) && e instanceof EntityPlayer) {
                    if(playerMode.getValString().equalsIgnoreCase("Box1")) {
                        if(e.isGlowing())
                            e.setGlowing(false);
                        RenderUtil.drawESP(e, playerColor.getR() / 255, playerColor.getG() / 255, playerColor.getB() / 255, playerColor.getA() / 255, event.getPartialTicks());
                    } else if(playerMode.getValString().equalsIgnoreCase("Glow")) {
                        e.setGlowing(true);
                    } else if(playerMode.getValString().equalsIgnoreCase("Box2")) {
                        RenderUtil.drawColorBox(e.getEntityBoundingBox(), playerColor.getR() / 255, playerColor.getG() / 255, playerColor.getB() / 255, playerColor.getA() / 255);
                    } else if(playerMode.getValString().equalsIgnoreCase("2D")) {
                        draw2D(event, e);
                    }
                } else {
                    if(e.isGlowing()) {
                        e.setGlowing(false);
                    }
                }
                if(!monstersMode.equalsIgnoreCase("None")) {
                    if(e instanceof EntityCreature || e instanceof EntitySlime || e instanceof EntitySquid) {
                        if(monstersMode.equalsIgnoreCase("Box1")) {
                            if(e.isGlowing())
                                e.setGlowing(false);
                            RenderUtil.drawESP(e, monstersColor.getR(), monstersColor.getG(), monstersColor.getB(), monstersColor.getA(), event.getPartialTicks());
                        } else if(monstersMode.equalsIgnoreCase("Glow")) {
                            e.setGlowing(true);
                        } else if(monstersMode.equalsIgnoreCase("Box2")) {
                            RenderUtil.drawColorBox(e.getCollisionBox(e), monstersColor.getR(), monstersColor.getG(), monstersColor.getB(), monstersColor.getA());
                            System.out.println("2");
                        }
                    }
                } else if(monstersMode.equalsIgnoreCase("None"))
                    e.setGlowing(false);
                if((!itemMode.equalsIgnoreCase("None")) && e instanceof EntityItem) {
                    if(itemMode.equalsIgnoreCase("Box1")) {
                        if(e.isGlowing()) {
                            e.setGlowing(false);
                        }
                        RenderUtil.drawESP(e, itemsColor.getR(), itemsColor.getG(), itemsColor.getB(), itemsColor.getA(), event.getPartialTicks());
                    }
                    else if(itemMode.equalsIgnoreCase("Box2")) {
                        if(e.isGlowing()) {
                            e.setGlowing(false);
                        }
                        //RenderUtil.drawBoundingBox(e.getCollisionBoundingBox());
                        RenderUtil.drawColorBox(e.getEntityBoundingBox(), 255, 0, 0, 255);
                        //RenderUtil.drawOutlineBoundingBox(e.getEntityBoundingBox());
                    }
                    else if(itemMode.equalsIgnoreCase("Glow")) {
                        e.setGlowing(true);
                    }
                }
                if((!crystalMode.equalsIgnoreCase("None")) && (e instanceof EntityXPOrb || e instanceof EntityExpBottle || e instanceof EntityEnderCrystal)) {
                    if(crystalMode.equalsIgnoreCase("Box1")) {
                        if(e.isGlowing()) {
                            e.setGlowing(false);
                        }
                        RenderUtil.drawESP(e, entityColor.getR(), entityColor.getG(), entityColor.getB(), entityColor.getA(), event.getPartialTicks());
                    } else if(crystalMode.equalsIgnoreCase("Box2")) {
                        if(e.isGlowing()) {
                            e.setGlowing(false);
                        }
                        RenderUtil.drawBoundingBox(e.getEntityBoundingBox());
                        //RenderUtil.drawColorBox(e.getEntityBoundingBox(), crystalR, crystalG, crystalB, crystalA);
                    }
                }
            }
        });
    }

    private void draw2D(RenderWorldLastEvent event, Entity e) {
        final double x = (e.lastTickPosX + (e.posX - e.lastTickPosX) * event.getPartialTicks()) - mc.getRenderManager().renderPosX;
        final double y = (e.lastTickPosY + (e.posY - e.lastTickPosY) * event.getPartialTicks()) - mc.getRenderManager().renderPosY;
        final double z = (e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * event.getPartialTicks()) - mc.getRenderManager().renderPosZ;
        glPushMatrix();
        glLineWidth(1.3f);
        glTranslated(x, y, z);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glRotated(-mc.getRenderManager().playerViewY, 0, 1, 0);
        //glRotated(mc.getRenderManager().playerViewX, 1, 0, 0);
        if(box.getValBoolean()) {
            glColor4d(0.8, 0.8, 0.8, 255);
            glBegin(GL_LINE_STRIP);
            glVertex3d(0.55, -0.2, 0);
            glVertex3d(0.55, e.height + 0.2, 0);
            glVertex3d(e.width - 1.15, e.height + 0.2, 0);
            glVertex3d(e.width - 1.15, -0.2, 0);
            glVertex3d(0.55, -0.2, 0);
            glEnd();
        }

        if(health.getValBoolean()) {

            Color health = Color.GREEN.darker();
            if(((EntityPlayer)e).getHealth() >= 16){
                health = Color.GREEN.darker();
            } else if(((EntityPlayer)e).getHealth() >= 8 && ((EntityPlayer)e).getHealth() <= 16){
                health = Color.YELLOW;
            } else if(((EntityPlayer)e).getHealth() > 0 && ((EntityPlayer)e).getHealth() <= 8){
                health = Color.RED;
            }
            glBegin(GL_LINE_STRIP);
            glColor4d(1, 1, 1, 1);
            glVertex3d(0.6, -0.2, 0);
            glVertex3d(0.6, e.height + 0.2, 0);
            glEnd();
            glBegin(GL_LINE_STRIP);
            glColor4d(health.getRed() / 255f, health.getGreen() / 255f, health.getBlue() / 255f, health.getAlpha() / 255f);
            glVertex3d(0.6, -0.2, 0);
            glVertex3d(0.6, (((EntityLivingBase) e).getHealth() / ((EntityLivingBase) e).getMaxHealth()) * (e.height + 0.2), 0);
            glVertex3d(0.6, -0.2, 0);


            glEnd();
        } if(hunger.getValBoolean()) {
            glBegin(GL_LINE_STRIP);
            glVertex3d(e.width - 1.20, e.height + 0.2, 0);
            glVertex3d(e.width - 1.20, -0.2, 0);
            glColor4d(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), 255);
            glVertex3d(e.width - 1.20, e.height + 0.2, 0);
            glVertex3d(e.width - 1.20, -0.2, 0);
            glColor4d(255, 255, 255, 255);
            glEnd();
        }
        final float size = 0.013f;
        glScaled(-size, -size, -size);
        if(tag.getValBoolean()){
            glEnable(GL_TEXTURE_2D);
            CustomFontUtil.drawStringWithShadow(e.getName(), 1 - (CustomFontUtil.getStringWidth(e.getName()) / 2), -170, -1);
            glDisable(GL_TEXTURE_2D);
        }if(healthValue.getValBoolean() && health.getValBoolean()){
            glEnable(GL_TEXTURE_2D);
            CustomFontUtil.drawStringWithShadow(String.valueOf((int)((((EntityPlayer)e).getHealth() / ((EntityPlayer)e).getMaxHealth()) * 100)), -50 - CustomFontUtil.getStringWidth(String.valueOf((int)((((EntityPlayer)e).getHealth() / ((EntityPlayer)e).getMaxHealth()) * 100))), (int)((((EntityLivingBase) e).getHealth() / ((EntityLivingBase) e).getMaxHealth()) * (e.height + 0.2)), -1);
            glDisable(GL_TEXTURE_2D);
        }if(currentItem.getValBoolean()){
            if(!(((EntityPlayer)e).getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock) && !(((EntityPlayer)e).getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemAir)){
                glEnable(GL_TEXTURE_2D);
                CustomFontUtil.drawStringWithShadow(((EntityPlayer)e).getHeldItem(EnumHand.MAIN_HAND).getDisplayName(),
                        1 - (CustomFontUtil.getStringWidth(((EntityPlayer)e).getHeldItem(EnumHand.MAIN_HAND).getDisplayName()) / 2), 20, -1);
                glDisable(GL_TEXTURE_2D);
            }
        }
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glPopMatrix();
    }
}
