package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.render.shader.FramebufferShader;
import com.kisman.cc.features.module.render.shader.shaders.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingEnum;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.collections.Pair;
import com.kisman.cc.util.enums.Shaders;
import com.kisman.cc.util.interfaces.Drawable;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.math.MathUtil;
import com.kisman.cc.util.render.ColorUtils;
import com.kisman.cc.util.render.Rendering;
import com.kisman.cc.util.render.shader.ShaderHelperKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ShaderCharms extends Module {
    private final Setting testtest = register(new Setting("Test Test", this, false));
    private final Setting testtest2 = register(new Setting("Test Test 2", this, false));
    private final Setting testtest3 = register(new Setting("Test Test 3", this, false));
    private final Setting testtest4 = register(new Setting("Test Test 4", this, false));
    private final Setting testtest5 = register(new Setting("Test Test 5", this, false));
    private final Setting testtest6 = register(new Setting("Test Test 6", this, false));
    private final Setting range = register(new Setting("Range", this, 32, 8, 64, true));
    public final SettingEnum<Shaders> mode = register(new SettingEnum<>("Mode", this, Shaders.AQUA));

    private final MultiThreaddableModulePattern threads = threads();

    private final SettingGroup types = register(new SettingGroup(new Setting("Types", this)));

    private final Setting crystals = register(types.add(new Setting("Crystals", this, true)));
    private final Setting players = register(types.add(new Setting("Players", this, false)));
    private final Setting friends = register(types.add(new Setting("Friends", this, true)));
    private final Setting mobs = register(types.add(new Setting("Mobs", this, false)));
    private final Setting animals = register(types.add(new Setting("Animals", this, false)));
    private final Setting enderPearls = register(types.add(new Setting("Ender Pearls", this, false)));
    private final Setting itemsEntity = register(types.add(new Setting("Items(Entity)", this, false)));
    public final Setting items = register(types.add(new Setting("Items", this, true)));
    private final Setting itemsFix = register(types.add(new Setting("Items Fix", this, false)));

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

    public ShaderCharms() {
        super("ShaderCharms", Category.RENDER);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");
        super.setToggled(true);
        super.toggleable = false;
    }

    public void onEnable() {
        super.onEnable();
        threads.reset();
        modulesToRender.clear();
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if(testtest6.getValBoolean()) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }

        if(items.getValBoolean() && itemsFix.getValBoolean() && !criticalSection) event.setCanceled(true);
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

    public boolean entityTypeCheck(Entity entity) {
        return entity != mc.player && ((entity instanceof EntityPlayer && players.getValBoolean())
                || (entity instanceof EntityPlayer && friends.getValBoolean() && FriendManager.instance.isFriend(entity.getName()))
                || (entity instanceof EntityEnderCrystal && crystals.getValBoolean())
                || ((entity instanceof EntityMob || entity instanceof EntitySlime) && mobs.getValBoolean())
                || ((entity instanceof EntityEnderPearl) && enderPearls.getValBoolean())
                || ((entity instanceof EntityItem) && itemsEntity.getValBoolean())
                || (entity instanceof EntityAnimal && animals.getValBoolean()));
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
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

            if(flag || flag2 || flag3) {
                if(testtest4.getValBoolean()) {
                    Rendering.setup();
                    Rendering.release();
                }

                if(flag && flag5) for(Drawable module : modulesToRender.keySet()) if(modulesToRender.get(module)) module.draw();


                if(testtest3.getValBoolean()) {
                    GlStateManager.disableLighting();
                    GlStateManager.depthMask(false);
                    GL11.glDisable(2929);
                }

                if(testtest5.getValBoolean()) {
                    GlStateManager.enableDepth();
                    GlStateManager.depthMask(false);
                }

                Function0<Unit> uniforms = () -> {
                    FramebufferShader framebufferShader = mode.getValEnum().getBuffer();
                    framebufferShader.animationSpeed = animationSpeed.getValInt();

                    if (mode.getValEnum() == Shaders.ITEMGLOW) {
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
                    } else if(mode.getValEnum() == Shaders.Circle) {
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
                    }

                    return Unit.INSTANCE;
                };

                ShaderHelperKt.startShader(mode.getValEnum(), uniforms, event.getPartialTicks());

                if(flag2) {
                    for (Entity entity : entities) {
                        Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
                        Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject(entity)).doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
                    }
                }

                if(flag) for(Drawable module : modulesToRender.keySet()) module.draw();

                if(flag3) {
                    criticalSection = true;
                    mc.entityRenderer.renderHand(event.getPartialTicks(), 2);
                    criticalSection = false;
                }

                ShaderHelperKt.endShader(mode.getValEnum());

            }

            if(!flag3 && testtest2.getValBoolean()) {
                mc.entityRenderer.renderHand(event.getPartialTicks(), 2);
            }
        } catch (Exception ignored) {
            if(Config.instance.antiOpenGLCrash.getValBoolean()) {
                ChatUtility.error().printClientModuleMessage("Error, Config -> AntiOpenGLCrash disabled ShaderCharms");
            }
        }
    }

    public Color getColor() {
        return rainbow.getValBoolean() ? ColorUtils.rainbowRGB(delay.getValInt(), saturation.getValFloat(), brightness.getValFloat()) : new Color(red.getValFloat(), green.getValFloat(), blue.getValFloat());
    }
}