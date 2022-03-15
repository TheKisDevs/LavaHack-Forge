package com.kisman.cc;

import com.kisman.cc.catlua.ScriptManager;
import com.kisman.cc.catlua.lua.utils.LuaRotation;
import com.kisman.cc.catlua.mapping.Remapper3000;
import com.kisman.cc.command.CommandManager;
import com.kisman.cc.console.GuiConsole;
import com.kisman.cc.event.*;
import com.kisman.cc.file.LoadConfig;
import com.kisman.cc.friend.FriendManager;
import com.kisman.cc.gui.halq.Frame;
import com.kisman.cc.hud.hudeditor.HudEditorGui;
import com.kisman.cc.hud.hudgui.HudGui;
import com.kisman.cc.hud.hudmodule.*;
import com.kisman.cc.module.client.Config;
import com.kisman.cc.gui.*;
import com.kisman.cc.module.*;
import com.kisman.cc.gui.csgo.ClickGuiNew;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.mainmenu.sandbox.SandBoxShaders;
import com.kisman.cc.gui.notification.NotificationsManager;
import com.kisman.cc.gui.vega.Gui;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.util.*;
import com.kisman.cc.util.customfont.CustomFontRenderer;
import com.kisman.cc.util.optimization.aiimpr.MainAiImpr;
import com.kisman.cc.util.protect.*;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.shaders.Shaders;
import com.kisman.cc.util.glow.ShaderShell;
import i.gishreloaded.gishcode.utils.visual.ChatUtils;
import me.zero.alpine.bus.EventManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.*;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.nio.file.*;
import java.util.HashMap;

public class Kisman {
    public static final String NAME = "kisman.cc+";
    public static final String MODID = "kisman";
    public static final String VERSION = "b0.1.6.1";
    public static final String HWIDS_LIST = "https://pastebin.com/raw/yM7s0G4u";
    public static final String fileName = "kisman.cc/";
    public static final String moduleName = "Modules/";
    public static final String mainName = "Main/";
    public static final String miscName = "Misc/";
    public static final String luaName = "Lua/";
    public static final String mappingName = "Mapping/";
    public static final String sandboxName = "SandBox/";
    public static final String pluginName = "Plugins/";

    public static Kisman instance;
    public static final EventManager EVENT_BUS = new EventManager();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final HashMap<GuiScreen, Float> map = new HashMap<>();

    public static EntityPlayer target_by_click = null;

    public static final boolean allowToConfiguredAnotherClients, remapped;
    public static boolean isOpenAuthGui;
    public static boolean autoUpdate;
    public static boolean canUseImprAstolfo = false;

    static {
        boolean remapped1;
        allowToConfiguredAnotherClients = HWID.getHWID().equals("42d17b8fbbd970b9f4db02f9a65fca3b");

        try {
            Minecraft.class.getDeclaredField("player");
            remapped1 = false;
        } catch(NoSuchFieldException e) {
            remapped1 = true;
        }
        remapped = remapped1;
    }

    public boolean init = false;

    private static Minecraft mc;

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
    public HudEditorGui hudEditorGui;
    public Gui gui;
    public HalqGui halqGui;
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
    public NotificationsManager notificationsManager;

    public MainAiImpr aiImpr;

    //catlua
    public EventProcessorLua eventProcessorLua;
    public Remapper3000 remapper3000;
    public LuaRotation luaRotation;
    public ScriptManager scriptManager;

    public Kisman() {
        instance = this;
    }

    public void preInit() throws IOException, NoSuchFieldException, IllegalAccessException {
        AntiDump.check();
    }

    public void init() throws IOException, NoSuchFieldException, IllegalAccessException {
        Display.setTitle(NAME + " | " + VERSION);
    	MinecraftForge.EVENT_BUS.register(this);

        aiImpr = new MainAiImpr();

        eventProcessor = new EventProcessor();
        eventProcessor.onInit();

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
        customFontRenderer = new CustomFontRenderer(new Font("Verdana", Font.PLAIN, 18), true, true);
        customFontRenderer1 = new CustomFontRenderer(new Font("Verdana", Font.PLAIN, 15), true, true);
        commandManager = new CommandManager();
        discord = new RPC();
        rotationUtils = new RotationUtils();
        serverManager = new ServerManager();
        shaders = new Shaders();
        sandBoxShaders = new SandBoxShaders();
        notificationsManager = new NotificationsManager();

        //load configs
        LoadConfig.init();
        //load glow shader
        ShaderShell.init();


        //catlua
        eventProcessorLua = new EventProcessorLua();
        remapper3000 = new Remapper3000();
        remapper3000.init();
        luaRotation = new LuaRotation();
        scriptManager = new ScriptManager();

        //gui's
        clickGuiNew = new ClickGuiNew();
        hudGui = new HudGui();
        hudEditorGui = new HudEditorGui();
        gui = new Gui();
        halqGui = new HalqGui();

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
                            notificationsManager.addNotifications(m.getName(), (m.isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + m.getName() + TextFormatting.GRAY + " has been " + (m.isToggled() ? "enabled" : "disabled"), true);
                    	}
                    }
                    for (HudModule m : hudModuleManager.modules) if (m.getKey() == keyCode) m.toggle();
                } else if(Keyboard.getEventKey() > 1) onRelease(Keyboard.getEventKey());
            }
        } catch (Exception ignored) {}
    }

    private void onRelease(int key) {
        for(Module m : moduleManager.modules) {
            if(m.getKey() == key) {
                if(m.hold) {
                    m.toggle();
                    if (moduleManager.getModule("Notification").isToggled()) ChatUtils.message(TextFormatting.GRAY + "Module " + (m.isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + m.getName() + TextFormatting.GRAY + " has been " + (m.isToggled() ? "enabled" : "disabled") + "!");
                    notificationsManager.addNotifications(m.getName(), (m.isToggled() ? TextFormatting.GREEN : TextFormatting.RED) + m.getName() + TextFormatting.GRAY + " has been " + (m.isToggled() ? "enabled" : "disabled"), true);
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
                case "kidman": return "kidman.club";
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
        if (!Files.exists(Paths.get(fileName + luaName))) {
            Files.createDirectories(Paths.get(fileName + luaName));
            LOGGER.info("Lua dir created");
        }
        if (!Files.exists(Paths.get(fileName + mappingName))) {
            Files.createDirectories(Paths.get(fileName + mappingName));
            LOGGER.info("Mapping dir created");
        }
    }

    public static void openLink(String link) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) desktop.browse(new URI(link));
        } catch (IOException | URISyntaxException e) {e.printStackTrace();}
    }

    public static void reloadGUIs() {
        if(mc.player != null && mc.world != null) mc.displayGuiScreen(null);
        instance.halqGui.frames.stream().filter(frame -> frame.cat.equals(Category.LUA)).forEach(Frame::reload);
        instance.clickGuiNew = new ClickGuiNew();
    }
}