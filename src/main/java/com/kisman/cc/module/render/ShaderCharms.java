package com.kisman.cc.module.render;

import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.*;
import com.kisman.cc.module.render.shader.*;
import com.kisman.cc.module.render.shader.shaders.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.MathUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class ShaderCharms extends Module {
    private Setting mode = new Setting("Mode", this, ShaderModes.AQUA);
    private Setting crystals = new Setting("Crystals", this, true);
    private Setting players = new Setting("Players", this, false);
    private Setting friends = new Setting("Friends", this, true);
    private Setting mobs = new Setting("Mobs", this, false);
    private Setting animals = new Setting("Animals", this, false);

    public ShaderCharms() {
        super("ShaderCharms", Category.RENDER);
        setmgr.rSetting(mode);
        setmgr.rSetting(players);
        setmgr.rSetting(friends);
        setmgr.rSetting(mobs);
        setmgr.rSetting(animals);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        FramebufferShader framebufferShader = null;
        switch(mode.getValString()) {
            case "AQUA": framebufferShader = AquaShader.AQUA_SHADER; break;
            case "RED": framebufferShader = RedShader.RED_SHADER; break;
            case "SMOKE": framebufferShader = SmokeShader.SMOKE_SHADER; break;
            case "FLOW": framebufferShader = FlowShader.FLOW_SHADER; break;
        }

        if(framebufferShader == null) return;
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        framebufferShader.startDraw(event.getPartialTicks());
        for (Entity entity : mc.world.loadedEntityList) {
            if(entity == mc.player || entity == mc.getRenderViewEntity()) continue;
            if(!((entity instanceof EntityPlayer && players.getValBoolean())
                    || (entity instanceof EntityPlayer && friends.getValBoolean() && FriendManager.instance.isFriend(entity.getName()))
                    || (entity instanceof EntityEnderCrystal && crystals.getValBoolean())
                    || ((entity instanceof EntityMob || entity instanceof EntitySlime) && mobs.getValBoolean())
                    || (entity instanceof EntityAnimal && animals.getValBoolean()))) continue;
            Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
            Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject(entity)).doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
        }
        framebufferShader.stopDraw();
        GlStateManager.color(1f, 1f, 1f);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
    }

    public enum ShaderModes {
        AQUA, RED, SMOKE, FLOW
    }
}
