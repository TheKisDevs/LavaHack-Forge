package com.kisman.cc.module.client;

import com.kisman.cc.file.*;
import com.kisman.cc.module.*;
import com.kisman.cc.oldclickgui.csgo.components.Slider;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.Colour;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Config extends Module {
    public static Config instance;

    public Setting friends = new Setting("Friends", this, true);
    public Setting nameMode = new Setting("Name Mode", this, NameMode.kismancc);
    public Setting customName = new Setting("Custom Name", this, "kisman.cc", "kisman.cc", true);
    public Setting scrollSpeed = new Setting("Scroll Speed", this, 15, 0, 100, Slider.NumberType.PERCENT);
    public Setting guiGlow = new Setting("Gui Glow", this, false);
    public Setting glowOffset = new Setting("Glow Offset", this, 6, 1, 20, true);
    public Setting glowRadius = new Setting("Glow Radius", this, 15, 0, 20, true);
    public Setting glowBoxSize = new Setting("Glow Box Size", this, 0, 0, 20, true);
    public Setting guiDesc = new Setting("Gui Desc", this, false);
    public Setting guiParticles = new Setting("Gui Particles", this, true);
    public Setting guiOutline = new Setting("Gui Outline", this, true);
    public Setting guiAstolfo = new Setting("Gui Astolfo", this, false);
    public Setting guiRenderSIze = new Setting("Gui Render Size", this, false);
    public Setting guiOpenAnimation = new Setting("Gui Open Animation", this, false);
    public Setting pulseMin = new Setting("Pulse Min", this, 255, 0, 255, true);
    public Setting pulseMax = new Setting("Pulse Max", this, 110, 0, 255, true);
    public Setting pulseSpeed = new Setting("Pulse Speed", this, 1.5, 0.1, 10, false);
    public Setting saveConfig = new Setting("Save Config", this, false);
    public Setting loadConfig = new Setting("Load Config", this, false);
    public Setting configurate = new Setting("Configurate", this, true);
    public Setting particlesColor = new Setting("Particles Color", this, "Particles Color", new Colour(0, 0, 255));

    public Setting TwoGradientParticles = new Setting("Two Gradient Particles", this, true);

    public Setting particlesGStartColor = new Setting("Particles Gradient StartColor", this, "Particles Gradient StartColor", new Colour(0, 0, 255));
    public Setting particlesGEndColor = new Setting("Particles Gradient EndColor", this, "Particles Gradient EndColor", new Colour(0, 0, 255));

    public Setting particlesWidth = new Setting("Particles Width", this, 0.5, 0.0, 5, false);

    public Config() {
        super("Config", Category.CLIENT, false);

        instance = this;

        setmgr.rSetting(friends);
        setmgr.rSetting(nameMode);
        setmgr.rSetting(customName);
        setmgr.rSetting(scrollSpeed);
        setmgr.rSetting(guiGlow);
        setmgr.rSetting(glowOffset);
        setmgr.rSetting(glowRadius);
        setmgr.rSetting(glowBoxSize);
        setmgr.rSetting(guiDesc);
        setmgr.rSetting(guiParticles);
        setmgr.rSetting(guiOutline);
        setmgr.rSetting(guiAstolfo);
        setmgr.rSetting(guiRenderSIze);
        setmgr.rSetting(guiOpenAnimation);
        setmgr.rSetting(pulseMin);
        setmgr.rSetting(pulseMax);
        setmgr.rSetting(pulseSpeed);
        setmgr.rSetting(saveConfig);
        setmgr.rSetting(loadConfig);
        setmgr.rSetting(configurate);
        setmgr.rSetting(particlesColor);
        setmgr.rSetting(TwoGradientParticles);
        setmgr.rSetting(particlesGStartColor);
        setmgr.rSetting(particlesGEndColor);
        setmgr.rSetting(particlesWidth);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent event) {
        if(saveConfig.getValBoolean()) {
            SaveConfig.init();
            ChatUtils.complete("Config saved");
            saveConfig.setValBoolean(false);
        }

        if(loadConfig.getValBoolean()) {
            LoadConfig.init();
            ChatUtils.complete("Config loaded");
            loadConfig.setValBoolean(false);
        }
    }

    public enum NameMode {kismancc, LavaHack, TheKisDevs, custom}
}
