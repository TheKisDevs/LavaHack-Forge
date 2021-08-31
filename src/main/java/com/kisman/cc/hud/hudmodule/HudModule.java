package com.kisman.cc.hud.hudmodule;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class HudModule {
    protected static Minecraft mc = Minecraft.getMinecraft();

	private String name, description;
	private int key;
	private HudCategory category;
	private boolean toggled;
	public boolean visible = true;
	
	public HudModule(String name, String description, HudCategory category) {
		//super();
		this.name = name;
		this.description = description;
		this.key = 0;
		this.category = category;
		this.toggled = false;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public boolean isToggled() {
		return toggled;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		
		if (this.toggled) {
			this.onEnable();
		} else {
			this.onDisable();
		}
	}
	
	public void toggle() {
		this.toggled = !this.toggled;
		
		if (this.toggled) {
			this.onEnable();
		} else {
			this.onDisable();
		}
	}
	
	public void onEnable() {
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
	}
	
	public String getName() {
		return this.name;
	}
	
	public HudCategory getCategory() {
		return this.category;
	}

	public void update(){}
	public void render(){}
}