package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.Command;

import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class Toggle extends Command{
    public Toggle() {
        super("toggle");
    }

    @Override
    public void runCommand(String s, String[] args) {
        String module = "";

        try {
            module = args[0];
        } catch(Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
            return;
        }

        try {
            Kisman.instance.moduleManager.getModule(module);
        } catch(Exception e) {
            ChatUtils.error("Module " + module + " does not exist!");
            return;
        }

        if(args.length > 1) {
            ChatUtils.error("Usage: " + getSyntax());
            return;
        }

        try {

            Kisman.instance.moduleManager.getModule(module).setToggled(
                !Kisman.instance.moduleManager.getModule(module).isToggled()
            );
        } catch(Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
	public String getDescription() {
		return "toggled modules";
	}

	@Override
	public String getSyntax() {
		return "toggle <module>";
	}
}
