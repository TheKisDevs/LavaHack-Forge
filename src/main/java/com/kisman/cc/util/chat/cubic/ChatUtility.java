package com.kisman.cc.util.chat.cubic;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.util.ReflectionUtilsKt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ChatUtility {

    private static final Minecraft mc;

    static final Map<Class<?>, Module> moduleMapping;

    private static final AbstractChatMessage COMPLETE = new ChatComplete();

    private static final AbstractChatMessage ERROR = new ChatError();

    private static final AbstractChatMessage INFO = new ChatInfo();

    private static final AbstractChatMessage MESSAGE = new ChatMessage();

    private static final AbstractChatMessage WARNING = new ChatWarning();

    static {
        mc = Minecraft.getMinecraft();
        moduleMapping = new HashMap<>(Kisman.instance.moduleManager.modules.size());
        initMappings();
    }

    private static void initMappings(){
        int size = Kisman.instance.moduleManager.modules.size();
        for(int i = 0; i < size; i++){
            Module m = Kisman.instance.moduleManager.modules.get(i);
            moduleMapping.put(m.getClass(), m);
        }
    }


    public static AbstractChatMessage complete(){
        COMPLETE.updateCaller(ReflectionUtilsKt.callerClass());
        return COMPLETE;
    }

    public static AbstractChatMessage error(){
        ERROR.updateCaller(ReflectionUtilsKt.callerClass());
        return ERROR;
    }

    public static AbstractChatMessage info(){
        INFO.updateCaller(ReflectionUtilsKt.callerClass());
        return INFO;
    }

    public static AbstractChatMessage message(){
        MESSAGE.updateCaller(ReflectionUtilsKt.callerClass());
        return MESSAGE;
    }

    public static AbstractChatMessage warning(){
        WARNING.updateCaller(ReflectionUtilsKt.callerClass());
        return WARNING;
    }

    public static void cleanMessage(String message) {
        mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation(message));
    }

    public static void sendComponent(ITextComponent component) {
        sendComponent(component, 0);
    }

    public static void sendComponent(ITextComponent c, int id) {
        applyIfPresent(g -> {
            /*TODO: if (PingBypassServer.isServer()) {
                TextComponentString string = new TextComponentString("<" + TextFormatting.DARK_RED + "PingBypass" + TextFormatting.WHITE + "> ");
                string.appendSibling(c);
                PingBypassServer.sendPacket(new S2CChatPacket(string, ChatType.SYSTEM, id));
            }*/

            g.printChatMessageWithOptionalDeletion(c, id);
        });
    }

    public static void applyIfPresent(Consumer<GuiNewChat> consumer) {
        GuiNewChat chat = getChatGui();

        if (chat != null) consumer.accept(chat);
    }

    public static GuiNewChat getChatGui() {
        if (mc.ingameGUI != null) return mc.ingameGUI.getChatGUI();

        return null;
    }
}
