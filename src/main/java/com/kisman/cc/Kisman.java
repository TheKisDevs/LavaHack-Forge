package com.kisman.cc;

import com.kisman.cc.app.MainWindow;
import com.kisman.cc.command.CommandManager;
import com.kisman.cc.console.GuiConsole;
import com.kisman.cc.dumper.MainDumper;
import com.kisman.cc.event.EventProcessor;
import com.kisman.cc.file.LoadConfig;
import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.hud.hudgui.HudGui;
import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.hypixel.skyblock.colf.MainColf;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.module.client.HUD;
import com.kisman.cc.newclickgui.*;
import com.kisman.cc.oldclickgui.*;
import com.kisman.cc.module.Module;
import com.kisman.cc.module.ModuleManager;
import com.kisman.cc.oldclickgui.mainmenu.sandbox.SandBoxShaders;
import com.kisman.cc.oldclickgui.vega.Gui;
import com.kisman.cc.particle.ParticleSystem;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontRenderer;
import com.kisman.cc.util.hwid.Verificator;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.shaders.Shaders;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import kisman.pasta.salhack.util.customfont.FontManager;
import me.zero.alpine.bus.EventManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.*;
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
    public static final String VERSION = "b0.1.6.1";
    public static final String HWIDS_LIST = "https://pastebin.com/raw/yM7s0G4u";
    public static final String HWID_LOGS = "https://" + MainWindow.d1 + "/" + MainWindow.d2 + "/905166844967673928/LgJFUe6o45hBx7e1xE5OxqwD7M5DdzIsg3s9-dd6d5jDJ6k7KaUf1Vettd5mf9LQz8aW";
    public static float TICK_TIMER = 1;

    public static Kisman instance;
    public static final EventManager EVENT_BUS = new EventManager();
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public boolean init = false;

    public ModuleManager moduleManager;
    public FriendManager friendManager;
    public HudModuleManager hudModuleManager;
    public SettingsManager settingsManager;
    public ClickGui clickGui;
    public BlockGui blockGui;
    public GuiConsole guiConsole;
    public ColorPicker colorPicker;
    public ColorUtil colorUtil;
    public HudGui hudGui;
    public NewGui newGui;
    public Gui gui;
    public CustomFontRenderer customFontRenderer;
    public CustomFontRenderer customFontRenderer1;
    public FontManager fontManager;
    public CommandManager commandManager;
    public RPC discord;
    public RotationUtils rotationUtils;
    public EventProcessor eventProcessor;
    public ParticleSystem particleSystem;
    public ServerManager serverManager;
    public Shaders shaders;
    public SandBoxShaders sandBoxShaders;
    public Managers managers;

    public Verificator d1;
    public MainDumper d2;
//    public MainColf d3;

    public Kisman() {
        instance = this;
    }

    public void init() {
        Display.setTitle(NAME + " | " + VERSION);
    	MinecraftForge.EVENT_BUS.register(this);

        managers = new Managers();
        managers.init();

        friendManager = new FriendManager();
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
        gui = new Gui();
        customFontRenderer = new CustomFontRenderer(new Font("Verdana", 0 , 18), false, false);
        customFontRenderer1 = new CustomFontRenderer(new Font("Verdana", Font.PLAIN, 45), false, true);
        fontManager = new FontManager();
        commandManager = new CommandManager();
        discord = new RPC();
        rotationUtils = new RotationUtils();
        eventProcessor = new EventProcessor();
        particleSystem = new ParticleSystem(100, true, 150);
        serverManager = new ServerManager();
        shaders = new Shaders();
        sandBoxShaders = new SandBoxShaders();

        d1 = new Verificator();
        d2 = new MainDumper();
//        d3 = new MainColf();

        //load configs
        LoadConfig.init();

        //salhack font manager load
//        fontManager.load();

        init = true;
    }
    
    @SubscribeEvent
    public void key(KeyInputEvent e) {
    	if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) {
            return;
        }

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

    public static String getName() {
        if(instance.init) {
            switch ((Config.NameMode) Config.instance.nameMode.getValEnum()) {
                case kismancc: {
                    return NAME;
                }
                case lavahack: {
                    return "LavaHack";
                }
                case custom: {
                    return Config.instance.customName.getValString();
                }
            }
        }
        return NAME;
    }

    public static String getVersion() {
        return VERSION;
    }
}