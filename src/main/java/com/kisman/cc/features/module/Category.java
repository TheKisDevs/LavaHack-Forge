package com.kisman.cc.features.module;

public enum Category {
	COMBAT("Combat", 0),
	CLIENT("Client", 1),
	MOVEMENT("Movement", 2),
	PLAYER("Player", 3),
	RENDER("Render", 4),
	MISC("Misc", 5),
	EXPLOIT("Exploit", 6),
	DEBUG("Debug", 7),
	WIP("WIP", 8),
	LUA("Addons", 9);

	private final String name;
	public final int index;

	Category(final String name, final int index) {
		this.name = name;
		this.index = index;
	}

	public final String getName() {return name;}
}