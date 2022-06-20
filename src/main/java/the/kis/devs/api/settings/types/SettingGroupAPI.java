package the.kis.devs.api.settings.types;

import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;

/**
 * @author _kisman_
 * @since 17:32 of 08.06.2022
 */
public class SettingGroupAPI extends SettingGroup {
    public SettingGroupAPI(Setting setting) {
        super(setting);
    }

    public Setting add(Setting setting) {
        return super.add(setting);
    }
}
