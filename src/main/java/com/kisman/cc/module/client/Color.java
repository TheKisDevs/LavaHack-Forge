package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.ColorPicker;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.HoveredMode;
import com.kisman.cc.util.LineMode;
import com.kisman.cc.util.TextMode;

import java.util.ArrayList;
import java.awt.*;

public class Color extends Module {
    ColorUtil colorUtil;

    boolean rainbowLine = false;

    public Color() {
        super("Color", "color setting", Category.CLIENT);
        colorUtil = new ColorUtil();
        ArrayList<String> lineMode = new ArrayList<>();
        lineMode.add("LeftLine");
        lineMode.add("LLine+TLine");
        lineMode.add("Box");
        ArrayList<String> rainbowLineMode = new ArrayList<>();
        rainbowLineMode.add("NoRainBow");
        rainbowLineMode.add("SimpleRainBow");
        rainbowLineMode.add("RainBow");
        ArrayList<String> textMode = new ArrayList<>();
        textMode.add("Default");
        textMode.add("ActiveText");
        ArrayList<String> hoveredMode = new ArrayList<>();
        hoveredMode.add("Hovered");
        hoveredMode.add("NoHovered");
        Kisman.instance.settingsManager.rSetting(new Setting("LineSetting",this, "Line"));
        Kisman.instance.settingsManager.rSetting(new Setting("Line", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("LineMode", this, "LeftLine", lineMode));
        Kisman.instance.settingsManager.rSetting(new Setting("LineColor", this, "LineColor", false));
        Kisman.instance.settingsManager.rSetting(new Setting("BackgroundSetting", this, "Background"));
        Kisman.instance.settingsManager.rSetting(new Setting("BackgroundColor", this, "BackgroundColor", false));
        Kisman.instance.settingsManager.rSetting(new Setting("TextSetting", this, "Text"));
        Kisman.instance.settingsManager.rSetting(new Setting("TextColor", this, "TextColor", false));
        Kisman.instance.settingsManager.rSetting(new Setting("ATextColor", this, "ATextColor", false));
        Kisman.instance.settingsManager.rSetting(new Setting("DifferentSetting", this, "Different"));
        Kisman.instance.settingsManager.rSetting(new Setting("HoveredColor", this, "HoveredColor", false));
        Kisman.instance.settingsManager.rSetting(new Setting("NoHoveredColor", this, "NoHoveredColor", false));
        Kisman.instance.settingsManager.rSetting(new Setting("Default", this, false));
    }

    public void update() {
        int RLine = Kisman.instance.settingsManager.getSettingByName(this, "LineColor").getR();
        int GLine = Kisman.instance.settingsManager.getSettingByName(this, "LineColor").getG();
        int BLine = Kisman.instance.settingsManager.getSettingByName(this, "LineColor").getB();
        int ALine = Kisman.instance.settingsManager.getSettingByName(this, "LineColor").getA();

        boolean line = Kisman.instance.settingsManager.getSettingByName(this, "Line").getValBoolean();
        String lineMode = Kisman.instance.settingsManager.getSettingByName(this, "LineMode").getValString();
        int RBackground = Kisman.instance.settingsManager.getSettingByName(this, "BackgroundColor").getR();
        int GBackground = Kisman.instance.settingsManager.getSettingByName(this, "BackgroundColor").getG();
        int BBackground = Kisman.instance.settingsManager.getSettingByName(this, "BackgroundColor").getB();
        int ABackground = Kisman.instance.settingsManager.getSettingByName(this, "BackgroundColor").getA();
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
        boolean isDefault = Kisman.instance.settingsManager.getSettingByName(this, "Default").getValBoolean();
        ClickGui.setLine(line);
        if(lineMode.equalsIgnoreCase("LeftLine")) {
            ClickGui.setLineMode(LineMode.LEFT);
        } else if(lineMode.equalsIgnoreCase("LLine+TLine")) {
            ClickGui.setLineMode(LineMode.LEFTONTOP);
        } else {
            ClickGui.setLineMode(LineMode.BOX);
        }

        ClickGui.setRLine(RLine);
        ClickGui.setGLine(GLine);
        ClickGui.setBLine(BLine);
        ClickGui.setALine(ALine);
        // colorPicker.setColor(
        //     colorPicker.alpha(
        //         new java.awt.Color(
        //             java.awt.Color.HSBtoRGB(
        //                 colorPicker.getColor(0), 
        //                 colorPicker.getColor(1), 
        //                 colorPicker.getColor(2)
        //             )
        //         ),
        //         colorPicker.getColor(3)
        //     )
        // );

        ClickGui.setRBackground(RBackground);
        ClickGui.setGBackground(GBackground);
        ClickGui.setBBackground(BBackground);
        ClickGui.setABackground(ABackground);
        ClickGui.setRText(RText);
        ClickGui.setGText(GText);
        ClickGui.setBText(BText);
        ClickGui.setAText(AText);
        ClickGui.setRActiveText(RAText);
        ClickGui.setGActiveText(GAText);
        ClickGui.setBActiveText(BAText);
        ClickGui.setAActiveText(AAText);
        ClickGui.setRHoveredModule(RHovered);
        ClickGui.setGHoveredModule(GHovered);
        ClickGui.setBHoveredModule(BHovered);
        ClickGui.setAHoveredModule(AHovered);
        ClickGui.setRNoHoveredModule(RNoHovered);
        ClickGui.setGNoHoveredModule(GNoHovered);
        ClickGui.setBNoHoveredModule(BNoHovered);
        ClickGui.setANoHoveredModule(ANoHovered);
        // if(isDefault) {
        //     ClickGui.setLine(false);
        //     ClickGui.setRainbowLine(false);
        //     ClickGui.setRainbowBackground(false);
        //     Kisman.instance.settingsManager.getSettingByName(this, "RainBowMode").setValString("NoRainBow");
        //     Kisman.instance.settingsManager.getSettingByName(this, "Line").setValBoolean(false);
        //     Kisman.instance.settingsManager.getSettingByName(this, "Seconds").setValDouble(2);
        //     Kisman.instance.settingsManager.getSettingByName(this, "Saturation").setValDouble(1);
        //     Kisman.instance.settingsManager.getSettingByName(this, "Briqhtness").setValDouble(1);
        //     if(ClickGui.getTextMode() == TextMode.DEFAULT) {
        //         Kisman.instance.settingsManager.getSettingByName(this, "RText").setValDouble(255);
        //         Kisman.instance.settingsManager.getSettingByName(this, "GText").setValDouble(255);
        //         Kisman.instance.settingsManager.getSettingByName(this, "BText").setValDouble(255);
        //         Kisman.instance.settingsManager.getSettingByName(this, "AText").setValDouble(255);
        //         ClickGui.setRText(166);
        //         ClickGui.setGText(161);
        //         ClickGui.setBText(160);
        //         ClickGui.setAText(255);
        //     } else {
        //         Kisman.instance.settingsManager.getSettingByName(this, "RText").setValDouble(166);
        //         Kisman.instance.settingsManager.getSettingByName(this, "GText").setValDouble(161);
        //         Kisman.instance.settingsManager.getSettingByName(this, "BText").setValDouble(160);
        //         Kisman.instance.settingsManager.getSettingByName(this, "AText").setValDouble(255);
        //         ClickGui.setRActiveText(255);
        //         ClickGui.setGActiveText(255);
        //         ClickGui.setBActiveText(255);
        //         ClickGui.setAActiveText(255);
        //     }
        //     if(ClickGui.getHoveredMode() == HoveredMode.NOHOVERED) {
        //         Kisman.instance.settingsManager.getSettingByName(this, "RHovered").setValDouble(14);
        //         Kisman.instance.settingsManager.getSettingByName(this, "GHovered").setValDouble(14);
        //         Kisman.instance.settingsManager.getSettingByName(this, "BHovered").setValDouble(14);
        //         Kisman.instance.settingsManager.getSettingByName(this, "AHovered").setValDouble(255);
        //         ClickGui.setRHoveredModule(95);
        //         ClickGui.setGHoveredModule(95);
        //         ClickGui.setBHoveredModule(87);
        //         ClickGui.setAHoveredModule(150);
        //     } else {
        //         Kisman.instance.settingsManager.getSettingByName(this, "RHovered").setValDouble(95);
        //         Kisman.instance.settingsManager.getSettingByName(this, "GHovered").setValDouble(95);
        //         Kisman.instance.settingsManager.getSettingByName(this, "BHovered").setValDouble(87);
        //         Kisman.instance.settingsManager.getSettingByName(this, "AHovered").setValDouble(150);
        //         ClickGui.setRNoHoveredModule(14);
        //         ClickGui.setGNoHoveredModule(14);
        //         ClickGui.setBNoHoveredModule(14);
        //         ClickGui.setANoHoveredModule(255);
        //     }
        //     Kisman.instance.settingsManager.getSettingByName(this, "TextMode").setValString("Default");
        //     Kisman.instance.settingsManager.getSettingByName(this, "HoveredMode").setValString("Hovered");
        //     Kisman.instance.settingsManager.getSettingByName(this, "RLine").setValDouble(255);
        //     Kisman.instance.settingsManager.getSettingByName(this, "GLine").setValDouble(0);
        //     Kisman.instance.settingsManager.getSettingByName(this, "BLine").setValDouble(0);
        //     Kisman.instance.settingsManager.getSettingByName(this, "ALine").setValDouble(150);
        //     Kisman.instance.settingsManager.getSettingByName(this, "RBackground").setValDouble(80);
        //     Kisman.instance.settingsManager.getSettingByName(this, "GBackground").setValDouble(75);
        //     Kisman.instance.settingsManager.getSettingByName(this, "BBackground").setValDouble(75);
        //     Kisman.instance.settingsManager.getSettingByName(this, "ABackground").setValDouble(150);
        //     Kisman.instance.settingsManager.getSettingByName(this, "Default").setValBoolean(false);
        //}
    }
}