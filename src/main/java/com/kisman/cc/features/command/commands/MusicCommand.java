package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.util.process.web.music.Player;

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
        this.dir = "";
        instance = this;
    }

    @Override
    public void runCommand(String s, String[] args) {
        if(args.length < 1)
            return;
        SubCommand command = SubCommand.getInstance(args[0]);
        if(command == null)
            return;
        command.runCommand(s, args);
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    private static abstract class SubCommand {

        private static final Map<String, SubCommand> instances;

        static {
            instances = new HashMap<>();
            instances.put("play", new CommandPlay());
            instances.put("stop", new CommandStop());
            instances.put("resume", new CommandResume());
            instances.put("pause", new CommandPause());
            instances.put("volume", new CommandVolume());
            instances.put("dir", new CommandDir());
        }

        private SubCommand(){
        }

        abstract void runCommand(String s, String[] args);

        private static SubCommand getInstance(String name){
            return instances.get(name);
        }
    }

    private static class CommandDir extends SubCommand {

        @Override
        void runCommand(String s, String[] args) {
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

        @Override
        void runCommand(String s, String[] args) {
            if(args.length < 2)
                return;
            int a = Integer.parseInt(args[1]);
            Player.setVolume(a);
            complete("Set music volume to: " + a);
        }
    }

    private static class CommandPause extends SubCommand {

        @Override
        void runCommand(String s, String[] args) {
            Player.pause();
            complete("Paused the music");
        }
    }

    private static class CommandResume extends SubCommand {

        @Override
        void runCommand(String s, String[] args) {
            Player.resume();
            complete("Resumed the music");
        }
    }

    private static class CommandStop extends SubCommand {

        @Override
        void runCommand(String s, String[] args) {
            Player.stop();
            complete("Stopped the music");
        }
    }

    private static class CommandPlay extends SubCommand {

        @Override
        void runCommand(String s, String[] args) {
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
