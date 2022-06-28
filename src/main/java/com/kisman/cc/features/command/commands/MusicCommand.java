package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.command.SubCommand;
import com.kisman.cc.util.process.web.music.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class MusicCommand extends Command {
    private static MusicCommand instance;

    private String dir;

    private boolean argBuilding;

    public MusicCommand(){
        super("music");
        addInstances(
                new CommandDir(this),
                new CommandPause(this),
                new CommandResume(this),
                new CommandVolume(this),
                new CommandPlay(this),
                new CommandStop(this),
                new CommandArgBuilding(this)
        );
        this.dir = "";
        this.argBuilding = false;
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

    private static class CommandArgBuilding extends SubCommand {

        public CommandArgBuilding(Command instance){
            super("argbuilding", instance);
        }

        @Override
        public void runCommand(@NotNull String s, @NotNull String[] args) {
            boolean val = Boolean.parseBoolean(args[1]);
            ((MusicCommand) getInstance()).argBuilding = val;
            complete("Arg building is now set to: " + val);
        }
    }

    private static class CommandDir extends SubCommand {
        public CommandDir(Command instance) {
            super("dir", instance);
        }

        @Override
        public void runCommand(String s, String[] args) {
            MusicCommand cmd = (MusicCommand) getInstance();
            if(args.length < 2)
                return;
            if(args[1].equals("file")){
                if(args.length < 3)
                    return;
                String arg = cmd.argBuilding ? buildArgs(args, 2) : args[2];
                File file = new File(arg);
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
                String arg = cmd.argBuilding ? buildArgs(args, 2) : args[2];
                URL url;
                try {
                    url = new URL(arg);
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
            MusicCommand cmd = (MusicCommand) getInstance();
            if(args.length < 2)
                return;
            if(args[1].equals("file")){
                if(args.length < 3)
                    return;
                String arg = cmd.argBuilding ? buildArgs(args, 2) : args[2];
                File file = new File(arg);
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
                String arg = cmd.argBuilding ? buildArgs(args, 2) : args[2];
                URL url;
                try {
                    url = new URL(arg);
                } catch (MalformedURLException ignored){
                    return;
                }
                Player.play(url.toExternalForm());
                complete("Now playing: " + url.toExternalForm());
                return;
            }
            String arg = cmd.argBuilding ? buildArgs(args, 1) : args[1];
            Player.play(instance.dir + arg);
            complete("Now playing: " + instance.dir + arg);
        }
    }

    private static String buildArgs(String[] args, int pos){
        StringBuilder sb = new StringBuilder(lenOf(pos, args));
        for(int i = 0; i < args.length; i++){
            if(i == args.length - 1){
                sb.append(args[i]);
                continue;
            }
            sb.append(args[i]);
            sb.append(' ');
        }
        return sb.toString();
    }

    private static int lenOf(int pos, String[] args){
        int len = 0;
        for(int i = pos; i < args.length; i++){
            len += args[i].length();
        }
        len += args.length; // don't forget to count the spaces
        return len;
    }
}
