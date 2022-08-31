package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.minecraft.GameProfiles;
import com.kisman.cc.util.thread.Promise;
import com.kisman.cc.util.thread.PromiseResult;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class GetUUID extends Command {

    public GetUUID(){
        super("getuuid");
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        if(args.length < 2){
            error("Too few arguments");
            return;
        }
        long timeOutMillis = -1;
        if(args.length >= 3)
            timeOutMillis = Long.parseLong(args[2]);
        boolean copy = args[0].split(":")[1].equalsIgnoreCase("true");
        String player = args[1];
        String uuid;
        if(timeOutMillis >= 0){
            Promise<String> promise = Promise.task(() -> GameProfiles.getUUIDString(player));
            promise.start();
            // wait the amount of the timeout millis
            long t = System.currentTimeMillis();
            boolean timedOut = true;
            while(System.currentTimeMillis() - t < timeOutMillis) {
                if (promise.isFinished()) {
                    timedOut = false;
                    break;
                }
            }
            if(timedOut){
                uuid = "error";
            } else {
                PromiseResult<String> result = promise.stop();
                uuid = result.expect("error");
            }
        } else {
            uuid = GameProfiles.getUUIDString(player);
        }
        if(uuid == null){
            error("The player " + player + " does not exist");
            return;
        }
        if(uuid.equals("error")){
            error("Request timed out");
            return;
        }
        if(copy)
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(uuid), null);
        complete("The UUID of " + player + " is " + uuid);
    }

    @Override
    public String getDescription() {
        return "gets the uuid of the specified player";
    }

    @Override
    public String getSyntax() {
        return "uuid copy:<true/false> <player> <timeOutMillis(Optional)>";
    }
}
