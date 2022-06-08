package the.kis.devs.api.settings;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;

import java.util.List;

/**
 * @author _kisman_
 * @since 17:09 of 08.06.2022
 */
public class SettingsManagerAPI {
    public static List<Setting> getSettings() {
        return Kisman.instance.settingsManager.getSettings();
    }

    public static List<Setting> getSettingsByModule(Module module) {
        return Kisman.instance.settingsManager.getSettingsByMod(module);
    }

    public static Setting getSettingByName(Module module, String name) {
        return Kisman.instance.settingsManager.getSettingByName(module, name);
    }

    public static Setting rSetting(Setting setting) {
        Kisman.instance.settingsManager.rSetting(setting);
        return setting;
    }
}
