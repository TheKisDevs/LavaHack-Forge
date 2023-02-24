package com.kisman.cc.util.chat.cubic;

import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.client.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class AbstractChatMessage {

    final Minecraft mc;

    Class<?> curCaller;

    public AbstractChatMessage(){
        this.mc = Minecraft.getMinecraft();
        curCaller = null;
    }

    void updateCaller(Class<?> caller){
        this.curCaller = caller;
    }

    final String formatModule(){
        try {
            Module m = ChatUtility.moduleMapping.get(curCaller);
            if (m == null)
                return "null";
            return m.getName();
        } catch(Exception ignored) {
            return "null";
        }
    }

    final String callerName(){
        try {
            return this.curCaller.getSimpleName();
        } catch(Exception ignored) {
            return "null";
        }
    }

    public final void printMessage(ITextComponent textComponent){
        if(mc.player == null)
            return;
        if(Config.instance.notificationMode.getValEnum() == Config.NotificationMode.MultiLine){
            mc.ingameGUI.getChatGUI().printChatMessage(textComponent);
            return;
        }
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(textComponent, 69);
    }

    public final void printMessage(String textComponentMessage){
        printMessage(new TextComponentTranslation(textComponentMessage));
    }


    public void printClientMessage(String format, Object... args){
        printClientMessage(String.format(format, args));
    }

    public void printClientModuleMessage(String format, Object... args){
        printClientModuleMessage(String.format(format, args));
    }

    public void printModuleMessage(String format, Object... args){
        printModuleMessage(String.format(format, args));
    }

    public void printClassMessage(String format, Object... args){
        printClassMessage(String.format(format, args));
    }

    public void printClientClassMessage(String format, Object... args){
        printClientClassMessage(String.format(format, args));
    }


    public abstract void printClientMessage(ITextComponent textComponent);

    public abstract void printClientMessage(String message);

    public abstract void printClientModuleMessage(String message, Module module);


    public abstract void printClientModuleMessage(ITextComponent textComponent, Module module);

    public abstract void printClientModuleMessage(ITextComponent textComponent);

    public abstract void printClientModuleMessage(String message);


    public abstract void printModuleMessage(ITextComponent textComponent);

    public abstract void printModuleMessage(String message);


    public abstract void printClassMessage(ITextComponent textComponent);

    public abstract void printClassMessage(String message);


    public abstract void printClientClassMessage(ITextComponent textComponent);

    public abstract void printClientClassMessage(String message);
}
