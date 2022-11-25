package com.kisman.cc.features.module.render;

import com.kisman.cc.Kisman;
import com.kisman.cc.event.events.RenderEntityEvent;
import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.render.shader.FramebufferShader;
import com.kisman.cc.features.module.render.shader.ShaderUtil;
import com.kisman.cc.features.module.render.shader.shaders.*;
import com.kisman.cc.features.module.render.shader.shaders.troll.ShaderHelper;
import com.kisman.cc.mixin.mixins.accessor.AccessorShaderGroup;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.settings.util.MultiThreaddableModulePattern;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.enums.ShaderModes;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.math.MathUtil;
import com.kisman.cc.util.render.ColorUtils;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class ShaderCharms extends Module {
    private final Setting range = register(new Setting("Range", this, 32, 8, 64, true));
    public final Setting mode = register(new Setting("Mode", this, ShaderModes.SMOKE));

    private final MultiThreaddableModulePattern multiThread = threads();

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

    private final Setting hideOriginal = register(config.add(new Setting("Hide Original", this, false).setVisible(() -> mode.checkValString("Outline2"))));
    private final Setting outlineAlpha = register(config.add(new Setting("Outline Alpha", this, 1, 0, 1, false).setVisible(() -> mode.checkValString("Outline2"))));
    private final Setting filledAlpha = register(config.add(new Setting("Filled Alpha", this, (63f / 255f), 0, 1, false).setVisible(() -> mode.checkValString("Outline2"))));
    private final Setting width = register(config.add(new Setting("Width", this, 2, 1, 8, false).setVisible(() -> mode.checkValString("Outline2"))));
    private final Setting ratio = register(config.add(new Setting("Ratio", this, 0.5, 0,1, false).setVisible(() -> mode.checkValString("Outline2"))));

    private final Setting rainbowSpeed = register(config.add(new Setting("Rainbow Speed", this, 0.4, 0, 1, false).setVisible(() -> mode.checkValString("OUTLINE") || mode.checkValString("InertiaOutline"))));
    private final Setting rainbowStrength = register(config.add(new Setting("Rainbow Strength", this, 0.3, 0, 1, false).setVisible(() -> mode.checkValString("OUTLINE") || mode.checkValString("InertiaOutline"))));
    private final Setting rainbowSaturation = register(config.add(new Setting("Rainbow Saturation", this, 0.5, 0, 1, false).setVisible(() -> mode.checkValString("OUTLINE") || mode.checkValString("InertiaOutline"))));

//    private final Setting useImage = register(config.add(new Setting("Use Image", this, false)));
//    private final Setting imageMix = register(config.add(new Setting("Image Mix", this, 0.5, 0, 1, false)));

    public static ShaderCharms instance;

    private boolean criticalSection = false;

    private final ShaderHelper shaderHelperOutline2 = new ShaderHelper(new ResourceLocation("shaders/post/esp_outline.json"));
    private final ShaderHelper shaderHelperInertiaOutline = new ShaderHelper(new ResourceLocation("shaders/post/inertia_entity_outline.json"));
    private final Framebuffer frameBufferFinalOutline2 = shaderHelperOutline2.getFrameBuffer("final");
    private final Framebuffer frameBufferFinalInertiaOutline = shaderHelperInertiaOutline.getFrameBuffer("final");

    private ArrayList<Entity> entities = new ArrayList<>();

    public ShaderCharms() {
        super("ShaderCharms", Category.RENDER);
        super.setDisplayInfo(() -> "[" + mode.getValString() + "]");

        instance = this;
    }

    public void onEnable() {
        super.onEnable();
        multiThread.reset();
        Kisman.EVENT_BUS.subscribe(renderListener);
    }

    public void onDisable() {
        super.onDisable();
        Kisman.EVENT_BUS.unsubscribe(renderListener);
    }

    @SubscribeEvent
    public void onRenderHand(RenderHandEvent event) {
        if(items.getValBoolean() && itemsFix.getValBoolean() && (!criticalSection || (mode.checkValString("Outline2") && hideOriginal.getValBoolean()))) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onFog(EntityViewRenderEvent.FogColors event) {
        ((AccessorShaderGroup) Objects.requireNonNull(shaderHelperOutline2.getShader())).getListFramebuffers().forEach(framebuffer -> {
            framebuffer.setFramebufferColor(event.getRed(), event.getGreen(), event.getBlue(), 0);
        });

        ((AccessorShaderGroup) Objects.requireNonNull(shaderHelperInertiaOutline.getShader())).getListFramebuffers().forEach(framebuffer -> {
            framebuffer.setFramebufferColor(event.getRed(), event.getGreen(), event.getBlue(), 0);
        });
    }

    @EventHandler
    private final Listener<RenderEntityEvent.All.Pre> renderListener = new Listener<>(event -> {
        if(mode.checkValString("Outline2") && !mc.renderManager.renderOutlines && hideOriginal.getValBoolean() && mc.player.getDistance(event.getEntity()) <= range.getValFloat() && entityTypeCheck(event.getEntity())) event.cancel();
    });

    public boolean entityTypeCheck(Entity entity) {
        return entity != mc.player && ((entity instanceof EntityPlayer && players.getValBoolean())
                || (entity instanceof EntityPlayer && friends.getValBoolean() && FriendManager.instance.isFriend(entity.getName()))
                || (entity instanceof EntityEnderCrystal && crystals.getValBoolean())
                || ((entity instanceof EntityMob || entity instanceof EntitySlime) && mobs.getValBoolean())
                || ((entity instanceof EntityEnderPearl) && enderPearls.getValBoolean())
                || ((entity instanceof EntityItem) && itemsEntity.getValBoolean())
                || (entity instanceof EntityAnimal && animals.getValBoolean()));
    }

    private void outline2Shader(float particalTicks) {
        if(frameBufferFinalOutline2 == null) return;
        Outline2Shader.INSTANCE.setupUniforms(outlineAlpha.getValFloat(), filledAlpha.getValFloat(), width.getValFloat(), (float) ((width.getAlpha() - 1.0f) * (Math.pow(ratio.getValFloat(), 3)) + 1.0f));
        Outline2Shader.INSTANCE.updateUniforms(shaderHelperOutline2);
        ShaderUtil.Companion.clearFrameBuffer(frameBufferFinalOutline2);
        Outline2Shader.INSTANCE.drawEntities(particalTicks, range.getValFloat());
        Outline2Shader.INSTANCE.drawShader(shaderHelperOutline2, frameBufferFinalOutline2, particalTicks);
    }

    private void inertiaOutlineShader(float ticks) {
        if(frameBufferFinalInertiaOutline == null) return;
//        InertiaOutlineShader.INSTANCE.setupUniforms(
//                new Colour(red.getValFloat(), green.getValFloat(), blue.getValFloat(), 1f),
//                radius.getValFloat(),
//                rainbowSpeed.getValFloat() != 0,
//                rainbowSpeed.getValFloat(),
//                getStrength(),
//                rainbowSaturation.getValFloat()
//        );
//        InertiaOutlineShader.INSTANCE.updateUniforms(shaderHelperInertiaOutline);
        ShaderUtil.Companion.clearFrameBuffer(frameBufferFinalInertiaOutline);
        InertiaOutlineShader.INSTANCE.drawEntities(ticks, range.getValFloat());
        InertiaOutlineShader.INSTANCE.drawShader(shaderHelperInertiaOutline, frameBufferFinalInertiaOutline, ticks);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        try {
            if(players.getValBoolean() || friends.getValBoolean() || crystals.getValBoolean() || mobs.getValBoolean() || enderPearls.getValBoolean() || itemsEntity.getValBoolean() || animals.getValBoolean()) {
                multiThread.update(() -> {
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
            } else {
                entities.clear();
            }

            if(!entities.isEmpty()) {
                if(mode.checkValString("Outline2")) outline2Shader(event.getPartialTicks());
                else if(mode.checkValString("InertiaOutline")) inertiaOutlineShader(event.getPartialTicks());
                else {
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
                        ((OutlineShader) framebufferShader).rainbowSpeed = rainbowSpeed.getValFloat();
                        ((OutlineShader) framebufferShader).rainbowStrength = rainbowStrength.getValFloat();
                        ((OutlineShader) framebufferShader).saturation = rainbowSaturation.getValFloat();
                    }
                    framebufferShader.startDraw(event.getPartialTicks());
                    for (Entity entity : entities) {
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
            }



            if (items.getValBoolean() && mc.gameSettings.thirdPersonView == 0 && !mode.checkValString("Outline2")) {
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
                    } else if (glow) {
                        ((GlowShader) framebufferShader).red = getColor().getRed() / 255f;
                        ((GlowShader) framebufferShader).green = getColor().getGreen() / 255f;
                        ((GlowShader) framebufferShader).blue = getColor().getBlue() / 255f;
                        ((GlowShader) framebufferShader).radius = radius.getValFloat();
                        ((GlowShader) framebufferShader).quality = quality.getValFloat();
                    } else if (outline) {
                        ((OutlineShader) framebufferShader).red = getColor().getRed() / 255f;
                        ((OutlineShader) framebufferShader).green = getColor().getGreen() / 255f;
                        ((OutlineShader) framebufferShader).blue = getColor().getBlue() / 255f;
                        ((OutlineShader) framebufferShader).radius = radius.getValFloat();
                        ((OutlineShader) framebufferShader).quality = quality.getValFloat();
                    }
                    criticalSection = true;
                    framebufferShader.startDraw(event.getPartialTicks());
                    mc.entityRenderer.renderHand(event.getPartialTicks(), 2);
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
                ChatUtility.error().printClientModuleMessage("[ShaderCharms] Error, Config -> AntiOpenGLCrash disabled ShaderCharms");
            }
        }
    }

    public Color getColor() {
        return rainbow.getValBoolean() ? ColorUtils.rainbowRGB(delay.getValInt(), saturation.getValFloat(), brightness.getValFloat()) : new Color(red.getValFloat(), green.getValFloat(), blue.getValFloat());
    }
}
