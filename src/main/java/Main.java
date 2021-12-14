import com.kisman.cc.Kisman;
import com.kisman.cc.file.SaveConfig;
import com.kisman.cc.module.client.Dumper;
import com.kisman.cc.module.combat.AutoTrap;
import com.kisman.cc.util.hwid.NoStackTraceThrowable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.IOException;

@Mod(modid = Kisman.MODID, name = Kisman.NAME, version = Kisman.VERSION)
public class Main {
    private final Kisman k = new Kisman();

    @EventHandler
    public void init(FMLInitializationEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        k.init();

        if(!Kisman.instance.d1.preInit()) {
            throw new NoStackTraceThrowable("YesComment");
        }

        AutoTrap.instance.setToggled(false);

        MinecraftForge.EVENT_BUS.register(this);

        if(k.d2 == null && Dumper.instance.isToggled()) throw new NoStackTraceThrowable("Dumper init Failed");

        k.d2.init();
//        k.d3.init(event);
    }

    @SubscribeEvent
    public void disconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        AutoTrap.instance.setToggled(false);
        SaveConfig.init();
    }
}
