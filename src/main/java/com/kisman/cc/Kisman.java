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
import com.kisman.cc.module.client.Config;
import com.kisman.cc.oldclickgui.*;
import com.kisman.cc.module.*;
import com.kisman.cc.oldclickgui.csgo.ClickGuiNew;
import com.kisman.cc.oldclickgui.mainmenu.sandbox.SandBoxShaders;
import com.kisman.cc.oldclickgui.vega.Gui;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontRenderer;
import com.kisman.cc.util.hwid.*;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.shaders.Shaders;
import com.kisman.cc.util.glow.ShaderShell;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.bus.EventManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.*;
import org.apache.logging.log4j.*;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class Kisman {
    public static final String NAME = "kisman.cc+";
    public static final String MODID = "kisman";
    public static final String VERSION = "b0.1.6.1";
    public static final String HWIDS_LIST = "https://pastebin.com/raw/yM7s0G4u";
    public static final String HWID_LOGS = "https://" + MainWindow.d1 + "/" + MainWindow.d2 + "/905166844967673928/LgJFUe6o45hBx7e1xE5OxqwD7M5DdzIsg3s9-dd6d5jDJ6k7KaUf1Vettd5mf9LQz8aW";
    public static final String fileName = "kisman.cc/";
    public static final String moduleName = "Modules/";
    public static final String mainName = "Main/";
    public static final String miscName = "Misc/";
    public static final String sandboxName = "SandBox/";
    public static final String pluginName = "Plugins/";
    public static float TICK_TIMER = 1;

    public static Kisman instance;
    public static final EventManager EVENT_BUS = new EventManager();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final HashMap<GuiScreen, Float> map = new HashMap<>();

    public static final boolean allowToConfiguredAnotherClients;

    static {
        allowToConfiguredAnotherClients = HWID.getHWID().equals("42d17b8fbbd970b9f4db02f9a65fca3b");
    }

    public boolean init = false;

    private Minecraft mc;

    public ModuleManager moduleManager;
    public FriendManager friendManager;
    public HudModuleManager hudModuleManager;
    public SettingsManager settingsManager;
    public ClickGuiNew clickGuiNew;
    public BlockGui blockGui;
    public GuiConsole guiConsole;
    public ColorPicker colorPicker;
    public ColorUtil colorUtil;
    public HudGui hudGui;
    public Gui gui;
    public CustomFontRenderer customFontRenderer;
    public CustomFontRenderer customFontRenderer1;
    public CommandManager commandManager;
    public RPC discord;
    public RotationUtils rotationUtils;
    public EventProcessor eventProcessor;
    public ServerManager serverManager;
    public Shaders shaders;
    public SandBoxShaders sandBoxShaders;
    public Managers managers;

    public Verificator d1;
    public MainDumper d2;

    public Kisman() {
        instance = this;
    }

    public void init() throws IOException, NoSuchFieldException, IllegalAccessException {
        d1 = new Verificator();

        if(!d1.preInit()) throw new NoStackTraceThrowable("YesComment");

        Display.setTitle(NAME + " | " + VERSION);
    	MinecraftForge.EVENT_BUS.register(this);

        mc = Minecraft.getMinecraft();

        managers = new Managers();
        managers.init();

        friendManager = new FriendManager();
    	settingsManager = new SettingsManager();
    	moduleManager = new ModuleManager();
        hudModuleManager = new HudModuleManager();
        clickGuiNew = new ClickGuiNew();
    	blockGui = new BlockGui();
        guiConsole = new GuiConsole();
        colorPicker = new ColorPicker();
        colorUtil = new ColorUtil();
    	hudGui = new HudGui();
        gui = new Gui();
        customFontRenderer = new CustomFontRenderer(new Font("Verdana", 0 , 18), true, true);
        customFontRenderer1 = new CustomFontRenderer(new Font("Verdana", 0, 15), true, true);
        commandManager = new CommandManager();
        discord = new RPC();
        rotationUtils = new RotationUtils();
        eventProcessor = new EventProcessor();
        serverManager = new ServerManager();
        shaders = new Shaders();
        sandBoxShaders = new SandBoxShaders();

        //load configs
        LoadConfig.init();
        //load glow shader
        ShaderShell.init();

        //load some features
        d2 = new MainDumper();
        d2.init();

        init = true;
    }
    
    @SubscribeEvent
    public void key(KeyInputEvent e) {
    	if (mc.world == null || mc.player == null) return;
    	try {
            if (Keyboard.isCreated()) {
                if (Keyboard.getEventKeyState()) {
                    int keyCode = Keyboard.getEventKey();
                    if (keyCode <= 1) return;
                    for (Module m : moduleManager.modules) {
                    	if (m.getKey() == keyCode) {
                            m.toggle();
                            if(moduleManager.getModule("Notification").isToggled()) ChatUtils.message(TextFormatting.GRAY + "Module " + (m.isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + m.getName() + TextFormatting.GRAY + " has been " + (m.isToggled() ? "enabled" : "disabled") + "!");
                    	}
                    }
                    for (HudModule m : hudModuleManager.modules) if (m.getKey() == keyCode) m.toggle();
                } else if(Keyboard.getEventKey() > 1) onRelease(Keyboard.getEventKey());
            }
        } catch (Exception q) { q.printStackTrace(); }
    }

    private void onRelease(int key) {
        for(Module m : moduleManager.modules) {
            if(m.getKey() == key) {
                if(m.hold) {
                    m.toggle();
                    if (moduleManager.getModule("Notification").isToggled()) ChatUtils.message(TextFormatting.GRAY + "Module " + (m.isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + m.getName() + TextFormatting.GRAY + " has been " + (m.isToggled() ? "enabled" : "disabled") + "!");
                }
            }
        }
    }

    public static String getName() {
        if(instance.init) {
            switch (Config.instance.nameMode.getValString()) {
                case "kismancc": return NAME;
                case "LavaHack": return "LavaHack";
                case "TheKisDevs": return "TheKisDevs";
                case "custom": return Config.instance.customName.getValString();
            }
        }
        return NAME;
    }

    public static String getVersion() {
        return VERSION;
    }

    public static void initDirs() throws IOException {
        if (!Files.exists(Paths.get(fileName))) {
            Files.createDirectories(Paths.get(fileName));
            LOGGER.info("Root dir created");
        }
        if (!Files.exists(Paths.get(fileName + moduleName))) {
            Files.createDirectories(Paths.get(fileName + moduleName));
            LOGGER.info("Module dir created");
        }
        if (!Files.exists(Paths.get(fileName + mainName))) {
            Files.createDirectories(Paths.get(fileName + mainName));
            LOGGER.info("Main dir created");
        }
        if (!Files.exists(Paths.get(fileName + miscName))) {
            Files.createDirectories(Paths.get(fileName + miscName));
            LOGGER.info("Misc dir created");
        }
        if(!Files.exists(Paths.get(fileName + sandboxName))) {
            Files.createDirectories(Paths.get(fileName + sandboxName));
            LOGGER.info("Sandboxes dir created");
        }
        if(!Files.exists(Paths.get(fileName + pluginName))) {
            Files.createDirectories(Paths.get(fileName + pluginName));
            LOGGER.info("Plugins dir created");
        }
    }
}