package com.kisman.cc.setting;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.Module;

import java.util.ArrayList;

public class SettingManager {
    public ArrayList<Setting> settings;

    public SettingManager() {
        Kisman.LOGGER.info("Settings init!");
        settings = new ArrayList<>();
    }

    public ArrayList<Setting> getSettingsInMod(Module mod) {
        ArrayList<Setting> sets = new ArrayList<>();
        for(Setting s : settings) {
            if(s.getMod() == mod) {
                sets.add(s);
            }
        }
        return sets;
    }

    public ArrayList<Setting> getSettings() {
        return settings;
    }
}
