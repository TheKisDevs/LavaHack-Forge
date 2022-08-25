package com.kisman.cc.features.module;

import com.kisman.cc.Kisman;
import me.zero.alpine.listener.Listenable;
import net.minecraftforge.common.MinecraftForge;

public class SubscribeMode {

    public static final int NONE = 0;

    public static final int MINECRAFT_FORGE = 1;

    public static final int ALPINE = 2;

    public static final int ALL = MINECRAFT_FORGE | ALPINE;

    public static final int OPTION_COUNT = 3;

    public static void register(Subscribes subscribes, Object obj){
        int mode = subscribes.mode();
        for(int i = 0; i < (32 - Integer.numberOfLeadingZeros(mode)); i++)
            if((i & 1) != 0)
                register[i].process(obj, false);
    }

    public static void unregister(Subscribes subscribes, Object obj){
        int mode = subscribes.mode();
        for(int i = 0; i < (32 - Integer.numberOfLeadingZeros(mode)); i++)
            if((i & 1) != 0)
                register[i].process(obj, true);
    }

    private interface Register {

        void process(Object obj, boolean unregister);
    }

    private static final Register[] register = new Register[OPTION_COUNT];

    static {
        register[0] = (obj, unregister) -> {};
        register[1] = (obj, unregister) -> {
            if(unregister) {
                MinecraftForge.EVENT_BUS.unregister(obj);
                return;
            }
            MinecraftForge.EVENT_BUS.register(obj);
        };
        register[2] = (obj, unregister) -> {
            if(!(obj instanceof Listenable)){
                Kisman.LOGGER.warn("[SubscribeMode] " + obj.getClass().getName() + " is not a Listenable and will not be processed!");
                return;
            }
            if(unregister){
                Kisman.EVENT_BUS.unsubscribe((Listenable) obj);
                return;
            }
            Kisman.EVENT_BUS.subscribe((Listenable) obj);
        };
    }
}
