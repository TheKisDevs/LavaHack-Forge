package com.kisman.cc.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.command.Command;
import com.kisman.cc.file.SaveConfig;

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
            String str = "";
            int startIndex = 0, endIndex = 0;
            for(int i = 1; i < args.length; i++) {
                startIndex = args[i].indexOf('\"');
                endIndex = args[i].lastIndexOf('\"', startIndex);
                if(endIndex > startIndex) break;
                str += args[i] + " ";
            }
            name = str.substring(startIndex, endIndex);
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
            SaveConfig.init();
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
		return "slider <module> <\"slider name\"> <value>";
	}
}
