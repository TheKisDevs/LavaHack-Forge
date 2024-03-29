package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.*;
import com.kisman.cc.features.module.render.shader.FramebufferShader;
import com.kisman.cc.features.module.render.shader.GlowableShader;
import com.kisman.cc.features.module.render.shader.shaders.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.client.collections.Pair;
import com.kisman.cc.util.client.interfaces.Drawable;
import com.kisman.cc.util.enums.Shaders;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.math.MathKt;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.shader.ShaderHelperKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

//TODO: remove renderSky method, add head and return hooks to default renderSky to apply shader at sandbox
@ModuleInfo(
        name = "ShaderCharms",
        display = "Shaders",
        desc = "Config of shader system",
        category = Category.RENDER,
        toggled = true,
        toggleable = false
)
public class ShaderCharms extends Module {
    public final SettingEnum<Shaders> mode = register(new SettingEnum<>("Mode", this, Shaders.AQUA));

    private final MultiThreaddableModulePattern threads = threads();

    private final SettingGroup types = register(new SettingGroup(new Setting("Types", this)));

    private final Setting crystals = register(types.add(new Setting("Crystals", this, false)));
    private final Setting players = register(types.add(new Setting("Players", this, false)));
    private final Setting friends = register(types.add(new Setting("Friends", this, false)));
    private final Setting mobs = register(types.add(new Setting("Mobs", this, false)));
    private final Setting animals = register(types.add(new Setting("Animals", this, false)));
    private final Setting enderPearls = register(types.add(new Setting("Ender Pearls", this, false)));
    private final Setting itemsEntity = register(types.add(new Setting("Entity Items", this, false)));
    public final Setting items = register(types.add(new Setting("Items", this, false)));
    private final Setting sky = /*register*/(/*types.add*/(new Setting("Sky", this, false)));

    private final SettingGroup config = register(new SettingGroup(new Setting("Config", this)));

    private final Setting animationSpeed = register(config.add(new Setting("Animation Speed", this, 0, 1, 10, false)));

    private final Setting blur = register(config.add(new Setting("Blur", this, true)));
    private final Setting radius = register(config.add(new Setting("Radius", this, 2, 0.1f, 10, false)));
    private final Setting mix = register(config.add(new Setting("Mix", this, 1, 0, 1, false)));
    private final Setting red = register(config.add(new Setting("Red", this, 1, 0, 1, false)));
    private final Setting green = register(config.add(new Setting("Green", this, 1, 0, 1, false)));
    private final Setting blue = register(config.add(new Setting("Blue", this, 1, 0, 1, false)));
    private final Setting rainbow = register(config.add(new Setting("RainBow", this, true)));
    private final Setting delay = register(config.add(new Setting("Delay", this, 100, 1, 2000, true)));
    private final Setting saturation = register(config.add(new Setting("Saturation", this, 36, 0, 100, NumberType.PERCENT)));
    private final Setting brightness = register(config.add(new Setting("Brightness", this, 100, 0, 100, NumberType.PERCENT)));

    private final Setting quality = register(config.add(new Setting("Quality", this, 1, 0, 20, false)));
    private final Setting gradientAlpha = register(config.add(new Setting("Gradient Alpha", this, false)));
    private final Setting alphaGradient = register(config.add(new Setting("Alpha Gradient Value", this, 255, 0, 255, true)));
    private final Setting duplicateOutline = register(config.add(new Setting("Duplicate Outline", this, 1, 0, 20, false)));
    private final Setting moreGradientOutline = register(config.add(new Setting("More Gradient", this, 1, 0, 10, false)));
    private final Setting creepyOutline = register(config.add(new Setting("Creepy", this, 1, 0, 20, false)));
    private final Setting alpha = register(config.add(new Setting("Alpha", this, 1, 0, 1, false)));
    private final Setting numOctavesOutline = register(config.add(new Setting("Num Octaves", this, 5, 1, 30, true)));
    private final Setting speedOutline = register(config.add(new Setting("Speed", this, 0.1, 0.001, 0.1, false)));

