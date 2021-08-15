package com.kisman.cc;

import com.kisman.cc.hud.hudgui.HudGui;
import com.kisman.cc.hud.hudmodule.ArrayList;
import com.kisman.cc.hud.hudmodule.Coord;
import com.kisman.cc.hud.hudmodule.Fps;
import com.kisman.cc.hud.hudmodule.Logo;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.ColorPicker;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.ModuleManager;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.util.customfont.CustomFontRenderer;
import me.zero.alpine.bus.EventManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;

public class Kisman
{
    public static final String NAME = "kisman.cc";
    public static final String MODID = "kisman";
    public static final String VERSION = "b0.1.2";

    public static Kisman instance;
    public static final EventManager EVENT_BUS = new EventManager();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static boolean notificatonModule = false;
    public ModuleManager moduleManager;
    public SettingsManager settingsManager;
    public ClickGui clickGui;
    public ColorPicker colorPicker;
    public HudGui hudGui;
    public CustomFontRenderer customFontRenderer;
    public ArrayList arrayList;
    public Coord coord;
    public Logo logo;
    public Fps fps;
    
    public void init() {
        Display.setTitle(NAME + " " + VERSION);
    	MinecraftForge.EVENT_BUS.register(this);
    	MinecraftForge.EVENT_BUS.register(new ArrayList());
    	MinecraftForge.EVENT_BUS.register(new Coord());
    	MinecraftForge.EVENT_BUS.register(new Logo(NAME, VERSION));
    	MinecraftForge.EVENT_BUS.register(new Fps());
    	settingsManager = new SettingsManager();
    	moduleManager = new ModuleManager();
    	clickGui = new ClickGui();
        colorPicker = new ColorPicker();
    	hudGui = new HudGui();
        customFontRenderer = new CustomFontRenderer(new Font("Verdana", 0 , 18), false, false);
        arrayList = new ArrayList();
        coord = new Coord();
        logo = new Logo(NAME, VERSION);
        fps = new Fps();
    }
    
    @SubscribeEvent
    public void key(KeyInputEvent e) {
    	if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null)
    		return; 
    	try {
             if (Keyboard.isCreated()) {
                 if (Keyboard.getEventKeyState()) {
                     int keyCode = Keyboard.getEventKey();
                     if (keyCode <= 0)
                    	 return;
                     for (Module m : moduleManager.modules) {
                    	 if (m.getKey() == keyCode && keyCode > 0) {
                    		 m.toggle();
                    	 }
                     }
                 }
             }
         } catch (Exception q) { q.printStackTrace(); }
    }

    public static boolean isNotificatonModule() {
        return notificatonModule;
    }

    public static void setNotificatonModule(boolean notificatonModule) {
        Kisman.notificatonModule = notificatonModule;
    }
}
