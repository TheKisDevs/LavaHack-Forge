package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.Command;

import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class Slider extends Command{
    public Slider() {
        super("slider");
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
            if(Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule(module), name) != null){
                Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule(module), name).setValDouble(value);
                ChatUtils.message("Slider " + name + " changed value to " + value);
            } else {
                String parsedName = name.replace('_', ' ');
                if(Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule(module), parsedName) != null){
                    Kisman.instance.settingsManager.getSettingByName(Kisman.instance.moduleManager.getModule(module), parsedName).setValDouble(value);
                    ChatUtils.message("Slider " + parsedName + " changed value to " + value);
                }
            }
        } catch(Exception e) {ChatUtils.error("Usage: " + getSyntax());}
    }

    @Override
	public String getDescription() {
		return "Change slider value from modules setting";
	}

	@Override
	public String getSyntax() {
		return "slider <module> <\"slider name\"> <value>";
	}
}
