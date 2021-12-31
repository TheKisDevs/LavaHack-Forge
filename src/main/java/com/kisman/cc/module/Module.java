package com.kisman.cc.module;

import com.kisman.cc.Kisman;

import com.kisman.cc.settings.*;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class Module {
	protected static Minecraft mc = Minecraft.getMinecraft();
	protected static SettingsManager setmgr;

	private String name, description, displayInfo;
	private int key;
	private int priority;
	private Category category;
	private boolean toggled;
	public boolean visible = true;

	public Module(String name, Category category) {this(name, "", category, 1);}
	public Module(String name, String description, Category category) {this(name, description, category, 1);}

	public Module(String name, String description, Category category, int key) {
		this.name = name;
		this.description = description;
		this.displayInfo = "";
		this.key = key;
		this.category = category;
		this.toggled = false;
		this.priority = 1;

		setmgr = Kisman.instance.settingsManager;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		if (this.toggled) onEnable();
		else onDisable();
	}

	public void toggle() {
		toggled = !toggled;
		if (toggled) onEnable();
		else onDisable();
	}

	public Setting register(Setting set) {
		setmgr.rSetting(set);
		return set;
	}

	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	public int getKey() {return key;}
	public int getPriority() {return priority;}
	public void setPriority(int priority) {this.priority = priority;}
	public void setKey(int key) {this.key = key;}
	public boolean isToggled() {return toggled;}
	public void onEnable() {MinecraftForge.EVENT_BUS.register(this);}
	public void onDisable() {MinecraftForge.EVENT_BUS.unregister(this);}
	public String getName() {return this.name;}
	public Category getCategory() {return this.category;}
	public String getDisplayInfo() {return this.displayInfo;}
	public void setDisplayInfo(String displayInfo) {this.displayInfo = displayInfo;}
	public void update(){}
	public void render(){}
	public void key() {}
	public void key(int key) {}
	public void key(char typedChar, int key) {}
	@Override public String toString() {return getName();}
}
