package com.kisman.cc.module;

import com.kisman.cc.Kisman;
import com.kisman.cc.module.modules.movement.Step;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;
import java.util.ArrayList;

public class ModuleManager {
    public ArrayList<Module> modules;

    public ModuleManager() {
        modules = new ArrayList<Module>();
        MinecraftForge.EVENT_BUS.register(this);
        init();
    }

    public void init() {
        Kisman.LOGGER.info("moduleManager init");
        //Movement
        modules.add(new Step());
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public Module getModule(String name) {
        for(Module m : modules) {
            if(m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public ArrayList<Module> getModsInCategory(Category cat) {
        ArrayList<Module> mods = new ArrayList<>();
        for(Module m : modules) {
            if(m.getCategory() == cat) {
                mods.add(m);
            }
        }
        return mods;
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent event) {
        if(Keyboard.getEventKeyState()) {
            for(Module m : modules) {
                if(m.getKey() ==Keyboard.getEventKey()) {
                    m.toggle();
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event) {
        for(Module m : modules) {
            if(m.isToggled()) {
                m.update();
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        for(Module m : modules) {
            if(m.isToggled()) {
                m.render();
            }
        }
    }
}
