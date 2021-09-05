package com.kisman.cc.command.commands;

import com.kisman.cc.command.Command;
import com.kisman.cc.file.SaveConfig;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;

public class SaveConfigCommand extends Command {
    public SaveConfigCommand() {
        super("saveconfig");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if(args.length > 0) {
                ChatUtils.error("Usage: " + getSyntax());
                return;
            }

            ChatUtils.warning("Start saving configs!");
            SaveConfig.init();
            ChatUtils.message("Saved Config!");
        } catch (Exception e) {
            ChatUtils.error("Saving config is failed!");
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "saving confing";
    }

    @Override
    public String getSyntax() {
        return "saveconfig";
    }
}
