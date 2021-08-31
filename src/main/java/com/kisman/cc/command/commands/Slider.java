package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.Command;
import com.kisman.cc.module.Module;
import com.kisman.cc.settings.Setting;

import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class Slider extends Command{
    public Slider() {
        super("Slider");
    }

    @Override
    public void runCommand(String s, String[] args) {
        String module = "";
        String name = "";
        double value;

        try {
            module = args[0];
            name = args[1];
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

        try {
            Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule(module), name).getValDouble();
        } catch(Exception e) {
            ChatUtils.error("Setting " + name + " in module " + module + " does not exist!");
            return;
        }

        try {
            value = Double.parseDouble(args[2]);
        } catch(Exception e) {
            ChatUtils.error("Value error! <value> not double!");
            return;
        }

        try {
            Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule(module), name).setValDouble(value);
            ChatUtils.message("Slider " + name + " change value to " + value);
        } catch(Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }


    }

    @Override
	public String getDescription() {
		return "Change slider value for modules setting";
	}

	@Override
	public String getSyntax() {
		return "slider <module> <slider name> <value>";
	}
}
