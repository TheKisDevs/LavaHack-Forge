package com.kisman.cc.features.macro.activator.activators;

import com.kisman.cc.Kisman;
import com.kisman.cc.features.macro.activator.Activator;
import com.kisman.cc.features.macro.impl.MacroImpl;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class KeyActivator extends Activator {

    public KeyActivator(String condition, MacroImpl macro) {
        super("key", condition, macro);
    }

    @Override
    protected void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent event){
        int key;
        try {
            key = Integer.parseInt(condition);
        } catch (Exception e){
            Kisman.LOGGER.error("[KeyActivator] " + condition + " is not a key");
            return;
        }
        if(key < 0 || key > Keyboard.KEYBOARD_SIZE){
            Kisman.LOGGER.error("[KeyActivator] Invalid key range: " + key);
            return;
        }
        if(Keyboard.isKeyDown(key))
            callMacro();
    }
}
