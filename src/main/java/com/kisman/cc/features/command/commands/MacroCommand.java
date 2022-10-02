package com.kisman.cc.features.command.commands;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.macro.Macro;
import com.kisman.cc.features.macro.MacroManager;
import com.kisman.cc.features.macro.activator.Activator;
import com.kisman.cc.features.macro.activator.ActivatorFactory;
import com.kisman.cc.features.macro.activator.ActivatorManager;
import com.kisman.cc.features.macro.impl.MacroImpl;
import com.kisman.cc.features.macro.impl.MacroImplFactory;
import com.kisman.cc.features.macro.impl.MacroImplManager;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cubic
 * @since 02.10.2022
 * This command is in very early progress. There
 * will be a lot more features added to this command.
 */
public class MacroCommand extends Command {

    public MacroCommand() {
        super("macro");
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        String complete = assemble(args);

        String name = complete.substring(complete.indexOf('(') + 1, complete.indexOf(')'));

        String macroString = complete.substring(complete.indexOf('{') + 1, complete.indexOf('}'));
        String[] parts = macroString.split(":");
        String macro = parts[0].trim();
        String arguments = parts[1].trim();
        MacroImplFactory<?> macroImplFactory = MacroImplManager.getMacroImpl(macro);
        if(macroImplFactory == null){
            log("[MacroCommand] " + macro + " is an invalid macro");
            return;
        }
        MacroImpl m = macroImplFactory.construct(arguments);

        String activatorString = complete.substring(complete.indexOf('[') + 1, complete.indexOf(']'));
        String[] activators = activatorString.split(",");
        List<Activator> activatorList = new ArrayList<>();
        for(String activator : activators){
            String[] split = activator.trim().split(":");
            String actualActivator = split[0].trim();
            String condition = split[1].trim();
            ActivatorFactory<?> factory = ActivatorManager.getFactory(actualActivator);
            if(factory == null){
                log("[MacroCommand] " + actualActivator + " is an invalid activator");
                return;
            }
            activatorList.add(factory.construct(condition, m));
        }
        Macro finalMacro = new Macro(name, m, activatorList);
        finalMacro.enable();
        MacroManager.addMacro(finalMacro);
    }

    private static String assemble(String[] args){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < args.length; i++){
            builder.append(args[i]);
            if(i < args.length - 1)
                builder.append(' ');
        }
        return builder.toString();
    }

    private static void log(String s){
        Kisman.LOGGER.error(s);
        ChatUtility.error().printClientMessage(s);
    }
}
