package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.manager.file.LoadConfig;

public class LoadConfigCommand extends Command {
    public LoadConfigCommand() {
        super("loadconfig");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            if(args.length > 0) {
                error("Usage: " + getSyntax());
                return;
            }

            warning("Start loading configs!");
            LoadConfig.init();
            message("Loaded Config!");
        } catch (Exception e) {
            error("Loaded config is failed!");
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return "loading config";
    }

    @Override
    public String getSyntax() {
        return "loadconfig";
    }
}
