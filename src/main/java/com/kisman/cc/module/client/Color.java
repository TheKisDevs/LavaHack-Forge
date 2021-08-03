package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.HoveredMode;
import com.kisman.cc.util.LineMode;
import com.kisman.cc.util.TextMode;

import java.util.ArrayList;

public class Color extends Module {

    public Color() {
        super("Color", "color setting", Category.CLIENT);
        ArrayList<String> lineMode = new ArrayList<>();
        lineMode.add("LeftLine");
        lineMode.add("LLine+TLine");
        lineMode.add("Box");
        ArrayList<String> textMode = new ArrayList<>();
        textMode.add("Default");
        textMode.add("ActiveText");
        ArrayList<String> hoveredMode = new ArrayList<>();
        hoveredMode.add("Hovered");
        hoveredMode.add("NoHovered");
        Kisman.instance.settingsManager.rSetting(new Setting("Line", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("LineMode", this, "LeftLine", lineMode));
        Kisman.instance.settingsManager.rSetting(new Setting("RLine", this, ClickGui.getRLine(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("GLine", this, ClickGui.getGLine(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("BLine", this, ClickGui.getBLine(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("ALine", this, ClickGui.getALine(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("RBackground", this, ClickGui.getRBackground(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("GBackground", this, ClickGui.getGBackground(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("BBackground", this, ClickGui.getBBackground(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("ABackground", this, ClickGui.getABackground(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("TextMode", this, "Default", textMode));
        Kisman.instance.settingsManager.rSetting(new Setting("RText", this, ClickGui.getTextMode() == TextMode.DEFAULT ? ClickGui.getRText() : ClickGui.getRActiveText(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("GText", this, ClickGui.getTextMode() == TextMode.DEFAULT ? ClickGui.getGText() : ClickGui.getGActiveText(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("BText", this, ClickGui.getTextMode() == TextMode.DEFAULT ? ClickGui.getBText() : ClickGui.getBActiveText(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("AText", this, ClickGui.getTextMode() == TextMode.DEFAULT ? ClickGui.getAText() : ClickGui.getAActiveText(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("HoveredMode", this, "Hovered", hoveredMode));
        Kisman.instance.settingsManager.rSetting(new Setting("RHovered", this, ClickGui.getHoveredMode() == HoveredMode.HOVERED ? ClickGui.getRHoveredModule() : ClickGui.getRNoHoveredModule(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("GHovered", this, ClickGui.getHoveredMode() == HoveredMode.HOVERED ? ClickGui.getGHoveredModule() : ClickGui.getGNoHoveredModule(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("BHovered", this, ClickGui.getHoveredMode() == HoveredMode.HOVERED ? ClickGui.getBHoveredModule() : ClickGui.getBNoHoveredModule(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("AHovered", this, ClickGui.getHoveredMode() == HoveredMode.HOVERED ? ClickGui.getAHoveredModule() : ClickGui.getANoHoveredModule(), 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("Default", this, false));
        Kisman.instance.settingsManager.rSetting(new Setting("OffexMode", this, false));
    }

    public void update() {
        boolean line = Kisman.instance.settingsManager.getSettingByName(this, "Line").getValBoolean();
        String lineMode = Kisman.instance.settingsManager.getSettingByName(this, "LineMode").getValString();
        int RLine = (int) Kisman.instance.settingsManager.getSettingByName(this, "RLine").getValDouble();
        int GLine = (int) Kisman.instance.settingsManager.getSettingByName(this, "GLine").getValDouble();
        int BLine = (int) Kisman.instance.settingsManager.getSettingByName(this, "BLine").getValDouble();
        int ALine = (int) Kisman.instance.settingsManager.getSettingByName(this, "ALine").getValDouble();
        int RBackground = (int) Kisman.instance.settingsManager.getSettingByName(this, "RBackground").getValDouble();
        int GBackground = (int) Kisman.instance.settingsManager.getSettingByName(this, "GBackground").getValDouble();
        int BBackground = (int) Kisman.instance.settingsManager.getSettingByName(this, "BBackground").getValDouble();
        int ABackground = (int) Kisman.instance.settingsManager.getSettingByName(this, "ABackground").getValDouble();
        String textMode = Kisman.instance.settingsManager.getSettingByName(this, "TextMode").getValString();
        int RText = (int) Kisman.instance.settingsManager.getSettingByName(this, "RText").getValDouble();
        int GText = (int) Kisman.instance.settingsManager.getSettingByName(this, "GText").getValDouble();
        int BText = (int) Kisman.instance.settingsManager.getSettingByName(this, "BText").getValDouble();
        int AText = (int) Kisman.instance.settingsManager.getSettingByName(this, "AText").getValDouble();
        String hoveredMode = Kisman.instance.settingsManager.getSettingByName(this, "HoveredMode").getValString();
        int RHovered = (int) Kisman.instance.settingsManager.getSettingByName(this, "RHovered").getValDouble();
        int GHovered = (int) Kisman.instance.settingsManager.getSettingByName(this, "GHovered").getValDouble();
        int BHovered = (int) Kisman.instance.settingsManager.getSettingByName(this, "BHovered").getValDouble();
        int AHovered = (int) Kisman.instance.settingsManager.getSettingByName(this, "AHovered").getValDouble();
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
        ClickGui.setRBackground(RBackground);
        ClickGui.setGBackground(GBackground);
        ClickGui.setBBackground(BBackground);
        ClickGui.setABackground(ABackground);
        if(textMode.equalsIgnoreCase("Default")) {
            ClickGui.setTextMode(TextMode.DEFAULT);
            ClickGui.setRText(RText);
            ClickGui.setGText(GText);
            ClickGui.setBText(BText);
            ClickGui.setAText(AText);
        } else {
            ClickGui.setTextMode(TextMode.ACTIVETEXT);
            ClickGui.setRActiveText(RText);
            ClickGui.setGActiveText(GText);
            ClickGui.setBActiveText(BText);
            ClickGui.setAActiveText(AText);
        }
        if(hoveredMode.equalsIgnoreCase("Hovered")) {
            ClickGui.setHoveredMode(HoveredMode.HOVERED);
            ClickGui.setRHoveredModule(RHovered);
            ClickGui.setGHoveredModule(GHovered);
            ClickGui.setBHoveredModule(BHovered);
            ClickGui.setAHoveredModule(AHovered);
        } else {
            ClickGui.setHoveredMode(HoveredMode.NOHOVERED);
            ClickGui.setRNoHoveredModule(RHovered);
            ClickGui.setGNoHoveredModule(GHovered);
            ClickGui.setBNoHoveredModule(BHovered);
            ClickGui.setANoHoveredModule(AHovered);
        }
        if(isDefault) {
            ClickGui.setLine(false);
            Kisman.instance.settingsManager.getSettingByName(this, "Line").setValBoolean(false);
            if(ClickGui.getTextMode() == TextMode.DEFAULT) {
                Kisman.instance.settingsManager.getSettingByName(this, "RText").setValDouble(255);
                Kisman.instance.settingsManager.getSettingByName(this, "GText").setValDouble(255);
                Kisman.instance.settingsManager.getSettingByName(this, "BText").setValDouble(255);
                Kisman.instance.settingsManager.getSettingByName(this, "AText").setValDouble(255);
                ClickGui.setRText(166);
                ClickGui.setGText(161);
                ClickGui.setBText(160);
                ClickGui.setAText(255);
            } else {
                Kisman.instance.settingsManager.getSettingByName(this, "RText").setValDouble(166);
                Kisman.instance.settingsManager.getSettingByName(this, "GText").setValDouble(161);
                Kisman.instance.settingsManager.getSettingByName(this, "BText").setValDouble(160);
                Kisman.instance.settingsManager.getSettingByName(this, "AText").setValDouble(255);
                ClickGui.setRActiveText(255);
                ClickGui.setGActiveText(255);
                ClickGui.setBActiveText(255);
                ClickGui.setAActiveText(255);
            }
            if(ClickGui.getHoveredMode() == HoveredMode.NOHOVERED) {
                Kisman.instance.settingsManager.getSettingByName(this, "RHovered").setValDouble(14);
                Kisman.instance.settingsManager.getSettingByName(this, "GHovered").setValDouble(14);
                Kisman.instance.settingsManager.getSettingByName(this, "BHovered").setValDouble(14);
                Kisman.instance.settingsManager.getSettingByName(this, "AHovered").setValDouble(255);
                ClickGui.setRHoveredModule(95);
                ClickGui.setGHoveredModule(95);
                ClickGui.setBHoveredModule(87);
                ClickGui.setAHoveredModule(150);
            } else {
                Kisman.instance.settingsManager.getSettingByName(this, "RHovered").setValDouble(95);
                Kisman.instance.settingsManager.getSettingByName(this, "GHovered").setValDouble(95);
                Kisman.instance.settingsManager.getSettingByName(this, "BHovered").setValDouble(87);
                Kisman.instance.settingsManager.getSettingByName(this, "AHovered").setValDouble(150);
                ClickGui.setRNoHoveredModule(14);
                ClickGui.setGNoHoveredModule(14);
                ClickGui.setBNoHoveredModule(14);
                ClickGui.setANoHoveredModule(255);
            }
            Kisman.instance.settingsManager.getSettingByName(this, "TextMode").setValString("Default");
            Kisman.instance.settingsManager.getSettingByName(this, "HoveredMode").setValString("Hovered");
            Kisman.instance.settingsManager.getSettingByName(this, "RLine").setValDouble(255);
            Kisman.instance.settingsManager.getSettingByName(this, "GLine").setValDouble(0);
            Kisman.instance.settingsManager.getSettingByName(this, "BLine").setValDouble(0);
            Kisman.instance.settingsManager.getSettingByName(this, "ALine").setValDouble(150);
            Kisman.instance.settingsManager.getSettingByName(this, "RBackground").setValDouble(80);
            Kisman.instance.settingsManager.getSettingByName(this, "GBackground").setValDouble(75);
            Kisman.instance.settingsManager.getSettingByName(this, "BBackground").setValDouble(75);
            Kisman.instance.settingsManager.getSettingByName(this, "ABackground").setValDouble(150);
            //ClickGui.setRBackground(80);
            //ClickGui.setGBackground(75);
            //ClickGui.setBBackground(75);
            //ClickGui.setABackground(150);
            Kisman.instance.settingsManager.getSettingByName(this, "Default").setValBoolean(false);
        }
    }
}