    private final Setting rainbowSpeed = register(config.add(new Setting("Rainbow Speed", this, 0.4, 0, 1, false)));
    private final Setting rainbowStrength = register(config.add(new Setting("Rainbow Strength", this, 0.3, 0, 1, false)));
    private final Setting rainbowSaturation = register(config.add(new Setting("Rainbow Saturation", this, 0.5, 0, 1, false)));

/*
    private final Setting color1 = register(config.add(new Setting("Color 1", this, new Colour(255, 0, 0, 255))));
    private final Setting color2 = register(config.add(new Setting("Color 2", this, new Colour(255, 0, 0, 255))));
    private final Setting filledColor = register(config.add(new Setting("Filled Color", this, new Colour(255, 0, 0, 255))));
    private final Setting outlineColor = register(config.add(new Setting("Outline Color", this, new Colour(255, 0, 0, 255))));
    private final Setting customAlpha = register(config.add(new Setting("Custom Alpha", this, true)));
    private final Setting filled = register(config.add(new Setting("Filled", this, false)));
    private final Setting rainbowFilled = register(config.add(new Setting("Rainbow Filled", this, false)));
    private final Setting rainbowAlpha = register(config.add(new Setting("Rainbow Alpha")));
    private final Setting circle = register(config.add(new Setting("Circle", this, false)));
    private final Setting circleRadius = register(config.add(new Setting("Circle Radius", this, 2, 0.1, 10, false)));
    private final Setting glow = register(config.add(new Setting("Glow", this, false)));
    //glow radius is just radius
    private final Setting outline = register(config.add(new Setting("Outline", this, false)));
    private final Setting fadeOutline = register(config.add(new Setting("Fade Outline", this, false)));
    //outline radius is just radius

    private final Setting speed = register(config.add(new Setting("Speed", this, 20.0, 0.0, 50.0, false)));
    private final Setting step = register(config.add(new Setting("Step", this, 10.0, 1.0, 30.0, true)));
    private final Setting ratio = register(config.add(new Setting("Ratio", this, 1.0, 0.0, 1.0, false)));
*/


//    private final Setting useImage = register(config.add(new Setting("Use Image", this, false)));
//    private final Setting imageMix = register(config.add(new Setting("Image Mix", this, 0.5, 0, 1, false)));

    @ModuleInstance
    public static ShaderCharms instance;

    private boolean criticalSection = false;
    private boolean flag = false;
    private boolean flag5 = false;

    private ArrayList<Entity> entities = new ArrayList<>();

    public static HashMap<Drawable, Pair<Supplier<Boolean>>> modules = new HashMap<>();
    private final HashMap<Drawable, Boolean> modulesToRender = new HashMap<>();

