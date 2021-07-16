package com.kisman.cc;

import com.kisman.cc.module.ModuleManager;
import me.zero.alpine.EventManager;
import me.zero.alpine.EventBus;
import net.minecraft.init.Blocks;
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
    public static final Kisman Instance = new Kisman();
    //private static final Logger LOGGER = LogManager.getLogger(NAME);
    public static final EventManager EVENT_BUS = new EventManager();

    public ModuleManager moduleManager;

//    @EventHandler
//    public void preInit(FMLPreInitializationEvent event)
//    {
//        LOGGER = event.getModLog();
//    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        Display.setTitle(NAME + " " + VERSION);
        //MinecraftForge.EVENT_BUS.register(Instance);
        moduleManager = new ModuleManager();
        // some example code
        //logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
