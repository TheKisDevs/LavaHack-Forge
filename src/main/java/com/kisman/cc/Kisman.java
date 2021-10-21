package com.kisman.cc;

import com.kisman.cc.command.CommandManager;
import com.kisman.cc.console.GuiConsole;
import com.kisman.cc.event.EventProcessor;
import com.kisman.cc.file.LoadConfig;
import com.kisman.cc.hud.hudgui.HudGui;
import com.kisman.cc.hud.hudmodule.HudModule;
import com.kisman.cc.hud.hudmodule.HudModuleManager;
import com.kisman.cc.newclickgui.NewGui;
import com.kisman.cc.oldclickgui.BlockGui;
import com.kisman.cc.oldclickgui.ClickGui;
import com.kisman.cc.oldclickgui.ColorPicker;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.ModuleManager;
import com.kisman.cc.particle.ParticleSystem;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.util.ColorUtil;
import com.kisman.cc.util.RotationUtils;
import com.kisman.cc.util.customfont.CustomFontRenderer;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import kisman.pasta.salhack.util.customfont.FontManager;
import me.zero.alpine.bus.EventManager;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;

public class Kisman {
    public static final String NAME = "kisman.cc+";
    public static final String MODID = "kisman";
    public static final String VERSION = "b0.1.5";

    public static Kisman instance;
    public static final EventManager EVENT_BUS = new EventManager();
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public ModuleManager moduleManager;
    public HudModuleManager hudModuleManager;
    public SettingsManager settingsManager;
    public ClickGui clickGui;
    public BlockGui blockGui;
    public GuiConsole guiConsole;
    public ColorPicker colorPicker;
    public ColorUtil colorUtil;
    public HudGui hudGui;
    public NewGui newGui;
    public CustomFontRenderer customFontRenderer;
    public FontManager fontManager;
    public CommandManager commandManager;
    public RPC discord;
    public RotationUtils rotationUtils;
    public EventProcessor eventProcessor;
    public ParticleSystem particleSystem;
    
    public void init() {
        Display.setTitle(NAME + " | " + VERSION);
    	MinecraftForge.EVENT_BUS.register(this);
    	settingsManager = new SettingsManager();
    	moduleManager = new ModuleManager();
        hudModuleManager = new HudModuleManager();
    	clickGui = new ClickGui();
    	blockGui = new BlockGui();
        guiConsole = new GuiConsole();
        colorPicker = new ColorPicker();
        colorUtil = new ColorUtil();
    	hudGui = new HudGui();
        newGui = new NewGui();
        customFontRenderer = new CustomFontRenderer(new Font("Verdana", 0 , 18), false, false);
        fontManager = new FontManager();
        commandManager = new CommandManager();
        discord = new RPC();
        rotationUtils = new RotationUtils();
        eventProcessor = new EventProcessor();
        particleSystem = new ParticleSystem(100, true, 150);

        //load configs
        LoadConfig.init();

        //salhack font manager load
        fontManager.load();
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
                            if(this.moduleManager.getModule("Notification").isToggled()) ChatUtils.message(TextFormatting.GRAY + "Module " + (m.isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + m.getName() + TextFormatting.GRAY + " has been " + (m.isToggled() ? "enabled" : "disabled") + "!");
//                            SaveConfig.init();
                    	}

/*                        for(Setting set : settingsManager.getSettingsByMod(m)) {
                            if(set.isBind() && set.getKey() == keyCode) {
                                System.out.println("3");
                                moduleManager.key(Keyboard.getEventCharacter(), Keyboard.getEventKey(), m);
                            }
                        }*/
                    }
                    for (HudModule m : hudModuleManager.modules) {
                        if (m.getKey() == keyCode && keyCode > 0) {
                            m.toggle();
                        }
                    }
                }
            }
        } catch (Exception q) { q.printStackTrace(); }
    }
}