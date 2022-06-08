package the.kis.devs.api.features.module;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;

import java.util.function.Supplier;

/**
 * @author _kisman_
 * @since 17:24 of 08.06.2022
 */
public class ModuleAPI extends Module {
    public ModuleAPI(String name, String description, CategoryAPI category) {super(name, description, category.category);}
    public void setToggled(boolean toggled) {super.setToggled(toggled);}
    public void toggle() {super.toggle();}
    public Setting register(Setting set) {return super.register(set);}
    public SettingGroup register(SettingGroup group) {return super.register(group);}
    public void setDescription(String description) {super.setDescription(description);}
    public void setKey(int key) {super.setKey(key);}
    public boolean isToggled() {return super.isToggled();}
    public void onEnable() {super.onEnable();}
    public void onDisable() {super.onDisable();}
    public void setDisplayInfo(String displayInfo) {super.setDisplayInfo(displayInfo);}
    public void setDisplayInfo(Supplier<String> fun) {super.setDisplayInfo(fun);}
    public void update() {super.update();}
    public boolean isVisible() {return super.isVisible();}
    public boolean isBeta() {return super.isBeta();}
}
