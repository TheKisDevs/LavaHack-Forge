package the.kis.devs.api.features.module;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Module;

import java.util.List;

/**
 * @author _kisman_
 * @since 17:06 of 08.06.2022
 */
public class ModuleManagerAPI {
    public static List<Module> getModules() {
        return Kisman.instance.moduleManager.modules;
    }

    public static Module getModule(String name) {
        return Kisman.instance.moduleManager.getModule(name);
    }
}
