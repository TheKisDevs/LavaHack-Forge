package com.kisman.cc.loader;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.lang.reflect.InvocationTargetException;

/**
 * @author _kisman_
 * @since 12:20 of 04.07.2022
 */
@Mod(modid = LavaFalconMod.MODID)
public class LavaFalconMod {
    public static final String MODID = "loader";

    public static Class<?> lavahack = null;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if(Utility.runningFromIntelliJ()) {
            return;
        }

        System.out.println("Init Event 1");

        try {
            lavahack.getMethod("init").invoke(lavahack.getField("instance").get(null));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
            Utility.unsafeCrash();
        }

        System.out.println("Init Event 2");
    }
}
