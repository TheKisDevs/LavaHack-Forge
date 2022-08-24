package com.kisman.cc.loader;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * @author _kisman_
 * @since 12:20 of 04.07.2022
 */
@Mod(
        name = LavaHackLoaderMod.NAME,
        modid = LavaHackLoaderMod.MODID,
        version = LavaHackLoaderMod.VERSION,
        useMetadata = true
)
public class LavaHackLoaderMod {
    public static final String NAME = "Loader";
    public static final String MODID = "loader";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        System.out.println("meow");

        if(Utility.runningFromIntelliJ() || !LavaHackLoaderCoreMod.Companion.getLoaded()) {
            return;
        }

        System.out.println("meow2");

        try {
            Class<?> lavahack = Class.forName("com.kisman.cc.Kisman");
            lavahack.getMethod("init").invoke(lavahack.getField("instance").get(null));
        } catch (Exception e) {
            e.printStackTrace();
            Utility.unsafeCrash();
        }
    }
}
