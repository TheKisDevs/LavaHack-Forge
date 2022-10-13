package com.kisman.cc.features.command.commands;

import com.kisman.cc.features.command.Command;
import com.kisman.cc.features.module.misc.Translate;
import com.kisman.cc.util.process.web.translate.Translator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Konas's Developers
 */
public class LangCommand extends Command {

    public LangCommand(){
        super("lang");
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getSyntax() {
        return "lang <language>";
    }

    @Override
    public void runCommand(@NotNull String s, @NotNull String[] args) {
        try {
            Map<String, String> langs = Translator.getLangs();
            System.out.println(langs);
            String language = Translator.getKeyOrValue(langs, args[1]);
            if (language != null) {
                Translate.targetLanguage = language;
                complete("Set target language to " + langs.get(language));
            } else {
                error("Could not find language");
            }
        } catch (Exception e){
            error("Something went wrong. Make sure to use the right syntax: " + getSyntax());
        }
    }
}
