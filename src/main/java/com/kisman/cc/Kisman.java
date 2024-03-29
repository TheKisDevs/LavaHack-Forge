package com.kisman.cc;

import com.kisman.cc.event.EventProcessor;
import com.kisman.cc.event.KismanEventBus;
import com.kisman.cc.features.aiimprovements.AIImprovementsMod;
import com.kisman.cc.features.command.CommandManager;
import com.kisman.cc.features.command.commands.ClientNameCommand;
import com.kisman.cc.features.command.commands.ClientVersionCommand;
import com.kisman.cc.features.hud.HudModuleManager;
import com.kisman.cc.features.module.ModuleManager;
import com.kisman.cc.features.module.client.Config;
import com.kisman.cc.features.module.client.CustomFontModule;
import com.kisman.cc.features.plugins.PluginHandler;
import com.kisman.cc.features.plugins.managers.PluginManager;
import com.kisman.cc.features.rpc.RPC;
import com.kisman.cc.features.schematica.schematica.Schematica;
import com.kisman.cc.features.subsystem.SubSystemManager;
import com.kisman.cc.features.viaforge.ViaForge;
import com.kisman.cc.features.viaforge.gui.ViaForgeGui;
import com.kisman.cc.gui.console.ConsoleGui;
import com.kisman.cc.gui.halq.Frame;
import com.kisman.cc.gui.halq.HalqGui;
import com.kisman.cc.gui.hudeditor.HalqHudGui;
import com.kisman.cc.gui.loadingscreen.progressbar.ProgressBarController;
import com.kisman.cc.gui.mainmenu.gui.MainMenuController;
import com.kisman.cc.gui.music.MusicGui;
import com.kisman.cc.gui.selectionbar.SelectionBar;
import com.kisman.cc.loader.LavaHackInterface;
import com.kisman.cc.settings.SettingsManager;
import com.kisman.cc.util.AccountDataCheckerKt;
import com.kisman.cc.util.UtilityKt;
import com.kisman.cc.util.manager.Managers;
import com.kisman.cc.util.manager.file.ConfigManager;
import com.kisman.cc.util.manager.friend.FriendManager;
import com.kisman.cc.util.render.customfont.CustomFontUtil;
import com.kisman.cc.util.render.shader.ShaderShell;
import com.kisman.cc.util.thread.kisman.ThreadManager;
import com.kisman.cc.util.thread.kisman.ThreadsKt;
import com.kisman.cc.websockets.WebSocketsManagerKt;
import me.zero.alpine.bus.EventManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import sun.misc.Unsafe;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Kisman {
    public static final String HASH = RandomStringUtils.random(10, true, true);

    public static final String NAME = "LavaHack";
    public static final String MODID = "kisman";
    public static final String VERSION = "b0.1.7";
    public static final String fileName = "kisman.cc/";
    public static final String luaName = "Lua/";
    public static final String mappingName = "Mapping/";
    public static final String imagesName = "Images/";
    public static final String pluginsName = "Plugins/";

    public static final Kisman instance = new Kisman();
    public static final EventManager EVENT_BUS = new KismanEventBus();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final HashMap<GuiScreen, Float> map = new HashMap<>();

    public static EntityPlayer target_by_click = null;

    /**
     * This will be the default value for {@code remapped} if
     * the check for remapped failed.
     * - Cubic
     */
    public static final boolean ASSUME_REMAPPED = false;

    public static boolean remapped = !UtilityKt.fromIntellij() || checkRemapped();
    public static boolean canInitializateCatLua = false;
    public static boolean callingFromGameLoop = false;

    public static String currentConfig = null;

    public boolean init = false;

    private static Minecraft mc;

    public ModuleManager moduleManager;
    public FriendManager friendManager;
    public HudModuleManager hudModuleManager;
    public SettingsManager settingsManager;
    public ThreadManager threadManager;
    public ConsoleGui consoleGui;
    public HalqGui halqGui;
    public HalqHudGui halqHudGui;
    public ViaForgeGui viaForgeGui;
    public SelectionBar selectionBar;
    public MusicGui musicGui;
    public MainMenuController mainMenuController;
    public CommandManager commandManager;
    public RPC discord;
    public EventProcessor eventProcessor;
    public Managers managers;
    public PluginManager pluginManager;
    public SubSystemManager subSystemManager;

    //Config
    public ConfigManager configManager;

    //Plugins
    public final PluginHandler pluginHandler = new PluginHandler();

    public ProgressBarController progressBar;

    public boolean haveLoader = false;

    private Kisman() {}

    public void coreModInit() {
        pluginHandler.coreModInit();
    }

    public void init() throws IOException, NoSuchFieldException, IllegalAccessException {
        if(init) return;

        LOGGER.info("Initializing LavaHack " + VERSION);

        ThreadsKt.getExecutor().submit(() -> {
            long timeStamp = System.currentTimeMillis();

            LOGGER.info("Initializing fonts: Part 1");

            CustomFontUtil.initFonts();

            LOGGER.info("Initialized fonts! It took " + (System.currentTimeMillis() - timeStamp) + " ms!");

            timeStamp = System.currentTimeMillis();

            LOGGER.info("Initializing ViaForge implementation!");

            ViaForge.getInstance().start();

            LOGGER.info("Initialized ViaForge implementation! It took " + timeStamp + " ms!");
        });

        long timeStamp = System.currentTimeMillis();

        //TODO: rewrite it
        try {
            haveLoader = LavaHackInterface.INSTANCE.isLoaded();
        } catch (Throwable ignored) {
            haveLoader = false;
        }

        LOGGER.info("We " + (haveLoader ? "" : "do not ") + "have loader!");

        processAccountData();
//        processContainerCheck();
        processResourceCacheCheck();

        ThreadsKt.getExecutor().submit(WebSocketsManagerKt::initClient);

        progressBar = new ProgressBarController("LavaHack");


        AIImprovementsMod.preInit();

        eventProcessor = new EventProcessor();
        managers = new Managers();
        managers.init();

        friendManager = new FriendManager();
        settingsManager = new SettingsManager();
        moduleManager = new ModuleManager();
        threadManager = new ThreadManager();
//        noComModuleManager = new NoComModuleManager();
        hudModuleManager = new HudModuleManager();

        progressBar.init();

        AIImprovementsMod.init();

        eventProcessor.init();
        moduleManager.init();
        hudModuleManager.init();

        progressBar.uninit();

        Display.setTitle(NAME + " | " + VERSION);
        MinecraftForge.EVENT_BUS.register(this);
        mc = Minecraft.getMinecraft();

        pluginManager = new PluginManager();

        commandManager = new CommandManager();
        discord = new RPC();
        pluginHandler.init();

        //load 2d shaders
        ShaderShell.init();

        LOGGER.info("Initializing guis: Part 1");

        //gui's
        consoleGui = new ConsoleGui();
        halqHudGui = new HalqHudGui();

        mainMenuController = new MainMenuController();
        mainMenuController.init();

        selectionBar = new SelectionBar(SelectionBar.Guis.ClickGui);

        musicGui = new MusicGui();

        LOGGER.info("Initializing default config manager!");

        configManager = new ConfigManager("config");

        LOGGER.info("Initializing subsystem manager!");

        subSystemManager = new SubSystemManager();
        subSystemManager.init();

        LOGGER.info("Initializing Schematica implementation!");

        Schematica.instance.init();

        LOGGER.info("Initializing fonts: Part 2");

        CustomFontUtil.setupTextures();
        CustomFontModule.instance.registerSettings();

        LOGGER.info("Loading default config!");

        configManager.getLoader().init();

        LOGGER.info("Initializing guis: Part 2");

        halqGui = new HalqGui();
        halqGui.init();
        viaForgeGui = new ViaForgeGui();

        LOGGER.info("Initialized LavaHack " + VERSION + "! It took " + (System.currentTimeMillis() - timeStamp) + " ms!");

        init = true;
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
                case "ClientName": return ClientNameCommand.NAME;
            }
        }
        return NAME;
    }

    public static String getVersion() {
        if(instance.init) {
            switch (Config.instance.nameMode.getValString()) {
                case "BloomWare": return "1.0";
                case "EarthHack": return "1.8.4";
                case "ClientName": return ClientVersionCommand.VERSION;
                default : return VERSION;
            }
        }
        return VERSION;
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

    /**
     * Checks if Minecraft is remapped using reflection to access a
     * Minecraft field by it's unmapped name. If Minecraft is remapped,
     * it will throw a {@link NoSuchFieldException}. If this happens,
     * Minecraft is (most likely) remapped.
     * @author Cubic
     * @since  05.10.2022
     * @return if Minecraft is remapped or not
     */
    public static boolean checkRemapped(){
        try {
            Minecraft.class.getDeclaredField("world");
            return false;
        } catch (NoSuchFieldException e){ // could not find the field because mc is remapped
            return true;
        } catch (SecurityException e){ // check failed, return what we assume
            LOGGER.error("Could not check if Minecraft is remapped!");
            return ASSUME_REMAPPED;
        }
    }

    public static void unsafeCrash() {
        Unsafe unsafe = null;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            FMLCommonHandler.instance().exitJava(-1, true);
            for (Field f : Minecraft.class.getDeclaredFields()) {
                try {
                    f.set(null, null);
                } catch (IllegalAccessException ignored) {}
            }
        }
        unsafe.putAddress(0, 0);
        unsafe.freeMemory(0);
    }

    private static void processContainerCheck() {
        try {
            Class.forName("ghost.classes.DevelopmentEnvironment");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.kisman.cc.loader.LavaHackLoaderCoreMod");
                unsafeCrash();
            } catch(ClassNotFoundException ignored) { }
        }
    }

    public static void processAccountData() {
        try {
            Class.forName("ghost.classes.DevelopmentEnvironment");
        } catch(Throwable ignored) {
            AccountDataCheckerKt.check();
        }
    }

    public static void processResourceCacheCheck() {
        try {
            Class.forName("ghost.classes.DevelopmentEnvironment");
        } catch(Throwable ignored) {
            try {
                for (Map.Entry<String, byte[]> entry : UtilityKt.resourceCache().entrySet()) if (entry.getKey().contains("lavahack") || entry.getKey().contains("kisman")) throw new Exception();

                unsafeCrash();
            } catch(Throwable ignored2) { }
        }
    }
}
