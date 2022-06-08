package the.kis.devs.api.features.hud;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.hud.HudModuleManager;

import java.util.List;

/**
 * @author _kisman_
 * @since 19:28 of 08.06.2022
 */
public class HudModuleManagerAPI {
    public static List<HudModule> getModules() {
        return Kisman.instance.hudModuleManager.modules;
    }

    public static HudModule getModule(String name) {
        return Kisman.instance.hudModuleManager.getModule(name);
    }
}
