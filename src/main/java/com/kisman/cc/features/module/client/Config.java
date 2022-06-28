package com.kisman.cc.features.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.manager.file.*;
import com.kisman.cc.features.module.*;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.enums.GradientModes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class Config extends Module {
    public static Config instance;

    private final SettingGroup main = register(new SettingGroup(new Setting("Main", this)));
    private final SettingGroup gui = register(new SettingGroup(new Setting("Gui", this)));
    private final SettingGroup glow = register(new SettingGroup(new Setting("Glow", this)));
    private final SettingGroup particles = register(new SettingGroup(new Setting("Particles", this)));
    private final SettingGroup other = register(new SettingGroup(new Setting("Other", this)));

    public Setting capeAPI = register(main.add(new Setting("Cape API", this, true)));
    public Setting astolfoColorMode = new Setting("Astolfo Color Mode", this, AstolfoColorMode.Old);
    public Setting friends = register(main.add(new Setting("Friends", this, true)));
    public Setting nameMode = register(main.add(new Setting("Name Mode", this, NameMode.kismancc)));
    public Setting customName = register(main.add(new Setting("Custom Name", this, "kisman.cc", "kisman.cc", true).setVisible(() -> nameMode.getValBoolean())));
    public Setting scrollSpeed = register(main.add(new Setting("Scroll Speed", this, 15, 0, 100, NumberType.PERCENT)));
    public Setting horizontalScroll = register(main.add(new Setting("Horizontal Scroll", this, true)));
    public Setting keyForHorizontalScroll = register(main.add(new Setting("Key for Horizontal Scroll", this, Keyboard.KEY_LSHIFT).setVisible(() -> horizontalScroll.getValBoolean())));
    public Setting notification = register(main.add(new Setting("Notification", this, true)));
    public Setting guiGlow = register(gui.add(new Setting("Gui Glow", this, false)));
    public Setting glowRadius = register(glow.add(new Setting("Glow Radius", this, 15, 0, 20, true)));
    public Setting glowBoxSize = register(glow.add(new Setting("Glow Box Size", this, 0, 0, 20, true)));
    public Setting guiGradient = register(gui.add(new Setting("Gui Gradient", this, GradientModes.None)));
    public Setting guiGradientDiff = register(gui.add(new Setting("Gui Gradient Diff", this, 1, 0, 1000, NumberType.TIME).setVisible(() -> guiGradient.getValEnum() != GradientModes.None)));
    public Setting guiDesc = register(gui.add(new Setting("Gui Desc", this, false)));
    public Setting guiParticles = register(gui.add(new Setting("Gui Particles", this, true)));
    public Setting guiGradientBackground = register(gui.add(new Setting("Gui Gradient Background", this, false)));
    public Setting ggbStartColorMode = register(gui.add(new Setting("GGB Start Color Mode", this, GGBColorMode.Custom).setVisible(guiGradientBackground::getValBoolean)));
    public Setting ggbStartColor = register(gui.add(new Setting("GGB Start Color", this, new Colour(0, 0, 0, 30)).setVisible(() -> guiGradientBackground.getValBoolean() && ggbStartColorMode.getValEnum() == GGBColorMode.Custom)));
    public Setting ggbEndColorMode = register(gui.add(new Setting("GGB End Color Mode", this, GGBColorMode.Custom).setVisible(guiGradientBackground::getValBoolean)));
    public Setting ggbEndColor = register(gui.add(new Setting("GGB End Color", this, new Colour(0, 0, 0, 30)).setVisible(() -> guiGradientBackground.getValBoolean() && ggbEndColorMode.getValEnum() == GGBColorMode.Custom)));
    public Setting guiOutline = register(gui.add(new Setting("Gui Outline", this, true)));
    public Setting guiAstolfo = register(gui.add(new Setting("Gui Astolfo", this, false)));
    public Setting guiRenderSize = register(gui.add(new Setting("Gui Render Size", this, false)));
    public Setting guiBetterCheckBox = register(gui.add(new Setting("Gui Better CheckBox", this, false)));
    public Setting guiBlur = register(gui.add(new Setting("Gui Blur", this, true)));
    public Setting guiVisualPreview = register(gui.add(new Setting("Gui Visual Preview", this, false)));
    public Setting guiShowBinds = register(gui.add(new Setting("Gui Show Binds", this, false)));
    public Setting pulseMin = register(other.add(new Setting("Pulse Min", this, 255, 0, 255, true)));
    public Setting pulseMax = register(other.add(new Setting("Pulse Max", this, 110, 0, 255, true)));
    public Setting pulseSpeed = register(other.add(new Setting("Pulse Speed", this, 1.5, 0.1, 10, false)));
    public Setting saveConfig = register(main.add(new Setting("Save Config", this, false)));
    public Setting loadConfig = register(main.add(new Setting("Load Config", this, false)));
    public Setting configurate = register(other.add(new Setting("Configurate", this, true)));

    public Setting particlesRenderPoints = register(particles.add(new Setting("Particles Render Points", this, true)));

    public Setting particlesColor = register(particles.add(new Setting("Particles Color", this, "Particles Dots Color", new Colour(0, 0, 255)).setVisible(particlesRenderPoints::getValBoolean)));

    public Setting particlesRenderLine = register(particles.add(new Setting("Particles Render Lines", this, true)));

    public Setting particlesGradientMode = register(particles.add(new Setting("Particles Gradient Mode", this, ParticlesGradientMode.None).setVisible(() -> guiParticles.getValBoolean() && particlesRenderLine.getValBoolean())));

    public Setting particlesGStartColor = register(particles.add(new Setting("Particles Gradient StartColor", this, "Particles Gradient Start Color", new Colour(0, 0, 255)).setVisible(() -> guiParticles.getValBoolean() && !particlesGradientMode.getValString().equalsIgnoreCase(ParticlesGradientMode.None.name()) && particlesRenderLine.getValBoolean())));
    public Setting particlesGMiddleColor = register(particles.add(new Setting("Particles Gradient MiddleColor", this, "Particles Gradient Middle Color", new Colour(255, 0, 0, 200)).setVisible(() -> guiParticles.getValBoolean() && particlesGradientMode.getValString().equalsIgnoreCase(ParticlesGradientMode.ThreeGradient.name()) && particlesRenderLine.getValBoolean())));
    public Setting particlesGEndColor = register(particles.add(new Setting("Particles Gradient EndColor", this, "Particles Gradient End Color", new Colour(0, 0, 255)).setVisible(() -> guiParticles.getValBoolean() && !particlesGradientMode.getValString().equalsIgnoreCase(ParticlesGradientMode.None.name()) && particlesRenderLine.getValBoolean())));

    public Setting particlesWidth = register(particles.add(new Setting("Particles Width", this, 0.5, 0.0, 5, false).setVisible(() -> guiParticles.getValBoolean() && particlesRenderLine.getValBoolean())));

    public Setting particleTest = register(particles.add(new Setting("Particle Test", this, true).setVisible(() -> guiParticles.getValBoolean() && particlesRenderLine.getValBoolean())));

    public Setting particlePointsRandomAlpha = register(particles.add(new Setting("Particle Points Random Alpha", this, false).setVisible(particlesRenderPoints::getValBoolean)));
    public Setting particlePointSizeModifier = register(particles.add(new Setting("Particle Point Size Modifier", this, 1, 0.5, 3, false).setVisible(particlesRenderPoints::getValBoolean)));

    public Setting particlesStartPointsCount = register(particles.add(new Setting("Particles Start Points Count", this, 300, 100, 500, true)));

    public Setting slowRender = register(other.add(new Setting("Slow Render", this, false)));
    public Setting antiOpenGLCrash = register(other.add(new Setting("Anti OpenGL Crash", this, false)));


    public Config() {
        super("Config", Category.CLIENT, false);
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if(saveConfig.getValBoolean()) {
            try {
                Kisman.instance.configManager.getSaver().init();
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveConfig.setValBoolean(false);
            if(mc.player != null && mc.world != null) ChatUtility.complete().printClientClassMessage("Config was saved!");
        }

        if(loadConfig.getValBoolean()) {
            LoadConfig.init();
            try {
                Kisman.instance.configManager.getLoader().init();
            } catch (IOException e) {
                e.printStackTrace();
            }
            loadConfig.setValBoolean(false);
            if(mc.player != null && mc.world != null) ChatUtility.complete().printClientClassMessage("Config was loaded!");
        }
    }

    public enum NameMode {kismancc, LavaHack, TheKisDevs, kidman, TheClient, BloomWare, UwU, kidmad, EarthHack, Ferret, custom}
    public enum ParticlesGradientMode {None, TwoGradient, ThreeGradient, Syns}
    public enum AstolfoColorMode {Old, Impr}
    public enum GGBColorMode {Custom, SynsWithGui}
}
