package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import org.cubic.dynamictask.AbstractTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SettingCommand extends Command {

    public SettingCommand() {
        super("setting");
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        if(args.length < 2){
            ChatUtility.error().printClientMessage("Not enough arguments: " + args.length);
            return;
        }
        Module module = Kisman.instance.moduleManager.getModule(args[0]);
        if(module == null){
            ChatUtility.error().printClientMessage("Could not find module: " + args[0]);
            return;
        }
        ArrayList<Setting> settings = Kisman.instance.settingsManager.getSettingsByMod(module);
        ModuleHandler handler  = new ModuleHandler(module, settings);
    }

    private static class ModuleHandler {

        private final Module module;

        private final ArrayList<Setting> settings;

        public ModuleHandler(Module module, ArrayList<Setting> settings){
            this.module = module;
            this.settings = settings;
        }

        public void handle(String s, String[] args){

        }

        public void handleDisplayList(String s, String[] args){
            for(Setting setting : settings){
                SettingModesEnum.SettingModes mode = SettingModesEnum.SettingModes.valueOf(setting.mode.toUpperCase());
                ChatUtility.message().printClientMessage(mode.getTask().doTask());
            }
        }
    }

    private static final class SettingModesEnum {

        private static final AbstractTask.DelegateAbstractTask<String> task = AbstractTask.types(String.class);

        public enum SettingModes {

            ;

            private final AbstractTask<String> abstractTask;

            SettingModes(AbstractTask<String> task){
                this.abstractTask = task;
            }

            public final AbstractTask<String> getTask(){
                return abstractTask;
            }
        }
    }
}
