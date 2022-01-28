package com.kisman.cc.module.render;

import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.*;
import com.kisman.cc.module.render.shader.*;
import com.kisman.cc.module.render.shader.shaders.*;
import com.kisman.cc.oldclickgui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.MathUtil;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Objects;

public class ShaderCharms extends Module {
    private Setting mode = new Setting("Mode", this, ShaderModes.SMOKE);
    private Setting crystals = new Setting("Crystals", this, true);
    private Setting players = new Setting("Players", this, false);
    private Setting friends = new Setting("Friends", this, true);
    private Setting mobs = new Setting("Mobs", this, false);
    private Setting animals = new Setting("Animals", this, false);
    private Setting items = new Setting("Items", this, true);

    private Setting blur = new Setting("Blur", this, true);
    private Setting radius = new Setting("Radius", this, 2, 0.1f, 10, false);
    private Setting mix = new Setting("Mix", this, 1, 0, 1, false);
    private Setting red = new Setting("Red", this, 1, 0, 1, false);
    private Setting green = new Setting("Green", this, 1, 0, 1, false);
    private Setting blue = new Setting("Blue", this, 1, 0, 1, false);
    private Setting rainbow = new Setting("RainBow", this, true);
    private Setting delay = new Setting("Delay", this, 100, 1, 2000, true);
    private Setting saturation = new Setting("Saturation", this, 36, 0, 100, Slider.NumberType.PERCENT);
    private Setting brightness = new Setting("Brightness", this, 100, 0, 100, Slider.NumberType.PERCENT);

    public ShaderCharms() {
        super("ShaderCharms", Category.RENDER);
        setmgr.rSetting(mode);
        setmgr.rSetting(crystals);
        setmgr.rSetting(players);
        setmgr.rSetting(friends);
        setmgr.rSetting(mobs);
        setmgr.rSetting(animals);
        setmgr.rSetting(items);

        setmgr.rSetting(blur);
        setmgr.rSetting(radius);
        setmgr.rSetting(mix);
        setmgr.rSetting(red);
        setmgr.rSetting(green);
        setmgr.rSetting(blue);
        setmgr.rSetting(rainbow);
        setmgr.rSetting(delay);
        setmgr.rSetting(saturation);
        setmgr.rSetting(brightness);
    }

    public void update() {
        super.setDisplayInfo("[" + mode.getValString() + "]");
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        {
            FramebufferShader framebufferShader = null;
            boolean itemglow = false;
            switch(mode.getValString()) {
              case "AQUA": framebufferShader = AquaShader.AQUA_SHADER; break;
              case "RED": framebufferShader = RedShader.RED_SHADER; break;
              case "SMOKE": framebufferShader = SmokeShader.SMOKE_SHADER; break;
              case "FLOW": framebufferShader = FlowShader.FLOW_SHADER; break;
              case "ITEMGLOW": framebufferShader = ItemShader.ITEM_SHADER; itemglow = true; break;
              case "PURPLE": framebufferShader = PurpleShader.PURPLE_SHADER; break;
              case "UNU": framebufferShader = UnuShader.UNU_SHADER; break;
            }

            if (framebufferShader == null) return;
            GlStateManager.matrixMode(5889);
            GlStateManager.pushMatrix();
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            if(itemglow && framebufferShader instanceof ItemShader) {
                ((ItemShader) framebufferShader).red = getColor().getRed() / 255f;
                ((ItemShader) framebufferShader).green = getColor().getGreen() / 255f;
                ((ItemShader) framebufferShader).blue = getColor().getBlue() / 255f;
                ((ItemShader) framebufferShader).radius = radius.getValFloat();
                ((ItemShader) framebufferShader).quality = 1;
                ((ItemShader) framebufferShader).blur = blur.getValBoolean();
                ((ItemShader) framebufferShader).mix = mix.getValFloat();
                ((ItemShader) framebufferShader).alpha = 1f;
                ((ItemShader) framebufferShader).useImage = false;
            }
            framebufferShader.startDraw(event.getPartialTicks());
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity == mc.player || entity == mc.getRenderViewEntity()) continue;
                if (!((entity instanceof EntityPlayer && players.getValBoolean())
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

        if(items.getValBoolean()) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableAlpha();
            ItemShader shader = ItemShader.ITEM_SHADER;
            shader.red = getColor().getRed() / 255f;
            shader.green = getColor().getGreen() / 255f;
            shader.blue = getColor().getBlue() / 255f;
            shader.radius = radius.getValFloat();
            shader.quality = 1;
            shader.blur = blur.getValBoolean();
            shader.mix = mix.getValFloat();
            shader.alpha = 1f;
            shader.useImage = false;
            shader.startDraw(event.getPartialTicks());
            mc.entityRenderer.renderHand(mc.getRenderPartialTicks(), 2);
            shader.stopDraw();
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.disableDepth();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }

    private Color getColor() {
        return rainbow.getValBoolean() ? ColorUtils.rainbowRGB(delay.getValInt(), saturation.getValFloat(), brightness.getValFloat()) : new Color(red.getValFloat(), green.getValFloat(), blue.getValFloat());
//        Color color = new Color(red.getValFloat(), green.getValFloat(), blue.getValFloat());
//        float hsb[] = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), new float[] {0, saturation.getValFloat(), brightness.getValInt()});
//        return Color.getHSBColor(hsb[0], saturation.getValFloat() / 100, brightness.getValInt());
    }

    public enum ShaderModes {
        AQUA, RED, SMOKE, FLOW, ITEMGLOW, PURPLE, UNU
    }
}
