package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.module.Module;

public class ToggleCommand extends Command {
    public ToggleCommand() {
        super("toggle");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            Module module = Kisman.instance.moduleManager.getModule(args[0]);

            if(module.toggleable) module.toggle();

            complete("Module " + module + " has been toggled!");
        } catch(Exception e) {
            error("Usage: " + getSyntax());
        }
    }

    @Override
	public String getDescription() {
		return "Allows to toggle any module";
	}

	@Override
	public String getSyntax() {
		return "toggle <module>";
	}
}
