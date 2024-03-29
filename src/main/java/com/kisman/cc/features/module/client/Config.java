package com.kisman.cc.features.module.client;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleInfo;
import com.kisman.cc.features.module.ModuleInstance;
import com.kisman.cc.features.subsystem.subsystems.RotationSystem;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.settings.types.number.NumberType;
import com.kisman.cc.util.Colour;
import com.kisman.cc.util.enums.GradientModes;
import org.lwjgl.input.Keyboard;

@ModuleInfo(
        name = "Config",
        desc = "bro think yourself more",
        category = Category.CLIENT,
        toggled = true,
        toggleable = false
)
public class Config extends Module {
    @ModuleInstance
    public static Config instance;

    public final Setting rotationTakeOff = register(new Setting("Rotation Take Off", this, 10.0, 0.0, 50.0, NumberType.TIME));

    private final SettingGroup main = register(new SettingGroup(new Setting("Main", this)));
    private final SettingGroup gui = register(new SettingGroup(new Setting("Gui", this)));
    private final SettingGroup glow = register(new SettingGroup(new Setting("Glow", this)));
    private final SettingGroup particles = register(new SettingGroup(new Setting("Particles", this)));
    private final SettingGroup other = register(new SettingGroup(new Setting("Other", this)));
    public Setting friends = register(main.add(new Setting("Friends", this, true)));
    public Setting nameMode = register(main.add(new Setting("Name Mode", this, NameMode.kismancc)));
    public Setting customName = register(main.add(new Setting("Custom Name", this, "kisman.cc", "kisman.cc", true).setVisible(() -> nameMode.getValBoolean())));
    public Setting scrollSpeed = register(main.add(new Setting("Scroll Speed", this, 15, 0, 100, NumberType.PERCENT)));
    public Setting horizontalScroll = register(main.add(new Setting("Horizontal Scroll", this, true)));
    public Setting keyForHorizontalScroll = register(main.add(new Setting("Key for Horizontal Scroll", this, Keyboard.KEY_LSHIFT).setVisible(() -> horizontalScroll.getValBoolean())));
    public Setting notification = register(main.add(new Setting("Notification", this, true)));
    public Setting notificationMode = register(main.add(new Setting("Notification Mode", this, NotificationMode.SingleLine)));
    public Setting guiGlow = register(gui.add(new Setting("Gui Glow", this, false).setTitle("Glow")));
    public Setting glowRadius = register(glow.add(new Setting("Glow Radius", this, 15, 0, 20, true).setTitle("Radius")));
    public Setting glowBoxSize = register(glow.add(new Setting("Glow Box Size", this, 0, 0, 20, true).setTitle("Size")));
    public Setting guiDesc = register(gui.add(new Setting("Gui Desc", this, true)));
    public Setting guiParticles = register(gui.add(new Setting("Gui Particles", this, false)));

    private final SettingGroup gradientGroup = register(gui.add(new SettingGroup(new Setting("Gradient", this))));
    private final SettingGroup colorGradientGroup = register(gradientGroup.add(new SettingGroup(new Setting("Color", this))));
    public Setting guiGradient = register(colorGradientGroup.add(new Setting("Gui Gradient", this, GradientModes.None).setTitle("Mode")));
    public Setting guiGradientDiff = register(colorGradientGroup.add(new Setting("Gui Gradient Diff", this, 1, 0, 1000, NumberType.TIME).setTitle("Diff").setVisible(() -> guiGradient.getValEnum() != GradientModes.None)));
    private final SettingGroup backgroundGradientGroup = register(gradientGroup.add(new SettingGroup(new Setting("Background", this))));
    public Setting guiGradientBackground = register(backgroundGradientGroup.add(new Setting("Gui Gradient Background", this, false).setTitle("State")));
    private final SettingGroup backgroundGradientStartColorGroup = register(backgroundGradientGroup.add(new SettingGroup(new Setting("Start", this))));
    private final SettingGroup backgroundGradientEndColourGroup = register(backgroundGradientGroup.add(new SettingGroup(new Setting("End", this))));
    public Setting ggbStartColorMode = register(backgroundGradientStartColorGroup.add(new Setting("GGB Start Color Mode", this, GGBColorMode.Custom).setTitle("Mode")));
    public Setting ggbStartColor = register(backgroundGradientStartColorGroup.add(new Setting("GGB Start Color", this, new Colour(0, 0, 0, 30)).setTitle("Color")));
    public Setting ggbEndColorMode = register(backgroundGradientEndColourGroup.add(new Setting("GGB End Color Mode", this, GGBColorMode.Custom).setTitle("Mode")));
    public Setting ggbEndColor = register(backgroundGradientEndColourGroup.add(new Setting("GGB End Color", this, new Colour(0, 0, 0, 30)).setTitle("Color")));

