package com.kisman.cc.features.module;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.settings.types.SettingGroup;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class Module implements IBindable {
	protected static Minecraft mc = Minecraft.getMinecraft();
	protected static SettingsManager setmgr;

	private String name, description, displayInfo;
	private int key;
	public int mouse = -1;
	public BindType bindType = BindType.Keyboard;
	private int priority;
	private final Category category;
	public boolean toggled;
	public boolean subscribes;
	public boolean visible = true;
	public boolean hold = false;
	public boolean block = false;
	private Supplier<String> displayInfoSupplier = null;

	public Module(String name, Category category) {this(name, "", category, 0, true);}
	public Module(String name, Category category, boolean subscribes) {this(name, "", category, 0, subscribes);}
	public Module(String name, String description, Category category) {this(name, description, category, 0, true);}

	public Module(String name, String description, Category category, int key, boolean subscribes) {
		this.name = name;
		this.description = description;
		this.displayInfo = "";
		this.key = key;
		this.category = category;
		this.toggled = false;
		this.subscribes = subscribes;
		this.priority = 1;

		setmgr = Kisman.instance.settingsManager;
	}

	public void setToggled(boolean toggled) {
		if(block) return;
		this.toggled = toggled;
		if (Kisman.instance.init && Config.instance.notification.getValBoolean()) ChatUtility.message().printClientModuleMessage(TextFormatting.GRAY + "Module " + (isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + getName() + TextFormatting.GRAY + " has been " + (isToggled() ? "enabled" : "disabled") + "!");
		if (this.toggled) onEnable();
		else onDisable();
	}

	public void toggle() {
		if(block) return;
		toggled = !toggled;
		if (Kisman.instance.init && Config.instance.notification.getValBoolean()) ChatUtility.message().printClientModuleMessage(TextFormatting.GRAY + "Module " + (isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + getName() + TextFormatting.GRAY + " has been " + (isToggled() ? "enabled" : "disabled") + "!");
		if (toggled) onEnable();
		else onDisable();
	}

	public Setting register(Setting set) {
		setmgr.rSetting(set);
		return set;
	}

	public SettingGroup register(SettingGroup group) {
		setmgr.rSetting(group);
		return group;
	}

	private boolean isBeta0(){
		return getClass().getAnnotation(Beta.class) != null;
	}

	private boolean isAddon0() {
		return getClass().getAnnotation(Addon.class) != null;
	}

	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	public int getKey() {return key;}
	public int getPriority() {return priority;}
	public void setPriority(int priority) {this.priority = priority;}
	public void setKey(int key) {this.key = key;}
	public boolean isToggled() {return toggled;}
	public void onEnable() {if(subscribes) MinecraftForge.EVENT_BUS.register(this);}
	public void onDisable() {if(subscribes) MinecraftForge.EVENT_BUS.unregister(this);}
	public String getName() {return this.name;}
	public Category getCategory() {return this.category;}
	public String getDisplayInfo() {return displayInfoSupplier == null ? displayInfo : displayInfoSupplier.get();}
	public void setDisplayInfo(String displayInfo) {this.displayInfo = displayInfo;}
	public void setDisplayInfo(Supplier<String> displayInfoSupplier) {this.displayInfoSupplier = displayInfoSupplier;}
	public void update(){}
	public void render(){}
	public void key() {}
	public void key(int key) {}
	public void key(char typedChar, int key) {}
	@Override public String toString() {return getName();}
	public boolean isVisible() {return true;}
	public boolean isBeta() {return isBeta0();}
	public boolean isAddon() {return isAddon0();}
	@Override public @NotNull BindType getType() {return bindType;}
	@Override public void setType(@NotNull BindType type) {this.bindType = type;}
	@Override public boolean isHold() {return hold;}
	@Override public void setHold(boolean hold) {this.hold = hold;}
	@Override public int getKeyboardKey() {return key;}
	@Override public void setKeyboardKey(int key) {this.key = key;}
	@Override public int getMouseButton() {return mouse;}
	@Override public void setMouseButton(int button) {this.mouse = button;}
	@Override public @NotNull String getButtonName() {return "Bind";}
}
