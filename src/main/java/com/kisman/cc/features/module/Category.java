package com.kisman.cc.features.module;

public enum Category {
	COMBAT("Combat"),
	CLIENT("Client"),
	MOVEMENT("Movement"),
	PLAYER("Player"),
	RENDER("Render"),
	MISC("Misc"),
	EXPLOIT("Exploit"),
	DEBUG("Debug"),
	WIP("WIP"),
	LUA("Plugin&Lua");

	private final String name;
	Category(final String name) { this.name = name;}
	public final String getName() {return name;}
}