package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.LineMode;
import i.gishreloaded.gishcode.utils.visual.ColorUtils;

import java.util.ArrayList;

public class ColorModule extends Module {
    public static ColorModule instance;

    private Setting synsLine = new Setting("SynsLine", this, "Syns");
    public Setting synsColor = new Setting("Syns", this, "SynsColor", new float[] {1, 1, 1, 1}, false);

    ColorUtil colorUtil;

    boolean rainbowLine = false;

    public ColorModule() {
        super("Color", "color setting", Category.CLIENT);
        colorUtil = new ColorUtil();

        instance = this;

        ArrayList<String> lineMode = new ArrayList<>();
        lineMode.add("LeftLine");
        lineMode.add("LLine+TLine");
        lineMode.add("Box");

        ArrayList<String> setLineMode = new ArrayList<>();
        setLineMode.add("Default");
        setLineMode.add("All");
        setLineMode.add("OnlySettings");

        Kisman.instance.settingsManager.rSetting(new Setting("LineSetting",this, "Line"));
        Kisman.instance.settingsManager.rSetting(new Setting("Line", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("LineMode", this, "LeftLine", lineMode));
        Kisman.instance.settingsManager.rSetting(new Setting("SetLineMode", this, "Default", setLineMode));
        Kisman.instance.settingsManager.rSetting(new Setting("LineColor", this, "LineColor", new float[] {0f, 1f, 1f, 1f}, false));
        Kisman.instance.settingsManager.rSetting(new Setting("BackgroundSetting", this, "Background"));
        Kisman.instance.settingsManager.rSetting(new Setting("BackgroundColor", this, "BackgroundColor", new float[] {0f, 0.02f, 0.59f, 0.6f}, false));
        Kisman.instance.settingsManager.rSetting(new Setting("ABackgroundColor", this, "ABackgroundColor", new float[] {0.52f, 0.74f, 0.73f, 1f}, false));
        Kisman.instance.settingsManager.rSetting(new Setting("TextSetting", this, "Text"));
        Kisman.instance.settingsManager.rSetting(new Setting("TextColor", this, "TextColor", new float[] {3.5f, 0.04f, 0.65f, 1f}, false));
        Kisman.instance.settingsManager.rSetting(new Setting("ATextColor", this, "ATextColor", new float[] {1f, 1f, 1f, 1f}, false));
        Kisman.instance.settingsManager.rSetting(new Setting("DifferentSetting", this, "Different"));
        Kisman.instance.settingsManager.rSetting(new Setting("HoveredColor", this, "HoveredColor", new float[] {0.6f, 0.03f, 0.62f, 0.6f}, false));
        Kisman.instance.settingsManager.rSetting(new Setting("NoHoveredColor", this, "NoHoveredColor", new float[] {0f, 0f, 0.05f, 1f}, false));
        setmgr.rSetting(synsLine);
        setmgr.rSetting(synsColor);
    }

    public void update() {
        int RLine = Kisman.instance.settingsManager.getSettingByName(this, "LineColor").getR();
        int GLine = Kisman.instance.settingsManager.getSettingByName(this, "LineColor").getG();
        int BLine = Kisman.instance.settingsManager.getSettingByName(this, "LineColor").getB();
        int ALine = Kisman.instance.settingsManager.getSettingByName(this, "LineColor").getA();

        boolean line = Kisman.instance.settingsManager.getSettingByName(this, "Line").getValBoolean();
        String lineMode = Kisman.instance.settingsManager.getSettingByName(this, "LineMode").getValString();
        String setLineMode = Kisman.instance.settingsManager.getSettingByName(this, "SetLineMode").getValString();
        int RBackground = Kisman.instance.settingsManager.getSettingByName(this, "BackgroundColor").getR();
        int GBackground = Kisman.instance.settingsManager.getSettingByName(this, "BackgroundColor").getG();
        int BBackground = Kisman.instance.settingsManager.getSettingByName(this, "BackgroundColor").getB();
        int ABackground = Kisman.instance.settingsManager.getSettingByName(this, "BackgroundColor").getA();
        Kisman.instance.settingsManager.getSettingByName(this, "BackgroundColor").setSyns(true);
        Kisman.instance.settingsManager.getSettingByName(this, "LineColor").setSyns(true);
        int RText = Kisman.instance.settingsManager.getSettingByName(this, "TextColor").getR();
        int GText = Kisman.instance.settingsManager.getSettingByName(this, "TextColor").getG();
        int BText = Kisman.instance.settingsManager.getSettingByName(this, "TextColor").getB();
        int AText = Kisman.instance.settingsManager.getSettingByName(this, "TextColor").getA();
        int RAText = Kisman.instance.settingsManager.getSettingByName(this, "ATextColor").getR();
        int GAText = Kisman.instance.settingsManager.getSettingByName(this, "ATextColor").getG();
        int BAText = Kisman.instance.settingsManager.getSettingByName(this, "ATextColor").getB();
        int AAText = Kisman.instance.settingsManager.getSettingByName(this, "ATextColor").getA();
        int RHovered = Kisman.instance.settingsManager.getSettingByName(this, "HoveredColor").getR();
        int GHovered = Kisman.instance.settingsManager.getSettingByName(this, "HoveredColor").getG();
        int BHovered = Kisman.instance.settingsManager.getSettingByName(this, "HoveredColor").getB();
        int AHovered = Kisman.instance.settingsManager.getSettingByName(this, "HoveredColor").getA();
        int RNoHovered = Kisman.instance.settingsManager.getSettingByName(this, "NoHoveredColor").getR();
        int GNoHovered = Kisman.instance.settingsManager.getSettingByName(this, "NoHoveredColor").getG();
        int BNoHovered = Kisman.instance.settingsManager.getSettingByName(this, "NoHoveredColor").getB();
        int ANoHovered = Kisman.instance.settingsManager.getSettingByName(this, "NoHoveredColor").getA();

        ClickGui.setLine(line);
        if(lineMode.equalsIgnoreCase("LeftLine")) {
            ClickGui.setLineMode(LineMode.LEFT);
        } else if(lineMode.equalsIgnoreCase("LLine+TLine")) {
            ClickGui.setLineMode(LineMode.LEFTONTOP);
        } else {
            ClickGui.setLineMode(LineMode.BOX);
        }

        if(setLineMode.equalsIgnoreCase("Default")) {
            ClickGui.setSetLineMode(LineMode.SETTINGDEFAULT);
        } else if(setLineMode.equalsIgnoreCase("All")) {
            ClickGui.setSetLineMode(LineMode.SETTINGALL);
        } else {
            ClickGui.setSetLineMode(LineMode.SETTINGONLYSET);
        }

        if(!Config.instance.guiAstolfo.getValBoolean()) {
            ClickGui.setRLine(RLine);
            ClickGui.setGLine(GLine);
            ClickGui.setBLine(BLine);
            ClickGui.setALine(ALine);

            ClickGui.setRActiveText(RAText);
            ClickGui.setGActiveText(GAText);
            ClickGui.setBActiveText(BAText);
            ClickGui.setAActiveText(AAText);

            ClickGui.setRBackground(RBackground);
            ClickGui.setGBackground(GBackground);
            ClickGui.setBBackground(BBackground);
            ClickGui.setABackground(ABackground);

            ClickGui.setRHoveredModule(RHovered);
            ClickGui.setGHoveredModule(GHovered);
            ClickGui.setBHoveredModule(BHovered);
            ClickGui.setAHoveredModule(AHovered);
            ClickGui.setRNoHoveredModule(RNoHovered);
            ClickGui.setGNoHoveredModule(GNoHovered);
            ClickGui.setBNoHoveredModule(BNoHovered);
            ClickGui.setANoHoveredModule(ANoHovered);
        } else {
            ClickGui.setRLine(ColorUtils.getRed(ColorUtils.astolfoColors(100, 100)));
            ClickGui.setGLine(ColorUtils.getGreen(ColorUtils.astolfoColors(100, 100)));
            ClickGui.setBLine(ColorUtils.getBlue(ColorUtils.astolfoColors(100, 100)));
            ClickGui.setALine(ColorUtils.getAlpha(ColorUtils.astolfoColors(100, 100)));

            ClickGui.setRActiveText(ColorUtils.getRed(ColorUtils.astolfoColors(100, 100)));
            ClickGui.setGActiveText(ColorUtils.getGreen(ColorUtils.astolfoColors(100, 100)));
            ClickGui.setBActiveText(ColorUtils.getBlue(ColorUtils.astolfoColors(100, 100)));
            ClickGui.setAActiveText(ColorUtils.getAlpha(ColorUtils.astolfoColors(100, 100)));

            ClickGui.setRBackground(255);
            ClickGui.setGBackground(255);
            ClickGui.setBBackground(255);
            ClickGui.setABackground(150);

            ClickGui.setRHoveredModule(54);
            ClickGui.setGHoveredModule(54);
            ClickGui.setBHoveredModule(54);
            ClickGui.setAHoveredModule(90);
            ClickGui.setRNoHoveredModule(54);
            ClickGui.setGNoHoveredModule(54);
            ClickGui.setBNoHoveredModule(54);
            ClickGui.setANoHoveredModule(43);
        }

        ClickGui.setRText(RText);
        ClickGui.setGText(GText);
        ClickGui.setBText(BText);
        ClickGui.setAText(AText);
    }
}