    /*public void onEnable() {
        super.onEnable();
//        threads.reset();
//        modulesToRender.clear();
    }*/

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if(items.getValBoolean() && !criticalSection) event.setCanceled(true);
    }

    public void update() {
        modulesToRender.clear();
        flag5 = false;

        for(Map.Entry<Drawable, Pair<Supplier<Boolean>>> entry : modules.entrySet()) {
            Drawable module = entry.getKey();
            Pair<Supplier<Boolean>> suppliers = entry.getValue();

            if(suppliers.getFirst().get()) modulesToRender.put(module, suppliers.getSecond().get());
            if(!flag5) flag5 = flag;
        }

        flag = !modulesToRender.isEmpty();
    }

    /*boolean flag1 = false;
    boolean flag2 = false;
    boolean flag3 = false;

    private ArrayList<Entity> entitiesPostRender = new ArrayList<>();

    private Listener<RenderEntitiesEvent.Start> renderEntitiesStart = new Listener<>(event -> {
        flag1 = players.getValBoolean() || friends.getValBoolean() || crystals.getValBoolean() || mobs.getValBoolean() || enderPearls.getValBoolean() || itemsEntity.getValBoolean() || animals.getValBoolean();
        flag2 = !entities.isEmpty();
        flag3 = items.getValBoolean() && mc.gameSettings.thirdPersonView == 0;
        entitiesPostRender.clear();

        if(flag && flag5) for(Drawable module : modulesToRender.keySet()) if(modulesToRender.get(module)) module.draw();

        Function0<Unit> uniforms = () -> {
            FramebufferShader framebufferShader = mode.getValEnum().getBuffer();
            framebufferShader.animationSpeed = animationSpeed.getValInt();

            if(framebufferShader instanceof GlowableShader) {
                ((GlowableShader) framebufferShader).radius = radius.getValFloat();
                ((GlowableShader) framebufferShader).quality = quality.getValFloat();
            } else if (mode.getValEnum() == Shaders.ITEMGLOW) {
                ((ItemShader) framebufferShader).red = getColor().getRed() / 255f;
                ((ItemShader) framebufferShader).green = getColor().getGreen() / 255f;
                ((ItemShader) framebufferShader).blue = getColor().getBlue() / 255f;
                ((ItemShader) framebufferShader).radius = radius.getValFloat();
                ((ItemShader) framebufferShader).quality = quality.getValFloat();
                ((ItemShader) framebufferShader).blur = blur.getValBoolean();
                ((ItemShader) framebufferShader).mix = mix.getValFloat();
                ((ItemShader) framebufferShader).alpha = 1f;
                ((ItemShader) framebufferShader).useImage = false;
            } else if (mode.getValEnum() == Shaders.GRADIENT) {
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

                ((GradientOutlineShader) framebufferShader).update(speedOutline.getValDouble());
            } else if(mode.getValEnum() == Shaders.GLOW) {
                ((GlowShader) framebufferShader).red = getColor().getRed() / 255f;
                ((GlowShader) framebufferShader).green = getColor().getGreen() / 255f;
                ((GlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
                ((GlowShader) framebufferShader).radius = radius.getValFloat();
                ((GlowShader) framebufferShader).quality = quality.getValFloat();
            } else if(mode.getValEnum() == Shaders.OUTLINE) {
                ((OutlineShader) framebufferShader).red = getColor().getRed() / 255f;
                ((OutlineShader) framebufferShader).green = getColor().getGreen() / 255f;
                ((OutlineShader) framebufferShader).blue = getColor().getBlue() / 255f;
                ((OutlineShader) framebufferShader).radius = radius.getValFloat();
                ((OutlineShader) framebufferShader).quality = quality.getValFloat();
                ((OutlineShader) framebufferShader).rainbowSpeed = rainbowSpeed.getValFloat();
                ((OutlineShader) framebufferShader).rainbowStrength = rainbowStrength.getValFloat();
                ((OutlineShader) framebufferShader).saturation = rainbowSaturation.getValFloat();
            } else if(mode.getValEnum() == Shaders.Kfc) {
                ((KfcShader) framebufferShader).radius = radius.getValFloat();
                ((KfcShader) framebufferShader).quality = quality.getValFloat();
            }

            return Unit.INSTANCE;
        };

        ShaderHelperKt.startShader(mode.getValEnum(), uniforms, mc.getRenderPartialTicks());
    });

    private Listener<RenderEntitiesEvent.End> renderEntitiesEnd = new Listener<>(event -> {
        if(flag) for(Drawable module : modulesToRender.keySet()) {
            if(module instanceof ShaderableModule) {
                ShaderableModule shaderable = (ShaderableModule) module;

                shaderable.handleDrawShadered();
            } else {
                module.draw();
            }
        }

        if(flag3) {
            criticalSection = true;
            mc.entityRenderer.renderHand(mc.getRenderPartialTicks(), 2);
            criticalSection = false;
        }

        ShaderHelperKt.endShader(mode.getValEnum());

        for(Entity entity : entitiesPostRender) {
            if(!entity.shouldRenderInPass(MinecraftForgeClient.getRenderPass())) continue;

            boolean flag = mc.renderManager.shouldRender(entity, camera, d0, d1, d2) || entity.isRidingOrBeingRiddenBy(mc.player);

            if (flag) {
                boolean flag_ = mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();

                if ((entity != mc.getRenderViewEntity() || mc.gameSettings.thirdPersonView != 0 || flag_) && (entity.posY < 0.0D || entity.posY >= 256.0D || mc.world.isBlockLoaded(blockpos$pooledmutableblockpos.setPos(entity)))) mc.renderManager.renderEntityStatic(entity, mc.getRenderPartialTicks(), false);
            }
        }
    });

    private Listener<RenderEntityEvent.All.Pre> renderEntityPre = new Listener<>(event -> {
        if (!((event.getEntity() instanceof EntityPlayer && players.getValBoolean())
                || (event.getEntity() instanceof EntityPlayer && friends.getValBoolean() && FriendManager.instance.isFriend(event.getEntity().getName()))
                || (event.getEntity() instanceof EntityEnderCrystal && crystals.getValBoolean())
                || ((event.getEntity() instanceof EntityMob || event.getEntity() instanceof EntitySlime) && mobs.getValBoolean())
                || ((event.getEntity() instanceof EntityEnderPearl) && enderPearls.getValBoolean())
                || ((event.getEntity() instanceof EntityItem) && itemsEntity.getValBoolean())
                || (event.getEntity() instanceof EntityAnimal && animals.getValBoolean()))) {
            event.cancel();
            return;
        }

        entitiesPostRender.add(event.getEntity());
    });*/

    @SubscribeEvent
    public void onRenderWorld(RenderGameOverlayEvent.Text event) {
        if(!Display.isActive() || !Display.isVisible() || mc.currentScreen instanceof GuiDownloadTerrain) return;

        try {
            boolean flag1 = players.getValBoolean() || friends.getValBoolean() || crystals.getValBoolean() || mobs.getValBoolean() || enderPearls.getValBoolean() || itemsEntity.getValBoolean() || animals.getValBoolean();

            if(flag1) {
                threads.update(() -> {
                    ArrayList<Entity> entities = new ArrayList<>();

                    for (Entity entity : mc.world.loadedEntityList) {
                        if (entity == mc.player || entity == mc.getRenderViewEntity()) continue;
                        if (!((entity instanceof EntityPlayer && players.getValBoolean())
                                || (entity instanceof EntityPlayer && friends.getValBoolean() && FriendManager.instance.isFriend(entity.getName()))
                                || (entity instanceof EntityEnderCrystal && crystals.getValBoolean())
                                || ((entity instanceof EntityMob || entity instanceof EntitySlime) && mobs.getValBoolean())
                                || ((entity instanceof EntityEnderPearl) && enderPearls.getValBoolean())
                                || ((entity instanceof EntityItem) && itemsEntity.getValBoolean())
                                || (entity instanceof EntityAnimal && animals.getValBoolean()))) continue;
                        entities.add(entity);
                    }

                    mc.addScheduledTask(() -> this.entities = entities);
                });
            } else entities.clear();

            boolean flag2 = !entities.isEmpty();
            boolean flag3 = items.getValBoolean() && mc.gameSettings.thirdPersonView == 0;
            boolean flag6 = sky.getValBoolean();

            if(flag || flag2 || flag3 || flag6) {
                if(flag && flag5) for(Drawable module : modulesToRender.keySet()) if(modulesToRender.get(module)) module.draw();

                Function0<Unit> uniforms = () -> {
                    FramebufferShader framebufferShader = mode.getValEnum().getBuffer();
                    framebufferShader.animationSpeed = animationSpeed.getValInt();

                    if(framebufferShader instanceof GlowableShader) {
                        ((GlowableShader) framebufferShader).radius = radius.getValFloat();
                        ((GlowableShader) framebufferShader).quality = quality.getValFloat();
                    } else if (mode.getValEnum() == Shaders.ITEMGLOW) {
                        ((ItemShader) framebufferShader).red = getColor().getRed() / 255f;
                        ((ItemShader) framebufferShader).green = getColor().getGreen() / 255f;
                        ((ItemShader) framebufferShader).blue = getColor().getBlue() / 255f;
                        ((ItemShader) framebufferShader).radius = radius.getValFloat();
                        ((ItemShader) framebufferShader).quality = quality.getValFloat();
                        ((ItemShader) framebufferShader).blur = blur.getValBoolean();
                        ((ItemShader) framebufferShader).mix = mix.getValFloat();
                        ((ItemShader) framebufferShader).alpha = 1f;
                        ((ItemShader) framebufferShader).useImage = false;

                        framebufferShader.swapUniforms();
                        framebufferShader.changeUniform("red", red);
                        framebufferShader.changeUniform("green", red);
                        framebufferShader.changeUniform("blue", red);
                        framebufferShader.changeUniform("radius", red);
                        framebufferShader.changeUniform("quality", red);
                        framebufferShader.changeUniform("blur", red);
                        framebufferShader.changeUniform("mix", red);
                    } else if (mode.getValEnum() == Shaders.GRADIENT) {
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

                        ((GradientOutlineShader) framebufferShader).update(speedOutline.getValDouble());
                    } else if(mode.getValEnum() == Shaders.GLOW) {
                        ((GlowShader) framebufferShader).red = getColor().getRed() / 255f;
                        ((GlowShader) framebufferShader).green = getColor().getGreen() / 255f;
                        ((GlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
                        ((GlowShader) framebufferShader).radius = radius.getValFloat();
                        ((GlowShader) framebufferShader).quality = quality.getValFloat();
                    } else if(mode.getValEnum() == Shaders.OUTLINE) {
                        ((OutlineShader) framebufferShader).red = getColor().getRed() / 255f;
                        ((OutlineShader) framebufferShader).green = getColor().getGreen() / 255f;
                        ((OutlineShader) framebufferShader).blue = getColor().getBlue() / 255f;
                        ((OutlineShader) framebufferShader).radius = radius.getValFloat();
                        ((OutlineShader) framebufferShader).quality = quality.getValFloat();
                        ((OutlineShader) framebufferShader).rainbowSpeed = rainbowSpeed.getValFloat();
                        ((OutlineShader) framebufferShader).rainbowStrength = rainbowStrength.getValFloat();
                        ((OutlineShader) framebufferShader).saturation = rainbowSaturation.getValFloat();
                    } else if(mode.getValEnum() == Shaders.Kfc) {
                        ((KfcShader) framebufferShader).radius = radius.getValFloat();
                        ((KfcShader) framebufferShader).quality = quality.getValFloat();
                    }
                    /* else if(mode.getValEnum() == Shaders.Circle) {
                        CircleShader.color1 = color1.getColour();
                        CircleShader.color2 = color2.getColour();
                        CircleShader.filledColor = filledColor.getColour();
                        CircleShader.outlineColor = outlineColor.getColour();
                        CircleShader.customAlpha = customAlpha.getValBoolean();
                        CircleShader.rainbow = rainbowFilled.getValBoolean();
                        CircleShader.circle = this.circle.getValBoolean();
                        CircleShader.filled = filled.getValBoolean();
                        CircleShader.glow = this.glow.getValBoolean();
                        CircleShader.outline = this.outline.getValBoolean();
                        CircleShader.fadeOutline = fadeOutline.getValBoolean();
                        CircleShader.mix = mix.getValFloat();
                        CircleShader.rainbowAlpha = rainbowAlpha.getValFloat();
                        CircleShader.circleRadius = circleRadius.getValFloat();
                        CircleShader.glowRadius = radius.getValFloat();
                        CircleShader.outlineRadius = radius.getValFloat();
                        CircleShader.quality = quality.getValFloat();
                    } else if(mode.getValEnum() == Shaders.Circle2) {
                        Circle2Shader.rgba = filledColor.getColour();
                        Circle2Shader.rgba1 = filledColor.getColour();
                        Circle2Shader.step = step.getValFloat();
                        Circle2Shader.speed = speed.getValFloat();
                        Circle2Shader.mix = mix.getValFloat();
                        Circle2Shader.customAlpha = customAlpha.getValBoolean();
                        Circle2Shader.alpha = alpha.getValFloat();
                    } else if(mode.getValEnum() == Shaders.Outline3) {
                        Outline3Shader.outlineColor = outlineColor.getColour();
                        Outline3Shader.filledColor = filledColor.getColour();
                        Outline3Shader.filledMix = mix.getValFloat();
                        Outline3Shader.radius = radius.getValFloat();
                        Outline3Shader.ratio = ratio.getValFloat();
                    } else if(mode.getValEnum() == Shaders.Gradient2) {
                        Gradient2Shader.rgba = filledColor.getColour();
                        Gradient2Shader.rgba1 = filledColor.getColour();
                        Gradient2Shader.step = step.getValFloat();
                        Gradient2Shader.speed = speed.getValFloat();
                        Gradient2Shader.mix = mix.getValFloat();
                    }*/

                    return Unit.INSTANCE;
                };

                ShaderHelperKt.startShader(mode.getValEnum(), uniforms, event.getPartialTicks());

                if(flag6) {
                    renderSky();
                }

                if(flag2) {
                    for (Entity entity : entities) {
                        Vec3d vector = MathKt.interpolated(entity, event.getPartialTicks());
                        Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject(entity)).doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
                    }
                }

                if(flag) for(Drawable module : modulesToRender.keySet()) {
                    if(module instanceof ShaderableModule) {
                        ShaderableModule shaderable = (ShaderableModule) module;

                        shaderable.handleDrawShadered();
                    } else {
                        module.draw();
                    }
                }

                if(flag3) {
                    criticalSection = true;
                    mc.entityRenderer.renderHand(event.getPartialTicks(), 2);
                    criticalSection = false;
                }

                ShaderHelperKt.endShader(mode.getValEnum());

            }
        } catch (Exception ignored) { }
    }

    public Color getColor() {
        return rainbow.getValBoolean() ? ColorUtils.rainbowRGB(delay.getValInt(), saturation.getValFloat(), brightness.getValFloat()) : new Color(red.getValFloat(), green.getValFloat(), blue.getValFloat());
    }

    private void renderSky() {
        IRenderHandler renderer = mc.world.provider.getSkyRenderer();

        if(renderer != null) {
            renderer.render(mc.getRenderPartialTicks(), mc.world, mc);
        }

        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for (int k1 = 0; k1 < 6; ++k1) {
            GlStateManager.pushMatrix();

            if (k1 == 1) GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            if (k1 == 2) GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            if (k1 == 3) GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            if (k1 == 4) GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            if (k1 == 5) GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);

            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            bufferbuilder.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
            bufferbuilder.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
            bufferbuilder.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
            bufferbuilder.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
    }
}