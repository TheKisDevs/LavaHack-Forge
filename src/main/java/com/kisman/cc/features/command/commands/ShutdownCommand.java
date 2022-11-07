package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;

public class ShutdownCommand extends Command {

    public ShutdownCommand(){
        super("shutdown");
    }


    @Override
    public String getDescription() {
        return "shuts down Minecraft";
    }

    @Override
    public String getSyntax() {
        return "shutdown client/hard/crash";
    }

    @Override
    public void runCommand(String s, String[] args) {
        if(args[0].equals("client")) mc.shutdownMinecraftApplet();
        else if(args[0].equals("hard")) mc.shutdown();
        else if(args[0].equals("crash")) System.exit(1);
    }
}
