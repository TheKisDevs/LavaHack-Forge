package com.kisman.cc.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.RenderUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import i.gishreloaded.gishcode.utils.*;
import i.gishreloaded.gishcode.wrappers.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Tracers extends Module{
    public Tracers() {
        super("Tracers", "gay++", Category.RENDER);
        Kisman.instance.settingsManager.rSetting(new Setting("Animals", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("AnimalsColour", this, "Red", new ArrayList<>(Arrays.asList("Black", "Red",  "Blue",  "Gray", "White", "Green", "Yellow", "Pink"))));
        Kisman.instance.settingsManager.rSetting(new Setting("Monsters", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("MonstersColour", this, "Red", new ArrayList<>(Arrays.asList("Black", "Red",  "Blue",  "Gray", "White", "Green", "Yellow", "Pink"))));
        Kisman.instance.settingsManager.rSetting(new Setting("Players", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("PlayersColour", this, "Red", new ArrayList<>(Arrays.asList("Black", "Red",  "Blue",  "Gray", "White", "Green", "Yellow", "Pink"))));

        Kisman.instance.settingsManager.rSetting(new Setting("Distance", this, 100, 10, 260, true));
    }

    // boolean animals = Kisman.instance.settingsManager.getSettingByName(this, "Animals").getValBoolean();
    // boolean monsters = Kisman.instance.settingsManager.getSettingByName(this, "Monsters").getValBoolean();
    // boolean players = Kisman.instance.settingsManager.getSettingByName(this, "Players").getValBoolean();  
    
    // String animalsColour = Kisman.instance.settingsManager.getSettingByName(this, "AnimalsColour").getValString();
    // String monstersColour = Kisman.instance.settingsManager.getSettingByName(this, "MonstersColour").getValString();
    // String playersColour = Kisman.instance.settingsManager.getSettingByName(this, "PlayersColour").getValString();

    @SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
        int distance = (int) Kisman.instance.settingsManager.getSettingByName(this, "Distance").getValDouble();

        mc.world.loadedEntityList.stream()
        .filter(e -> e instanceof EntityPlayer)
        .filter(e -> e != mc.player)
        .forEach(e -> {
            if (mc.player.getDistance(e) > distance) {
                return;
            } else {
                if(mc.player.getDistance(e) <= 50) {
                    RenderUtil.drawESP(e, 255, 255, 255, 255, event.getPartialTicks());
                    //RenderUtil.drawTracer(e, 255, 255, 255, 255, event.getPartialTicks());
                }
            }
        });
	}
	
	void render(EntityLivingBase entity, float ticks) {
    	if(entity instanceof EntityPlayer) {
    		EntityPlayer player = (EntityPlayer)entity;
    		String ID = Utils.getPlayerName(player);
    		if(true) {//EnemyManager.enemysList.contains(ID)
    			RenderUtil.drawTracer(entity, 0.8f, 0.3f, 0.0f, 1.0f, ticks);
                System.out.println("1");
    			return;
    		}
    		// if(FriendManager.friendsList.contains(ID)) {
    		// 	RenderUtils.drawTracer(entity, 0.0f, 0.7f, 1.0f, 1.0f, ticks);
    		// 	return;
    		// }
    	}
    	if(true) {//HackManager.getHack("Targets").isToggledValue("Murder")
    		if(Utils.isMurder(entity)) {//
    			RenderUtil.drawTracer(entity, 1.0f, 0.0f, 0.8f, 1.0f, ticks);
                System.out.println("2");
        		return;
    		}
    		if(Utils.isDetect(entity)) {
    			RenderUtil.drawTracer(entity, 0.0f, 0.0f, 1.0f, 0.5f, ticks);
        		return;
    		}
		} 
    	if(entity.isInvisible()) {
			RenderUtil.drawTracer(entity, 0.0f, 0.0f, 0.0f, 0.5f, ticks);
			return;
    	}
    	if(entity.hurtTime > 0) {
    		RenderUtil.drawTracer(entity, 1.0f, 0.0f, 0.0f, 1.0f, ticks);
    		return;
    	}
    	RenderUtil.drawTracer(entity, 1.0f, 1.0f, 1.0f, 0.5f, ticks);
        if(ValidUtils.isValidEntity(entity) || entity == Wrapper.INSTANCE.player()) { 
			System.out.println("3");
            return;
    	}
    }
}
