package com.kisman.cc.loader;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

import java.lang.reflect.InvocationTargetException;

/**
 * @author _kisman_
 * @since 12:20 of 04.07.2022
 */
@Mod(
        modid = LavaFalconMod.MODID,
        name = LavaFalconMod.NAME,
        version = LavaFalconMod.VERSION
)
public class LavaFalconMod {
    public static final String MODID = "lavafalcon";
    public static final String NAME = "LavaFalcon";
    public static final String VERSION = "1.0";

//    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if(Utility.runningFromIntelliJ()) {
            return;
        }

        System.out.println("Init Event 1");

        try {
            Class.forName("com.kisman.cc.Kisman").getMethod("init").invoke(Class.forName("com.kisman.cc.Kisman").getDeclaredField("instance").get(null));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
            exit();
        }

        System.out.println("Init Event 2");
    }

    private void exit() {
        System.out.println("Cant find main class of lavahack or preInit/init method or instance field! Shutdown!");
        Minecraft.getMinecraft().shutdown();
    }
}
