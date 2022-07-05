package com.kisman.cc.loader;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.lang.reflect.InvocationTargetException;

/**
 * @author _kisman_
 * @since 12:20 of 04.07.2022
 */
public class LavaFalconMod {
    public static final String MOD_ID = "lavafalcon";
    public static final String MOD_NAME = "LavaFalcon";
    public static final String VERSION = "1.0";

    private final Object lavahack;

    public LavaFalconMod() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        lavahack = Class.forName("Main").newInstance();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class.forName("Main").getMethod("preInit", event.getClass()).invoke(lavahack, event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class.forName("Main").getMethod("init", event.getClass()).invoke(lavahack, event);
    }
}
