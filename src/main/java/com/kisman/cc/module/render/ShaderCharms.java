package com.kisman.cc.module.render;

import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.module.*;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.module.render.shader.*;
import com.kisman.cc.module.render.shader.shaders.*;
import com.kisman.cc.gui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.MathUtil;
import com.kisman.cc.util.enums.ShaderModes;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Objects;

public class ShaderCharms extends Module {
    private final Setting mode = new Setting("Mode", this, ShaderModes.SMOKE);
    private final Setting crystals = new Setting("Crystals", this, true);
    private final Setting players = new Setting("Players", this, false);
    private final Setting friends = new Setting("Friends", this, true);
    private final Setting mobs = new Setting("Mobs", this, false);
    private final Setting animals = new Setting("Animals", this, false);
    private final Setting enderPearls = new Setting("Ender Pearls", this, false);
    private final Setting itemsEntity = new Setting("Items(Entity)", this, false);
    private final Setting items = new Setting("Items", this, true);
    private final Setting itemsFix = new Setting("Items Fix", this, false).setVisible(items::getValBoolean);
//    private Setting storages = new Setting("Storages(cfg from StorageEsp)", this, false);

    private final Setting animationSpeed = new Setting("Animation Speed", this, 0, 1, 10, true).setVisible(() -> !mode.checkValString("GRADIENT"));