    public Setting guiOutline = register(gui.add(new Setting("Gui Outline", this, true).setTitle("Outline")));
    public Setting guiAstolfo = register(gui.add(new Setting("Gui Astolfo", this, false).setTitle("Astolfo")));
    public Setting guiRenderSize = register(gui.add(new Setting("Gui Render Size", this, false).setTitle("Render Size")));
    public Setting guiBetterCheckBox = register(gui.add(new Setting("Gui Better CheckBox", this, false).setTitle("Better CheckBox")));
    public Setting guiVisualPreview = register(gui.add(new Setting("Gui Visual Preview", this, false).setTitle("Visual Preview")));
    public Setting guiShowBinds = register(gui.add(new Setting("Gui Show Binds", this, false).setTitle("Show Binds")));
    public Setting configurate = register(other.add(new Setting("Configurate", this, true)));

    public Setting particlesRenderPoints = register(particles.add(new Setting("Particles Render Points", this, true).setTitle("Render Points")));

    public Setting particlesColor = register(particles.add(new Setting("Particles Color", this, "Dots Color", new Colour(0, 0, 255)).setVisible(particlesRenderPoints::getValBoolean)));

    public Setting particlesRenderLine = register(particles.add(new Setting("Particles Render Lines", this, true)));

    public Setting particlesGradientMode = register(particles.add(new Setting("Particles Gradient Mode", this, ParticlesGradientMode.None).setVisible(() -> guiParticles.getValBoolean() && particlesRenderLine.getValBoolean())));

    private final SettingGroup particlesColorsGroup = register(particles.add(new SettingGroup(new Setting("Colors", this))));

    public Setting particlesGStartColor = register(particlesColorsGroup.add(new Setting("Particles Gradient StartColor", this, "Start", new Colour(0, 0, 255)).setVisible(() -> guiParticles.getValBoolean() && !particlesGradientMode.getValString().equalsIgnoreCase(ParticlesGradientMode.None.name()) && particlesRenderLine.getValBoolean())));
    public Setting particlesGMiddleColor = register(particlesColorsGroup.add(new Setting("Particles Gradient MiddleColor", this, "Middle", new Colour(255, 0, 0, 200)).setVisible(() -> guiParticles.getValBoolean() && particlesGradientMode.getValString().equalsIgnoreCase(ParticlesGradientMode.ThreeGradient.name()) && particlesRenderLine.getValBoolean())));
    public Setting particlesGEndColor = register(particlesColorsGroup.add(new Setting("Particles Gradient EndColor", this, "End", new Colour(0, 0, 255)).setVisible(() -> guiParticles.getValBoolean() && !particlesGradientMode.getValString().equalsIgnoreCase(ParticlesGradientMode.None.name()) && particlesRenderLine.getValBoolean())));

    public Setting particlesWidth = register(particles.add(new Setting("Particles Width", this, 0.5, 0.0, 5, false).setTitle("Width").setVisible(() -> guiParticles.getValBoolean() && particlesRenderLine.getValBoolean())));

    public Setting particleTest = register(particles.add(new Setting("Particle Test", this, true).setTitle("Test").setVisible(() -> guiParticles.getValBoolean() && particlesRenderLine.getValBoolean())));

    private final SettingGroup particlesPointsGroup = register(particles.add(new SettingGroup(new Setting("Points", this))));

    public Setting particlePointsRandomAlpha = register(particlesPointsGroup.add(new Setting("Particle Points Random Alpha", this, false).setTitle("Alpha").setVisible(particlesRenderPoints::getValBoolean)));
    public Setting particlePointSizeModifier = register(particlesPointsGroup.add(new Setting("Particle Point Size Modifier", this, 1, 0.5, 3, false).setTitle("Size Mod").setVisible(particlesRenderPoints::getValBoolean)));

    public Setting particlesStartPointsCount = register(particlesPointsGroup.add(new Setting("Particles Start Points Count", this, 300, 100, 500, true).setTitle("Start Count")));

    public Config() {
        super();

        RotationSystem.takeOffDelaySetting = rotationTakeOff;
    }

    public enum NameMode {kismancc, LavaHack, TheKisDevs, kidman, TheClient, BloomWare, UwU, kidmad, EarthHack, Ferret, custom, ClientName}
    public enum ParticlesGradientMode {None, TwoGradient, ThreeGradient, Syns}
    public enum AstolfoColorMode {Old, Impr}
    public enum GGBColorMode {Custom, SynsWithGui}
    public enum NotificationMode {MultiLine, SingleLine};
}
