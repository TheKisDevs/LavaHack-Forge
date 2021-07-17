package com.kisman.cc;

import com.kisman.cc.module.ModuleManager;
import me.zero.alpine.bus.EventBus;
import me.zero.alpine.bus.EventManager;
import me.zero.alpine.listener.Listenable;
import me.zero.alpine.listener.Listener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

@Mod(modid = Kisman.MODID, name = Kisman.NAME, version = Kisman.VERSION)
public class Kisman
{
    public static final String MODID = "kisman";
    public static final String NAME = "kisman.cc";
    public static final String VERSION = "b0.0.1";

    @Instance
    //public static final Kisman Instance = new Kisman();
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final EventBus EVENT_BUS = new EventManager() {
//        @Override
//        public void subscribe(Listenable listenable) {}
//
//        @Override
//        public void subscribe(Listener listener) {}
//
//        @Override
//        public void unsubscribe(Listenable listenable) {}
//
//        @Override
//        public void unsubscribe(Listener listener) {}
//
//        @Override
//        public void post(Object event) {}
    };

    @Mod.Instance
    public static Kisman INSTANCE;
    public Kisman() {
        INSTANCE = this;
    }
    private static ModuleManager moduleManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {}

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("kisman.cc Starting!");
        Display.setTitle(NAME + " " + VERSION);
        LOGGER.info("START LOAD!");
        //MinecraftForge.EVENT_BUS.register();
        load();
        LOGGER.info("Finish load");
    }

    public static void load() {
        LOGGER.info("\n\nLoading kisman.cc");
        moduleManager = new ModuleManager();
    }
}
