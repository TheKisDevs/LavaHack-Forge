package com.kisman.cc.features.module.misc;

import com.kisman.cc.features.module.Category;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.process.web.translate.Translator;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Konas's Developers
 */
public class Translate extends Module {

    private static final ReentrantLock mutex = new ReentrantLock();

    private boolean translatedMessageIncoming = false;

    public static String targetLanguage = null;

    public Translate(){
        super("Translate", Category.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if(targetLanguage == null){
            ChatUtility.error().printClientModuleMessage("You must select a language. Use: lang <language> command");
            toggle();
            return;
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onChat(ClientChatEvent event){

        String s = event.getMessage();

        if (s.startsWith("/")) return;

        if (translatedMessageIncoming) {
            event.setCanceled(true);
            translatedMessageIncoming = false;
        }

        String[] sArray = s.split(" ");

        final String[] inputLanguage = {null};

        try {
            new Thread(() -> {
                try {

                    mutex.lock();

                    ArrayList<String> detectedLanguages = new ArrayList<>();

                    for (String i : sArray) {
                        detectedLanguages.add(Translator.detectLanguage(i));
                    }

                    inputLanguage[0] = detectedLanguages.stream()
                            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                            .entrySet().stream().max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey).orElse(null);

                } catch (IOException e) {
                    e.printStackTrace();
                    ChatUtility.error().printClientModuleMessage("Couldnt find Input Language");
                } finally {
                    mutex.unlock();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String[] translatedMessage = {""};

        try {
            String finalTargetLanguage = targetLanguage;
            String finalS = s;
            new Thread(() -> {
                try {
                    mutex.lock();
                    translatedMessage[0] = Translator.translate(finalS, inputLanguage[0], finalTargetLanguage);
                    if (!translatedMessage[0].equals("")) {
                        translatedMessageIncoming = true;
                        mc.player.sendChatMessage(translatedMessage[0]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    ChatUtility.error().printClientModuleMessage("Couldnt find Input Language");
                } finally {
                    mutex.unlock();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
