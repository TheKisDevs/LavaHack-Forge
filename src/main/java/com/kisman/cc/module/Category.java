package com.kisman.cc.module;

public enum Category {
	COMBAT("Combat"),
	CLIENT("Client"),
	CHAT("Chat"),
	MOVEMENT("Movement"),
	PLAYER("Player"),
	RENDER("Render"),
	MISC("Misc"),
	EXPLOIT("Exploit");

	private final String name;

	Category(String name) { this.name = name; }
}
