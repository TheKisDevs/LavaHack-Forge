package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.command.SubCommand;
import com.kisman.cc.util.process.web.music.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MusicCommand extends Command {
    private static MusicCommand instance;

    private String dir;

    public MusicCommand(){
        super("music");
        addInstances(
                new CommandDir(this),
                new CommandPause(this),
                new CommandResume(this),
                new CommandVolume(this),
                new CommandPlay(this),
                new CommandStop(this)
        );
        this.dir = "";
        instance = this;
    }

    @Override
    public void runCommand(String s, String[] args) {
        if(args.length < 1)
            return;
        SubCommand command = getInstance(args[0]);
        if(command == null)
            return;
        command.runCommand(s, args);
    }

    @Override
    public String getDescription() {
        return "null";
    }

    @Override
    public String getSyntax() {
        return "null";
    }

    private static class CommandDir extends SubCommand {
        public CommandDir(Command instance) {
            super("dir", instance);
        }

        @Override
        public void runCommand(String s, String[] args) {
            if(args.length < 2)
                return;
            if(args[1].equals("file")){
                if(args.length < 3)
                    return;
                File file = new File(args[2]);
                if(!file.exists() || file.isDirectory())
                    return;
                URL url;
                try {
                    url = file.toURI().toURL();
                } catch (MalformedURLException ignored){
                    return;
                }
                instance.dir = url.toExternalForm();
                return;
            }
            if(args[1].equals("url")){
                if(args.length < 3)
                    return;
                URL url;
                try {
                    url = new URL(args[2]);
                } catch (MalformedURLException ignored){
                    return;
                }
                instance.dir = url.toExternalForm();
                complete("Set music user directory url to: " + url.toExternalForm());
                return;
            }
            if(args[1].equals("clear")){
                instance.dir = "";
                complete("Cleared the music user directory");
            }
        }
    }

    private static class CommandVolume extends SubCommand {
        public CommandVolume(Command instance) {
            super("volume", instance);
        }

        @Override
        public void runCommand(String s, String[] args) {
            if(args.length < 2)
                return;
            int a = Integer.parseInt(args[1]);
            Player.setVolume(a);
            complete("Set music volume to: " + a);
        }
    }

    private static class CommandPause extends SubCommand {
        public CommandPause(Command instance) {
            super("pause", instance);
        }

        @Override
        public void runCommand(String s, String[] args) {
            Player.pause();
            complete("Paused the music");
        }
    }

    private static class CommandResume extends SubCommand {
        public CommandResume(Command instance) {
            super("resume", instance);
        }

        @Override
        public void runCommand(String s, String[] args) {
            Player.resume();
            complete("Resumed the music");
        }
    }

    private static class CommandStop extends SubCommand {
        public CommandStop(Command instance) {
            super("stop", instance);
        }

        @Override
        public void runCommand(String s, String[] args) {
            Player.stop();
            complete("Stopped the music");
        }
    }

    private static class CommandPlay extends SubCommand {
        public CommandPlay(Command instance) {
            super("play", instance);
        }

        @Override
        public void runCommand(String s, String[] args) {
            if(args.length < 2)
                return;
            if(args[1].equals("file")){
                if(args.length < 3)
                    return;
                File file = new File(args[2]);
                if(!file.exists() || file.isDirectory())
                    return;
                URL url;
                try {
                    url = file.toURI().toURL();
                } catch (MalformedURLException ignored){
                    return;
                }
                Player.play(url.toExternalForm());
                complete("Now playing: " + url.toExternalForm());
                return;
            }
            if(args[1].equals("url")){
                if(args.length < 3)
                    return;
                URL url;
                try {
                    url = new URL(args[2]);
                } catch (MalformedURLException ignored){
                    return;
                }
                Player.play(url.toExternalForm());
                complete("Now playing: " + url.toExternalForm());
                return;
            }
            Player.play(instance.dir + args[1]);
            complete("Now playing: " + instance.dir + args[1]);
        }
    }
}
