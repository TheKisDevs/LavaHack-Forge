package com.kisman.cc;

import com.kisman.cc.api.cape.CapeAPI;
import com.kisman.cc.event.EventProcessor;
import com.kisman.cc.event.EventProcessorLua;
import com.kisman.cc.features.Binder;
import com.kisman.cc.features.catlua.ScriptManager;
import com.kisman.cc.features.catlua.lua.utils.LuaRotation;
import com.kisman.cc.features.catlua.mapping.ForgeMappings;
import com.kisman.cc.features.catlua.mapping.Remapper3000;
import com.kisman.cc.features.command.CommandManager;
import com.kisman.cc.features.hud.HudModule;
import com.kisman.cc.features.hud.HudModuleManager;
import com.kisman.cc.features.module.BindType;
import com.kisman.cc.features.module.IBindable;
import com.kisman.cc.features.module.Module;
import com.kisman.cc.features.module.ModuleManager;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.nocom.NoComModuleManager;
import com.kisman.cc.features.nocom.gui.NoComGui;
import com.kisman.cc.features.plugins.PluginHandler;
import com.kisman.cc.features.plugins.managers.PluginManager;
import com.kisman.cc.features.rpc.RPC;
import com.kisman.cc.gui.MainGui;
import com.kisman.cc.gui.console.ConsoleGui;
import com.kisman.cc.gui.csgo.ClickGuiNew;
import com.kisman.cc.gui.halq.Frame;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.hudeditor.HalqHudGui;
import com.kisman.cc.gui.mainmenu.gui.MainMenuController;
import com.kisman.cc.gui.mainmenu.sandbox.SandBoxShaders;
import com.kisman.cc.gui.other.music.MusicGui;
import com.kisman.cc.gui.other.search.SearchGui;
import com.kisman.cc.pingbypass.server.features.modules.PingBypassModuleManager;
import com.kisman.cc.pingbypass.server.gui.PingBypassGui;
import com.kisman.cc.settings.Setting;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.util.chat.cubic.ChatUtility;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.manager.ServerManager;
import com.kisman.cc.util.manager.file.ConfigManager;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.math.vectors.VectorUtils;
import com.kisman.cc.util.optimization.aiimpr.MainAiImpr;
import com.kisman.cc.util.render.shader.ShaderShell;
import me.zero.alpine.bus.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import sun.misc.Unsafe;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Kisman {
    public static final String NAME = "kisman.cc+";
    public static final String MODID = "kisman";
    public static final String VERSION = "b0.1.6.5";
    public static final String HWIDS_LIST = "https://pastebin.com/raw/yM7s0G4u";
    public static final String fileName = "kisman.cc/";
    public static final String moduleName = "Modules/";
    public static final String hudName = "Hud/";
    public static final String mainName = "Main/";
    public static final String miscName = "Misc/";
    public static final String luaName = "Lua/";
    public static final String mappingName = "Mapping/";
    public static final String imagesName = "Images/";
    public static final String pluginsName = "Plugins/";

    public static final boolean MODULE_DEBUG = false;

    public static final Kisman instance = new Kisman();
    public static final EventManager EVENT_BUS = new EventManager();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final HashMap<GuiScreen, Float> map = new HashMap<>();

    public static EntityPlayer target_by_click = null;

    public static boolean bypassAuth = true, remapped = !runningFromIntelliJ();
    public static boolean isOpenAuthGui;
    public static boolean canUseImprAstolfo = false;
    public static boolean canInitializateCatLua = true;

    public static String currentConfig = null;

    public boolean init = false;

    private static Minecraft mc;

    public VectorUtils vectorUtils;

    public ModuleManager moduleManager;
    public NoComModuleManager noComModuleManager;
    public FriendManager friendManager;
    public HudModuleManager hudModuleManager;
    public SettingsManager settingsManager;
    public ClickGuiNew clickGuiNew;
    public ConsoleGui consoleGui;
    public HalqGui halqGui;
    public HalqHudGui halqHudGui;
    public PingBypassGui pingBypassGui;
    public NoComGui noComGui;
    public MainGui.SelectionBar selectionBar;
    public MainGui.GuiGradient guiGradient;
    public SearchGui searchGui;
    public MusicGui musicGui;
    public MainMenuController mainMenuController;
    public CommandManager commandManager;
    public RPC discord;
    public EventProcessor eventProcessor;
    public ServerManager serverManager;
    public SandBoxShaders sandBoxShaders;
    public Managers managers;
    public CapeAPI capeAPI;
    public PluginManager pluginManager;

    public MainAiImpr aiImpr;

    //catlua
    public EventProcessorLua eventProcessorLua;
    public Remapper3000 remapper3000;
    public ForgeMappings forgeMappings;
    public LuaRotation luaRotation;
    public ScriptManager scriptManager;

    //Config
    public ConfigManager configManager;

    //Phobos Plugins
    public final PluginHandler pluginHandler = new PluginHandler();

    private Kisman() {}

    public void coreModInit() {
        pluginHandler.coreModInit();
    }

    public void init() throws IOException, NoSuchFieldException, IllegalAccessException {
        if(init) return;

        aiImpr = new MainAiImpr();
        eventProcessor = new EventProcessor();
        managers = new Managers();
        managers.init();
        aiImpr.init();

        Display.setTitle(NAME + " | " + VERSION);
        MinecraftForge.EVENT_BUS.register(this);
        mc = Minecraft.getMinecraft();

        vectorUtils = new VectorUtils();
        pluginManager = new PluginManager();

        friendManager = new FriendManager();
    	settingsManager = new SettingsManager();
    	moduleManager = new ModuleManager();
        PingBypassModuleManager.INSTANCE.init();
//        noComModuleManager = new NoComModuleManager();
        hudModuleManager = new HudModuleManager();
        clickGuiNew = new ClickGuiNew();
        consoleGui = new ConsoleGui();
        commandManager = new CommandManager();
        discord = new RPC();
        serverManager = new ServerManager();
        sandBoxShaders = new SandBoxShaders();
        capeAPI = new CapeAPI();
        pluginHandler.init();

        //load 2d shaders
        ShaderShell.init();

        //catlua
        eventProcessorLua = new EventProcessorLua();
        remapper3000 = new Remapper3000();
        remapper3000.init();
        luaRotation = new LuaRotation();
        scriptManager = new ScriptManager();

        //gui's
        clickGuiNew = new ClickGuiNew();
        halqGui = new HalqGui();
        halqHudGui = new HalqHudGui();
        pingBypassGui = new PingBypassGui();
//        noComGui = new NoComGui();

        mainMenuController = new MainMenuController();
        mainMenuController.init();

        selectionBar = new MainGui.SelectionBar(MainGui.Guis.ClickGui);
        guiGradient = new MainGui.GuiGradient();

        //For test
        searchGui = new SearchGui(new Setting("Test"), null);
        musicGui = new MusicGui();

        configManager = new ConfigManager("config");
        configManager.getLoader().init();

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
                    for (Module m : moduleManager.modules) if (m.getKeyboardKey() == keyCode && m.bindType == BindType.Keyboard) m.toggle();
                    for (HudModule m : hudModuleManager.modules) if (m.getKeyboardKey() == keyCode && m.bindType == BindType.Keyboard) m.toggle();
                    for (Setting s : settingsManager.getSettings()) {
                        if(s.isCombo()) {
                            for(String option : s.binders.keySet()) {
                                Binder binder = s.binders.get(option);
                                if(binder.getKeyboardKey() == keyCode && binder.getType() == BindType.Keyboard) {
                                    s.setValString(option);
                                    if(init && Config.instance.notification.getValBoolean()) ChatUtility.message().printClientMessage(TextFormatting.GRAY + "Setting " + s.toDisplayString() + " has been changed to " + option + "!");
                                }
                            }
                        } else if(s.getKeyboardKey() == keyCode && s.bindType == BindType.Keyboard && s.isCheck()) {
                            s.setValBoolean(!s.getValBoolean());
                            if(init && Config.instance.notification.getValBoolean()) ChatUtility.message().printClientMessage(TextFormatting.GRAY + "Setting " + (s.getValBoolean() ? TextFormatting.GREEN : TextFormatting.RED) + s.toDisplayString()/*s.getParentMod().getName() + "->" + s.getName()*/ + TextFormatting.GRAY + " has been " + (s.getValBoolean() ? "enabled" : "disabled") + "!");
                        }
                    }
                } else if(Keyboard.getEventKey() > 1) onRelease(Keyboard.getEventKey(), false);
            }
        } catch (Exception ignored) {}
    }

    @SubscribeEvent
    public void mouse(InputEvent e) {
        if (mc.world == null || mc.player == null) return;
        try {
            if (Mouse.isCreated()) {
                if (Mouse.getEventButtonState()) {
                    int button = Mouse.getEventButton();
                    if (button <= 1) return;
                    for (Module m : moduleManager.modules) if (IBindable.Companion.getKey(m) == button && m.getType() == BindType.Mouse) m.toggle();
                    for (HudModule m : hudModuleManager.modules) if (IBindable.Companion.getKey(m) == button && m.getType() == BindType.Mouse) m.toggle();
                    for (Setting s : settingsManager.getSettings()) {
                        if(s.isCombo()) {
                            for(String option : s.binders.keySet()) {
                                Binder binder = s.binders.get(option);
                                if(IBindable.Companion.getKey(s) == button && binder.getType() == BindType.Mouse) {
                                    s.setValString(option);
                                    if(init && Config.instance.notification.getValBoolean()) ChatUtility.message().printClientMessage(TextFormatting.GRAY + "Setting " + s.toDisplayString() + " has been changed to " + option + "!");
                                }
                            }
                        } else if(IBindable.Companion.getKey(s) == button && s.getType() == BindType.Mouse && s.isCheck()) {
                            s.setValBoolean(!s.getValBoolean());
                            if(init && Config.instance.notification.getValBoolean()) ChatUtility.message().printClientMessage(TextFormatting.GRAY + "Setting " + (s.getValBoolean() ? TextFormatting.GREEN : TextFormatting.RED) + s.getParentMod().getName() + "->" + s.getName() + TextFormatting.GRAY + " has been " + (s.getValBoolean() ? "enabled" : "disabled") + "!");
                        }
                    }
                } else if(Mouse.getEventButton() > 1) onRelease(Mouse.getEventButton(), true);
            }
        } catch (Exception ignored) {}
    }

    private void onRelease(int key, boolean mouse) {
        for(Module m : moduleManager.modules) if(IBindable.Companion.getKey(m) == key && (!mouse || (m.getType() == BindType.Mouse))) if(m.hold) m.toggle();
        for(HudModule m : hudModuleManager.modules) if(IBindable.Companion.getKey(m) == key && (!mouse || (m.getType() == BindType.Mouse))) if(m.hold) m.toggle();
        for (Setting s : settingsManager.getSettings()) if(IBindable.Companion.getKey(s) == key && s.getType() == BindType.Mouse && s.isCheck()) {
            s.setValBoolean(!s.getValBoolean());
            if(init && Config.instance.notification.getValBoolean()) ChatUtility.message().printClientMessage(TextFormatting.GRAY + "Setting " + (s.getValBoolean() ? TextFormatting.GREEN : TextFormatting.RED) + s.getParentMod().getName() + "->" + s.getName() + TextFormatting.GRAY + " has been " + (s.getValBoolean() ? "enabled" : "disabled") + "!");
        }
    }

    public static String getName() {
        return instance.name();
    }

    public String name() {
        if(init) {
            switch (Config.instance.nameMode.getValString()) {
                case "kismancc": return NAME;
                case "LavaHack": return "LavaHack";
                case "TheKisDevs": return "TheKisDevs";
                case "kidman": return "kidman.club";
                case "TheClient": return "TheClient";
                case "BloomWare": return "BloomWare";
                case "kidmad": return "kidmad.sex";
                case "UwU": return "UwU";
                case "EarthHack": return "3arthH4ck";
                case "custom": return Config.instance.customName.getValString();
            }
        }
        return NAME;
    }

    public static String getVersion() {
        if(instance.init) {
            switch (Config.instance.nameMode.getValString()) {
                case "BloomWare": return "1.0";
                case "EarthHack": return "1.6.2";
                default : return VERSION;
            }
        }
        return VERSION;
    }

    public static void initDirs() throws IOException {
        if (!Files.exists(Paths.get(fileName))) {
            Files.createDirectories(Paths.get(fileName));
            LOGGER.info("Root dir created");
        }
        if (!Files.exists(Paths.get(fileName + imagesName))) {
            Files.createDirectories(Paths.get(fileName + imagesName));
            LOGGER.info("Images dir created");
        }
        if (!Files.exists(Paths.get(fileName + luaName))) {
            Files.createDirectories(Paths.get(fileName + luaName));
            LOGGER.info("Lua dir created");
        }
        if (!Files.exists(Paths.get(fileName + mappingName))) {
            Files.createDirectories(Paths.get(fileName + mappingName));
            LOGGER.info("Mapping dir created");
        }
        if (!Files.exists(Paths.get(fileName + pluginsName))) {
            Files.createDirectories(Paths.get(fileName + pluginsName));
            LOGGER.info("Plugins dir created");
        }
    }

    public static void openLink(String link) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) desktop.browse(new URI(link));
        } catch (IOException | URISyntaxException e) {e.printStackTrace();}
    }

    //lua
    public static void reloadGUIs() {
        if(instance.halqGui == null) return;
        boolean flag = false;
        if(mc.currentScreen instanceof HalqGui) {
            mc.displayGuiScreen(null);
            flag = true;
        }
        instance.halqGui.frames.forEach(Frame::reload);
        if(flag) {
            mc.displayGuiScreen(instance.halqGui);
        }
    }

    public static boolean runningFromIntelliJ() {
        return System.getProperty("java.class.path").contains("idea_rt.jar");
    }

    public static void unsafeCrash() {
        Unsafe unsafe = null;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            System.exit(-1);
            for (Field f : Minecraft.class.getDeclaredFields()) {
                try {
                    f.set(null, null);
                } catch (IllegalAccessException ignored) {}
            }
        }
        unsafe.putAddress(0, 0);
        unsafe.freeMemory(0);
    }
}