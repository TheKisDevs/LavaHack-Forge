package com.kisman.cc.module;

import com.kisman.cc.Kisman;
import com.kisman.cc.notification.Notification;
import com.kisman.cc.notification.NotificationType;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class Module {

	protected static Minecraft mc = Minecraft.getMinecraft();

	private Notification notification;

	private String name, description;
	private int key;
	private Category category;
	private boolean toggled;
	public boolean visible = true;
	
	public Module(String name, String description, Category category) {
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
		if(Kisman.isNotificatonModule()) {
			notification = new com.kisman.cc.notification.Notification(NotificationType.INFO, getName(), "Module enable", 100, Minecraft.getMinecraft().displayWidth - 90, Minecraft.getMinecraft().displayHeight - 40, 90, 40);
			notification.render();
		}
	}
	
	public void onDisable() {
		MinecraftForge.EVENT_BUS.unregister(this);
		if(Kisman.isNotificatonModule()) {
			notification = new com.kisman.cc.notification.Notification(NotificationType.INFO, getName(), "Module disable", 100, Minecraft.getMinecraft().displayWidth - 90, Minecraft.getMinecraft().displayHeight - 40, 90, 40);
			notification.render();
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public Category getCategory() {
		return this.category;
	}

	public void update(){}
	public void render(){}
}
