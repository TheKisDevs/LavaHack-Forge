package com.kisman.cc.module.render;

import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;
import com.kisman.cc.util.SettingUtil;

import java.util.ArrayList;
import java.util.Arrays;

import com.kisman.cc.Kisman;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityESP extends Module{
    public EntityESP() {
        super("EntityESP", "esp 1", Category.RENDER);

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, 100, 10, 260, true));
        //SettingUtil.ColorSetting(this, "Players");
        Kisman.instance.settingsManager.rSetting(new Setting("PlayersLine", this, "Players"));
        Kisman.instance.settingsManager.rSetting(new Setting("PlayersR", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("PlayersG", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("PlayersB", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("PlayersA", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Players", this, "None", new ArrayList<String>(Arrays.asList("None", "Box1", "Box2", "Glow"))));
        SettingUtil.ColorSetting(this, "Monsters");
        Kisman.instance.settingsManager.rSetting(new Setting("MonstersLine", this, "Monsters"));
        Kisman.instance.settingsManager.rSetting(new Setting("MonstersR", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("MonstersG", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("MonstersB", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("MonstersA", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Monsters", this, "None", new ArrayList<>(Arrays.asList("None", "Box1", "Box2", "Glow"))));
        Kisman.instance.settingsManager.rSetting(new Setting("ItemsLine", this, "Items"));
        Kisman.instance.settingsManager.rSetting(new Setting("ItemsR", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("ItemsG", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("ItemsB", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("ItemsA", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Items", this, "None", new ArrayList<>(Arrays.asList("None", "Box1", "Box2", "Glow"))));
        Kisman.instance.settingsManager.rSetting(new Setting("EntityLine", this, "Entity"));
        Kisman.instance.settingsManager.rSetting(new Setting("EntityR", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("EntityG", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("EntityB", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("EntityA", this, 1, 0, 1, false));
        Kisman.instance.settingsManager.rSetting(new Setting("Entity", this, "None", new ArrayList<>(Arrays.asList("None", "Box1", "Box2", "Glow"))));
        
    }

    @SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
        float distance = (float) Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();

        float playerR = (float) Kisman.instance.settingsManager.getSettingByName(this, "MonstersR").getValDouble();
        float playerG = (float) Kisman.instance.settingsManager.getSettingByName(this, "MonstersG").getValDouble();
        float playerB = (float) Kisman.instance.settingsManager.getSettingByName(this, "MonstersB").getValDouble();
        float playerA = (float) Kisman.instance.settingsManager.getSettingByName(this, "MonstersA").getValDouble();

        float monsterR = (float) Kisman.instance.settingsManager.getSettingByName(this, "PlayersR").getValDouble();
        float monsterG = (float) Kisman.instance.settingsManager.getSettingByName(this, "PlayersG").getValDouble();
        float monsterB = (float) Kisman.instance.settingsManager.getSettingByName(this, "PlayersB").getValDouble();
        float monsterA = (float) Kisman.instance.settingsManager.getSettingByName(this, "PlayersA").getValDouble();

        float itemR = (float) Kisman.instance.settingsManager.getSettingByName(this, "itemsR").getValDouble();
        float itemG = (float) Kisman.instance.settingsManager.getSettingByName(this, "itemsG").getValDouble();
        float itemB = (float) Kisman.instance.settingsManager.getSettingByName(this, "itemsB").getValDouble();
        float itemA = (float) Kisman.instance.settingsManager.getSettingByName(this, "itemsA").getValDouble();

        float crystalR = (float) Kisman.instance.settingsManager.getSettingByName(this, "EntityR").getValDouble();
        float crystalG = (float) Kisman.instance.settingsManager.getSettingByName(this, "EntityG").getValDouble();
        float crystalB = (float) Kisman.instance.settingsManager.getSettingByName(this, "EntityB").getValDouble();
        float crystalA = (float) Kisman.instance.settingsManager.getSettingByName(this, "EntityA").getValDouble();

        String playersMode = Kisman.instance.settingsManager.getSettingByName(this, "Players").getValString();
        String monstersMode = Kisman.instance.settingsManager.getSettingByName(this, "Monsters").getValString();
        String itemMode = Kisman.instance.settingsManager.getSettingByName(this, "Items").getValString();
        String crystalMode = Kisman.instance.settingsManager.getSettingByName(this, "Entity").getValString();

        mc.world.loadedEntityList.stream()
        //.filter(e -> e instanceof EntityPlayer)
        .filter(e -> e != mc.player)
        .forEach(e -> {
            if (mc.player.getDistance(e) > distance) {
                return;
            } else {
                if((!playersMode.equalsIgnoreCase("None")) && e instanceof EntityPlayer) {
                    if(playersMode.equalsIgnoreCase("Box1")) {
                        if(e.isGlowing())
                            e.setGlowing(false);
                        RenderUtil.drawESP(e, playerR, playerG, playerB, playerA, event.getPartialTicks());
                    } else if(playersMode.equalsIgnoreCase("Glow")) {
                        e.setGlowing(true);
                    } else if(playersMode.equalsIgnoreCase("Box2"))
                        RenderUtil.drawColorBox(e.getEntityBoundingBox(), playerR, playerG, playerB, playerA);
                    //RenderUtil.drawBoundingBox(e.getEntityBoundingBox());
                    //RenderUtil.drawESP(e, playerR, playerG, playerB, playerA, event.getPartialTicks());
                    //RenderUtil.drawTracer(e, 255, 255, 255, 255, event.getPartialTicks());
                } else {
                    if(e.isGlowing())
                        e.setGlowing(false);
                }
                if(!monstersMode.equalsIgnoreCase("None")) {
                    if(e instanceof EntityCreature || e instanceof EntitySlime || e instanceof EntitySquid) {
                        if(monstersMode.equalsIgnoreCase("Box1")) {
                            if(e.isGlowing())
                                e.setGlowing(false);
                            RenderUtil.drawESP(e, 255, 0, 0, 255, event.getPartialTicks());
                        } else if(monstersMode.equalsIgnoreCase("Glow")) {
                            e.setGlowing(true);
                        } else if(monstersMode.equalsIgnoreCase("Box2")) {
                            RenderUtil.drawColorBox(e.getEntityBoundingBox(), 255, 0, 0, 0);
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
                        RenderUtil.drawESP(e, itemR, itemG, itemB, itemA, event.getPartialTicks());
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
                        RenderUtil.drawESP(e, crystalR, crystalG, crystalB, crystalA, event.getPartialTicks());
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
}