    private final Setting blur = new Setting("Blur", this, true).setVisible(() -> mode.checkValString("ITEMGLOW"));
    private final Setting radius = new Setting("Radius", this, 2, 0.1f, 10, false).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE") || mode.checkValString("GRADIENT"));
    private final Setting mix = new Setting("Mix", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("ITEMGLOW"));
    private final Setting red = new Setting("Red", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE"));
    private final Setting green = new Setting("Green", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE"));
    private final Setting blue = new Setting("Blue", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE"));
    private final Setting rainbow = new Setting("RainBow", this, true).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE"));
    private final Setting delay = new Setting("Delay", this, 100, 1, 2000, true);
    private final Setting saturation = new Setting("Saturation", this, 36, 0, 100, Slider.NumberType.PERCENT);
    private final Setting brightness = new Setting("Brightness", this, 100, 0, 100, Slider.NumberType.PERCENT);

    private final Setting quality = new Setting("Quality", this, 1, 0, 20, false).setVisible(() -> mode.checkValString("GRADIENT") || mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE"));
    private final Setting gradientAlpha = new Setting("Gradient Alpha", this, false).setVisible(() -> mode.checkValString("GRADIENT"));
    private final Setting alphaGradient = new Setting("Alpha Gradient Value", this, 255, 0, 255, true).setVisible(() -> mode.checkValString("GRADIENT"));
    private final Setting duplicateOutline = new Setting("Duplicate Outline", this, 1, 0, 20, false).setVisible(() -> mode.checkValString("GRADIENT"));
    private final Setting moreGradientOutline = new Setting("More Gradient", this, 1, 0, 10, false).setVisible(() -> mode.checkValString("GRADIENT"));
    private final Setting creepyOutline = new Setting("Creepy", this, 1, 0, 20, false).setVisible(() -> mode.checkValString("GRADIENT"));
    private final Setting alpha = new Setting("Alpha", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("GRADIENT"));
    private final Setting numOctavesOutline = new Setting("Num Octaves", this, 5, 1, 30, true).setVisible(() -> mode.checkValString("GRADIENT"));
    private final Setting speedOutline = new Setting("Speed", this, 0.1, 0.001, 0.1, false).setVisible(() -> mode.checkValString("GRADIENT"));

    public static ShaderCharms instance;

    private boolean criticalSection = false;

    public ShaderCharms() {
        super("ShaderCharms", Category.RENDER);

        instance = this;

        setmgr.rSetting(mode);
        setmgr.rSetting(crystals);
        setmgr.rSetting(players);
        setmgr.rSetting(friends);
        setmgr.rSetting(mobs);
        setmgr.rSetting(animals);
        setmgr.rSetting(enderPearls);
        setmgr.rSetting(itemsEntity);
        setmgr.rSetting(items);
        setmgr.rSetting(itemsFix);
//        setmgr.rSetting(storages);

        setmgr.rSetting(animationSpeed);

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

        setmgr.rSetting(quality);
        setmgr.rSetting(gradientAlpha);
        setmgr.rSetting(alphaGradient);
        setmgr.rSetting(duplicateOutline);
        setmgr.rSetting(moreGradientOutline);
        setmgr.rSetting(creepyOutline);
        setmgr.rSetting(alpha);
        setmgr.rSetting(numOctavesOutline);
        setmgr.rSetting(speedOutline);
    }

    public void update() {
        super.setDisplayInfo("[" + mode.getValString() + "]");
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if(items.getValBoolean() && itemsFix.getValBoolean() && !criticalSection) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        try {
            {
                FramebufferShader framebufferShader = null;
                boolean itemglow = false, gradient = false, glow = false, outline = false;

                switch (mode.getValString()) {
                    case "AQUA":
                        framebufferShader = AquaShader.AQUA_SHADER;
                        break;
                    case "RED":
                        framebufferShader = RedShader.RED_SHADER;
                        break;
                    case "SMOKE":
                        framebufferShader = SmokeShader.SMOKE_SHADER;
                        break;
                    case "FLOW":
                        framebufferShader = FlowShader.FLOW_SHADER;
                        break;
                    case "ITEMGLOW":
                        framebufferShader = ItemShader.ITEM_SHADER;
                        itemglow = true;
                        break;
                    case "PURPLE":
                        framebufferShader = PurpleShader.PURPLE_SHADER;
                        break;
                    case "GRADIENT":
                        framebufferShader = GradientOutlineShader.INSTANCE;
                        gradient = true;
                        break;
                    case "UNU":
                        framebufferShader = UnuShader.UNU_SHADER;
                        break;
                    case "GLOW":
                        framebufferShader = GlowShader.GLOW_SHADER;
                        glow = true;
                        break;
                    case "OUTLINE":
                        framebufferShader = OutlineShader.OUTLINE_SHADER;
                        outline = true;
                        break;
                    case "BlueFlames":
                        framebufferShader = BlueFlamesShader.BlueFlames_SHADER;
                        break;
                    case "CodeX":
                        framebufferShader = CodeXShader.CodeX_SHADER;
                        break;
                    case "Crazy":
                        framebufferShader = CrazyShader.CRAZY_SHADER;
                        break;
                    case "Golden":
                        framebufferShader = GoldenShader.GOLDEN_SHADER;
                        break;
                    case "HideF":
                        framebufferShader = HideFShader.HideF_SHADER;
                        break;
                    case "HolyFuck":
                        framebufferShader = HolyFuckShader.HolyFuckF_SHADER;
                        break;
                    case "HotShit":
                        framebufferShader = HotShitShader.HotShit_SHADER;
                        break;
                    case "Kfc":
                        framebufferShader = KfcShader.KFC_SHADER;
                        break;
                    case "Sheldon":
                        framebufferShader = SheldonShader.SHELDON_SHADER;
                        break;
                    case "Smoky":
                        framebufferShader = SmokyShader.SMOKY_SHADER;
                        break;
                    case "SNOW":
                        framebufferShader = SnowShader.SNOW_SHADER;
                        break;
                    case "Techno":
                        framebufferShader = TechnoShader.TECHNO_SHADER;
                        break;
                }

                if (framebufferShader == null) return;

                framebufferShader.animationSpeed = animationSpeed.getValInt();

                GlStateManager.matrixMode(5889);
                GlStateManager.pushMatrix();
                GlStateManager.matrixMode(5888);
                GlStateManager.pushMatrix();
                if (itemglow) {
                    ((ItemShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((ItemShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((ItemShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((ItemShader) framebufferShader).radius = radius.getValFloat();
                    ((ItemShader) framebufferShader).quality = quality.getValFloat();
                    ((ItemShader) framebufferShader).blur = blur.getValBoolean();
                    ((ItemShader) framebufferShader).mix = mix.getValFloat();
                    ((ItemShader) framebufferShader).alpha = 1f;
                    ((ItemShader) framebufferShader).useImage = false;
                } else if (gradient) {
                    ((GradientOutlineShader) framebufferShader).color = getColor();
                    ((GradientOutlineShader) framebufferShader).radius = radius.getValFloat();
                    ((GradientOutlineShader) framebufferShader).quality = quality.getValFloat();
                    ((GradientOutlineShader) framebufferShader).gradientAlpha = gradientAlpha.getValBoolean();
                    ((GradientOutlineShader) framebufferShader).alphaOutline = alphaGradient.getValInt();
                    ((GradientOutlineShader) framebufferShader).duplicate = duplicateOutline.getValFloat();
                    ((GradientOutlineShader) framebufferShader).moreGradient = moreGradientOutline.getValFloat();
                    ((GradientOutlineShader) framebufferShader).creepy = creepyOutline.getValFloat();
                    ((GradientOutlineShader) framebufferShader).alpha = alpha.getValFloat();
                    ((GradientOutlineShader) framebufferShader).numOctaves = numOctavesOutline.getValInt();
                } else if(glow) {
                    ((GlowShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((GlowShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((GlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((GlowShader) framebufferShader).radius = radius.getValFloat();
                    ((GlowShader) framebufferShader).quality = quality.getValFloat();
                } else if(outline) {
                    ((OutlineShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((OutlineShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((OutlineShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((OutlineShader) framebufferShader).radius = radius.getValFloat();
                    ((OutlineShader) framebufferShader).quality = quality.getValFloat();
                }
                framebufferShader.startDraw(event.getPartialTicks());
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity == mc.player || entity == mc.getRenderViewEntity()) continue;
                    if (!((entity instanceof EntityPlayer && players.getValBoolean())
                            || (entity instanceof EntityPlayer && friends.getValBoolean() && FriendManager.instance.isFriend(entity.getName()))
                            || (entity instanceof EntityEnderCrystal && crystals.getValBoolean())
                            || ((entity instanceof EntityMob || entity instanceof EntitySlime) && mobs.getValBoolean())
                            || ((entity instanceof EntityEnderPearl) && enderPearls.getValBoolean())
                            || ((entity instanceof EntityItem) && itemsEntity.getValBoolean())
                            || (entity instanceof EntityAnimal && animals.getValBoolean()))) continue;
                    Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
                    Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject(entity)).doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
                }
                framebufferShader.stopDraw();
                if (gradient) ((GradientOutlineShader) framebufferShader).update(speedOutline.getValDouble());
                GlStateManager.color(1f, 1f, 1f);
                GlStateManager.matrixMode(5889);
                GlStateManager.popMatrix();
                GlStateManager.matrixMode(5888);
                GlStateManager.popMatrix();
            }

            if (items.getValBoolean() && mc.gameSettings.thirdPersonView == 0) {
                FramebufferShader framebufferShader = null;
                boolean itemglow = false, gradient = false, glow = false, outline = false;
                switch (mode.getValString()) {
                    case "AQUA":
                        framebufferShader = AquaShader.AQUA_SHADER;
                        break;
                    case "RED":
                        framebufferShader = RedShader.RED_SHADER;
                        break;
                    case "SMOKE":
                        framebufferShader = SmokeShader.SMOKE_SHADER;
                        break;
                    case "FLOW":
                        framebufferShader = FlowShader.FLOW_SHADER;
                        break;
                    case "ITEMGLOW":
                        framebufferShader = ItemShader.ITEM_SHADER;
                        itemglow = true;
                        break;
                    case "PURPLE":
                        framebufferShader = PurpleShader.PURPLE_SHADER;
                        break;
                    case "GRADIENT":
                        framebufferShader = GradientOutlineShader.INSTANCE;
                        gradient = true;
                        break;
                    case "GLOW":
                        framebufferShader = GlowShader.GLOW_SHADER;
                        glow = true;
                        break;
                    case "OUTLINE":
                        framebufferShader = OutlineShader.OUTLINE_SHADER;
                        outline = true;
                        break;
                    case "BlueFlames":
                        framebufferShader = BlueFlamesShader.BlueFlames_SHADER;
                        break;
                    case "CodeX":
                        framebufferShader = CodeXShader.CodeX_SHADER;
                        break;
                    case "Crazy":
                        framebufferShader = CrazyShader.CRAZY_SHADER;
                        break;
                    case "Golden":
                        framebufferShader = GoldenShader.GOLDEN_SHADER;
                        break;
                    case "HideF":
                        framebufferShader = HideFShader.HideF_SHADER;
                        break;
                    case "HolyFuck":
                        framebufferShader = HolyFuckShader.HolyFuckF_SHADER;
                        break;
                    case "HotShit":
                        framebufferShader = HotShitShader.HotShit_SHADER;
                        break;
                    case "Kfc":
                        framebufferShader = KfcShader.KFC_SHADER;
                        break;
                    case "Sheldon":
                        framebufferShader = SheldonShader.SHELDON_SHADER;
                        break;
                    case "Smoky":
                        framebufferShader = SmokyShader.SMOKY_SHADER;
                        break;
                    case "SNOW":
                        framebufferShader = SnowShader.SNOW_SHADER;
                        break;
                    case "Techno":
                        framebufferShader = TechnoShader.TECHNO_SHADER;
                        break;
                }

                if (framebufferShader == null) return;
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GlStateManager.enableAlpha();
                if (itemglow) {
                    ((ItemShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((ItemShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((ItemShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((ItemShader) framebufferShader).radius = radius.getValFloat();
                    ((ItemShader) framebufferShader).quality = 1;
                    ((ItemShader) framebufferShader).blur = blur.getValBoolean();
                    ((ItemShader) framebufferShader).mix = mix.getValFloat();
                    ((ItemShader) framebufferShader).alpha = 1f;
                    ((ItemShader) framebufferShader).useImage = false;
                } else if (gradient) {
                    ((GradientOutlineShader) framebufferShader).color = getColor();
                    ((GradientOutlineShader) framebufferShader).radius = radius.getValFloat();
                    ((GradientOutlineShader) framebufferShader).quality = quality.getValFloat();
                    ((GradientOutlineShader) framebufferShader).gradientAlpha = gradientAlpha.getValBoolean();
                    ((GradientOutlineShader) framebufferShader).alphaOutline = alphaGradient.getValInt();
                    ((GradientOutlineShader) framebufferShader).duplicate = duplicateOutline.getValFloat();
                    ((GradientOutlineShader) framebufferShader).moreGradient = moreGradientOutline.getValFloat();
                    ((GradientOutlineShader) framebufferShader).creepy = creepyOutline.getValFloat();
                    ((GradientOutlineShader) framebufferShader).alpha = alpha.getValFloat();
                    ((GradientOutlineShader) framebufferShader).numOctaves = numOctavesOutline.getValInt();
                }else if(glow) {
                    ((GlowShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((GlowShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((GlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((GlowShader) framebufferShader).radius = radius.getValFloat();
                    ((GlowShader) framebufferShader).quality = quality.getValFloat();
                } else if(outline) {
                    ((OutlineShader) framebufferShader).red = getColor().getRed() / 255f;
                    ((OutlineShader) framebufferShader).green = getColor().getGreen() / 255f;
                    ((OutlineShader) framebufferShader).blue = getColor().getBlue() / 255f;
                    ((OutlineShader) framebufferShader).radius = radius.getValFloat();
                    ((OutlineShader) framebufferShader).quality = quality.getValFloat();
                }
                criticalSection = true;
                framebufferShader.startDraw(event.getPartialTicks());
                mc.entityRenderer.renderHand(mc.getRenderPartialTicks(), 2);
                framebufferShader.stopDraw();
                criticalSection = false;
                if (gradient) ((GradientOutlineShader) framebufferShader).update(speedOutline.getValDouble());
                GlStateManager.disableBlend();
                GlStateManager.disableAlpha();
                GlStateManager.disableDepth();
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
        } catch (Exception ignored) {
            if(Config.instance.antiOpenGLCrash.getValBoolean()) {
                super.setToggled(false);
                ChatUtils.error("[ShaderCharms] Error, Config -> AntiOpenGLCrash disabled ShaderCharms");
            }
        }
    }

    private Color getColor() {
        return rainbow.getValBoolean() ? ColorUtils.rainbowRGB(delay.getValInt(), saturation.getValFloat(), brightness.getValFloat()) : new Color(red.getValFloat(), green.getValFloat(), blue.getValFloat());
    }
}
