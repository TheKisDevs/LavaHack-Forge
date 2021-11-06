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
    private Setting range = new Setting("Range", this, 50, 0, 100, true);

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

        setmgr.rSetting(range);

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
}
