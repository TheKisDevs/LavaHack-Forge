package com.kisman.cc.module.client;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Category;
import com.kisman.cc.module.Module;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.settings.Setting;

public class Color extends Module {
    public Color() {
        super("Color", "color setting", Category.CLIENT);
        Kisman.instance.settingsManager.rSetting(new Setting("RLine", this, 255, 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("GLine", this, 0, 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("BLine", this, 0, 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("RBackground", this, 80, 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("GBackground", this, 75, 0, 255, true));
        Kisman.instance.settingsManager.rSetting(new Setting("BBackground", this, 75, 0, 255, true));
    }

    public void update() {
        int RLine = (int) Kisman.instance.settingsManager.getSettingByName(this, "RLine").getValDouble();
        int GLine = (int) Kisman.instance.settingsManager.getSettingByName(this, "GLine").getValDouble();
        int BLine = (int) Kisman.instance.settingsManager.getSettingByName(this, "BLine").getValDouble();
        int RBackground = (int) Kisman.instance.settingsManager.getSettingByName(this, "RBackground").getValDouble();
        int GBackground = (int) Kisman.instance.settingsManager.getSettingByName(this, "GBackground").getValDouble();
        int BBackground = (int) Kisman.instance.settingsManager.getSettingByName(this, "BBackground").getValDouble();
        ClickGui.setRLine(RLine);
        ClickGui.setGLine(GLine);
        ClickGui.setBLine(BLine);
        ClickGui.setRBackground(RBackground);
        ClickGui.setGBackground(GBackground);
        ClickGui.setBBackground(BBackground);
    }
}
