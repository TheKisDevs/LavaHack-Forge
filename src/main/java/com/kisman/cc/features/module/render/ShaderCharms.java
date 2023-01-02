package com.kisman.cc.features.module.render;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.render.shader.FramebufferShader;
import com.kisman.cc.features.module.render.shader.shaders.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.enums.ShaderModes;
import com.kisman.cc.util.interfaces.Drawable;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.math.MathUtil;
import com.kisman.cc.util.render.ColorUtils;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ShaderCharms extends Module {
    private final Setting range = register(new Setting("Range", this, 32, 8, 64, true));
    public final Setting mode = register(new Setting("Mode", this, ShaderModes.SMOKE));

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
    private final Setting itemsFix = register(types.add(new Setting("Items Fix", this, false).setVisible(items::getValBoolean)));

    private final SettingGroup config = register(new SettingGroup(new Setting("Config", this)));

    private final Setting animationSpeed = register(config.add(new Setting("Animation Speed", this, 0, 1, 10, false).setVisible(() -> !mode.checkValString("GRADIENT"))));

    private final Setting blur = register(config.add(new Setting("Blur", this, true).setVisible(() -> mode.checkValString("ITEMGLOW"))));
    private final Setting radius = register(config.add(new Setting("Radius", this, 2, 0.1f, 10, false).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE") || mode.checkValString("GRADIENT") || mode.checkValString("Outline2"))));
    private final Setting mix = register(config.add(new Setting("Mix", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("Outline2"))));
    private final Setting red = register(config.add(new Setting("Red", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE") || mode.checkValString("Outline2") || mode.checkValString("InertiaOutline"))));
    private final Setting green = register(config.add(new Setting("Green", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE") || mode.checkValString("Outline2") || mode.checkValString("InertiaOutline"))));
    private final Setting blue = register(config.add(new Setting("Blue", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE") || mode.checkValString("Outline2") || mode.checkValString("InertiaOutline"))));
    private final Setting rainbow = config.add(new Setting("RainBow", this, true).setVisible(() -> mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("Outline2")));
    private final Setting delay = register(config.add(new Setting("Delay", this, 100, 1, 2000, true)));
    private final Setting saturation = register(config.add(new Setting("Saturation", this, 36, 0, 100, NumberType.PERCENT)));
    private final Setting brightness = register(config.add(new Setting("Brightness", this, 100, 0, 100, NumberType.PERCENT)));

    private final Setting quality = register(config.add(new Setting("Quality", this, 1, 0, 20, false).setVisible(() -> mode.checkValString("GRADIENT") || mode.checkValString("ITEMGLOW") || mode.checkValString("GLOW") || mode.checkValString("OUTLINE"))));
    private final Setting gradientAlpha = register(config.add(new Setting("Gradient Alpha", this, false).setVisible(() -> mode.checkValString("GRADIENT"))));
    private final Setting alphaGradient = register(config.add(new Setting("Alpha Gradient Value", this, 255, 0, 255, true).setVisible(() -> mode.checkValString("GRADIENT"))));
    private final Setting duplicateOutline = register(config.add(new Setting("Duplicate Outline", this, 1, 0, 20, false).setVisible(() -> mode.checkValString("GRADIENT"))));
    private final Setting moreGradientOutline = register(config.add(new Setting("More Gradient", this, 1, 0, 10, false).setVisible(() -> mode.checkValString("GRADIENT"))));
    private final Setting creepyOutline = register(config.add(new Setting("Creepy", this, 1, 0, 20, false).setVisible(() -> mode.checkValString("GRADIENT"))));
    private final Setting alpha = register(config.add(new Setting("Alpha", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("GRADIENT"))));
    private final Setting numOctavesOutline = register(config.add(new Setting("Num Octaves", this, 5, 1, 30, true).setVisible(() -> mode.checkValString("GRADIENT"))));
    private final Setting speedOutline = register(config.add(new Setting("Speed", this, 0.1, 0.001, 0.1, false).setVisible(() -> mode.checkValString("GRADIENT"))));

    private final Setting rainbowSpeed = register(config.add(new Setting("Rainbow Speed", this, 0.4, 0, 1, false).setVisible(() -> mode.checkValString("OUTLINE") || mode.checkValString("InertiaOutline"))));
    private final Setting rainbowStrength = register(config.add(new Setting("Rainbow Strength", this, 0.3, 0, 1, false).setVisible(() -> mode.checkValString("OUTLINE") || mode.checkValString("InertiaOutline"))));
    private final Setting rainbowSaturation = register(config.add(new Setting("Rainbow Saturation", this, 0.5, 0, 1, false).setVisible(() -> mode.checkValString("OUTLINE") || mode.checkValString("InertiaOutline"))));

//    private final Setting useImage = register(config.add(new Setting("Use Image", this, false)));
//    private final Setting imageMix = register(config.add(new Setting("Image Mix", this, 0.5, 0, 1, false)));

    @ModuleInstance
    public static ShaderCharms instance;

    private boolean criticalSection = false;
    private boolean flag = false;

    private ArrayList<Entity> entities = new ArrayList<>();

    public static HashMap<Drawable, Supplier<Boolean>> modules = new HashMap<>();
    private final ArrayList<Drawable> modulesToRender = new ArrayList<>();

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
        if(items.getValBoolean() && itemsFix.getValBoolean() && !criticalSection) event.setCanceled(true);
    }

    public void update() {
        modulesToRender.clear();

        for(Map.Entry<Drawable, Supplier<Boolean>> entry : modules.entrySet()) {
            Drawable module = entry.getKey();
            Supplier<Boolean> supplier = entry.getValue();

            if(supplier.get()) modulesToRender.add(module);
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
                //shitty code tbh

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
                    ((OutlineShader) framebufferShader).rainbowSpeed = rainbowSpeed.getValFloat();
                    ((OutlineShader) framebufferShader).rainbowStrength = rainbowStrength.getValFloat();
                    ((OutlineShader) framebufferShader).saturation = rainbowSaturation.getValFloat();
                }
                framebufferShader.startDraw(event.getPartialTicks());

                if(flag2) {
                    for (Entity entity : entities) {
                        Vec3d vector = MathUtil.getInterpolatedRenderPos(entity, event.getPartialTicks());
                        Objects.requireNonNull(mc.getRenderManager().getEntityRenderObject(entity)).doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, event.getPartialTicks());
                    }
                }

                if(flag3) {
                    criticalSection = true;
                    mc.entityRenderer.renderHand(event.getPartialTicks(), 2);
                    criticalSection = false;
                }

                if(flag) for(Drawable module : modulesToRender) module.draw();

                framebufferShader.stopDraw();
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
                ChatUtility.error().printClientModuleMessage("[ShaderCharms] Error, Config -> AntiOpenGLCrash disabled ShaderCharms");
            }
        }
    }

    public Color getColor() {
        return rainbow.getValBoolean() ? ColorUtils.rainbowRGB(delay.getValInt(), saturation.getValFloat(), brightness.getValFloat()) : new Color(red.getValFloat(), green.getValFloat(), blue.getValFloat());
    }
}