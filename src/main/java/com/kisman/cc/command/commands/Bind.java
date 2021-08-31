package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.Command;
import com.kisman.cc.module.Module;

import org.lwjgl.input.Keyboard;

import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class Bind extends Command{
    public Bind() {
        super("Bind");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            String key = args[0];
            // for(Command cmd : Kisman.instance.commandManager.commands) {

            // }
            for(Module mod : Kisman.instance.moduleManager.modules) {
                if(mod.getName().equalsIgnoreCase(args[1])) {
                    mod.setKey(Keyboard.getKeyIndex((key.toUpperCase())));
                    ChatUtils.message(mod.getName() + " binned to " + Keyboard.getKeyName(mod.getKey()));
                }
            }
        } catch(Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
	public String getDescription() {
		return "Change key for modules/commands.";
	}

	@Override
	public String getSyntax() {
		return "bind <key> <command/module>";
	}
}
