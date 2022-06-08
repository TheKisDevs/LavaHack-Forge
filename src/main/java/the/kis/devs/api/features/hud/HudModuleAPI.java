package the.kis.devs.api.features.hud;

import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.types.SettingGroup;

import java.util.function.Supplier;

/**
 * @author _kisman_
 * @since 18:02 of 08.06.2022
 */
public class HudModuleAPI extends HudModule {
    public HudModuleAPI(String name, String description) {this(name, description, false);}
    public HudModuleAPI(String name, String description, boolean drag) {super(name, description, drag);}
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
    public double getX() {return super.getX();}
    public void setX(double x) {super.setX(x);}
    public double getY() {return super.getY();}
    public void setY(double y) {super.setY(y);}
    public double getW() {return super.getW();}
    public void setW(double w) {super.setW(w);}
    public double getH() {return super.getH();}
    public void setH(double h) {super.setH(h);}
}
